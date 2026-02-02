package com.primus.model.deck;

/**
 * Enum representing the colors of cards in the game.
 */

public enum Color {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    BLACK;

    /**
     * Checks if the given color is BLACK.
     *
     * @param c the color to check
     * @return true if the color is BLACK, false otherwise
     */

    public static boolean isBlack(final Color c) {
        return c == BLACK;
    }
}
