package com.primus.model.player.bot;

import com.primus.model.player.Player;

/**
 * Factory interface for creating different types of Bot players.
 * This interface abstracts the creation logic for bots, allowing the client
 * to instantiate bots with specific strategies (Random, Aggressive, Cheater)
 * without knowing the implementation details of the strategies themselves.
 */
public interface BotFactory {

    /**
     * Creates a "Fortuitus" bot initialized with a random strategy.
     * This bot selects valid cards to play purely at random, making its behavior unpredictable.
     *
     * @param id the unique identifier to assign to the new bot
     * @return a new {@link Player} instance configured as a Fortuitus bot
     */
    Player createFortuitus(int id);

    /**
     * Creates an "Implacabilis" bot initialized with an aggressive strategy.
     * This bot prioritizes moves that penalize opponents.
     *
     * @param id the unique identifier to assign to the new bot
     * @return a new {@link Player} instance configured as an Implacabilis bot
     */
    Player createImplacabilis(int id);

    /**
     * Creates a "Fallax" bot initialized with a cheating strategy.
     * This bot requires a specific victim to spy on. It uses information about the
     * victim's hand to make calculated decisions.
     *
     * @param id     the unique identifier to assign to the new bot
     * @param victim the {@link Player} whose hand will be exposed to this bot via {@link OpponentInfo}
     * @return a new {@link Player} instance configured as a Fallax bot
     */
    Player createFallax(int id, Player victim);
}
