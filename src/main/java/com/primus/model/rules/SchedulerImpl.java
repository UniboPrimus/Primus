package com.primus.model.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * Implementation of the {@link Scheduler} to manage player turns. It supports
 * clockwise and counter-clockwise turn orders, as well as skipping turns.
 */
public final class SchedulerImpl implements Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerImpl.class);

    private final List<Integer> playersIDs;
    private int currentIndex = -1;
    private boolean isClockwise = true;

    /**
     * @param playerIDs players
     */
    public SchedulerImpl(final Set<Integer> playerIDs) {
        Objects.requireNonNull(playerIDs);
        if (playerIDs.isEmpty()) {
            LOGGER.error("Failed to initialize Scheduler: Zero players provided.");
            throw new IllegalArgumentException("Zero players provided to Scheduler");
        }
        this.playersIDs = List.copyOf(playerIDs);
        LOGGER.info("Scheduler initialized with {} players. Order: {}", playersIDs.size(), playersIDs);
    }

    @Override
    public int getCurrentPlayer() {
        // If currentIndex is -1, it means the game has not started yet, so we return the first player
        return currentIndex == -1 ? this.playersIDs.getFirst() : this.playersIDs.get(this.currentIndex);
    }

    @Override
    public List<Integer> getPlayersDisposition() {
        return List.copyOf(this.playersIDs);
    }

    @Override
    public int nextPlayer() {
        moveIndex();
        LOGGER.debug("Turn passed to player ID: {}", playersIDs.get(currentIndex));
        return playersIDs.get(currentIndex);
    }

    @Override
    public void reverseDirection() {
        LOGGER.info("Game direction reversed. Clockwise: {}", isClockwise);
        isClockwise = !isClockwise;
    }

    @Override
    public void skipTurn() {
        LOGGER.info("Turn skipped for player ID: {}", playersIDs.get(currentIndex));
        moveIndex();
    }

    /**
     * Moves the current index based on the turn order direction.
     */
    private void moveIndex() {
        // If currentIndex is -1, it means the game has not started yet, so it set it to 0 to start with the first player
        if (currentIndex == -1) {
            currentIndex = 0;
            return;
        }

        if (isClockwise) {
            currentIndex = (currentIndex + 1) % playersIDs.size();
        } else {
            currentIndex = currentIndex - 1 < 0 ? playersIDs.size() - 1 : currentIndex - 1;
        }
    }
}
