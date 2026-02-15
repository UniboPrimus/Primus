package com.primus.model.player.bot.strategy.card;

import com.primus.model.deck.Card;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * A strategy implementation for a bot that selects a card to play at random.
 * This strategy does not consider any specific game logic or constraints
 * and simply picks a card randomly from the provided list of possible cards.
 */
public final class RandomStrategy implements CardStrategy {
    private final Random random = new Random();

    /**
     * Creates a new instance of the RandomStrategy.
     */
    public RandomStrategy() {
        // Default constructor intentionally empty
    }

    /**
     * Chooses a card randomly from the list of possible cards.
     *
     * @param possibleCards the list of cards the bot can choose from.
     * @return an {@link Optional} containing a randomly selected card,
     *      or {@code Optional.empty()} if the list is empty.
     * @throws NullPointerException if {@code possibleCard} is {@code null}.
     */
    @Override
    public Optional<Card> chooseCard(final List<Card> possibleCards) {
        Objects.requireNonNull(possibleCards);
        if (possibleCards.isEmpty()) {
            return Optional.empty();
        }
        final Card selectedCard = possibleCards.get(random.nextInt(possibleCards.size()));
        return selectedCard == null ? Optional.empty() : Optional.of(selectedCard);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
