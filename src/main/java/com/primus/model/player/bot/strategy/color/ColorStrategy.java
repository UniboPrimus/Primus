package com.primus.model.player.bot.strategy.color;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;

import java.util.List;

/**
 * Strategy interface responsible for choosing a color when a Wild card is played.
 * Implementations define how a bot selects the declared color
 * after playing a Jolly or Wild Draw Four.
 */
@FunctionalInterface
public interface ColorStrategy {

    /**
     * Decides the color to declare based on the bot's current hand.
     *
     * @param hand the current hand of the bot.
     * @return the chosen {@link Color}.
     */
    Color chooseColor(List<Card> hand);
}
