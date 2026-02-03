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
     * * Gets the value of the card.
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

    /**
     * Checks if the card is natively a Wild card (Wild or Wild Draw Four),
     * regardless of its current effective color.
     * This is crucial to identify Jolly cards that have already been played/colored.
     *
     * @return true if the card is intrinsically a Wild card
     */
    boolean isNativeBlack();

    /**
     * Creates a NEW copy of this card with the specified color.
     * Use this method when a player chooses a color for a Wild card.
     *
     * @param color the new color to assign
     * @return a new Card instance with the updated color and the same value
     */
    Card withColor(Color color);
}
