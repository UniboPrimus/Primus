package com.primus.controller;

import com.primus.model.core.GameManager;
import com.primus.model.deck.Card;
import com.primus.model.player.Player;
import com.primus.view.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
//TODO importare le classi non ancora create ma necessarie
// import com.primus.model.player.Bot;

// TODO gestire tutta interazione con la view

/**
 * Implementation of {@link GameController} to manage the game loop and act as a bridge between view and model.
 */
public final class GameControllerImpl implements GameController {
    private static final int BOT_DELAY = 1000;

    private final GameManager manager;
    private final List<GameView> views = new ArrayList<>();
    private CompletableFuture<Card> humanInputFuture;

    // Flag to control the game loop, useful for stopping the game gracefully
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
        this.isRunning = true;
        manager.init();

        for (final GameView v : views) {
            v.initGame(manager.getGameSetup()); //TOdo li chiamo qui o nel addview
            v.updateView(manager.getGameState());
        }

        // Game Loop
        while (manager.getWinner().isEmpty() && isRunning) {
            final Player currentPlayer = manager.nextPlayer();

            for (final GameView v : views) {
                v.showCurrentPlayer(currentPlayer.getId());
            }

            // Management of turn based on player type
            if (currentPlayer.isBot()) {
                handleBotTurn(currentPlayer);
            } else {
                handleHumanTurn(currentPlayer);
            }

            for (final GameView v : views) {
                v.updateView(manager.getGameState());
            }

        }

        if (manager.getWinner().isPresent()) {
            //TODO gestire vittoria

            for (final GameView v : views) {
                v.showMessage("PARTITA TERMINATA!");
            }

        }
    }

    @Override
    public void stop() {
        this.isRunning = false;
        if (this.humanInputFuture != null && !this.humanInputFuture.isDone()) {
            this.humanInputFuture.cancel(true);
        }
    }

    @Override
    public void addView(final GameView view) {
        views.add(view);

        view.setCardPlayedListener(this::humanPlayedCard);
        view.setDrawListener(this::humanDrewCard);

        //view.initGame(manager.getGameSetup());
        //view.updateView(manager.getGameState());
    }

    @Override
    public void humanPlayedCard(final Card card) {
        Objects.requireNonNull(card);
        if (this.humanInputFuture != null && !this.humanInputFuture.isDone()) {
            this.humanInputFuture.complete(card);
        }
    }

    @Override
    public void humanDrewCard() {
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

        // Loop until the bot completes its turn in a valid way
        while (!turnCompleted) {
            sleep(); // Little delay for realism

            // Ask the bot for its intention
            final Optional<Card> intention = player.playCard();

            // Bot decides to draw a card
            if (intention.isEmpty()) {
                manager.executeTurn(null);
                for (final GameView v : views) {
                    v.showMessage(player.getId() + " ha pescato.");
                }

                turnCompleted = true;
            } else {
                // Bot decides to play a card
                final Card cardToPlay = intention.get();

                // Try to execute the turn with the chosen card
                final boolean moveAccepted = manager.executeTurn(cardToPlay);

                if (moveAccepted) {
                    for (final GameView v : views) {
                        v.showMessage(player.getId() + " gioca " + cardToPlay);
                    }
                    turnCompleted = true;
                }
                // If move not accepted, bot must choose again
            }
        }
        for (final GameView v : views) {
            v.updateView(manager.getGameState());
        }
    }

    /**
     * Human handling (Asynchronous loop).
     *
     * @param player the human player
     */
    private void handleHumanTurn(final Player player) {
        Objects.requireNonNull(player);
        boolean turnCompleted = false;

        for (final GameView v : views) {
            v.showMessage("Turno di human player");
        }

        while (!turnCompleted) {
            try {
                this.humanInputFuture = new CompletableFuture<>();

                // Await user input (either play a card or draw) from the view
                final Card chosenCard = this.humanInputFuture.get();

                // Try to execute the turn with the chosen card (null if drawing)
                final boolean moveAccepted = manager.executeTurn(chosenCard);

                if (moveAccepted) {
                    turnCompleted = true;
                } else {
                    for (final GameView v : views) {
                        v.showError("Mossa non valida! Riprova.");
                    }
                    // If move not accepted, human must choose again
                }

            } catch (InterruptedException | ExecutionException e) {
                // If thread is interrupted the game should stop gracefully
                stop();
                Thread.currentThread().interrupt();
            } catch (final java.util.concurrent.CancellationException e) {
                // Future was cancelled
                stop();
            }
        }
    }

    /**
     * Sleeps the current thread for a specified duration.
     */
    private void sleep() {
        try {
            Thread.sleep(GameControllerImpl.BOT_DELAY);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
