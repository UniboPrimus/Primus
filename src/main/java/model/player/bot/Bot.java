package model.player.bot;

import model.deck.Card;
import model.player.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a bot player in the game. The bot implements the {@link Player} interface
 * and provides its own behavior for playing cards, passing turns, and managing its hand.
 */

public final class Bot implements Player {

    private final int id;
    private final List<Card> hand = new ArrayList<>();
    private final Set<Card> rejectedCards = new LinkedHashSet<>();
    private Card currentTriedCard;
    private final BotStrategy strategy;

    /**
     * Constructs a new Bot instance with a unique identifier and the relate strategy.
     *
     * @param id the unique identifier for this bot
     * @param strategy the algorithm used fot this bot
     */
    public Bot(final int id, final BotStrategy strategy) {
        this.id = id;
        this.strategy = strategy;
    }

    @Override
    public Card playCard() {
        currentTriedCard = strategy.chooseCard(hand.stream().filter(card -> !rejectedCards.contains(card)).toList());
        return currentTriedCard;
    }

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    public List<Card> getHand() {
        return List.copyOf(hand);
    }

    @Override
    public void addCards(final List<Card> cards) {
        hand.addAll(cards);
    }

    @Override
    public boolean passTurn() {
        return false;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void notifyMoveResult(final boolean valid) {
        if (!valid) {
            rejectedCards.add(currentTriedCard);
        } else {
            resetTurn();
        }
    }

    private void resetTurn() {
        hand.remove(currentTriedCard);
        currentTriedCard = null;
        rejectedCards.clear();
    }
}
