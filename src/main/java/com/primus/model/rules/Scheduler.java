package com.primus.model.rules;

import java.util.List;

/**
 * Scheduler interface to manage player turns.
 */
public interface Scheduler {
    /**
     * @return the next player ID by advancing the turn order.
     */
    int nextPlayer();

    /**
     * Reverses the turn order direction.
     */
    void reverseDirection();

    /**
     * Skips the next player's turn.
     */
    void skipTurn();

    /**
     * @return the current player ID without advancing the turn order
     */
    int getCurrentPlayer();

    /**
     * Returns the disposition of players at the table as a list of player IDs. The order of the list represents the
     * seating arrangement around the table, this can be useful for visualization purposes in the
     * view layer, allowing the game to display players in their correct positions relative to each other.
     *
     * @return a list of player IDs in the order they are placed on the table
     */
    List<Integer> getPlayersDisposition();
}
