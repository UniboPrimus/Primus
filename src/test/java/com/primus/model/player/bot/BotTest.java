package com.primus.model.player.bot;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.deck.PrimusCard;
import com.primus.model.deck.Values;
import com.primus.model.player.bot.strategy.card.RandomStrategy;
import com.primus.model.player.bot.strategy.color.RandomColorStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BotTest {

    private Bot bot;

    private Card card(final Color c, final Values v) {
        return new PrimusCard(c, v);
    }

    @BeforeEach
    void setUp() {
        bot = new Bot(1, "Test", possibleCards -> possibleCards.stream().findFirst(), hand -> Color.RED);
    }

    @Test
    void testIsBot() {
        assertTrue(bot.isBot(), "Bot should return true for isBot()");
    }

    @Test
    void testEquals() {
        final Bot temp = new Bot(1, "Test", new RandomStrategy(), new RandomColorStrategy());
        assertEquals(temp, bot, "Bot with same id should be equals ");
    }

    @Test
    void testAddCards() {
        final Card card1 = card(Color.BLUE, Values.ONE);
        final Card card2 = card(Color.RED, Values.ONE);
        final List<Card> cards = List.of(card1, card2);
        bot.addCards(cards);
        assertEquals(2, bot.getHand().size(), "Hand should contain 2 cards");
        assertTrue(bot.getHand().containsAll(cards), "Hand should contain the added cards");
    }

    @Test
    void testPlayCard() {
        final Card card = card(Color.BLUE, Values.ONE);
        bot.addCards(List.of(card));
        final Optional<Card> playedCard = bot.playCard();
        assertTrue(playedCard.isPresent(), "Bot should play a card when available");
        assertEquals(card, playedCard.get(), "Bot should play the card in hand");
    }

    @Test
    void testColorStrategy() {
        final Card wildCard = card(Color.BLACK, Values.WILD);
        bot.addCards(List.of(wildCard));
        final Optional<Card> playedCard = bot.playCard();
        assertTrue(playedCard.isPresent(), "Bot should play the wild card");
        assertEquals(Color.RED, playedCard.get().getColor(), "Bot should apply color strategy (RED) to wild card");
    }

    @Test
    void testPlayCardPassTurn() {
        final Optional<Card> playedCard = bot.playCard();
        assertTrue(playedCard.isEmpty(), "Bot should pass turn (empty optional) when no moves available");
    }

    @Test
    void testNotifyMoveResultAccepted() {
        final Card card = card(Color.GREEN, Values.ONE);
        bot.addCards(List.of(card));
        bot.notifyMoveResult(card, true);
        assertTrue(bot.getHand().isEmpty(), "Card should be removed from hand after valid move");
    }

    @Test
    void testNotifyMoveResultRefused() {
        final Card card = card(Color.GREEN, Values.ONE);
        bot.addCards(List.of(card));
        bot.notifyMoveResult(card, false);
        assertEquals(1, bot.getHand().size(), "Card should remain in hand after invalid move");
        assertTrue(bot.getHand().contains(card));
    }

    @Test
    void notifyMoveResultShouldAddCardToRejectedCardsIfInvalid() {
        final Card card = card(Color.BLUE, Values.ONE);
        bot.addCards(List.of(card));
        bot.notifyMoveResult(card, false);
        final var playedCard = bot.playCard();
        assertTrue(playedCard.isEmpty(), "Rejected card should not be chosen again");
    }

    @Test
    void testNotifyMoveResultException() {
        final Card card = card(Color.YELLOW, Values.ONE);
        assertThrows(IllegalStateException.class, () ->
                        bot.notifyMoveResult(card, true),
                "Should throw exception if validated card is not in hand");
    }

    @Test
    void testGetHand() {
        final Card card = card(Color.RED, Values.ONE);
        bot.addCards(List.of(card));
        final List<Card> hand = bot.getHand();
        assertThrows(UnsupportedOperationException.class, () ->
                        hand.add(card(Color.BLUE, Values.ONE)),
                "getHand should return an unmodifiable list");
    }

    @Test
    void testPlayCardShouldNotReplayRejectedWildCard() {
        final Card wildCard = card(Color.BLACK, Values.WILD);
        bot.addCards(List.of(wildCard));
        final Optional<Card> playedCard = bot.playCard();
        assertTrue(playedCard.isPresent(), "Bot should play the wild card");
        bot.notifyMoveResult(playedCard.get(), false);
        assertEquals(1, bot.getHand().size(), "Wild card should remain in hand after rejection");
        assertTrue(bot.playCard().isEmpty(), "Rejected wild card should not be played again");
    }
}
