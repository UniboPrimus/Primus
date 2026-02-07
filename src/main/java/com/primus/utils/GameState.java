package com.primus.utils;

import com.primus.model.deck.Card;
import com.primus.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO class which represents the game state.
 *
 * @param topCard currently played card
 * @param activeHand current player's hand
 * @param playerId current player ID
 */
public record GameState(
        Card topCard,
        List<Card> activeHand,
        int playerId
) {

    /**
     * Constuctor that ensures immutability of the activeHand list and non-null values for topCard and activePlayer.
     *
     * @param topCard currently played card
     * @param activeHand current player's hand
     * @param playerId current player's ID
     */
    public GameState {
        Objects.requireNonNull(topCard);
        Objects.requireNonNull(activeHand);

        activeHand = List.copyOf(activeHand);
    }

    /**
     * @return a copy of the state changing the top card
     */
    public GameState withTopCard(final Card newTopCard) {
        return new GameState(newTopCard, this.activeHand, this.playerId);
    }

    /**
     * @return a copy of the state changing the active player ID
     */
    public GameState withActivePlayer(final int playerId) {
        return new GameState(this.topCard, this.activeHand, playerId);
    }

    /**
     * @return a copy of the state with a new card added to the active hand.
     */
    public GameState withAddedCard(final Card card) {
        final List<Card> newHand = new ArrayList<>(this.activeHand);
        newHand.add(card);
        return new GameState(this.topCard, newHand, this.playerId);
    }
}