package com.primus.model.deck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class responsible for parsing deck configuration files.
 *
 * <p>
 * It reads a CSV-like format (COLOR,VALUE,QUANTITY) and converts it
 * into a list of {@link PrimusCard} objects.
 * </p>
 *
 */
public final class DeckFileReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeckFileReader.class);

    private static final String COMMENT_PREFIX = "#";
    private static final String SEPARATOR = ",";
    private static final String EFFECTS_SEPARATOR = "\\|"; // | pipe separator for multiple effects

    private static final int MIN_PARTS = 3;

    /**
     * Default constructor for DeckFileReader.
     */
    public DeckFileReader() {
        //Empty constructor
    }

    /**
     * Parses a configuration file from the resources folder and generates a list of cards.
     *
     * @param fileName the name of the file to read (must be in resources/ classpath).
     * @return a List of {@link Card} objects ready to be used in a Deck.
     * @throws NullPointerException     if the provided fileName is null.
     * @throws IllegalArgumentException if the file is not found or has an invalid format.
     * @throws IllegalStateException    if the file cannot be read due to an I/O error or parsing failure.
     * @throws IOException              if an I/O error occurs while reading the file.
     */
    public List<Card> loadDeck(final String fileName) throws IOException {
        final List<Card> cards = new ArrayList<>();
        Objects.requireNonNull(fileName, "File name must not be null");

        LOGGER.info("Starting to load deck from file: {}", fileName);

        final String resourcePath = fileName.startsWith("/") ? fileName : "/" + fileName;

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                LOGGER.error("Deck configuration file not found: {}", fileName);
                throw new IllegalArgumentException("File not found: " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                while (true) {
                    final String line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    final String cleanLine = line.trim();

                    if (cleanLine.isEmpty() || cleanLine.startsWith(COMMENT_PREFIX)) {
                        continue;
                    }

                    try {
                        cards.addAll(parseLine(cleanLine));
                    } catch (final IllegalArgumentException e) {
                        LOGGER.error("Format error in deck file {}: {}", fileName, e.getMessage());
                        throw new IllegalArgumentException("Error parsing file " + fileName
                                + ": " + e.getMessage(), e);
                    }
                }
            }
        } catch (final IOException e) {
            LOGGER.error("I/O error while reading deck file {}: {}", fileName, e.getMessage());
            throw new IllegalStateException("Failed to load the deck from file " + fileName, e);
        }

        LOGGER.info("Deck loaded successfully from file: {}. Total cards parsed: {}", fileName, cards.size());
        return cards;
    }

    /**
     * Parses a single line in the format: COLOR,VALUE,QUANTITY.
     *
     * @param line the line to parse.
     * @return a list containing N copies of the card.
     */
    private List<Card> parseLine(final String line) {
        final String[] parts = line.split(SEPARATOR, -1);
        if (parts.length < MIN_PARTS) {
            throw new IllegalArgumentException("Invalid format expected 3 parts " + parts.length);
        }

        final String colorString = parts[0].trim().toUpperCase(Locale.ROOT);
        final Color color = Color.valueOf(colorString);

        final String valueSting = parts[1].trim().toUpperCase(Locale.ROOT);
        final Values value = Values.valueOf(valueSting);

        final int quantity = Integer.parseInt(parts[2].trim());
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive " + quantity);
        }

        final Set<CardEffect> effects = EnumSet.noneOf(CardEffect.class);
        if (parts.length > 3 && !parts[3].isBlank()) {
            final String[] effectNames = parts[3].trim().split(EFFECTS_SEPARATOR);
            for (final String effectName : effectNames) {
                if (!effectName.isBlank()) {
                    effects.add(CardEffect.valueOf(effectName.trim().toUpperCase(Locale.ROOT)));
                }
            }
        }

        int drawAmount = 0;
        if (parts.length > 4 && !parts[4].isBlank()) {
            drawAmount = Integer.parseInt(parts[4].trim());
        }

        final List<Card> parsedCards = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            parsedCards.add(new PrimusCard(color, value, drawAmount, effects));
        }
        return parsedCards;
    }
}
