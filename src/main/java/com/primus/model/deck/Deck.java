package com.primus.model.deck;

/**
 * Deck interface representing a deck of cards in the game.
 */
public interface Deck {

    /**
     * Initializes the deck (loads cards from file and shuffles it).
     */
    void init();

    /**
     * Shuffles the deck of cards.
     */
    void shuffle();

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Draws a card from the top of the deck.
     *
     * @return the drawn card
     * @throws IllegalStateException if the deck is empty
     */
    Card drawCard();

    /**
     * Refills the deck from the given drop pile.
     * When the deck is empty but the game is still ongoing
     *
     * @param discardPile the drop pile to refill from
     */
    void refillFrom(DropPile discardPile);
}
