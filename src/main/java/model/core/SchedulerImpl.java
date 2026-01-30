package model.core;

import model.player.Player;
import java.util.List;

/**
 * Implementation of the Scheduler interface to manage player turns. It supports
 * clockwise and counter-clockwise turn orders, as well as skipping turns.
 */
public final class SchedulerImpl implements Scheduler {
    private final List<Player> players;
    private int currentIndex;
    private boolean isClockwise = true;

    /**
     * @param players players
     */
    public SchedulerImpl(final List<Player> players) {
        this.players = List.copyOf(players);
    }

    @Override
    public Player getNextPlayer() {
        moveIndex();
        return players.get(currentIndex);
    }

    @Override
    public Player peekNextPlayer() {
        return players.get(currentIndex);
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
            currentIndex = (currentIndex + 1) % players.size();
        } else {
            currentIndex = currentIndex - 1 < 0 ? players.size() : currentIndex - 1;
        }
    }
}
