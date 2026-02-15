package com.primus.model.deck;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the DropPile interface representing the discard pile in the Primus game.
 */
public final class PrimusDropPile implements DropPile {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimusDropPile.class);
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
        if (card == null) {
            LOGGER.warn("Attempted to add a null card to the discard pile");
            return;
        }
        this.pile.add(card);
        LOGGER.debug("Card added to DropPile: {}. Total stack size: {}", card, this.pile.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Card peek() {
        if (this.pile.isEmpty()) {
            LOGGER.warn("Peek requested on an empty discard pile");
            throw new IllegalStateException("Discard pile is empty. No top card");
        }
        //IMPORTANT: This is replaceable with the ...getLast that does the same thing but works only from Java 21
        return this.pile.get(this.pile.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> extractAllExceptTop() {
        if (this.pile.isEmpty()) {
            LOGGER.debug("Recycle requested on an empty discard pile. Returning empty list.");
            return new ArrayList<>();
        }

        final int size = this.pile.size();

        if (size == 1) {
            LOGGER.debug("Recycle requested but only one card in discard pile. Keeping top card and returning empty list.");
            return new ArrayList<>();
        }

        final Card topCard = this.pile.get(size - 1);
        final List<Card> cardsToRecycle = new ArrayList<>(this.pile.subList(0, size - 1));

        this.pile.clear();
        this.pile.add(topCard);

        LOGGER.info("Recycling {} cards from discard pile to deck. Top card {} remains.", cardsToRecycle.size(), topCard);

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
