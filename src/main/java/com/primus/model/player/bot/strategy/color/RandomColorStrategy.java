package com.primus.model.player.bot.strategy.color;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link ColorStrategy} that selects a color uniformly at random
 * from the set of available {@link Color} values.
 * This strategy ignores the provided {@code hand} content and returns a
 * purely random choice.
 */
public final class RandomColorStrategy implements ColorStrategy {
    private final Random random = new Random();

    /**
     * Creates a new instance of the RandomColorStrategy.
     */
    public RandomColorStrategy() {
        // Default constructor intentionally empty
    }

    /**
     * {@inheritDoc}
     * Implementation logic:
     * Selects a random color excluding {@link Color#BLACK}.
     *
     * @throws NullPointerException     if the hand is null.
     * @throws IllegalArgumentException if the hand is empty.
     */
    @Override
    public Color chooseColor(final List<Card> hand) {
        Objects.requireNonNull(hand);
        if (hand.isEmpty()) {
            throw new IllegalArgumentException("Hand can't be empty");
        }
        final List<Color> validColors = Arrays.stream(Color.values())
                .filter(c -> c != Color.BLACK)
                .toList();
        return validColors.get(random.nextInt(validColors.size()));
    }
}
