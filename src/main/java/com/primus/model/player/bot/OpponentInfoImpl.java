package com.primus.model.player.bot;

import com.primus.model.deck.Card;
import com.primus.model.player.Player;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.Objects;

/**
 * Basic implementation of the {@link OpponentInfo} interface.
 * It acts as a read-only wrapper around a {@link Player} instance.
 */
public final class OpponentInfoImpl implements OpponentInfo {

    private final Player player;

    /**
     * Constructs a new info wrapper for a specific player.
     *
     * @param player the real player instance to wrap.
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Intentional design: This class acts as a wrapper for the Player object. "
                    + "It must store the reference to the live mutable object to reflect "
                    + "real-time game state changes (e.g. card counts)."
    )
    public OpponentInfoImpl(final Player player) {
        Objects.requireNonNull(player, "Player to wrap cannot be null");
        this.player = player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return player.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> getHand() {
        return List.copyOf(player.getHand());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCardCount() {
        return getHand().size();
    }
}
