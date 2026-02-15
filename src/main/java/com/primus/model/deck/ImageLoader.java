package com.primus.model.deck;

import java.awt.Image;
import java.util.Optional;

/**
 * Interface for loading images corresponding to cards. This abstraction allows for different implementations
 * (e.g., using BufferedImage, SVG, etc.) without coupling the card model to a specific image loading mechanism.
 */

public interface ImageLoader {
    /**
     * Loads and returns the corresponding image for the given card.
     *
     * @param card the card for which to load the image
     * @return an {@link Optional} containing the corresponding {@link Image} for the given card,
     *     or {@link Optional#empty()} if the image cannot be loaded
     */
    Optional<Image> getImage(Card card);

    /**
     * Loads and returns the image for the back of a card.
     *
     * @return an {@link Optional} containing the {@link Image} for the back of a card,
     *     or {@link Optional#empty()} if the image cannot be loaded
     */
    Optional<Image> getBackImage();
}
