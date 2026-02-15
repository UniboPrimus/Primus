package com.primus.utils;

import java.util.Objects;

/**
 * DTO class to hold player setup data. Mainly used to transfer player information from
 * {@link com.primus.model.core.GameManager} to {@link com.primus.view.GameView}
 * when initialising the game and creating player instances.
 *
 * @param isHuman flag indicating if the player is human or AI
 * @param id the player ID
 * @param name the player name
 */
public record PlayerSetupData(int id, String name, boolean isHuman) {

    /**
     * Compact constructor that ensures non-null values for name and valid ID.
     *
     * @param id the player ID, must be non-negative
     * @param name the player name, must not be null
     * @param isHuman flag indicating if the player is human or AI
     * @throws IllegalArgumentException if id is negative
     * @throws NullPointerException if name is null
     */
    public PlayerSetupData {
        Objects.requireNonNull(name, "Player name cannot be null");
        if (id < 0) {
            throw new IllegalArgumentException("Player ID must be non-negative, got: " + id);
        }
    }
}
