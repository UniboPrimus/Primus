package model.player.bot;

import model.deck.Card;

import java.util.List;
import java.util.Optional;

/**
 * Defines the strategy for a bot to choose a card to play during the game.
 * Implementations of this interface provide the logic for selecting the best card
 * from the bot's hand, taking into account the cards that have already been tried
 * and failed.
 */
@FunctionalInterface
public interface BotStrategy {

    /*todo vedere come gestire la questione di nessuna carta, lancio una eccezione se la lista di possibili carte è
        vuota (allora aggiungere
       la @trhow al metodo?
       oppure uso gli Optional di carta e lascio a zito?
    */
    /**
     * Chooses the best card from the hand, ignoring the cards present in failedAttempts.
     *
     * @param possibleCards the list of cards that can be played based on hand - rejected
     * @return the card to play, or null if there are no valid/strategic cards (indicates PASS)
     */
    Optional<Card> chooseCard(List<Card> possibleCards);
}
