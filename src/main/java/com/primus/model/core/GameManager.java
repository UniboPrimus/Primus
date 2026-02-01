package com.primus.model.core;

import com.primus.model.deck.Card;
import com.primus.utils.GameState;
import com.primus.model.player.Player;

/**
 * Game manager interface which handles game states.
 */
public interface GameManager {
    /**
     * Creates instances and sets data to start the game.
     */
    void init();

    /**
     * @return the game state
     */
    GameState getGameState();

    /**
     * @return the next player by advancing the turn order.
     */
    Player nextPlayer();

    /**
     * Valida una carta basandosi sullo stato di gioco.
     *
     * @param carta card to be validated
     * @param giocatore player who is playing
     * @return `True` if the card is valid
     */
    boolean validateCard(Card carta, Player giocatore);

    /**
     * Play a card.
     *
     * @param carta played card
     * @param giocatore player
     */
    void playCard(Card carta, Player giocatore);

    /**
     * @return `True` if the match is concluded
     */
    boolean isFinished();
}
