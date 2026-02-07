package com.primus.controller;

import com.primus.model.deck.Card;
import com.primus.view.GameView;

/**
 * Game controller interface, manages the game loop and acts as a bridge between view and model.
 */
public interface GameController {
    /**
     * Starts the game loop.
     */
    void start();

    /**
     * Stops the game loop.
     */
    void stop();

    /**
     * Adds a view to the controller, allowing it to receive updates and user input.
     *
     * @param view the GameView to be added to the controller
     */
    void addView(GameView view);

    /**
     * Notifies the Game that the player has chosen to play a card.
     *
     * @param card the card played by the human player
     */
    void humanPlayedCard(Card card);

    /**
     * Notifies the Game that the player has chosen to pass their turn.
     */
    void humanDrewCard();
}
