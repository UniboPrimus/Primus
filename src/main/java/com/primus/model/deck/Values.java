package com.primus.model.deck;

/**
 * Enum representing the values of cards in the game.
 */

public enum Values {
    //Numeric Cards (index 0-9)
    /** Numeric Card value representing the number 0. */
    ZERO("0", 0),
    /** Numeric Card value representing the number 1. */
    ONE("1", 1),
    /** Numeric Card value representing the number 2. */
    TWO("2", 2),
    /** Numeric Card value representing the number 3. */
    THREE("3", 3),
    /** Numeric Card value representing the number 4. */
    FOUR("4", 4),
    /** Numeric Card value representing the number 5. */
    FIVE("5", 5),
    /** Numeric Card value representing the number 6. */
    SIX("6", 6),
    /** Numeric Card value representing the number 7. */
    SEVEN("7", 7),
    /** Numeric Card value representing the number 8. */
    EIGHT("8", 8),
    /** Numeric Card value representing the number 9. */
    NINE("9", 9),


    //Colored Action Cards (index 20)
    /** Action Card that forces the next player to draw two cards. */
    DRAW_TWO("+2", 20),
    /** Action Card that reverses the direction of play. */
    REVERSE("Reverse", 20),
    /** Action Card that skips the next player's turn. */
    SKIP("Skip", 20),

    //Black Special Cards (index 50)
    /** Special Card that allows the player to change the current color in play. */
    WILD("Wild", 50),
    /** Special Card that allows the player to change the current color in play and forces the next player to draw four cards. */
    WILD_DRAW_FOUR("+4 Wild", 50);

    private final String label;
    private final int index;

    Values(final String label, final int index) {
        this.label = label;
        this.index = index;
    }

    /**
     * Gets the label of the card value.
     *
     * @return the string label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the numeric index/score of the card value.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }
}
