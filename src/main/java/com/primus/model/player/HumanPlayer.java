package com.primus.model.player;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a human player in the game. This class implements the {@link Player} interface but does not
 * implement the {@code playCard} method, as human input should be handled through
 * the {@link com.primus.view.GameView} layer. The {@link HumanPlayer}.
 * class manages the player's hand and provides methods to add cards and get the player's ID. It
 * also includes a method to notify the player of the result of their move, allowing the hand to be updated
 * accordingly.
 */
public final class HumanPlayer implements Player {
    private static final Logger LOGGER = LoggerFactory.getLogger(HumanPlayer.class);

    private final List<Card> hand = new ArrayList<>();
    private final int id;
    private final String name;

    /**
     * Constructor for HumanPlayer.
     *
     * @param id the unique identifier for the player
     * @param name the name of the player
     */
    public HumanPlayer(final int id, final String name) {
        Objects.requireNonNull(name);
        this.id = id;
        this.name = name;
        LOGGER.debug("HumanPlayer created with ID: {}", id);
    }

    @Override
    public Optional<Card> playCard() {
        LOGGER.error("Unexpected call to playCard() on HumanPlayer (ID: {}). Human input must come from the View!", id);
        throw new UnsupportedOperationException("HumanPlayer does not implement playCard."
                + "Human input should be handled through the view.");
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public List<Card> getHand() {
        return List.copyOf(hand);
    }

    @Override
    public void addCards(final List<Card> cards) {
        Objects.requireNonNull(cards);
        LOGGER.debug("HumanPlayer (ID: {}) received {} cards: {}", id, cards.size(), cards);
        hand.addAll(cards);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void notifyMoveResult(final Card cardPlayed, final boolean valid) {
        Objects.requireNonNull(cardPlayed);
        final Card cardInHand;
        if (cardPlayed.isNativeBlack()) {
            cardInHand = cardPlayed.withColor(Color.BLACK);
        } else {
            cardInHand = cardPlayed;
        }
        if (valid) {
            if (hand.remove(cardInHand)) {
                LOGGER.info("HumanPlayer (ID: {}) successfully played card: {}", id, cardPlayed);
            } else {
                LOGGER.error("HumanPlayer (ID: {}) tried to play {} but it was NOT in hand! Hand: {}", id, cardPlayed, hand);
                throw new IllegalStateException("The card validated is not present in the hand: " + cardPlayed);
            }
        } else {
            LOGGER.warn("HumanPlayer (ID: {}) move rejected. Card: {}", id, cardPlayed);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HumanPlayer human = (HumanPlayer) o;
        return id == human.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Bot{"
                + "id=" + id
                + ", hand=" + hand
                + '}';
    }
}
