package com.primus.model.player.bot;

import model.player.bot.Bot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BotTest {

    private Bot bot;

    @BeforeEach
    void setUp() {
        bot = new Bot();
    }

    @Test
    void testIsBot() {
        assertTrue(bot.isBot(), "Bot should return true for isBot()");
    }
}
