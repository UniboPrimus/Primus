package com.primus.model.player.bot;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.player.Player;
import com.primus.model.player.bot.strategy.card.CardStrategy;
import com.primus.model.player.bot.strategy.color.ColorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a bot player in the game.
 * The bot implements the {@link Player} interface and provides its own behavior for playing cards,
 * passing turns, and managing its hand based on injected strategies.
 */
public final class Bot implements Player {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private final int id;
    private final List<Card> hand = new ArrayList<>();
    private final Set<Card> rejectedCards = new LinkedHashSet<>();
    private final CardStrategy cardStrategy;
    private final ColorStrategy colorStrategy;

    /**
     * Constructs a new Bot with specific strategies for card selection and color decision.
     *
     * @param id            the unique identifier.
     * @param cardStrategy  the logic to select cards.
     * @param colorStrategy the logic to select colors for Wild cards.
     * @throws NullPointerException if any strategy is null.
     */
    public Bot(final int id, final CardStrategy cardStrategy, final ColorStrategy colorStrategy) {
        this.id = id;
        this.cardStrategy = Objects.requireNonNull(cardStrategy);
        this.colorStrategy = Objects.requireNonNull(colorStrategy);
    }

    /**
     * {@inheritDoc}
     * The bot uses the CardStrategy to pick a move.
     * If a Wild card is selected, the ColorStrategy determines the new color.
     */
    @Override
    public Optional<Card> playCard() {
        LOGGER.debug("Bot: {} is starting turn. Current hand: {}", id, hand);
        // The card strategy pick a card among possible moves
        final Optional<Card> chosenOpt = cardStrategy.chooseCard(calculatePossibleMoves());
        if (chosenOpt.isPresent()) {
            final Card card = chosenOpt.get();
            LOGGER.info("{} decided to play: {}", id, card);
            // if the selected card is a black card decide its new color using color strategy and return it
            if (card.isNativeBlack()) {
                final Color chosenColor = colorStrategy.chooseColor(getHand());
                LOGGER.info("{} selected Wild color: {}", id, chosenColor);
                return Optional.of(card.withColor(chosenColor));
            }
        } else { //pass turn
            LOGGER.info("{} has no valid moves and PASSES the turn.", id);
            rejectedCards.clear();
        }
        return chosenOpt;
    }

    /**
     * Filters the hand excluding cards that have already been rejected during this turn.
     *
     * @return a list of potential candidates for the move.
     */
    private List<Card> calculatePossibleMoves() {
        return hand.stream()
                .filter(card -> !rejectedCards.contains(card))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBot() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> getHand() {
        return List.copyOf(hand);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the list is null.
     */
    @Override
    public void addCards(final List<Card> cards) {
        Objects.requireNonNull(cards);
        hand.addAll(cards);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     * Updates the internal state. If valid, the card is removed from hand.
     * If invalid, it is added to the rejected set to avoid retrying it immediately.
     *
     * @throws NullPointerException  if cardPlayed is null.
     * @throws IllegalStateException if a validated card is not found in hand.
     */
    @Override
    public void notifyMoveResult(final Card cardPlayed, final boolean valid) {
        Objects.requireNonNull(cardPlayed);
        /* If it's a Wild the bot holds it as BLACK.
             else use the card as is. */
        final Card cardInHand;
        if (cardPlayed.isNativeBlack()) {
            cardInHand = cardPlayed.withColor(Color.BLACK);
        } else {
            cardInHand = cardPlayed;
        }
        if (!hand.contains(cardInHand)) {
            throw new IllegalStateException("The card validated is not present in the hand: " + cardPlayed);
        }
        if (!valid) {
            LOGGER.info("Move refused for Bot {}: {}", id, cardPlayed);
            rejectedCards.add(cardInHand);
        } else { // If the card is valid, remove the first occurrence from the hand and end the turn
            LOGGER.debug("Move accepted for Bot {}", id);
            hand.remove(cardInHand);
            rejectedCards.clear();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Bot bot = (Bot) o;
        return id == bot.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Bot{"
                + "id=" + id
                + ", hand=" + hand
                + ", rejectedCards=" + rejectedCards
                + ", card strategy=" + cardStrategy
                + ", color strategy=" + colorStrategy
                + '}';
    }
}
