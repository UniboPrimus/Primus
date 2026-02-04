package com.primus.model.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the Deck interface representing a deck of cards in the Primus game.
 * This class provides methods to initialize, shuffle, draw cards, and refill the deck
 * from a discard pile.
 */
public final class PrimusDeck implements Deck {

    private static final String DEFAULT_CONFIG = "standard_deck.csv";
    private final String configFileName;
    private final List<Card> cards;

    /**
     * Constructs a PrimusDeck with the default configuration file.
     */
    public PrimusDeck() {
        this(DEFAULT_CONFIG);
    }

    /**
     * Constructs a PrimusDeck with the specified configuration file.
     *
     * @param configFileName the name of the configuration file to load the deck from (Events)
     */
    public PrimusDeck(final String configFileName) {
        this.configFileName = Objects.requireNonNull(configFileName, "config file cannot be null");
        this.cards = new ArrayList<>();
        init();
    }

    @Override
    public void init() {
        this.cards.clear();
        final DeckFileReader loader = new DeckFileReader();
        this.cards.addAll(loader.loadDeck(this.configFileName));
        shuffle();
    }

    @Override
    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    @Override
    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    @Override
    public Card drawCard() {
        if (this.cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty, call the refillFrom() method before drawing");
        }
        //IMPORTANTE: Questo metodo è sostituibile con il ...removeLast che fa la medesima cosa ma funziona solo da Java 21
        return this.cards.remove(this.cards.size() - 1);
    }

    @Override
    public void refillFrom(final DropPile discardPile) {
        Objects.requireNonNull(discardPile, "DropPile cannot be null");
        final List<Card> recycledCards = discardPile.extractAllExceptTop();

        if (recycledCards.isEmpty()) {
            return;
        }

        this.cards.addAll(recycledCards);
        shuffle();
    }

    /**
     * Returns the current size of the deck.
     *
     * @return the number of cards in the deck
     */
    public int size() {
        return this.cards.size();
    }
}
