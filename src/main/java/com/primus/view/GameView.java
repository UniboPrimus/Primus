package com.primus.view;

import com.primus.model.deck.Card;
import com.primus.utils.GameState;
import com.primus.utils.PlayerSetupData;

import java.util.List;
import java.util.function.Consumer;

/**
 * Game view interface.
 */
public interface GameView {

    /**
     * Initilize the game view with the necessary data to create player instances and configure the view.
     *
     * @param players a list of {@link PlayerSetupData} containing the necessary information to create player
     */
    void initGame(List<PlayerSetupData> players);

    /**
     * Links a listener for the card played event, in particular when a player clicks on a card
     * in their hand to play it.
     *
     * @param listener a Consumer that accepts the Card played by the user, to be passed
     *                 to the controller for processing the move.
     */
    void setCardPlayedListener(Consumer<Card> listener);

    /**
     * Links a listener for the draw card event, in particular when a player clicks on the deck to draw a card.
     *
     * @param listener a Runnable that will be executed when the user clicks on the deck to draw a card, to be passed
     */
    void setDrawListener(Runnable listener);

    /**
     * Links a listener for the new match event, in particular when a player wants to start a new match after the
     * current game has ended, or when they want to exit the game.
     *
     * @param listener a Consumer that accepts a Boolean which is {@code true} if the user wants to start a new
     *                 match, and {@code false} if the user wants to exit the game
     */
    void setNewMatchListener(Consumer<Boolean> listener);

    /**
     * Updates the view to reflect the current game state. This method should be called by the controller
     * after any change in the game state (e.g., after a player plays a card, draws a card, or at the end of a turn)
     *
     * @param gameState the current state of the game, containing information such as the top card,
     *                  the active player's hand, and the active player's ID.
     */
    void updateView(GameState gameState);

    /**
     * Highlights the current active player in the view, allowing users to easily identify whose turn it is.
     *
     * @param currentPlayer the ID of the current active player, used to highlight the corresponding player in the view.
     */
    void showCurrentPlayer(int currentPlayer);

    /**
     * Displays a message to the user, which can be used for various purposes such as showing the result of a move.
     *
     * @param message the text of the message to be displayed
     */
    void showMessage(String message);

    /**
     * Displays an error message to the user, which can be used to inform them about invalid moves
     * game errors, or other issues that may arise during gameplay.
     *
     * @param errorMessage the text of the error message to be displayed
     */
    void showError(String errorMessage);

    /**
     * Displays a game over message to the user, indicating that the game has ended and announcing the winner.
     *
     * @param winnerName the name of the winning player, to be included in the game over message displayed to the user.
     */
    void showGameOverMessage(String winnerName);
}
