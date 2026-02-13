package com.primus.model.deck;

/**
 * Enum representing the colors of cards in the game.
 */

public enum Color {
    /** Red color for cards. */
    RED,
    /** Blue color for cards. */
    BLUE,
    /** Green color for cards. */
    GREEN,
    /** Yellow color for cards. */
    YELLOW,
    /** Black color for special cards. */
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
