package com.primus.model.core;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of the Scheduler interface to manage player turns. It supports
 * clockwise and counter-clockwise turn orders, as well as skipping turns.
 */
public final class SchedulerImpl implements Scheduler {
    private final List<Integer> playersIDs;
    private int currentIndex = 0;
    private boolean isClockwise = true;

    /**
     * @param playerIDs players
     */
    public SchedulerImpl(final Set<Integer> playerIDs) {
        Objects.requireNonNull(playerIDs);
        if (playerIDs.isEmpty()) {
            throw new IllegalArgumentException("Zero players provided to Scheduler");
        }
        this.playersIDs = List.copyOf(playerIDs);
    }

    @Override
    public int getCurrentPlayer() {
        return this.playersIDs.get(this.currentIndex);
    }

    @Override
    public int nextPlayer() {
        moveIndex();
        return playersIDs.get(currentIndex);
    }

    @Override
    public void reverseDirection() {
        isClockwise = !isClockwise;
    }

    @Override
    public void skipTurn() {
        moveIndex();
    }

    /**
     * Moves the current index based on the turn order direction.
     */
    private void moveIndex() {
        if (isClockwise) {
            currentIndex = (currentIndex + 1) % playersIDs.size();
        } else {
            currentIndex = currentIndex - 1 < 0 ? playersIDs.size() - 1 : currentIndex - 1;
        }
    }
}
