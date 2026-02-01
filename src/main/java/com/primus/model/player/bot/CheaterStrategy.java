package com.primus.model.player.bot;

import com.primus.model.deck.Card;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * An advanced implementation of {@link BotStrategy} representing a "cheater" personality.
 * This strategy has access to an opponent's information via {@link OpponentInfo},
 * allowing it to make decisions based on the opponent's current hand.
 */
public final class CheaterStrategy implements BotStrategy {
    private final OpponentInfo victim;

    /**
     * Constructs a CheaterStrategy targeting a specific opponent.
     *
     * @param victim the opponent info to spy on during the match
     */
    public CheaterStrategy(final OpponentInfo victim) {
        this.victim = victim;
    }

    /**
     * Chooses a card by analyzing both the bot's possible moves and the victim's hand.
     * * @param possibleCards the list of cards that can be played
     *
     * @return an Optional containing the chosen card, or empty if no card is selected
     */
    @Override
    public Optional<Card> chooseCard(final List<Card> possibleCards) {
        return possibleCards.stream().max(Comparator.comparingInt(this::calculateScore));
    }

    /**
     * Calculates a strategic score for a card based on the current game context.
     *
     * @param c the card to evaluate
     * @return an integer representing the card's priority (higher is better)
     */
    private int calculateScore(final Card c) {
        victim.getCardCount();
        if (c == null) {
            return 1;
        } else {
            return 2;
        }
    }
}
