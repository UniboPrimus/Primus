package com.primus.model.player.bot.strategy;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.deck.PrimusCard;
import com.primus.model.deck.Values;
import com.primus.model.player.bot.strategy.color.ColorStrategy;
import com.primus.model.player.bot.strategy.color.MostFrequentColorStrategy;
import com.primus.model.player.bot.strategy.color.RandomColorStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColorStrategyTest {

    private Card card(final Color c) {
        return new PrimusCard(c, Values.ONE);
    }

    @Test
    void testRandomColorStrategyReturnsValidColor() {
        final ColorStrategy strategy = new RandomColorStrategy();
        final List<Card> hand = List.of(card(Color.RED));
        final Color result = strategy.chooseColor(hand);
        assertNotNull(result);
        assertTrue(result != Color.BLACK, "Strategy should not choose BLACK as a declared color");
        assertTrue(List.of(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW).contains(result));
    }

    @Test
    void testMostFrequentStrategyPicksMajority() {
        final ColorStrategy strategy = new MostFrequentColorStrategy();
        final List<Card> hand = List.of(
                card(Color.RED), card(Color.RED), card(Color.RED),
                card(Color.BLUE)
        );
        final Color result = strategy.chooseColor(hand);
        assertEquals(Color.RED, result, "Should pick Red because it appears 3 times");
    }

    @Test
    void testMostFrequentStrategyIgnoresWildCards() {
        final ColorStrategy strategy = new MostFrequentColorStrategy();
        // Hand: 2 Blue and 3 Black
        final List<Card> hand = List.of(
                card(Color.BLUE), card(Color.BLUE),
                new PrimusCard(Color.BLACK, Values.WILD),
                new PrimusCard(Color.BLACK, Values.WILD_DRAW_FOUR),
                new PrimusCard(Color.BLACK, Values.WILD)
        );

        final Color result = strategy.chooseColor(hand);
        assertEquals(Color.BLUE, result, "Should ignore Black cards and pick Blue");
    }

    @Test
    void testMostFrequentStrategyFallback() {
        final ColorStrategy strategy = new MostFrequentColorStrategy();
        final List<Card> hand = List.of(
                new PrimusCard(Color.BLACK, Values.WILD)
        );
        final Color result = strategy.chooseColor(hand);
        assertEquals(Color.RED, result);
    }
}
