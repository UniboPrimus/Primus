package com.primus.model.player.bot;

import com.primus.model.deck.Card;

import java.util.List;

/**
 * A restricted interface that exposes only public or "spiable" data of a player.
 * It prevents access to mutable methods such as playCard() or addCards() to ensure
 * game integrity during bot decision-making.
 */
public interface OpponentInfo {

    /**
     * Retrieves the unique identifier of the opponent.
     *
     * @return the opponent's ID
     */
    int getId();

    /**
     * Retrieves the current hand of the opponent.
     *
     * @return a list of cards in the opponent's hand
     */
    List<Card> getHand();

    /**
     * Gets the total number of cards currently held by the opponent.
     *
     * @return the count of cards in hand
     */
    int getCardCount();
}
