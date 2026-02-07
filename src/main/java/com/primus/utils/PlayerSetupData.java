package com.primus.utils;

/**
 * DTO class to hold player setup data. Mainly used to transfer player information from
 * {@link com.primus.model.core.GameManager} to {@link com.primus.view.GameView}
 * when initialising the game and creating player instances.
 *
 * @param isHuman flag indicating if the player is human or AI
 * @param id the player ID
 */
public record PlayerSetupData(int id, boolean isHuman) {
}
