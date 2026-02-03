package com.primus.model.player.bot;

import com.primus.model.deck.Card;

import java.util.List;
import java.util.Optional;

/**
 * Defines the strategy for a bot to choose a card to play during the game.
 * Implementations of this interface provide the logic for selecting the best card
 * from the bot's hand, taking into account the cards that have already been tried
 * and failed.
 */
@FunctionalInterface
public interface CardStrategy {

    /**
     * Chooses the best card from the hand, ignoring the cards present in failedAttempts.
     *
     * @param possibleCards the list of cards that can be played based on hand - rejected
     * @return the card to play, or null if there are no valid/strategic cards (indicates PASS)
     */
    Optional<Card> chooseCard(List<Card> possibleCards);
}
