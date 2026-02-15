package com.primus.controller;

import com.primus.model.core.GameManager;
import com.primus.model.deck.Card;
import com.primus.model.player.Player;
import com.primus.utils.PlayerSetupData;
import com.primus.view.GameView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of {@link GameController} to manage the game loop and act as a bridge between view and model.
 */
public final class GameControllerImpl implements GameController {
    private static final int MAX_BOT_DELAY = 3000;
    private static final int MIN_BOT_DELAY = 1500;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameControllerImpl.class);
    private static final Random RANDOM = new Random();

    private final GameManager manager;
    private final List<GameView> views = new ArrayList<>();
    private CompletableFuture<Card> humanInputFuture;
    private CompletableFuture<Boolean> playAgainFuture;

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

        while (isRunning) {
            manager.init();

            views.forEach(v -> {
                v.initGame(manager.getGameSetup());
                v.updateView(manager.getGameState());
            });

            LOGGER.info("Game loop is starting");

            // Game Loop
            while (manager.getWinner().isEmpty() && isRunning) {
                final Player currentPlayer = manager.nextPlayer();

                LOGGER.debug("Starting turn for player with ID: {}", currentPlayer.getId());

                views.forEach(v -> {
                    v.showCurrentPlayer(currentPlayer.getId());
                    v.updateView(manager.getGameState());
                });

                // Management of turn based on player type
                if (currentPlayer.isBot()) {
                    handleBotTurn(currentPlayer);
                } else {
                    handleHumanTurn(currentPlayer);
                }

                views.forEach(v -> v.updateView(manager.getGameState()));

            }

            // If the game was forcefully stopped, exit immediately
            if (!isRunning) {
                LOGGER.info("Game loop was stopped before completion. Exiting...");
                break;
            }

            if (manager.getWinner().isPresent()) {
                final int winnerId = manager.getWinner().get();

                // Try to get the winner's name from the game setup, fallback to "Giocatore {ID}" if not found
                final String winnerName = manager.getGameSetup().stream().filter(p -> p.id() == winnerId)
                        .map(PlayerSetupData::name).findFirst().orElse("Giocatore " + winnerId);

                LOGGER.info("Game ended. Winner: {} ({})", winnerName, winnerId);

                // After the game ends, ask the user if they want to play again or exit.
                // This is done asynchronously to avoid blocking the UI thread.
                this.playAgainFuture = new CompletableFuture<>();
                views.forEach(v -> v.showGameOverMessage(winnerName));

                try {
                    final boolean wantsToPlayAgain = this.playAgainFuture.get();

                    if (!wantsToPlayAgain) {
                        LOGGER.info("User chose to quit the game.");
                        this.isRunning = false;
                    } else {
                        LOGGER.info("User chose to play again. Restarting game loop...");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Error while waiting for game over choice", e);
                    Thread.currentThread().interrupt();
                    this.isRunning = false;
                }
            } else {
                LOGGER.warn("Game ended without a winner");
            }
        }
        LOGGER.info("Game loop terminated. Closing views...");
        views.forEach(GameView::close);
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

        view.setCardPlayedListener(this::onCardPlayed);
        view.setDrawListener(this::onDrawCard);
        view.setNewMatchListener(this::onNewMatchRequested);

        LOGGER.debug("New view added to controller");
    }

    /**
     * Callback invoked when the human player requests to start a new match or exit after the current game has ended.
     *
     * @param startNew a Boolean which is {@code true} if the user wants to start a new match, and {@code false} if the
     *                user wants to exit the game
     */
    private void onNewMatchRequested(final Boolean startNew) {
        Objects.requireNonNull(startNew);
        LOGGER.debug("Callback View: Human player wants to {} now", startNew ? "start a new match" : "exit");

        if (this.playAgainFuture != null && !this.playAgainFuture.isDone()) {
            this.playAgainFuture.complete(startNew);
        } else {
            LOGGER.warn("Received unexpected input for new match request");
        }
    }

    /**
     * Callback invoked when the human player plays a card.
     *
     * @param card the card played by the human player
     */
    private void onCardPlayed(final Card card) {
        Objects.requireNonNull(card);
        LOGGER.debug("Callback View: Human player wants to play {}", card);

        if (this.humanInputFuture != null && !this.humanInputFuture.isDone()) {
            this.humanInputFuture.complete(card);
        } else {
            LOGGER.warn("Received unexpected input from the human player");
        }
    }

    /**
     * Callback invoked when the human player draws a card.
     */
    private void onDrawCard() {
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

        sleep(); // Little delay for realism
        // Loop until the bot completes its turn in a valid way
        while (!turnCompleted) {

            // Ask the bot for its intention
            final Optional<Card> intention = player.playCard();

            // Bot decides to draw a card
            if (intention.isEmpty()) {
                LOGGER.info("BOT {} drawed a car.", player.getId());
                manager.executeTurn(null);
                views.forEach(v -> v.showMessage(player.getName() + " ha pescato."));

                turnCompleted = true;
            } else {
                // Bot decides to play a card
                final Card cardToPlay = intention.get();

                LOGGER.info("BOT {} trying to play {}", player.getId(), cardToPlay);

                // Try to execute the turn with the chosen card
                final boolean moveAccepted = manager.executeTurn(cardToPlay);

                if (moveAccepted) {
                    LOGGER.debug("Move accepted");
                    views.forEach(v -> v.showMessage(player.getName() + " gioca " + cardToPlay));
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

        views.forEach(v -> v.showMessage("Tuo turno"));

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
            Thread.sleep(RANDOM.nextInt(MIN_BOT_DELAY, MAX_BOT_DELAY));
        } catch (final InterruptedException e) {
            LOGGER.error("BOT sleep interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
