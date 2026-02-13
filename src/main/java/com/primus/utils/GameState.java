package com.primus.utils;

import com.primus.model.deck.Card;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DTO class which represents the game state.
 *
 * @param topCard currently played card
 * @param humanHand current player's hand
 * @param playerId current player ID
 * @param playersCardCounts a map of player IDs to the count of cards in their hands
 * @param isMalusActive flag indicating if there are cards to be drawn at the start
 */
public record GameState(
        Card topCard,
        List<Card> humanHand,
        Map<Integer, Integer> playersCardCounts,
        int playerId,
        boolean isMalusActive
) {

    /**
     * Constuctor that ensures immutability of the humanHand list and non-null values for topCard and activePlayer.
     *
     * @param topCard currently played card
     * @param humanHand current player's hand
     * @param playerId current player's ID
     * @param playersCardCounts a map of player IDs to the count of cards in their hands
     * @param isMalusActive flag indicating if there are cards to be drawn at the start of the turn due to a
     *                      malus effect
     */
    public GameState {
        Objects.requireNonNull(topCard);
        Objects.requireNonNull(humanHand);
        Objects.requireNonNull(playersCardCounts);

        playersCardCounts = Map.copyOf(playersCardCounts);
        humanHand = List.copyOf(humanHand);
    }
}
