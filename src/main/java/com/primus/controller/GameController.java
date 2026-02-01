package com.primus.controller;

import com.primus.model.deck.Card;

/**
 * Game controller interface, manages the game loop and acts as a bridge between view and model.
 */
public interface GameController {
    /**
     * Starts the game loop in a separate thread.
     */
    void start();

    /**
     * Stops the game loop.
     */
    void stop();

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
