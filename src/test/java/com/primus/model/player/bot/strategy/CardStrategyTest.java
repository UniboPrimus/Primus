package com.primus.model.player.bot.strategy;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.deck.PrimusCard;
import com.primus.model.deck.Values;
import com.primus.model.player.bot.OpponentInfo;
import com.primus.model.player.bot.strategy.card.AggressiveStrategy;
import com.primus.model.player.bot.strategy.card.CardStrategy;
import com.primus.model.player.bot.strategy.card.CheaterStrategy;
import com.primus.model.player.bot.strategy.card.RandomStrategy;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardStrategyTest {

    private Card card(final Color c, final Values v) {
        return new PrimusCard(c, v);
    }

    @Test
    void testAggressiveStrategyPicksWildDrawFour() {
        final CardStrategy strategy = new AggressiveStrategy();
        final Card weakCard = card(Color.RED, Values.ONE);
        final Card strongCard = card(Color.BLACK, Values.WILD_DRAW_FOUR);
        final Card mediumCard = card(Color.BLACK, Values.WILD);
        final Optional<Card> result = strategy.chooseCard(List.of(weakCard, strongCard, mediumCard));
        assertTrue(result.isPresent());
        assertEquals(strongCard, result.get(), "Should choose Wild Draw Four (highest priority)");
    }

    @Test
    void testAggressiveStrategyPicksDrawTwoOverWild() {
        final CardStrategy strategy = new AggressiveStrategy();
        final Card wildCard = card(Color.BLACK, Values.WILD);
        final Card drawTwo = card(Color.BLUE, Values.DRAW_TWO);
        final Optional<Card> result = strategy.chooseCard(List.of(wildCard, drawTwo));
        assertTrue(result.isPresent());
        assertEquals(drawTwo, result.get(), "Should choose Draw Two over Wild");
    }

    @Test
    void testAggressiveStrategyStandard() {
        final CardStrategy strategy = new AggressiveStrategy();
        final Card c1 = card(Color.RED, Values.ONE);
        final Card c2 = card(Color.BLUE, Values.FIVE);
        final Optional<Card> result = strategy.chooseCard(List.of(c1, c2));
        assertTrue(result.isPresent());
        assertTrue(List.of(c1, c2).contains(result.get()));
    }

    @Test
    void testRandomStrategyReturnsEmptyOnEmptyList() {
        final CardStrategy strategy = new RandomStrategy();
        final Optional<Card> result = strategy.chooseCard(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void testRandomStrategyPicksCard() {
        final CardStrategy strategy = new RandomStrategy();
        final Card c1 = card(Color.RED, Values.ONE);
        final Optional<Card> result = strategy.chooseCard(List.of(c1));
        assertTrue(result.isPresent());
        assertEquals(c1, result.get());
    }

    private OpponentInfo getVictim(final List<Card> victimHand) {
        return new OpponentInfo() {
            private static final int ID = 99;

            @Override
            public List<Card> getHand() {
                return List.copyOf(victimHand);
            }

            @Override
            public int getCardCount() {
                return victimHand.size();
            }

            @Override
            public int getId() {
                return ID;
            }
        };
    }

    @Test
    void testCheaterStrategyAttacksUrgentVictim() {
        // Scenario: Victim has only 2 cards (Urgent state)
        final OpponentInfo urgentVictim = getVictim(List.of(card(Color.RED, Values.ONE), card(Color.BLUE, Values.TWO)));
        final CardStrategy strategy = new CheaterStrategy(urgentVictim);
        final Card normalCard = card(Color.GREEN, Values.FIVE);
        final Card attackCard = card(Color.BLACK, Values.WILD_DRAW_FOUR);
        final Optional<Card> result = strategy.chooseCard(List.of(normalCard, attackCard));
        assertTrue(result.isPresent());
        assertEquals(attackCard, result.get(), "Cheater should use Wild Draw 4 to stop winning opponent");
    }

    @Test
    void testCheaterStrategyAvoidsDefendableMove() {
        // Scenario: Victim has a WILD_DRAW_FOUR in hand
        final OpponentInfo smartVictim = getVictim(List.of(card(Color.BLACK, Values.WILD_DRAW_FOUR)));
        final CardStrategy strategy = new CheaterStrategy(smartVictim);
        final Card defendableAttack = card(Color.BLACK, Values.WILD_DRAW_FOUR);
        final Card safeCard = card(Color.RED, Values.FIVE);
        final Optional<Card> result = strategy.chooseCard(List.of(defendableAttack, safeCard));
        assertTrue(result.isPresent());
        assertEquals(safeCard, result.get(), "Cheater should avoid playing Wild Draw 4 if victim has one too");
    }

    @Test
    void testCheaterStrategyChangesColorToStarveVictim() {
        // Scenario: Victim has many RED cards.
        final OpponentInfo redHeavyVictim = getVictim(List.of(
                card(Color.RED, Values.ONE),
                card(Color.RED, Values.TWO),
                card(Color.RED, Values.SKIP)
        ));
        final CardStrategy strategy = new CheaterStrategy(redHeavyVictim);
        final Card redCard = card(Color.RED, Values.FIVE);
        final Card blueCard = card(Color.BLUE, Values.FIVE);
        final Optional<Card> result = strategy.chooseCard(List.of(redCard, blueCard));
        assertTrue(result.isPresent());
        assertEquals(blueCard, result.get(), "Cheater should switch to Blue purely because victim has many Reds");
    }
}
