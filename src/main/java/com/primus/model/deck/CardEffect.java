package com.primus.model.deck;

/**
 * Enumeration representing special effects or capabilities a card can possess.
 * This allows defining card behaviors dynamically via configuration (CSV).
 */
public enum CardEffect {
    /**
     * Reverses the direction of play.
     */
    REVERSE_TURN,
    /**
     * Skips the next player's turn.
     */
    SKIP_NEXT,
    /**
     * Allows the player to choose a new color (Wild card effect).
     */
    CHANGE_COLOR,
    /**
     * Indicates the card can always be played, ignoring standard matching rules.
     */
    ALWAYS_PLAYABLE,
}
