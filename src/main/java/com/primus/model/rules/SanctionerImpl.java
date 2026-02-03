package com.primus.model.rules;

import com.primus.model.deck.Card;

import java.util.Objects;

/**
 * Standard implementation of the {@link Sanctioner} interface.
 * Maintains a simple integer counter for the accumulated penalties.
 */
public final class SanctionerImpl implements Sanctioner {
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
    public void accumulate(final Card c) {
        Objects.requireNonNull(c);
        switch (c.getValue()) {
            //todo refactoring senza numeri magici
            case DRAW_TWO -> malusAmount += 2;
            case WILD_DRAW_FOUR -> malusAmount += 4;
            default -> {
            }
        }
    }

    @Override
    public void reset() {
        this.malusAmount = 0;
    }
}
