package controller;

import model.Card;

/**
 * Controller del gioco, gestisce il gameLoop e fa da tramite tra view e model.
 */
public interface GameController {
    /**
     * Avvia il gameLoop in un nuovo thread separato.
     */
    void start();

    /**
     * Ferma il thread del gameLoop.
     */
    void stop();

    /**
     * Notifica il Game che il giocatore ha giocato la carta.
     *
     * @param card la carta scelta dal giocatore
     */
    void humanPlayedCard(Card card);

    /**
     * Notifica il Game che il giocatore ha passato il turno.
     */
    void humanDrewCard();
}
