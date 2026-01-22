package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta lo stato del gioco in questo momento.
 *
 * @param topCard carta attualmente in gioco
 * @param playerDeck mano attuale del giocatore
 */
public record GameState(Card topCard, List<Card> playerDeck) {
    /**
     * @param topCard carta attualmente in gioco
     * @param playerDeck mano attuale del giocatore
     */
    public GameState {
        playerDeck = List.copyOf(playerDeck);
    }

    /**
     * @param newTopCard nuova carta attualmente in gioco
     * @return `GameState` basato su quello attuale modificando carta in gioco
     */
    public GameState withTopCard(final Card newTopCard) {
        return new GameState(newTopCard, this.playerDeck);
    }

    /**
     * @param card carta da aggiungere alla mano del giocatore
     * @return `GameState` basato su quello attuale aggiungendo una carta alla mano del giocatore
     */
    public GameState withAddedCard(final Card card) {
        final var newDeck = new ArrayList<>(this.playerDeck);
        newDeck.add(card);
        return new GameState(this.topCard, newDeck);
    }

    /**
     * @param newTopCard nuova carta attualmente in gioco
     * @param card carta da aggiungere alla mano del giocatore
     * @return `GameState` basato su quello attuale modificando carta in gioco e aggiungendo carta alla mano del giocatore
     */
    public GameState newFromCurrent(final Card newTopCard, final Card card) {
        return withAddedCard(card).withTopCard(topCard);
    }
}

