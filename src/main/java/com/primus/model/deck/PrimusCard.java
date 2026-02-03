package com.primus.model.deck;

import java.util.Objects;

/**
 * Represents a card in the Primus game with a specific color and value.
 */

public final class PrimusCard implements Card {
    private final Color color;
    private final Values value;

    /**
     * Creates a new PrimusCard with the specified color and value.
     *
     * @param color the color of the card (cannot be null)
     * @param value the value of the card (cannot be null)
     */
    public PrimusCard(final Color color, final Values value) {

        //Check for null values to avoid NullPointerException
        this.color = Objects.requireNonNull(color, "Color cannot be null");
        this.value = Objects.requireNonNull(value, "Value cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getColor() {
        return color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Values getValue() {
        return value;
    }

    @Override
    public boolean isNativeBlack() {
        //todo
        return false;
    }

    @Override
    public Card withColor(final Color c) {
        //todo
        return null;
    }

    @Override
    public String toString() {
        return "PrimusCard{"
                + "color=" + color
                + ", value=" + value
                + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PrimusCard that = (PrimusCard) o;
        return color == that.color && value == that.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(color, value);
    }
}
