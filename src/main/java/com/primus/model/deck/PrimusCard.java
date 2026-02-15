package com.primus.model.deck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a card in the Primus game with a specific color and value.
 */

public final class PrimusCard implements Card {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimusCard.class);

    private final Color color;
    private final Values value;

    private final int drawAmount;
    private final Set<CardEffect> effects;

    /**
     * Creates a new PrimusCard with the specified color and value.
     *
     * @param color      the color of the card (cannot be null)
     * @param value      the value of the card (cannot be null)
     * @param drawAmount the number of cards the next player must draw when this card is played (non-negative)
     * @param effects    the set of special effects associated with this card
     */
    public PrimusCard(final Color color, final Values value, final int drawAmount, final Set<CardEffect> effects) {

        //Check for null values to avoid NullPointerException
        this.color = Objects.requireNonNull(color, "Color cannot be null");
        this.value = Objects.requireNonNull(value, "Value cannot be null");
        this.drawAmount = Math.max(0, drawAmount);
        this.effects = effects == null ? Collections.emptySet() : Set.copyOf(effects);

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Created PrimusCard: color={}, value={}, drawAmount={}, effects={}",
                    this.color, this.value, this.drawAmount, this.effects);
        }
    }

    /**
     * Allows creating cards in Tests without specifying effects/power manually.
     *
     * @param color the color of the card.
     * @param value the value of the card.
     */
    public PrimusCard(final Color color, final Values value) {
        this(color, value, 0, Collections.emptySet());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDrawAmount() {
        return drawAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEffect(final CardEffect effect) {
        return effects.contains(effect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNativeBlack() {
        return value == Values.WILD || value == Values.WILD_DRAW_FOUR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Card withColor(final Color newColor) {
        Objects.requireNonNull(newColor, "New color cannot be null");

        if (this.color == newColor) {
            return this;
        }

        LOGGER.debug("Morphing card {} to new color {}", this, newColor);
        return new PrimusCard(newColor, this.value, this.drawAmount, this.effects);
    }

    @Override
    public String toString() {
        final String cleanValue = value.name().replace('_', ' ');
        final StringBuilder sb = new StringBuilder();

        sb.append('[').append(color).append(' ').append(cleanValue).append(']');

        if (drawAmount > 0) {
            sb.append(" (+").append(drawAmount).append(')');
        }

        if (!effects.isEmpty()) {
            sb.append(' ').append(effects);
        }

        return sb.toString();
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
        return color == that.color
                && value == that.value
                && drawAmount == that.drawAmount
                && Objects.equals(effects, that.effects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(color, value, drawAmount, effects);
    }
}
