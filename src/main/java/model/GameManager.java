package model;

/**
 * Manager centrale che gestisce stati del gioco.
 */
public interface GameManager {
    /**
     * Crea istanze e imposta dati per poter iniziare la partita.
     */
    void init();

    /**
     * @return lo stato di gioco attuale
     */
    GameState getGameState();

    /**
     * @return il giocatore giocante in questo turno
     */
    Player getCurrentPlayer();

    /**
     * Valida una carta basandosi sullo stato di gioco.
     *
     * @param carta carta da valutare
     * @param giocatore giocante
     * @return `True` se la carta rispetta le regole per essere giocata
     */
    boolean validateCard(Card carta, Player giocatore);

    /**
     * Gioca una carta.
     *
     * @param carta carta giocata
     * @param giocatore che fa la mossa
     */
    void playCard(Card carta, Player giocatore);

    /**
     * @return `True` se la partita è conclusa
     */
    boolean isFinished();
}
