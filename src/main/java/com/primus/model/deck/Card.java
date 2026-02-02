package com.primus.model.deck;

/**
 * Card interface representing a playing card in the game.
 */
public interface Card {

    /**
     * Gets the color of the card.
     *
     * @return the color of the card
     */
    Color getColor();

    /**
     * Gets the value of the card.
     *
     * @return the value of the card
     */
    Values getValue();

    /**
     * Checks if the card is black.
     *
     * @return true if the card is black, false otherwise
     */
    default boolean isBlack() {
        return Color.isBlack(this.getColor());
    }
}
