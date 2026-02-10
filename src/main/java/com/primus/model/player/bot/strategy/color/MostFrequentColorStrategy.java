package com.primus.model.player.bot.strategy.color;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link ColorStrategy} implementation for choosing a color based on frequency.
 * This strategy analyzes the bot's hand and selects the color that appears
 * the most times.
 */
public final class MostFrequentColorStrategy implements ColorStrategy {
    /**
     * Creates a new instance of the MostFrequentColorStrategy.
     */
    public MostFrequentColorStrategy() {
        // Default constructor intentionally empty
    }

    /**
     * {@inheritDoc}
     * Implementation logic:
     * 1. Filters out Wilds cards.
     * 2. Counts occurrences of each color.
     * 3. Selects the color with the highest count.
     * 4. Defaults to {@link Color#RED} if the hand contains only Black cards.
     *
     * @throws NullPointerException     if the hand list is null.
     * @throws IllegalArgumentException if the hand list is empty.
     */
    @Override
    public Color chooseColor(final List<Card> hand) {
        Objects.requireNonNull(hand);
        if (hand.isEmpty()) {
            throw new IllegalArgumentException("Hand can't be empty");
        }
        final Map<Color, Integer> map = new EnumMap<>(Color.class);
        for (final Card card : hand) {
            if (card.getColor() != Color.BLACK) {
                map.put(card.getColor(), map.getOrDefault(card.getColor(), 0) + 1);
            }
        }
        final var chooseColor = map.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
        // E.g. all cards are black means the color is not important
        if (chooseColor.isEmpty()) {
            return Color.RED;
        }
        return chooseColor.get().getKey();
    }
}
