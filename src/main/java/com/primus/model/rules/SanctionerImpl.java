package com.primus.model.rules;

import com.primus.model.deck.Card;

import java.util.Objects;

/**
 * Standard implementation of the {@link Sanctioner} interface.
 * Maintains a simple integer counter for the accumulated penalties.
 */
public final class SanctionerImpl implements Sanctioner {
    private static final int DRAW_TWO_PENALTY = 2;
    private static final int DRAW_FOUR_PENALTY = 4;

    private int malusAmount;

    @Override
    public boolean isActive() {
        return malusAmount > 0;
    }

    @Override
    public int getMalusAmount() {
        return malusAmount;
    }

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

    @Override
    public void reset() {
        this.malusAmount = 0;
    }
}
