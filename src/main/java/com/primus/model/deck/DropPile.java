package com.primus.model.deck;

import java.util.List;

/**
 * DropPile interface representing the pile where played cards are placed.
 */
public interface DropPile {

    /**
     * Adds a card to the top of the drop pile.
     *
     * @param card the card to add
     */
    void addCard(Card card);

    /**
     * Retrieves the top card of the drop pile without removing it.
     *
     * @return the top card
     */
    Card peek();

    /**
     * Removes all cards from the drop pile except the top one
     * and returns them for shuffling back into the deck.
     *
     * @return a list of cards to be shuffled back into the deck
     */
    List<Card> extractAllExceptTop();

    /**
     * Checks if the drop pile is empty.
     *
     * @return true if the drop pile is empty, false otherwise
     */
    boolean isEmpty();
}
