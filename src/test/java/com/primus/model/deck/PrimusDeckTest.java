package com.primus.model.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Deck, Card, and DropPile logic.
 * Includes tests for the new Data-Driven features (Effects & Draw Power).
 */
class PrimusDeckTest {

    private static final int STANDARD_DECK_SIZE = 108;
    private static final int REFILLED_DECK_SIZE = 9;
    private static final int CHAOS_DRAW_AMOUNT = 10;

    // Constants for standard tests
    private static final int STANDARD_DRAW_TWO = 2;
    private static final int STANDARD_DRAW_FOUR = 4;

    // Constants for Event tests
    private static final int BOOSTED_DRAW_TWO = 4;
    private static final int BOOSTED_DRAW_FOUR = 8;

    private static final int DROP_PILE_REFILL_SIZE = 10;

    private PrimusDeck deck;
    private PrimusDropPile dropPile;

    @BeforeEach
    void setUp() {
        deck = new PrimusDeck();
        deck.init();
        dropPile = new PrimusDropPile();

        deck.init();
    }

    // --- Base Tests ---

    @Test
    @DisplayName("Deck should initialize with 108 cards")
    void testDeckInitialization() {
        assertNotNull(deck, "Deck should not be null");
        assertEquals(STANDARD_DECK_SIZE, deck.size(), "A standard UNO deck must have 108 cards");
        assertFalse(deck.isEmpty(), "Newly created deck should not be empty");
    }

    @Test
    @DisplayName("Drawing a card should reduce deck size")
    void testDrawCard() {
        final int initialSize = deck.size();
        final Card card = deck.drawCard();

        assertNotNull(card, "Drawn card should not be null");
        assertEquals(initialSize - 1, deck.size(), "Deck size should decrease by 1");
    }

    @Test
    @DisplayName("Empty Deck: drawing should throw exception")
    void testDrawFromEmptyDeck() {
        // Drain the deck
        while (!deck.isEmpty()) {
            deck.drawCard();
        }
        assertThrows(IllegalStateException.class, deck::drawCard);
    }

    @Test
    @DisplayName("DropPile: extractAllExceptTop should leave the last card on pile")
    void testDropPileExtraction() {
        final Card c1 = new PrimusCard(Color.RED, Values.FIVE);
        final Card c2 = new PrimusCard(Color.BLUE, Values.TWO);
        final Card topCard = new PrimusCard(Color.GREEN, Values.SKIP);

        dropPile.addCard(c1);
        dropPile.addCard(c2);
        dropPile.addCard(topCard);

        final List<Card> recycled = dropPile.extractAllExceptTop();

        assertEquals(2, recycled.size(), "Should return 2 cards (3 total - 1 top)");
        assertTrue(recycled.contains(c1));
        assertTrue(recycled.contains(c2));
        assertFalse(recycled.contains(topCard), "Recycled list must NOT contain the top card");

        assertEquals(topCard, dropPile.peek());
    }

    @Test
    @DisplayName("Integration: Deck refills correctly from DropPile")
    void testRefillFromDropPile() {
        while (!deck.isEmpty()) {
            deck.drawCard();
        }

        for (int i = 0; i < DROP_PILE_REFILL_SIZE; i++) {
            dropPile.addCard(new PrimusCard(Color.YELLOW, Values.ONE));
        }

        deck.refillFrom(dropPile);

        assertEquals(REFILLED_DECK_SIZE, deck.size(), "Deck should contain recycled cards (10 - 1)");
        assertFalse(deck.isEmpty());
    }

    // --- Events Tests ---

    @Test
    @DisplayName("STANDARD: Draw Two cards must have DrawAmount = 2")
    void testStandardDrawTwoProperties() {
        final Card drawTwo = findCardByValue(Values.DRAW_TWO);

        assertNotNull(drawTwo, "Deck must contain at least one Draw Two");
        assertEquals(STANDARD_DRAW_TWO, drawTwo.getDrawAmount(), "Standard Draw Two must draw 2 cards");
    }

    @Test
    @DisplayName("DOUBLE_TROUBLE: Draw cards must have double power")
    void testDoubleTroubleEvent() {
        // 1. Change Event and Reload
        deck.setGameEvent(GameEvent.DOUBLE_TROUBLE);
        deck.init();

        // 2. Find a +2
        final Card boostedDrawTwo = findCardByValue(Values.DRAW_TWO);
        assertNotNull(boostedDrawTwo, "Deck must contain Draw Two cards");

        // 3. Verify power is doubled
        assertEquals(BOOSTED_DRAW_TWO, boostedDrawTwo.getDrawAmount(),
                "In Double Trouble, Draw Two must draw 4 cards");

        // 4. Verify Wild +4 -> +8
        deck.setGameEvent(GameEvent.DOUBLE_TROUBLE); // Reset needed because findCard drains the deck
        deck.init();
        final Card boostedWildFour = findCardByValue(Values.WILD_DRAW_FOUR);
        assertEquals(BOOSTED_DRAW_FOUR, boostedWildFour.getDrawAmount(),
                "In Double Trouble, Wild Draw Four must draw 8 cards");
    }

