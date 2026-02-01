package com.primus.model.player.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BotTest {

    private Bot bot;

    @BeforeEach
    void setUp() {
        bot = new Bot(1, new RandomStrategy());
    }

    @Test
    void testIsBot() {
        assertTrue(bot.isBot(), "Bot should return true for isBot()");
    }

    @Test
    void testEquals() {
        final Bot temp = new Bot(1, new RandomStrategy());
        assertEquals(temp, bot, "Bot with same id must be equals ");
    }
}
