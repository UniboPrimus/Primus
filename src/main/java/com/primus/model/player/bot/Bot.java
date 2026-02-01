package com.primus.model.player.bot;

import com.primus.model.deck.Card;
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
    private final BotStrategy strategy;

    /**
     * Constructs a new Bot instance with a unique identifier and the relate strategy.
     *
     * @param id       the unique identifier for this bot
     * @param strategy the algorithm used fot this bot
     */
    public Bot(final int id, final BotStrategy strategy) {
        this.id = id;
        this.strategy = strategy;
    }

    @Override
    public Optional<Card> playCard() {
        return strategy.chooseCard(calculatePossibleMoves());
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
    public boolean passTurn() {
        // ask the strategy want to pass or not
        return strategy.chooseCard(calculatePossibleMoves()).isEmpty();
    }

    private List<Card> calculatePossibleMoves() {
        return hand.stream()
                .filter(card -> !rejectedCards.contains(card))
                .toList();
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
                + ", strategy=" + strategy
                + '}';
    }
}
