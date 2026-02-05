package com.primus.model.player.bot.strategy.color;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A strategy implementation for choosing a color based on frequency.
 * This strategy analyzes the bot's hand and selects the color that appears
 * the most times.
 */
public final class MostFrequentColorStrategy implements ColorStrategy {
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
        // E.g. all card are black means the color is not important
        if (chooseColor.isEmpty()) {
            return Color.RED;
        }
        return chooseColor.get().getKey();
    }
}
