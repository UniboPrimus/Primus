package com.primus.model.rules;

import com.primus.model.deck.Card;

/**
 * Defines the contract for validating card moves.
 * The Validator distinguishes between standard moves (Peace Mode) and
 * defensive moves during a penalty chain (Stacking).
 */
public interface Validator {

    /**
     * Validates if a card can be played on top of another in a standard game context.
     * Based on standard rules: match by color, match by value, or wild cards.
     *
     * @param topCard    the card currently on top of the discard pile.
     * @param toValidate the card the player intends to play.
     * @return true if the move is valid according to standard matching rules.
     */
    boolean isValidCard(Card topCard, Card toValidate);

    /**
     * Validates if a card can be played as a defense/response to an active penalty.
     * This is used when {@link Sanctioner#isActive()} returns true.
     *
     * @param topCard    the penalty card currently on top (the source of the attack).
     * @param toValidate the card the player intends to use as defense.
     * @return true if the card is a valid defense (stackable).
     */
    boolean isValidDefense(Card topCard, Card toValidate);
}
