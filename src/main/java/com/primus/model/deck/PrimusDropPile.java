package com.primus.model.deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the DropPile interface representing the discard pile in the Primus game.
 */
public final class PrimusDropPile implements DropPile {

    private final List<Card> pile;

    /**
     * Constructs an empty PrimusDropPile.
     */
    public PrimusDropPile() {
        this.pile = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCard(final Card card) {
        Objects.requireNonNull(card, "Cannot add a null card to the discard pile");
        this.pile.add(card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Card peek() {
        if (this.pile.isEmpty()) {
            throw new IllegalStateException("Discard pile is empty. No top card");
        }
        //IMPORTANTE: Questo metodo è sostituibile con il ...getLast che fa la medesima cosa ma funziona solo da Java 21
        return this.pile.get(this.pile.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> extractAllExceptTop() {
        if (this.pile.isEmpty()) {
            return new ArrayList<>();
        }

        final int size = this.pile.size();

        if (size == 1) {
            return new ArrayList<>();
        }

        final Card topCard = this.pile.get(size - 1);

        final List<Card> cardsToRecycle = new ArrayList<>(this.pile.subList(0, size - 1));

        this.pile.clear();

        this.pile.add(topCard);

        return cardsToRecycle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return this.pile.isEmpty();
    }

    /**
     * Returns a string representation of the PrimusDropPile.
     *
     * @return a string representing the PrimusDropPile status
     */
    @Override
    public String toString() {
        return "PrimusDropPile{"
                + "size=" + pile.size()
                + " top=" + (pile.isEmpty() ? "None" : peek())
                + '}';
    }
}
