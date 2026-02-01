package com.primus.model.player.bot;

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
public final class RandomStrategy implements BotStrategy {
    private final Random random = new Random();

    /**
     * Chooses a card randomly from the list of possible cards.
     *
     * @param possibleCards the list of cards the bot can choose from
     * @return a randomly selected card from the list
     * @throws IllegalArgumentException if the list of possible cards is empty
     */
    @Override
    public Optional<Card> chooseCard(final List<Card> possibleCards) {
        Objects.requireNonNull(possibleCards);
        if (possibleCards.isEmpty()) {
            return Optional.empty();
        }
        final Card selectedCard = possibleCards.get(random.nextInt(0, possibleCards.size()));
        return selectedCard == null ? Optional.empty() : Optional.of(selectedCard);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
