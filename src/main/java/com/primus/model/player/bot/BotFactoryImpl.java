package com.primus.model.player.bot;

import com.primus.model.player.Player;
import com.primus.model.player.bot.strategy.card.AggressiveStrategy;
import com.primus.model.player.bot.strategy.card.CheaterStrategy;
import com.primus.model.player.bot.strategy.card.RandomStrategy;
import com.primus.model.player.bot.strategy.color.MostFrequentColorStrategy;
import com.primus.model.player.bot.strategy.color.RandomColorStrategy;

import java.util.Objects;

/**
 * Concrete implementation of the {@link BotFactory} interface.
 */
public final class BotFactoryImpl implements BotFactory {

    /**
     * Creates a new instance of the BotFactoryImpl.
     */
    public BotFactoryImpl() {
        // Default constructor intentionally empty
    }

    /**
     * {@inheritDoc}
     * Implementation: Uses {@link RandomStrategy} and {@link RandomColorStrategy}.
     */
    @Override
    public Player createFortuitus(final int id) {
        return new Bot(id, "Fortuitus", new RandomStrategy(), new RandomColorStrategy());
    }

    /**
     * {@inheritDoc}
     * Implementation: Uses {@link AggressiveStrategy} and {@link MostFrequentColorStrategy}.
     */
    @Override
    public Player createImplacabilis(final int id) {
        return new Bot(id, "Implacabilis", new AggressiveStrategy(), new MostFrequentColorStrategy());
    }

    /**
     * {@inheritDoc}
     * Implementation: Uses {@link CheaterStrategy} and {@link MostFrequentColorStrategy}.
     *
     * @throws NullPointerException if the victim is null.
     */
    @Override
    public Player createFallax(final int id, final Player victim) {
        Objects.requireNonNull(victim, "Victim player cannot be null for Fallax bot");
        return new Bot(id, "Fallax", new CheaterStrategy(new OpponentInfoImpl(victim)), new MostFrequentColorStrategy());
    }
}
