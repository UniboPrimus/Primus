package com.primus.model.core;

import com.primus.model.player.Player;

/**
 * Scheduler interface to manage player turns.
 */
public interface Scheduler {
    /**
     * @return the next player ID by advancing the turn order.
     */
    int nextPlayer();

    /**
     * @return the next player ID without advancing the turn order
     */
    int peekNextPlayer();

    /**
     * Reverses the turn order direction.
     */
    void reverseDirection();

    /**
     * Skips the next player's turn.
     */
    void skipTurn();


}
