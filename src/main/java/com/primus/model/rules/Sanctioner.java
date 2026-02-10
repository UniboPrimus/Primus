package com.primus.model.rules;

import com.primus.model.deck.Card;

/**
 * Manages the accumulation of penalty cards (malus) during the game session.
 * The Sanctioner acts as a state buffer for aggressive cards (e.g., Draw Two, Wild Draw Four),
 * tracking the cumulative number of cards a player is required to draw if they cannot
 * provide a valid defense.
 */
public interface Sanctioner {

    /**
     * Checks if there is currently an active penalty chain.
     * A return value of {@code true} indicates that the game is in a "stacking phase",
     * requiring the current player to either defend or draw cards.
     *
     * @return {@code true} if there is a positive number of accumulated cards to draw;
     *      {@code false} otherwise.
     */
    boolean isActive();

    /**
     * Retrieves the current total number of cards pending to be drawn.
     *
     * @return the total amount of malus cards accumulated (e.g., 2, 4, 6...).
     */
    int getMalusAmount();

    /**
     * Accumulates a penalty based on the provided card's value.
     * This method is typically invoked when a player defends against a malus
     * or initiates a new attack chain.
     *
     * @param card the card containing the penalty value to add.
     *          Must not be {@code null}.
     * @throws NullPointerException if {@code c} is null.
     */
    void accumulate(Card card);

    /**
     * Resets the penalty counter to zero.
     * This operation should be performed immediately after the penalty has been
     * applied (i.e., the player has drawn the cards) or when the chain is
     * successfully resolved by other means.
     */
    void reset();
}
