package com.primus.model.core;

import com.primus.model.deck.Card;
import com.primus.utils.GameState;
import com.primus.model.player.Player;
import com.primus.utils.PlayerSetupData;

import java.util.List;
import java.util.Optional;

/**
 * Interface to manage the game flow.
 */
public interface GameManager {
    /**
     * Creates instances and sets data to start the game, useful when restarting a new game.
     */
    void init();

    /**
     * Returns the current game state.
     *
     * @return the game state in current turn
     */
    GameState getGameState();

    /**
     * Returns the setup data for all players in the game.
     *
     * @return a list of {@link PlayerSetupData} containing the necessary information to create player
     *     instances and initialize the game.
     */
    List<PlayerSetupData> getGameSetup();

    /**
     * Advances the turn order and returns the next player.
     *
     * @return the next player by advancing the turn order.
     */
    Player nextPlayer();

    /**
     * Returns the winner of the game if the game is finished.
     *
     * @return An {@link Optional} containing the winner player ID if the game is finished, empty otherwise
     */
    Optional<Integer> getWinner();

    /**
     * Executes the turn for the current player with the chosen card.
     *
     * @param chosenCard the card chosen to play, may be null if drawing
     * @return {@code True} if the turn was executed successfully
     */
    boolean executeTurn(Card chosenCard);
}
