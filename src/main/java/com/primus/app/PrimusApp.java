package com.primus.app;

import com.primus.controller.GameController;
import com.primus.controller.GameControllerImpl;
import com.primus.model.core.GameManager;
import com.primus.model.core.GameManagerImpl;
import com.primus.view.GameView;
import com.primus.view.PrimusGameView;

/**
 * App entry point.
 */
public final class PrimusApp {

    /**
     * Private constructor to prevent instantiation.
     */
    private PrimusApp() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final GameManager manager = new GameManagerImpl();
        final GameView view = new PrimusGameView();
        final GameController controller = new GameControllerImpl(manager);
        controller.addView(view);
        controller.start();
    }
}
