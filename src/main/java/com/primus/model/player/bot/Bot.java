package com.primus.model.player.bot;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.player.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a bot player in the game. The bot implements the {@link Player} interface
 * and provides its own behavior for playing cards, passing turns, and managing its hand.
 */
public final class Bot implements Player {
    private final int id;
    private final List<Card> hand = new ArrayList<>();
    private final Set<Card> rejectedCards = new LinkedHashSet<>();
    private final CardStrategy cardStrategy;
    private final ColorStrategy colorStrategy;

    /**
     * Constructs a new Bot with specific strategies for card selection and color decision.
     *
     * @param id            the unique identifier
     * @param cardStrategy  the logic to select cards
     * @param colorStrategy the logic to select colors for Wild cards
     */
    public Bot(final int id, final CardStrategy cardStrategy, final ColorStrategy colorStrategy) {
        this.id = id;
        this.cardStrategy = Objects.requireNonNull(cardStrategy);
        this.colorStrategy = Objects.requireNonNull(colorStrategy);
    }

    @Override
    public Optional<Card> playCard() {
        // the card strategy pick a card among possible moves
        final Optional<Card> chosenOpt = cardStrategy.chooseCard(calculatePossibleMoves());
        if (chosenOpt.isPresent()) {
            final Card card = chosenOpt.get();
            // if the selected card is a black card decide its new color using color strategy and return it
            if (card.isNativeBlack()) {
                final Color chosenColor = colorStrategy.chooseColor(getHand());
                return Optional.of(card.withColor(chosenColor));
            }
        }
        return chosenOpt;
    }

    @Override
    public boolean passTurn() {
        return cardStrategy.chooseCard(calculatePossibleMoves()).isEmpty();
    }

    private List<Card> calculatePossibleMoves() {
        return hand.stream()
                .filter(card -> !rejectedCards.contains(card))
                .toList();
    }

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    public List<Card> getHand() {
        return List.copyOf(hand);
    }

    @Override
    public void addCards(final List<Card> cards) {
        Objects.requireNonNull(cards);
        hand.addAll(cards);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void notifyMoveResult(final Card cardPlayed, final boolean valid) {
        Objects.requireNonNull(cardPlayed);
        if (!valid) {
            rejectedCards.add(cardPlayed);
        } else {
            if (!hand.contains(cardPlayed)) {
                throw new IllegalStateException("The card validated is not present in the hand");
            }
            hand.remove(cardPlayed);
            rejectedCards.clear();
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

        final Bot bot = (Bot) o;

        return id == bot.id;
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
                + ", rejectedCards=" + rejectedCards
                + ", card strategy=" + cardStrategy
                + ", color strategy=" + colorStrategy
                + '}';
    }
}
