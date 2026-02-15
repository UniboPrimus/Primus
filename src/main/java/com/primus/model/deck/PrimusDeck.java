package com.primus.model.deck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimusDeck.class);
    private String configFileName;
    private final List<Card> cards;
    private boolean isInitialized;

    /**
     * Constructs a PrimusDeck with the default configuration file.
     */
    public PrimusDeck() {
        this.configFileName = GameEvent.STANDARD.getFileName();
        this.cards = new ArrayList<>();
    }

    @Override
    public void init() {
        LOGGER.info("Initializing PrimusDeck...");
        isInitialized = true;

        this.cards.clear();
        try {
            final DeckFileReader loader = new DeckFileReader();
            final List<Card> loadedCards = loader.loadDeck(this.configFileName);

            if (loadedCards.isEmpty()) {
                LOGGER.error("Deck file parsed but no cards were loaded.");
                throw new IllegalStateException("Loaded deck is empty.");
            }
            this.cards.addAll(loadedCards);
            LOGGER.info("Deck initialized successfully. Total cards loaded: {}", this.cards.size());
            shuffle();
        } catch (final IOException e) {
            LOGGER.error("Failed to initialize deck from file: {}", this.configFileName, e);
            throw new IllegalStateException("Failed to initialize deck", e);
        }
    }

    /**
     * Sets the current game event and updates the deck configuration accordingly.
     *
     * @param event the GameEvent to set for this deck
     */
    public void setGameEvent(final GameEvent event) {
        Objects.requireNonNull(event, "GameEvent cannot be null");
        this.configFileName = event.getFileName();
        LOGGER.info("Deck configuration set to event: {} (file: {})", event.getDescription(), this.configFileName);
    }

    @Override
    public void shuffle() {
        ensureInitialized();
        LOGGER.debug("Shuffling the deck containing {} cards.", this.cards.size());
        Collections.shuffle(this.cards);
    }

    @Override
    public boolean isEmpty() {
        ensureInitialized();
        return this.cards.isEmpty();
    }

    @Override
    public Card drawCard() {
        ensureInitialized();
        if (this.cards.isEmpty()) {
            LOGGER.warn("Attempted to draw a card from an empty deck.");
            throw new IllegalStateException("Deck is empty, call the refillFrom() method before drawing");
        }

        //IMPORTANT: This is replaceable with the ...removeLast that does the same thing but works only from Java 21
        final Card drawnCard = this.cards.remove(this.cards.size() - 1);
        LOGGER.debug("Drawing card: {}", drawnCard);

        return drawnCard;
    }

    @Override
    public Card drawStartCard() {
        ensureInitialized();
        LOGGER.debug("Searching for a safe starting card in the deck...");
        if (this.cards.isEmpty()) {
            LOGGER.error("Deck is empty when attempting to draw a starting card.");
            throw new IllegalStateException("Deck is empty, call the refillFrom() method before drawing");
        }
        for (int i = 0; i < cards.size(); i++) {
            final Card candidate = cards.get(i);

            if (isSafeStartCard(candidate)) {
                LOGGER.info("Found safe starting card: {}", candidate);
                return cards.remove(i);
            }
        }
        final Card forced = cards.remove(0);
        LOGGER.warn("No safe starting card found. Forcing draw of: {}", forced);
        return forced;
    }

    private boolean isSafeStartCard(final Card card) {
        if (card.isNativeBlack()) {
            return false;
        }
        if (card.getDrawAmount() > 0) {
            return false;
        }
        final boolean isActionValue = card.getValue() == Values.SKIP || card.getValue() == Values.REVERSE;
        final boolean hasActionEffect = card.hasEffect(CardEffect.SKIP_NEXT) || card.hasEffect(CardEffect.REVERSE_TURN);

        return !isActionValue && !hasActionEffect;
    }

    @Override
    public void refillFrom(final DropPile discardPile) {
        ensureInitialized();
        Objects.requireNonNull(discardPile, "DropPile cannot be null");
        LOGGER.info("Deck is empty. Refilling from discard pile...");
        final List<Card> recycledCards = discardPile.extractAllExceptTop();

        if (recycledCards.isEmpty()) {
            LOGGER.warn("Refill failed: Discard pile has no cards to recycle.");
            return;
        }

        this.cards.addAll(recycledCards);
        LOGGER.info("Refill successful. {} cards added to the deck.", recycledCards.size());
        shuffle();
    }

    /**
     * Returns the current size of the deck.
     *
     * @return the number of cards in the deck
     */
    public int size() {
        ensureInitialized();
        return this.cards.size();
    }

    /**
     * Ensures that the deck has been initialized before performing operations.
     *
     * @throws IllegalStateException if the deck is not initialized
     */
    private void ensureInitialized() {
        if (!isInitialized) {
            LOGGER.error("Attempted to perform operation on uninitialized PrimusDeck");
            throw new IllegalStateException("PrimusDeck must be initialized before performing this operation. "
                    + "Call init() first.");
        }
    }
}
