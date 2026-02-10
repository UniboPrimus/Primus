package com.primus.model.rules;

import com.primus.model.deck.Card;

/**
 * Defines the contract for validating card moves within the game.
 * The Validator distinguishes between two main game contexts:
 * Standard Move: Playing a card on the discard pile.
 * Defensive Move: Responding to an active penalty (Stacking Mode)
 *
 */
public interface Validator {

    /**
     * Validates if a card can be played on top of another in a standard game context.
     * Adheres to standard rules: match by color, match by value, or usage of wild cards.
     *
     * @param topCard    the card currently on top of the discard pile. Must not be null.
     * @param toValidate the card the player intends to play. Must not be null.
     * @return {@code true} if the move is valid according to standard matching rules;
     *      {@code false} otherwise.
     * @throws NullPointerException if either argument is null.
     */
    boolean isValidCard(Card topCard, Card toValidate);

    /**
     * Validates if a card can be played as a defense/response to an active penalty.
     * This method is strictly used when {@link Sanctioner#isActive()} returns {@code true}.
     *
     * @param topCard    the penalty card currently on top (the source of the attack).
     * @param toValidate the card the player intends to use as defense.
     * @return {@code true} if the card is a valid defense (stackable); {@code false} otherwise.
     * @throws NullPointerException if either argument is null.
     */
    boolean isValidDefense(Card topCard, Card toValidate);
}
