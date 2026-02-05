package com.primus.model.player.bot;

import com.primus.model.player.Player;
import com.primus.model.player.bot.strategy.card.AggressiveStrategy;
import com.primus.model.player.bot.strategy.card.CardStrategy;
import com.primus.model.player.bot.strategy.card.CheaterStrategy;
import com.primus.model.player.bot.strategy.card.RandomStrategy;
import com.primus.model.player.bot.strategy.color.MostFrequentColorStrategy;
import com.primus.model.player.bot.strategy.color.RandomColorStrategy;

import java.util.Objects;

/**
 * Concrete implementation of the {@link BotFactory} interface.
 * Responsible for instantiating {@link Bot} objects and injecting the appropriate
 * {@link CardStrategy} and unique identifier into them.
 */
public final class BotFactoryImpl implements BotFactory {

    @Override
    public Player createFortuitus(final int id) {
        return new Bot(id, new RandomStrategy(), new RandomColorStrategy());
    }

    @Override
    public Player createImplacabilis(final int id) {
        return new Bot(id, new AggressiveStrategy(), new MostFrequentColorStrategy());
    }

    @Override
    public Player createFallax(final int id, final Player victim) {
        Objects.requireNonNull(victim);
        return new Bot(id, new CheaterStrategy(new OpponentInfoImpl(victim)), new MostFrequentColorStrategy());
    }
}
