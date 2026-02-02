package com.primus.model.deck;

/**
 * Enum representing the values of cards in the game.
 */

public enum Values {
    ZERO("0", 0),
    ONE("1", 1),
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),


    //Colored Action Cards (index 20)
    DRAW_TWO("+2", 20),
    REVERSE("Reverese", 20),
    SKIP("Skip", 20),

    //Black Special Cards (index 50)
    WILD("Wild", 50),
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
