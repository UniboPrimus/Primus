package com.primus.controller;

import com.primus.model.core.GameManager;
import com.primus.model.deck.Card;
import com.primus.model.player.Player;
import com.primus.utils.GameState;

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
    private final GameView view;
    private CompletableFuture<Card> humanInputFuture;

    /**
     * Constructor for GameControllerImpl.
     *
     * @param manager game manager
     * @param view    game view
     */
    public GameControllerImpl(final GameManager manager, final GameView view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void start() {
        manager.init();
        view.updateView(manager.getGameState());

        // Game Loop
        while (manager.getWinner().isEmpty()) {
            final Player currentPlayer = manager.nextPlayer();
            view.showCurrentPlayer(currentPlayer);

            // Check if the current player has to skip turn due to malus
            if (manager.resolvePreTurnMalus()) {
                view.showMessage(currentPlayer.getId() + " salta il turno per penalità.");
                view.updateView(manager.getGameState());
                continue;
            }

            // Management of turn based on player type
            if (currentPlayer.isBot()) {
                handleBotTurn(currentPlayer);
            } else {
                handleHumanTurn(currentPlayer);
            }

            view.updateView(manager.getGameState());
        }

        if (manager.getWinner().isPresent()) {
            //TODO gestire vittoria
            view.showMessage("PARTITA TERMINATA!");
        }
    }

    @Override
    public void stop() {
        if (this.humanInputFuture != null && !this.humanInputFuture.isDone()) {
            this.humanInputFuture.cancel(true);
        }
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
            sleep(this.BOT_DELAY); // Little delay for realism

            // Ask the bot for its intention
            final Optional<Card> intention = player.playCard();

            // Bot decides to draw a card
            if (intention.isEmpty()) {
                manager.executeTurn(player, null);
                view.showMessage(player.getId() + " ha pescato.");
                turnCompleted = true;
            } else {
                // Bot decides to play a card
                final Card cardToPlay = intention.get();

                // Try to execute the turn with the chosen card
                final boolean moveAccepted = manager.executeTurn(player, cardToPlay);

                if (moveAccepted) {
                    view.showMessage(player.getId() + " gioca " + cardToPlay);
                    turnCompleted = true;
                }
                // If move not accepted, bot must choose again
            }
        }
        view.updateView(manager.getGameState());
    }

    /**
     * Human handling (Asynchronous loop).
     *
     * @param player the human player
     */
    private void handleHumanTurn(final Player player) {
        Objects.requireNonNull(player);
        boolean turnCompleted = false;

        view.showMessage("Turno di human player");

        while (!turnCompleted) {
            try {
                this.humanInputFuture = new CompletableFuture<>();

                // Await user input (either play a card or draw) from the view
                final Card chosenCard = this.humanInputFuture.get();

                // Try to execute the turn with the chosen card (null if drawing)
                final boolean moveAccepted = manager.executeTurn(player, chosenCard);

                if (moveAccepted) {
                    turnCompleted = true;
                } else {
                    view.showError("Mossa non valida! Riprova.");
                    // If move not accepted, human must choose again
                }

            } catch (InterruptedException | ExecutionException e) {
                //TODO gestire i log
                stop();
            }
        }
    }

    /**
     * Sleeps the current thread for a specified duration.
     *
     * @param ms milliseconds to sleep
     */
    private void sleep(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Necessary development stubs, development only

    public interface GameView {

        void updateView(GameState gameState);

        void showCurrentPlayer(Player currentPlayer);

        void showMessage(String s);

        void showError(String s);
    }

    private final class GameViewImpl implements GameView {

        @Override
        public void updateView(final GameState gameState) {
        }

        @Override
        public void showCurrentPlayer(final Player currentPlayer) {
        }

        @Override
        public void showMessage(String s) {
        }

        @Override
        public void showError(String s) {
        }
    }
}
