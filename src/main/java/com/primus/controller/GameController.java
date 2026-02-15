package com.primus.controller;

import com.primus.view.GameView;

/**
 * Game controller interface, manages the game loop and acts as a bridge between view and model.
 */
public interface GameController {
    /**
     * Starts the game loop.
     */
    void start();

    /**
     * Stops the game loop.
     */
    void stop();

    /**
     * Adds a view to the controller, allowing it to receive updates and user input.
     *
     * @param view the GameView to be added to the controller
     */
    void addView(GameView view);
}
