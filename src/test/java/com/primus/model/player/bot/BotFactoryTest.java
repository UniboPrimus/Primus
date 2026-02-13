package com.primus.model.player.bot;

import com.primus.model.player.HumanPlayer;
import com.primus.model.player.Player;
import com.primus.model.player.bot.strategy.card.AggressiveStrategy;
import com.primus.model.player.bot.strategy.card.CardStrategy;
import com.primus.model.player.bot.strategy.card.CheaterStrategy;
import com.primus.model.player.bot.strategy.card.RandomStrategy;
import com.primus.model.player.bot.strategy.color.MostFrequentColorStrategy;
import com.primus.model.player.bot.strategy.color.RandomColorStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BotFactoryTest {
    private static final String CARD_STRATEGY_FIELD = "cardStrategy";
    private static final String COLOR_STRATEGY_FIELD = "colorStrategy";

    private BotFactory factory;

    @BeforeEach
    void setUp() {
        factory = new BotFactoryImpl();
    }

    @Test
    void testCreateImplacabilis() throws NoSuchFieldException, IllegalAccessException {
        final int botId = 99;
        final Player player = factory.createImplacabilis(botId);
        assertTrue(player instanceof Bot, "Factory should return a Bot instance");
        final Bot bot = (Bot) player;
        final Field cardStrategyField = Bot.class.getDeclaredField(CARD_STRATEGY_FIELD);
        cardStrategyField.setAccessible(true); //NOPMD - Reflection required to verify internal dependency injection
        final CardStrategy actualCardStrategy = (CardStrategy) cardStrategyField.get(bot);
        assertTrue(actualCardStrategy instanceof AggressiveStrategy,
                "Implacabilis bot should have AggressiveStrategy injected");
        final Field colorStrategyField = Bot.class.getDeclaredField(COLOR_STRATEGY_FIELD);
        colorStrategyField.setAccessible(true); //NOPMD - Reflection required to verify internal dependency injection
        final Object actualColorStrategy = colorStrategyField.get(bot);
        assertTrue(actualColorStrategy instanceof MostFrequentColorStrategy,
                "Implacabilis bot should have MostFrequentColorStrategy injected");
    }

    @Test
    void testCreateFortuitus() throws NoSuchFieldException, IllegalAccessException {
        final int botId = 99;
        final Player player = factory.createFortuitus(botId);
        assertTrue(player instanceof Bot, "Factory should return a Bot instance for Fortuitus");
        final Bot bot = (Bot) player;
        final Field cardStrategyField = Bot.class.getDeclaredField(CARD_STRATEGY_FIELD);
        cardStrategyField.setAccessible(true); //NOPMD - Reflection required to verify internal dependency injection
        final CardStrategy actualCardStrategy = (CardStrategy) cardStrategyField.get(bot);
        assertTrue(actualCardStrategy instanceof RandomStrategy,
                "Fortuitus bot should have RandomStrategy injected");
        final Field colorStrategyField = Bot.class.getDeclaredField(COLOR_STRATEGY_FIELD);
        colorStrategyField.setAccessible(true); //NOPMD - Reflection required to verify internal dependency injection
        final Object actualColorStrategy = colorStrategyField.get(bot);
        assertTrue(actualColorStrategy instanceof RandomColorStrategy,
                "Fortuitus bot should have RandomColorStrategy injected");
    }

    @Test
    void testCreateFallax() throws NoSuchFieldException, IllegalAccessException {
        final int botId = 66;
        final Player victim = new HumanPlayer(1, "You");
        final Player player = factory.createFallax(botId, victim);
        assertTrue(player instanceof Bot, "Factory should return a Bot instance for Fallax");
        final Bot bot = (Bot) player;
        final Field cardStrategyField = Bot.class.getDeclaredField(CARD_STRATEGY_FIELD);
        cardStrategyField.setAccessible(true); //NOPMD - Reflection required to verify internal dependency injection
        final CardStrategy actualCardStrategy = (CardStrategy) cardStrategyField.get(bot);
        assertTrue(actualCardStrategy instanceof CheaterStrategy,
                "Fallax bot should have CheaterStrategy injected");
        final Field colorStrategyField = Bot.class.getDeclaredField(COLOR_STRATEGY_FIELD);
        colorStrategyField.setAccessible(true); //NOPMD - Reflection required to verify internal dependency injection
        final Object actualColorStrategy = colorStrategyField.get(bot);
        assertTrue(actualColorStrategy instanceof MostFrequentColorStrategy,
                "Fallax bot should have MostFrequentColorStrategy injected");
    }
}
