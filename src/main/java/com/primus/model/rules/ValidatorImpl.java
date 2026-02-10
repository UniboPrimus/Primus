package com.primus.model.rules;

import com.primus.model.deck.Card;
import com.primus.model.deck.Values;

import java.util.Objects;

/**
 * Standard implementation of the {@link Validator} interface.
 */
public final class ValidatorImpl implements Validator {

    /**
     * Creates a new instance of the ValidatorImpl.
     */
    public ValidatorImpl() {
        // Default constructor intentionally empty
    }

    /**
     * {@inheritDoc}
     * * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean isValidCard(final Card topCard, final Card toValidate) {
        Objects.requireNonNull(toValidate, "Card to validate cannot be null");
        Objects.requireNonNull(topCard, "Top card cannot be null");
        return toValidate.isNativeBlack()
                || toValidate.getColor() == topCard.getColor()
                || toValidate.getValue() == topCard.getValue();
    }

    /**
     * {@inheritDoc}
     * * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean isValidDefense(final Card topCard, final Card toValidate) {
        Objects.requireNonNull(toValidate, "Defense card cannot be null");
        Objects.requireNonNull(topCard, "Attack card cannot be null");
        return topCard.getValue() == Values.WILD_DRAW_FOUR && toValidate.getValue() == Values.WILD_DRAW_FOUR
                || topCard.getValue() == Values.DRAW_TWO && toValidate.getValue() == Values.DRAW_TWO;
    }
}