    @Test
    @DisplayName("REVERSE_ZERO: Zero cards must reverse the turn")
    void testReverseZeroEvent() {
        deck.setGameEvent(GameEvent.REVERSE_ZERO);
        deck.init();

        final Card zeroCard = findCardByValue(Values.ZERO);
        assertNotNull(zeroCard, "Deck must contain Zero cards");

        // Zero must now behave like a Reverse
        assertTrue(zeroCard.hasEffect(CardEffect.REVERSE_TURN),
                "In Reverse Zero, Zero card must have REVERSE_TURN effect");
    }

    @Test
    @DisplayName("BLOCK_SEVEN: Seven cards must skip the turn")
    void testBlockSevenEvent() {
        deck.setGameEvent(GameEvent.BLOCK_SEVEN);
        deck.init();

        final Card sevenCard = findCardByValue(Values.SEVEN);
        assertNotNull(sevenCard, "Deck must contain Seven cards");

        // Seven must now behave like a Skip
        assertTrue(sevenCard.hasEffect(CardEffect.SKIP_NEXT),
                "In Block Seven, Seven card must have SKIP_NEXT effect");
    }

    @Test
    @DisplayName("TOTAL_CHAOS: Must have all special rules active")
    void testTotalChaosEvent() {
        deck.setGameEvent(GameEvent.TOTAL_CHAOS);
        deck.init();

        // Verify Rule 1: Seven Skips
        final Card seven = findCardByValue(Values.SEVEN);
        assertTrue(seven.hasEffect(CardEffect.SKIP_NEXT), "In Chaos, 7 must skip");

        // Reload to find another card
        deck.init();

        // Verify Rule 2: Zero Reverses
        final Card zero = findCardByValue(Values.ZERO);
        assertTrue(zero.hasEffect(CardEffect.REVERSE_TURN), "In Chaos, 0 must reverse");

        // Reload
        deck.init();

        // Verify Rule 3: Double Draws
        final Card drawTwo = findCardByValue(Values.DRAW_TWO);
        assertEquals(BOOSTED_DRAW_TWO, drawTwo.getDrawAmount(), "In Chaos, Draw Two must draw 4 cards");
    }

    // --- Helper & Utility Tests ---

    @Test
    @DisplayName("Manual Creation: Can create a custom 'Chaos' Event Card")
    void testCustomChaosCardCreation() {
        final Set<CardEffect> chaosEffects = EnumSet.of(CardEffect.REVERSE_TURN, CardEffect.ALWAYS_PLAYABLE);
        final Card chaosCard = new PrimusCard(Color.RED, Values.SEVEN, CHAOS_DRAW_AMOUNT, chaosEffects);

        assertEquals(CHAOS_DRAW_AMOUNT, chaosCard.getDrawAmount(), "Chaos card must draw 10");
        assertTrue(chaosCard.hasEffect(CardEffect.REVERSE_TURN));
        assertTrue(chaosCard.hasEffect(CardEffect.ALWAYS_PLAYABLE));
    }

    @Test
    @DisplayName("Immutability: withColor must copy properties")
    void testWitherMaintainsDataDrivenProperties() {
        final Set<CardEffect> effects = EnumSet.of(CardEffect.SKIP_NEXT);
        final Card original = new PrimusCard(Color.BLACK, Values.WILD_DRAW_FOUR, STANDARD_DRAW_FOUR, effects);

        final Card blueCopy = original.withColor(Color.BLUE);

        assertNotSame(original, blueCopy);
        assertEquals(Color.BLUE, blueCopy.getColor());
        assertEquals(STANDARD_DRAW_FOUR, blueCopy.getDrawAmount());
        assertTrue(blueCopy.hasEffect(CardEffect.SKIP_NEXT));
    }

    /**
     * Helper method to find a card in the current deck.
     * WARNING: This method drains the deck! Call deck.init() again if you need to reuse the deck.
     *
     * @param val the value of the card to find
     * @return the first card found with the specified value, or null if not found
     */
    private Card findCardByValue(final Values val) {
        while (!deck.isEmpty()) {
            final Card c = deck.drawCard();
            if (c.getValue() == val) {
                return c;
            }
        }
        return null; // Not found
    }
}
