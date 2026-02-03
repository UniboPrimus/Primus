package com.primus.model.player.bot;

import com.primus.model.player.Player;

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
        return new Bot(id, new AggressiveStrategy(), new RandomColorStrategy());
    }

    @Override
    public Player createFallax(final int id, final Player victim) {
        Objects.requireNonNull(victim);
        return new Bot(id, new CheaterStrategy(new OpponentInfoImpl(victim)), new RandomColorStrategy());
    }
}
