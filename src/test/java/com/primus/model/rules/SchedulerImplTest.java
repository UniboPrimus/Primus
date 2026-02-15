package com.primus.model.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchedulerImplTest {

    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        // Using LinkedHashSet to maintain insertion order for predictable testing.
        final Set<Integer> players = new LinkedHashSet<>();
        players.add(1);
        players.add(2);
        players.add(3);

        scheduler = new SchedulerImpl(players);
    }

    @Test
    @DisplayName("Correct turn sequence")
    void testNextPlayerClockwise() {
        assertEquals(1, scheduler.getCurrentPlayer(), "Initial player should be 1");

        assertEquals(1, scheduler.nextPlayer(), "First turn should belong to 1");

        assertEquals(2, scheduler.nextPlayer(), "Next player should be 2");

        assertEquals(3, scheduler.nextPlayer(), "Next player should be 3");

        assertEquals(1, scheduler.nextPlayer(), "Should loop back to player 1");
    }

    @Test
    @DisplayName("Correct reverse turn sequence")
    void testReverseDirection() {
        scheduler.reverseDirection();

        assertEquals(1, scheduler.nextPlayer(), "First turn should belong to 1");

        assertEquals(3, scheduler.nextPlayer(), "Should continue backwards to player 3");

        assertEquals(2, scheduler.nextPlayer(), "Should loop backwards to player 2");

        assertEquals(1, scheduler.nextPlayer(), "Should loop backwards to player 1");
    }

    @Test
    @DisplayName("Double reverse doesn't break the sequence")
    void testDoubleReverse() {
        scheduler.reverseDirection();
        scheduler.reverseDirection();

        assertEquals(1, scheduler.nextPlayer(), "First turn 1");
        assertEquals(2, scheduler.nextPlayer(), "Should behave normally after double reverse");
    }

    @Test
    @DisplayName("Skip a player turn")
    void testSkipTurn() {
        assertEquals(1, scheduler.nextPlayer());

        scheduler.skipTurn();

        assertEquals(3, scheduler.nextPlayer(), "Should have skipped player 2 and returned 3");
    }

    @Test
    @DisplayName("Skip a player while reverse")
    void testSkipTurnInReverse() {
        scheduler.reverseDirection();

        assertEquals(1, scheduler.nextPlayer());

        scheduler.skipTurn();

        assertEquals(2, scheduler.nextPlayer(), "Should have skipped player 3 and returned 2");
    }

    @Test
    @DisplayName("Error if zero players provided")
    void testInitializationError() {
        final Set<Integer> emptyPlayers = Collections.emptySet();

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> new SchedulerImpl(emptyPlayers));

        assertEquals("Zero players provided to Scheduler", exception.getMessage());
    }
}
