package com.primus.model.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Deck, Card, and DropPile logic.
 * Includes tests for the new Data-Driven features (Effects & Draw Power).
 */
class PrimusDeckTest {

    private static final int STANDARD_DECK_SIZE = 108;
    private static final int REFILLED_DECK_SIZE = 9;
    private static final int CHAOS_DRAW_AMOUNT = 10;
    private static final int DRAW_TWO_AMOUNT = 2;
    private static final int DRAW_FOUR_AMOUNT = 4;
    private static final int DROP_PILE_REFILL_SIZE = 10;

    private PrimusDeck deck;
    private PrimusDropPile dropPile;

    @BeforeEach
    void setUp() {
        deck = new PrimusDeck();
        dropPile = new PrimusDropPile();
    }

    // Base Tests

    @Test
    @DisplayName("Il mazzo deve inizializzarsi con 108 carte")
    void testDeckInitialization() {
        assertNotNull(deck, "Il mazzo non deve essere null");
        assertEquals(STANDARD_DECK_SIZE, deck.size(), "Un mazzo standard UNO deve avere 108 carte");
        assertFalse(deck.isEmpty(), "Il mazzo appena creato non deve essere vuoto");
    }

    @Test
    @DisplayName("Pescare una carta deve ridurre la dimensione del mazzo")
    void testDrawCard() {
        final int initialSize = deck.size();
        final Card card = deck.drawCard();

        assertNotNull(card, "La carta pescata non deve essere null");
        assertEquals(initialSize - 1, deck.size(), "La dimensione del mazzo deve diminuire di 1");
    }

    @Test
    @DisplayName("Mazzo Vuoto: pescare deve lanciare eccezione")
    void testDrawFromEmptyDeck() {
        while (!deck.isEmpty()) {
            deck.drawCard();
        }
        assertThrows(IllegalStateException.class, deck::drawCard);
    }

    @Test
    @DisplayName("DropPile: extractAllExceptTop deve lasciare l'ultima carta sulla pila")
    void testDropPileExtraction() {
        final Card c1 = new PrimusCard(Color.RED, Values.FIVE);
        final Card c2 = new PrimusCard(Color.BLUE, Values.TWO);
        final Card topCard = new PrimusCard(Color.GREEN, Values.SKIP);

        dropPile.addCard(c1);
        dropPile.addCard(c2);
        dropPile.addCard(topCard);

        final List<Card> recycled = dropPile.extractAllExceptTop();

        assertEquals(2, recycled.size(), "Deve restituire 2 carte (3 totali - 1 top)");
        assertTrue(recycled.contains(c1));
        assertTrue(recycled.contains(c2));
        assertFalse(recycled.contains(topCard), "La lista riciclata NON deve contenere la top card");

        assertEquals(topCard, dropPile.peek());
    }

    @Test
    @DisplayName("Integrazione: Il mazzo si ricarica correttamente dagli scarti")
    void testRefillFromDropPile() {
        while (!deck.isEmpty()) {
            deck.drawCard();
        }

        for (int i = 0; i < DROP_PILE_REFILL_SIZE; i++) {
            dropPile.addCard(new PrimusCard(Color.YELLOW, Values.ONE));
        }

        deck.refillFrom(dropPile);

        assertEquals(REFILLED_DECK_SIZE, deck.size(), "Il mazzo deve contenere le carte riciclate (10 - 1)");
        assertFalse(deck.isEmpty());
    }

    // Events Tests

    @Test
    @DisplayName("CSV Parsing: Le carte +2 devono avere DrawAmount = 2 e Effetto SKIP")
    void testDrawTwoPropertiesFromCSV() {
        Card drawTwo = null;
        while (!deck.isEmpty()) {
            final Card c = deck.drawCard();
            if (c.getValue() == Values.DRAW_TWO) {
                drawTwo = c;
                break;
            }
        }

        assertNotNull(drawTwo, "Il mazzo deve contenere almeno un +2");

        assertEquals(DRAW_TWO_AMOUNT, drawTwo.getDrawAmount(), "Il +2 deve avere drawAmount = 2");
        assertTrue(drawTwo.hasEffect(CardEffect.SKIP_NEXT), "Il +2 standard deve far saltare il turno");
    }

    @Test
    @DisplayName("CSV Parsing: I Jolly +4 devono avere DrawAmount = 4 e Effetti corretti")
    void testWildDrawFourPropertiesFromCSV() {
        Card wildFour = null;
        while (!deck.isEmpty()) {
            final Card c = deck.drawCard();
            if (c.getValue() == Values.WILD_DRAW_FOUR) {
                wildFour = c;
                break;
            }
        }

        assertNotNull(wildFour, "Il mazzo deve contenere Jolly +4");

        assertEquals(DRAW_FOUR_AMOUNT, wildFour.getDrawAmount(), "Il +4 deve avere drawAmount = 4");
        assertTrue(wildFour.hasEffect(CardEffect.CHANGE_COLOR), "Il +4 deve permettere il cambio colore");
        assertTrue(wildFour.hasEffect(CardEffect.SKIP_NEXT), "Il +4 deve far saltare il turno (regola standard)");
        assertTrue(wildFour.isNativeBlack(), "Il +4 deve essere nativamente nero");
    }

    @Test
    @DisplayName("Manual Creation: Posso creare una carta Evento 'Caos' personalizzata")
    void testCustomChaosCardCreation() {
        final Set<CardEffect> chaosEffects = EnumSet.of(CardEffect.REVERSE_TURN, CardEffect.ALWAYS_PLAYABLE);
        final Card chaosCard = new PrimusCard(Color.RED, Values.SEVEN, CHAOS_DRAW_AMOUNT, chaosEffects);

        assertEquals(CHAOS_DRAW_AMOUNT, chaosCard.getDrawAmount(), "La carta caos deve far pescare 10");
        assertTrue(chaosCard.hasEffect(CardEffect.REVERSE_TURN), "La carta caos deve invertire");
        assertTrue(chaosCard.hasEffect(CardEffect.ALWAYS_PLAYABLE), "La carta caos è sempre giocabile");
        assertFalse(chaosCard.hasEffect(CardEffect.SKIP_NEXT), "Non deve avere effetti non specificati");
    }

    @Test
    @DisplayName("Immutabilità Potenziata: withColor deve copiare anche Effetti e Potenza")
    void testWitherMaintainsDataDrivenProperties() {
        final Set<CardEffect> effects = EnumSet.of(CardEffect.CHANGE_COLOR, CardEffect.SKIP_NEXT);
        final Card original = new PrimusCard(Color.BLACK, Values.WILD_DRAW_FOUR, DRAW_FOUR_AMOUNT, effects);

        final Card blueCopy = original.withColor(Color.BLUE);

        assertNotSame(original, blueCopy);
        assertEquals(Color.BLUE, blueCopy.getColor());

        assertEquals(DRAW_FOUR_AMOUNT, blueCopy.getDrawAmount(), "La copia deve mantenere la potenza di pesca");
        assertTrue(blueCopy.hasEffect(CardEffect.SKIP_NEXT), "La copia deve mantenere gli effetti speciali");
        assertTrue(blueCopy.isNativeBlack(), "La copia deve ricordare di essere un Jolly");
    }
}
