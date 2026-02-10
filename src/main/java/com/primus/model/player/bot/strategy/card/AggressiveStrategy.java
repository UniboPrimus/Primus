package com.primus.model.player.bot.strategy.card;

import com.primus.model.deck.Card;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link CardStrategy} strategy implementation for a bot that prioritizes aggressive gameplay.
 * This strategy aims to select the most impactful card to play based on the
 * provided list of possible cards, assigning weights to penalty cards.
 *
 */
public final class AggressiveStrategy implements CardStrategy {

    /**
     * Creates a new instance of the aggressive strategy.
     */
    public AggressiveStrategy() {
        // Default constructor intentionally empty
    }

    /**
     * Chooses the most aggressive card from the list of possible cards.
     * It sorts the playable cards based on a priority score.
     *
     * @param possibleCards the list of cards the bot can choose from.
     * @return an {@link Optional} containing the card deemed most aggressive,
     *       or {@code Optional.empty()} if the list is empty.
     * @throws NullPointerException if {@code possibleCard} is {@code null}.
     */
    @Override
    public Optional<Card> chooseCard(final List<Card> possibleCards) {
        Objects.requireNonNull(possibleCards);
        return possibleCards.stream().max(Comparator.comparingInt(this::calculateScore));
    }

    /**
     * Calculates score of a card.
     *
     * @param c the card to evaluate.
     * @return the integer score associated with the card type.
     */
    private int calculateScore(final Card c) {
        return switch (c.getValue()) {
            case WILD_DRAW_FOUR -> Priority.ULTIMATE.score;
            case DRAW_TWO -> Priority.HIGH.score;
            case WILD -> Priority.MEDIUM.score;
            default -> Priority.LOW.score;
        };
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Enumeration to define strategic weights for card types.
     */
    private enum Priority {
        ULTIMATE(100),
        HIGH(50),
        MEDIUM(20),
        LOW(1);
        private final int score;

        Priority(final int score) {
            this.score = score;
        }
    }
}
