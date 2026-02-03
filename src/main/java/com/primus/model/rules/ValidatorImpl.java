package com.primus.model.rules;

import com.primus.model.deck.Card;
import com.primus.model.deck.Values;

import java.util.Objects;

/**
 * Concrete implementation of the game rules for validation.
 */
public final class ValidatorImpl implements Validator {

    @Override
    public boolean isValidCard(final Card topCard, final Card toValidate) {
        Objects.requireNonNull(toValidate);
        Objects.requireNonNull(topCard);
        return toValidate.isBlack()
                || toValidate.getColor() == topCard.getColor()
                || toValidate.getValue() == topCard.getValue();
    }

    @Override
    public boolean isValidDefense(final Card topCard, final Card toValidate) {
        Objects.requireNonNull(toValidate);
        Objects.requireNonNull(topCard);
        return topCard.getValue() == Values.WILD_DRAW_FOUR && toValidate.getValue() == Values.WILD_DRAW_FOUR
                || topCard.getValue() == Values.DRAW_TWO && toValidate.getValue() == Values.DRAW_TWO;
    }
}
