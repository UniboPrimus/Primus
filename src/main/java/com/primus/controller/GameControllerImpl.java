package com.primus.controller;

import com.primus.model.core.GameManager;
import com.primus.model.deck.Card;
import com.primus.model.player.Player;
import com.primus.view.GameView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of {@link GameController} to manage the game loop and act as a bridge between view and model.
 */
public final class GameControllerImpl implements GameController {
    private static final int BOT_DELAY = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameControllerImpl.class);

    private final GameManager manager;
    private final List<GameView> views = new ArrayList<>();
    private CompletableFuture<Card> humanInputFuture;

    // Flag to control the game loop, accessed from multiple threads (start/stop)
    @SuppressWarnings("PMD.SingularField")
    private volatile boolean isRunning;

    /**
     * Constructor for GameControllerImpl.
     *
     * @param manager game manager
     */
    public GameControllerImpl(final GameManager manager) {
        this.manager = manager;
    }

    @Override
    public void start() {
        LOGGER.info("Starting GameController");
        this.isRunning = true;
        LOGGER.debug("GameManager initialized");

        views.forEach(v -> {
            v.initGame(manager.getGameSetup());
            v.updateView(manager.getGameState());
        });

        LOGGER.info("Game loop is starting");

        // Game Loop
        while (manager.getWinner().isEmpty() && isRunning) {
            final Player currentPlayer = manager.nextPlayer();

            LOGGER.debug("Starting turn for player with ID: {}", currentPlayer.getId());

            views.forEach(v -> v.showCurrentPlayer(currentPlayer.getId()));

            // Management of turn based on player type
            if (currentPlayer.isBot()) {
                handleBotTurn(currentPlayer);
            } else {
                handleHumanTurn(currentPlayer);
            }

            views.forEach(v -> v.updateView(manager.getGameState()));

        }

        if (manager.getWinner().isPresent()) {
            //TODO gestire vittoria
            LOGGER.info("Game ended. Winner: {}", manager.getWinner().get());
            views.forEach(v -> v.showMessage("PARTITA TERMINATA!"));
        } else {
            LOGGER.warn("Game ended without a winner");
        }
    }

    @Override
    public void stop() {
        LOGGER.info("Game loop stop requested");
        this.isRunning = false;
        if (this.humanInputFuture != null && !this.humanInputFuture.isDone()) {
            this.humanInputFuture.cancel(true);
            LOGGER.debug("Cancelling human input future");
        }
    }

    @Override
    public void addView(final GameView view) {
        views.add(view);

        view.setCardPlayedListener(this::humanPlayedCard);
        view.setDrawListener(this::humanDrewCard);

        LOGGER.debug("New view added to controller");
    }

    @Override
    public void humanPlayedCard(final Card card) {
        Objects.requireNonNull(card);

        LOGGER.debug("Callback View: Human player wants to play {}", card);

        if (this.humanInputFuture != null && !this.humanInputFuture.isDone()) {
            this.humanInputFuture.complete(card);
        } else {
            LOGGER.warn("Received unexpected input from the human player");
        }
    }

    @Override
    public void humanDrewCard() {
        LOGGER.debug("Callback View: Human player drawed a card");
        if (this.humanInputFuture != null && !this.humanInputFuture.isDone()) {
            this.humanInputFuture.complete(null);
        }
    }

    /**
     * Bot handling (Synchronous loop).
     *
     * @param player the bot player
     */
    private void handleBotTurn(final Player player) {
        Objects.requireNonNull(player);
        boolean turnCompleted = false;

        LOGGER.debug("Shift started for the BOT ID: {}", player.getId());

        // Loop until the bot completes its turn in a valid way
        while (!turnCompleted) {
            sleep(); // Little delay for realism

            // Ask the bot for its intention
            final Optional<Card> intention = player.playCard();

            // Bot decides to draw a card
            if (intention.isEmpty()) {
                LOGGER.info("BOT {} drawed a car.", player.getId());
                manager.executeTurn(null);
                views.forEach(v -> v.showMessage(player.getId() + " ha pescato."));

                turnCompleted = true;
            } else {
                // Bot decides to play a card
                final Card cardToPlay = intention.get();

                LOGGER.info("BOT {} trying to play {}", player.getId(), cardToPlay);

                // Try to execute the turn with the chosen card
                final boolean moveAccepted = manager.executeTurn(cardToPlay);

                if (moveAccepted) {
                    LOGGER.debug("Move accepted");
                    views.forEach(v -> v.showMessage(player.getId() + " gioca " + cardToPlay));
                    turnCompleted = true;
                } else {
                    // If move not accepted, bot must choose again
                    LOGGER.warn("BOT move rejected: {} tried to play {}.", player.getId(), cardToPlay);
                }
            }
        }
        views.forEach(v -> v.updateView(manager.getGameState()));
    }

    /**
     * Human handling (Asynchronous loop).
     *
     * @param player the human player
     */
    private void handleHumanTurn(final Player player) {
        Objects.requireNonNull(player);
        boolean turnCompleted = false;

        LOGGER.debug("Waiting an input from human player");

        views.forEach(v -> v.showMessage("Turno di human player"));

        while (!turnCompleted) {
            try {
                this.humanInputFuture = new CompletableFuture<>();

                // Await user input (either play a card or draw) from the view
                final Card chosenCard = this.humanInputFuture.get();

                LOGGER.debug("Processing human move: {}", chosenCard == null ? "Draw a card" : chosenCard);

                // Try to execute the turn with the chosen card (null if drawing)
                final boolean moveAccepted = manager.executeTurn(chosenCard);

                if (moveAccepted) {
                    LOGGER.info("Human move accepted");
                    turnCompleted = true;
                } else {
                    LOGGER.info("Human move rejected. A new move is requested");
                    views.forEach(v -> v.showError("Mossa non valida! Riprova."));
                    // If move not accepted, human must choose again
                }

            } catch (InterruptedException | ExecutionException e) {
                // If thread is interrupted the game should stop gracefully
                LOGGER.error("Crtitical error during human shift (Thread interrupted or ExecutionException)", e);
                stop();
                Thread.currentThread().interrupt();
            } catch (final java.util.concurrent.CancellationException e) {
                // Future was cancelled
                LOGGER.info("Human waiting cancelled (game probably has been stopped)");
                stop();
            }
        }
    }

    /**
     * Sleeps the current thread for a specified duration.
     */
    private void sleep() {
        try {
            Thread.sleep(BOT_DELAY);
        } catch (final InterruptedException e) {
            LOGGER.error("BOT sleep interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
