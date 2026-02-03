package com.primus.model.player.bot;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;

import java.util.List;

/**
 * Strategy interface responsible for choosing a color when a Wild card is played.
 */
@FunctionalInterface
public interface ColorStrategy {

    /**
     * Decides the color to declare based on the bot's current hand.
     *
     * @param hand the current hand of the bot (useful to pick the most frequent color).
     * @return the chosen Color.
     */
    Color chooseColor(List<Card> hand);
}
