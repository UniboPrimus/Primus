package com.primus.app;

import com.primus.controller.GameController;
import com.primus.controller.GameControllerImpl;
import com.primus.model.core.GameManager;
import com.primus.model.core.GameManagerImpl;
import com.primus.model.deck.BufferedImageLoader;
import com.primus.model.deck.ImageLoader;
import com.primus.view.GameView;
import com.primus.view.PrimusGameView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * App entry point.
 */
public final class PrimusApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimusApp.class);

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
        LOGGER.info("========================================");
        LOGGER.info("      Primus Application Starting       ");

        LOGGER.debug("Initializing Core Components");

        final GameManager manager = new GameManagerImpl();
        LOGGER.debug("Manager created");

        // One image loader instance can be shared across the application, as it is stateless and thread-safe
        final ImageLoader imageLoader = new BufferedImageLoader();

        final GameView view = new PrimusGameView(imageLoader);
        LOGGER.debug("View created.");

        final GameController controller = new GameControllerImpl(manager);
        controller.addView(view);
        LOGGER.debug("Controller initialized and View wired");

        LOGGER.info("Initialization complete. Starting Game Controller");
        controller.start();

        LOGGER.info("Application finished gracefully.");
    }
}
