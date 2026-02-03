package com.primus.model.rules;

import com.primus.model.deck.Card;

/**
 * Manages the accumulation of penalties (malus) during the game.
 * The Sanctioner acts as a buffer for penalty cards (e.g., Draw Two, Wild Draw Four),
 * tracking the total number of cards a player must draw if they cannot defend themselves.
 */
public interface Sanctioner {

    /**
     * Checks if there is currently an active penalty.
     * This state indicates that the game is in stacking phase.
     *
     * @return true if there are accumulated cards to draw, false otherwise.
     */
    boolean isActive();

    /**
     * Retrieves the current total number of cards to be drawn.
     * Note: This method acts as a pure query and does NOT reset the counter.
     *
     * @return the total amount of malus cards accumulated.
     */
    int getMalusAmount();

    /**
     * Accumulates a penalty based on the played card's value.
     * Usually invoked when a player defends against a malus or initiates a new one.
     *
     * @param c the card containing the penalty value (e.g., DRAW_TWO adds 2, WILD_DRAW_FOUR adds 4).
     */
    void accumulate(Card c);

    /**
     * Resets the penalty counter to zero.
     * This should be called after the penalty has been applied (the player drew the cards)
     * or when the chain is successfully resolved.
     */
    void reset();
}
