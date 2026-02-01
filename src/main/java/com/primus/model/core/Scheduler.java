package com.primus.model.core;

import com.primus.model.player.Player;

/**
 * Scheduler interface to manage player turns.
 */
public interface Scheduler {
    /**
     * @return the next player by advancing the turn order.
     */
    Player getNextPlayer();

    /**
     * @return the next player without advancing the turn order
     */
    Player peekNextPlayer();

    /**
     * Reverses the turn order direction.
     */
    void reverseDirection();

    /**
     * Skips the next player's turn.
     */
    void skipTurn();
}
