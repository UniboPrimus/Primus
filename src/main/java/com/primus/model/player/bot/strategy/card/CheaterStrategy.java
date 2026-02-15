package com.primus.model.player.bot.strategy.card;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.deck.Values;
import com.primus.model.player.bot.OpponentInfo;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An advanced implementation of {@link CardStrategy} representing a "cheater" personality.
 * This strategy has access to an opponent's information via {@link OpponentInfo},
 * allowing it to make decisions based on the opponent's current hand.
 */
public final class CheaterStrategy implements CardStrategy {
    private static final long SCORE_SKIP_REVERSE = 10L;
    private static final long SCORE_DRAW_TWO = 15L;
    private static final long SCORE_WILD = 25L;
    private static final long SCORE_DEFENDABLE_MOVE = -1_000_000L;
    private static final long SCORE_NORMAL_CARD = 1L;
    private final OpponentInfo victim;

    /**
     * Constructs a CheaterStrategy targeting a specific opponent.
     *
     * @param victim the {@link OpponentInfo} describing the opponent to target.
     * @throws NullPointerException if {@code victim} is {@code null}.
     */
    public CheaterStrategy(final OpponentInfo victim) {
        Objects.requireNonNull(victim, "Victim info cannot be null");
        this.victim = victim;
    }

    /**
     * {@inheritDoc}
     * Analyzes the victim's hand and selects the card that maximizes the damage
     * or minimizes the victim's chance to close the game.
     *
     * @throws NullPointerException if {@code possibleCard} is {@code null}.
     */
    @Override
    public Optional<Card> chooseCard(final List<Card> possibleCards) {
        Objects.requireNonNull(possibleCards);
        final VictimAnalysis victimAnalysis = analyzeVictimHand();
        return possibleCards.stream()
                .max(Comparator.comparingLong(card -> calculateScore(card, victimAnalysis)));
    }

    private long calculateScore(final Card card, final VictimAnalysis victimAnalysis) {
        if (card.isNativeBlack()) {
            return calculateBlackCardScore(card, victimAnalysis);
        }
        if (card.getValue() == Values.DRAW_TWO) {
            return calculateDrawTwoScore(victimAnalysis);
        }
        if (card.getValue() == Values.SKIP || card.getValue() == Values.REVERSE) {
            return SCORE_SKIP_REVERSE;
        }
        return calculateColorCardScore(card, victimAnalysis);
    }

    private long calculateBlackCardScore(final Card card, final VictimAnalysis victimAnalysis) {
        if (card.getValue() == Values.WILD_DRAW_FOUR) {
            if (victimAnalysis.hasCard(Values.WILD_DRAW_FOUR)) {
                return SCORE_DEFENDABLE_MOVE;
            }
            return SCORE_WILD * calculateUrgencyMultiplier(victimAnalysis);
        }
        return SCORE_WILD;
    }

    private long calculateDrawTwoScore(final VictimAnalysis victimAnalysis) {
        if (victimAnalysis.hasCard(Values.DRAW_TWO)) {
            return SCORE_DEFENDABLE_MOVE;
        }
        return SCORE_DRAW_TWO * calculateUrgencyMultiplier(victimAnalysis);
    }

    private long calculateUrgencyMultiplier(final VictimAnalysis victimAnalysis) {
        return Math.max(1, 10 - victimAnalysis.totalCards());
    }

    private long calculateColorCardScore(final Card card, final VictimAnalysis victimAnalysis) {
        final int victimColorCount = victimAnalysis.getColorCount(card.getColor());
        return Math.max(0, SCORE_NORMAL_CARD * (victimAnalysis.totalCards() - victimColorCount));
    }

    private VictimAnalysis analyzeVictimHand() {
        final List<Card> hand = victim.getHand();
        final Map<Color, Integer> colorMap = new EnumMap<>(Color.class);
        final Map<Values, Integer> valuesMap = new EnumMap<>(Values.class);
        for (final Card card : hand) {
            if (!card.isNativeBlack()) {
                colorMap.put(card.getColor(), colorMap.getOrDefault(card.getColor(), 0) + 1);
            }
            valuesMap.put(card.getValue(), valuesMap.getOrDefault(card.getValue(), 0) + 1);
        }
        return new VictimAnalysis(colorMap, valuesMap, victim.getCardCount());
    }

    private record VictimAnalysis(
            Map<Color, Integer> colorCount,
            Map<Values, Integer> cardCount,
            int totalCards
    ) {
        boolean hasCard(final Values value) {
            return cardCount.containsKey(value);
        }

        int getColorCount(final Color color) {
            return colorCount.getOrDefault(color, 0);
        }
    }
}
