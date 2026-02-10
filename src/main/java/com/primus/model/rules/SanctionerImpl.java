package com.primus.model.rules;

import com.primus.model.deck.Card;

import java.util.Objects;

/**
 * Standard implementation of the {@link Sanctioner} interface.
 * This class maintains a simple integer counter to track accumulated penalties.
 */
public final class SanctionerImpl implements Sanctioner {

    private static final int DRAW_TWO_PENALTY = 2;
    private static final int DRAW_FOUR_PENALTY = 4;

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
     * * @implNote This implementation specifically checks for {@code DRAW_TWO} and
     * {@code WILD_DRAW_FOUR} values. Other card types are ignored.
     *
     * @throws NullPointerException if the card is null.
     */
    @Override
    public void accumulate(final Card card) {
        Objects.requireNonNull(card);
        switch (card.getValue()) {
            case DRAW_TWO -> malusAmount += DRAW_TWO_PENALTY;
            case WILD_DRAW_FOUR -> malusAmount += DRAW_FOUR_PENALTY;
            default -> {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        this.malusAmount = 0;
    }

    @Override
    public String toString() {
        return "SanctionerImpl{pendingMalus=" + malusAmount + "}";
    }
}
