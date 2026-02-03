package com.primus.model.player.bot;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;

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

    @Override
    public Color chooseColor(final List<Card> hand) {
        Objects.requireNonNull(hand);
        return Color.values()[random.nextInt(0, Color.values().length)];
    }
}
