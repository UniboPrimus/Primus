package com.primus.model.deck;

import java.util.Random;

/**
 * Enumeration representing different game events or modes in the Primus card game.
 * Each event corresponds to a corresponding deck configuration file.
 */
public enum GameEvent {
    /** Standard Game: The classic game with the original rules and card effects. */
    STANDARD("Standard Game", "standard_deck.csv"),
    /**
     * Double Draws: A game mode where all Draw Two and Wild Draw Four cards require players to
     * draw double the usual number of cards.
     * */
    DOUBLE_TROUBLE("Double Draws", "double_trouble_deck.csv"),
    /** Reverse Zeros: A game mode where playing a Zero card reverses the direction of play. */
    REVERSE_ZERO("Reverse Zeros", "reverse_zero_deck.csv"),
    /** Block Sevens: A game mode where playing a Seven card allows the player to block the next player's turn. */
    BLOCK_SEVEN("Block Sevens", "block_seven_deck.csv"),
    /** Total Chaos: The mix of all the above events, where all special rules are active simultaneously. */
    TOTAL_CHAOS("Total Chaos", "total_chaos_deck.csv");

    private static final Random RND = new Random();
    private final String description;
    private final String fileName;

    /**
     * Constructor for GameEvent enum.
     *
     * @param description a brief description of the game event
     * @param fileName the name of the CSV file containing the deck configuration for this event
     */
    GameEvent(final String description, final String fileName) {
        this.description = description;
        this.fileName = fileName;
    }

    /**
     * Gets the description of the game event.
     *
     * @return the description of the game event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the file name of the deck configuration associated with this game event.
     *
     * @return the file name of the deck configuration
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Selects and returns a random GameEvent from the available events.
     *
     * @return a randomly selected GameEvent
     */
    public static GameEvent getRandomEvent() {
        final GameEvent[] events = values();
        return events[RND.nextInt(events.length)];
    }
}
