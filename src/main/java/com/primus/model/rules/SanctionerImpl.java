package com.primus.model.rules;

import com.primus.model.deck.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Standard implementation of the {@link Sanctioner} interface.
 * This class maintains a simple integer counter to track accumulated penalties.
 */
public final class SanctionerImpl implements Sanctioner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SanctionerImpl.class);

    /**
     * The current counter of cards to be drawn.
     */
    private int malusAmount;

    /**
     * Creates a new instance of the SanctionerImpl.
     */
    public SanctionerImpl() {
        // Default constructor intentionally empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
        return malusAmount > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMalusAmount() {
        return malusAmount;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the card is null.
     */
    @Override
    public void accumulate(final Card card) {
        Objects.requireNonNull(card);
        LOGGER.debug("Accumulating penalty for card: {} with {} to draw", card, card.getDrawAmount());
        malusAmount += card.getDrawAmount();
        LOGGER.info("Penalty updated total cards to draw = {}", malusAmount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        LOGGER.info("Sanctioner reset. Penalty cleared (was {}).", malusAmount);
        this.malusAmount = 0;
    }

    @Override
    public String toString() {
        return "SanctionerImpl{pendingMalus=" + malusAmount + "}";
    }
}
