package com.primus.model.player.bot.strategy.card;

import com.primus.model.deck.Card;

import java.util.List;
import java.util.Optional;

/**
 * Defines the strategy for a bot to choose a card to play during the game.
 */
@FunctionalInterface
public interface CardStrategy {

    /**
     * Chooses a card from possibleCards.
     *
     * @param possibleCards the list of cards that can be played based on hand minus rejected cards.
     * @return an {@link Optional} containing the card to play,
     *      or {@code Optional.empty()} if the strategy decides to pass the turn (no valid moves).
     */
    Optional<Card> chooseCard(List<Card> possibleCards);
}
