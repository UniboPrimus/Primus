package com.primus.model.player;

import com.primus.model.deck.Card;

import java.util.List;
import java.util.Optional;

/**
 * Represents a player in the game. A player can either be a human or a bot.
 * This interface defines the basic actions and properties of a player,
 * including playing a card, checking if the player is a bot, and retrieving
 * the player's current hand of cards.
 */
public interface Player {
    /**
     * Attempts to play a card from the player's hand.
     *
     * @return an {@code Optional} containing the card if the player is trying to play a card,
     *      or an empty {@code Optional} if the player decides to pass the turn.
     */
    Optional<Card> playCard();

    /**
     * Checks if the player is a bot.
     *
     * @return true if the player is a bot, false otherwise
     */
    boolean isBot();

    /**
     * Retrieves the current hand of the player.
     *
     * @return unmodifiable list of cards in the player's hand
     */
    List<Card> getHand();

    /**
     * Adds a list of cards to the player's hand. This can happen, for example,
     * as a penalty (malus) or as a result of passing a turn. The list can contain
     * from 1 to n cards.
     *
     * @param cards the list of cards to be added to the player's hand
     */
    void addCards(List<Card> cards);

    /**
     * Retrieves the unique identifier of the player.
     *
     * @return the unique ID of the player
     */
    int getId();

    /**
     * Notifies the player about the outcome of the last card played.
     * This method allows the bot to update its internal state (e.g., memory of rejected cards)
     * or to finalize the turn (e.g., removing the card from hand if valid).
     *
     * @param cardPlayed the card been validated
     * @param valid      true if the move was accepted by the rules, false otherwise.
     */
    void notifyMoveResult(Card cardPlayed, boolean valid);

}
