package com.primus.utils;

import com.primus.model.deck.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO class which represents the game state.
 *
 * @param topCard currently played card
 * @param playerDeck current player's hand
 */
public record GameState(Card topCard, List<Card> playerDeck) {
    /**
     * @param topCard currently played card
     * @param playerDeck current player's hand
     */
    public GameState {
        playerDeck = List.copyOf(playerDeck);
    }

    /**
     * @param newTopCard new card currently in play
     * @return `GameState` based on the current one modifying the card in play
     */
    public GameState withTopCard(final Card newTopCard) {
        return new GameState(newTopCard, this.playerDeck);
    }

    /**
     * @param card card to add to the player's hand
     * @return `GameState` based on the current one adding a card to the player's hand
     */
    public GameState withAddedCard(final Card card) {
        final var newDeck = new ArrayList<>(this.playerDeck);
        newDeck.add(card);
        return new GameState(this.topCard, newDeck);
    }

    /**
     * @param newTopCard new card currently in play
     * @param card card to add to the player's hand
     * @return `GameState` based on the current one modifying the card in play and adding a card to the player's hand
     */
    public GameState newFromCurrent(final Card newTopCard, final Card card) {
        return withAddedCard(card).withTopCard(topCard);
    }
}

