package com.primus.model.rules;

import com.primus.model.deck.Color;
import com.primus.model.deck.PrimusCard;
import com.primus.model.deck.Values;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SanctionerTest {
    private Sanctioner sanctioner;

    @BeforeEach
    void setUp() {
        sanctioner = new SanctionerImpl();
    }

    @Test
    void testIsActiveInitiallyFalse() {
        assertFalse(sanctioner.isActive(), "Sanctioner should not be active initially.");
    }

    @Test
    void testAccumulateDrawTwo() {
        sanctioner.accumulate(new PrimusCard(Color.RED, Values.DRAW_TWO, 2, Collections.emptySet()));
        assertTrue(sanctioner.isActive(), "Sanctioner should be active after accumulating a penalty.");
        final int result = 2;
        assertEquals(result, sanctioner.getMalusAmount(), "Malus amount should be 2 after accumulating a DRAW_TWO card.");
    }

    @Test
    void testAccumulateWildDrawFour() {
        sanctioner.accumulate(new PrimusCard(Color.BLACK, Values.WILD_DRAW_FOUR, 4, Collections.emptySet()));
        assertTrue(sanctioner.isActive(), "Sanctioner should be active after accumulating a penalty.");
        final int result = 4;
        assertEquals(result, sanctioner.getMalusAmount(), "Malus amount should be 4 after accumulating a WILD_DRAW_FOUR card.");
    }

    @Test
    void testAccumulateMultipleCards() {
        sanctioner.accumulate(new PrimusCard(Color.RED, Values.DRAW_TWO, 2, Collections.emptySet()));
        sanctioner.accumulate(new PrimusCard(Color.BLACK, Values.WILD_DRAW_FOUR, 4, Collections.emptySet()));
        assertTrue(sanctioner.isActive(), "Sanctioner should be active after accumulating multiple penalties.");
        final int result = 6;
        assertEquals(result, sanctioner.getMalusAmount(), "Malus amount should be the sum of all accumulated penalties.");
    }

    @Test
    void testReset() {
        sanctioner.accumulate(new PrimusCard(Color.RED, Values.DRAW_TWO, 2, Collections.emptySet()));
        sanctioner.reset();
        assertFalse(sanctioner.isActive(), "Sanctioner should not be active after reset.");
        final int result = 0;
        assertEquals(result, sanctioner.getMalusAmount(), "Malus amount should be 0 after reset.");
    }

    @Test
    void testGetMalusAmountInitiallyZero() {
        final int result = 0;
        assertEquals(result, sanctioner.getMalusAmount(), "Malus amount should be 0 initially.");
    }
}
