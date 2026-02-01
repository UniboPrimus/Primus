package com.primus.model.player.bot;

import com.primus.model.deck.Card;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A strategy implementation for a bot that prioritizes aggressive gameplay.
 * This strategy aims to select the most impactful card to play based on the
 * provided list of possible cards.
 */
public final class AggressiveStrategy implements BotStrategy {
    /**
     * Chooses the most aggressive card from the list of possible cards.
     * The logic for determining the "most aggressive" card should be implemented here.
     *
     * @param possibleCards the list of cards the bot can choose from
     * @return the card deemed most aggressive, or null if no valid card is available
     */
    @Override
    public Optional<Card> chooseCard(final List<Card> possibleCards) {
        return possibleCards.stream().max(Comparator.comparingInt(this::calculateScore));
    }

    private int calculateScore(final Card c) {
        if (c == null) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
