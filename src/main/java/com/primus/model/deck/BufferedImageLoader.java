package com.primus.model.deck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.awt.Image;
import java.util.Locale;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Optional;

/**
 * Concrete implementation of {@link ImageLoader} that loads PNG images from the classpath.
 *
 * <p>
 * This implementation:
 * <ul>
 *     <li>Loads images from {@code /assets/cards/} directory</li>
 *     <li>Uses PNG format exclusively</li>
 *     <li>Caches loaded images in memory for better performance</li>
 * </ul>
 * </p>
 */
public final class BufferedImageLoader implements ImageLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedImageLoader.class);
    private static final String PATH = "/assets/cards/";
    private final Map<String, Image> bufferedImages = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public Optional<Image> getImage(final Card card) {
        final String key = (card.getColor().name() + "_" + card.getValue().name()).toLowerCase(Locale.ROOT);
        final String fullPath = PATH + key + ".png";

        // Return cached image if available
        if (bufferedImages.containsKey(key)) {
            return Optional.of(bufferedImages.get(key));
        }

        // Load image from resources
        try (InputStream asset = getClass().getResourceAsStream(fullPath)) {
            if (asset == null) {
                LOGGER.warn("Image resource not found: {}", fullPath);
                return Optional.empty();
            }

            final BufferedImage image = ImageIO.read(asset);
            if (image == null) {
                LOGGER.error("Failed to read image from stream: {}", fullPath);
                return Optional.empty();
            }

            bufferedImages.put(key, image);
            LOGGER.debug("Successfully loaded and cached image: {}", fullPath);
            return Optional.of(image);

        } catch (final IOException e) {
            LOGGER.error("IOException while loading image: {}", fullPath, e);
            return Optional.empty();
        }
    }
}
