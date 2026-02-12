package com.primus.model.rules;

import com.primus.model.deck.Card;
import com.primus.model.deck.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Standard implementation of the {@link Validator} interface.
 */
public final class ValidatorImpl implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorImpl.class);

    /**
     * Creates a new instance of the ValidatorImpl.
     */
    public ValidatorImpl() {
        // Default constructor intentionally empty
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean isValidCard(final Card topCard, final Card toValidate) {
        Objects.requireNonNull(toValidate, "Card to validate cannot be null");
        Objects.requireNonNull(topCard, "Top card cannot be null");
        LOGGER.debug("Checking validity for card: {} on top of: {}", toValidate, topCard);
        return toValidate.isNativeBlack()
                || toValidate.getColor() == topCard.getColor()
                || toValidate.getValue() == topCard.getValue();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean isValidDefense(final Card topCard, final Card toValidate) {
        Objects.requireNonNull(toValidate, "Defense card cannot be null");
        Objects.requireNonNull(topCard, "Attack card cannot be null");
        LOGGER.debug("Checking defense for card: {} on top of: {}", toValidate, topCard);
        return topCard.getValue() == Values.WILD_DRAW_FOUR && toValidate.getValue() == Values.WILD_DRAW_FOUR
                || topCard.getValue() == Values.DRAW_TWO && toValidate.getValue() == Values.DRAW_TWO;
    }
}
