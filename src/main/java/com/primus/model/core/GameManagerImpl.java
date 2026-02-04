package com.primus.model.core;

import com.primus.model.deck.Card;
import com.primus.model.deck.Deck;
import com.primus.model.deck.DropPile;
import com.primus.model.player.Player;
import com.primus.model.player.bot.BotFactory;
import com.primus.model.rules.Sanctioner;
import com.primus.model.rules.Validator;
import com.primus.model.deck.PrimusDropPile;
import com.primus.model.deck.PrimusDeck;
import com.primus.model.player.bot.BotFactoryImpl;
import com.primus.model.rules.SanctionerImpl;
import com.primus.model.rules.ValidatorImpl;
import com.primus.utils.GameState;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
// TODO aggiungere i veri import
// import com.primus.model.player.HumanPlayer;

/**
 * Implementation of {@link GameManager} to manage the game flow. It offers an API
 * to initialize the game, advance turns, execute player actions, and check for game completion to the
 * {@link com.primus.controller.GameController}
 */
public final class GameManagerImpl implements GameManager {
    private static final int CARD_NUMBER = 7;

    private final List<Player> players;
    private final Sanctioner sanctioner;
    private final Validator validator;
    private Deck deck;
    private DropPile discardPile;
    private Scheduler scheduler;
    private Player activePlayer;

    /**
     * Constructor initializes the game manager with necessary components.
     */
    public GameManagerImpl() {
        this.deck = new PrimusDeck();
        this.sanctioner = new SanctionerImpl();
        this.validator = new ValidatorImpl();
        this.players = new ArrayList<>();
        this.init(); // Ensure the game is initialized upon creation
    }

    @Override
    public void init() {
        this.discardPile = new PrimusDropPile();
        this.deck = new PrimusDeck();
        this.deck.init();
        this.players.clear();
        this.activePlayer = null;
        this.sanctioner.reset();
        final BotFactory botFactory = new BotFactoryImpl();

        // TODO: creare il giocatore umano e poi fornirlo a bot Fallax

        this.players.add(botFactory.createFortuitus(1));
        this.players.add(botFactory.createImplacabilis(2));
        //this.players.add(botFactory.createFallax(3, HUMAN_PLAYER));

        // Create the scheduler by passing the players to it
        this.scheduler = new SchedulerImpl(this.players);

        // Distribute cards
        for (final Player p : this.players) {
            for (int i = 0; i < CARD_NUMBER; i++) {
                final Card c = this.drawDeckCard();
                p.addCards(List.of(c));
            }
        }

        // Draw the start card
        this.discardPile.addCard(this.drawDeckCard());
    }

    @Override
    public GameState getGameState() {
        if (this.activePlayer == null) {
            return new GameState(this.discardPile.peek(), scheduler.peekNextPlayer().getHand());
        }
        return new GameState(this.discardPile.peek(), this.activePlayer.getHand());
    }

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "Player is a public API interface - intentional exposure"
    )
    @Override
    public Player nextPlayer() {
        this.activePlayer = scheduler.nextPlayer();
        return this.activePlayer;
    }

    @Override
    public boolean executeTurn(final Card card) {

        // If there's an active sanction, the player must resolve instead of playing a normal turn
        if (sanctioner.isActive()) {
            return handleMalus(this.activePlayer, card);
        }

        // User chooses to draw a card
        if (card == null) {
            drawCardForPlayer(this.activePlayer);
            return true;
        }

        // User plays a card, so it must be validated
        if (!validator.isValidCard(discardPile.peek(), card)) {
            this.activePlayer.notifyMoveResult(card, false);
            return false;
        }

        // Confirm the move and apply effects
        this.activePlayer.notifyMoveResult(card, true);
        this.discardPile.addCard(card);

        applyCardEffects(card);

        return true;
    }

    @Override
    public Optional<Player> getWinner() {
        for (final Player p : players) {
            if (p.getHand().isEmpty()) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    /**
     * Handles the situation where a player is under a sanction (malus). The player can either
     * <ul>
     *  <li>defend against the sanction by playing a valid {@link Card}</li>
     *  <li>accept the sanction by drawing the required number of {@link Card}</li>
     * </ul>
     * Trying to play an invalid card will result in a failed attempt.
     *
     * @param player the player whose turn it is
     * @param card   the card played by the player to defend against the sanction, or null if drawing
     * @return {@code true} if player successfully defends or accepts the malus
     */
    private boolean handleMalus(final Player player, final Card card) {
        Objects.requireNonNull(player);

        // Player chooses not to defend (probably he couldn't)
        if (card == null) {
            final int amount = sanctioner.getMalusAmount();

            // Apply malus
            for (int i = 0; i < amount; i++) {
                drawCardForPlayer(player);
            }
            sanctioner.reset();

            return true;
        }

        // Player is defending against an active sanction
        if (sanctioner.isActive() && validator.isValidDefense(discardPile.peek(), card)) {
            player.notifyMoveResult(card, true);
            discardPile.addCard(card);
            applyCardEffects(card);
            return true;
        }

        // Invalid defense attempt
        player.notifyMoveResult(card, false);
        return false;
    }

    /**
     * Draws a card from the deck, refilling it from the discard pile if necessary.
     *
     * @return the drawn card
     */
    private Card drawDeckCard() {
        if (this.deck.isEmpty()) {
            this.deck.refillFrom(this.discardPile);
        }
        return this.deck.drawCard();
    }

    /**
     * Draws a card from the deck and adds it to the player's hand.
     *
     * @param player the player drawing the card
     */
    private void drawCardForPlayer(final Player player) {
        final Card c = this.drawDeckCard();
        if (c != null) {
            player.addCards(List.of(c));
        }
    }

    /**
     * Applies the effects of the played card to the game state.
     *
     * @param card the card whose effects are to be applied
     */
    private void applyCardEffects(final Card card) {
        Objects.requireNonNull(card);

        switch (card.getValue()) {
            case SKIP -> scheduler.skipTurn();
            case REVERSE -> scheduler.reverseDirection();
            default -> {
            }
        }

        // Accumulate sanctions if the card has any effect that triggers them (e.g., Draw Two, Wild Draw Four)
        sanctioner.accumulate(card);
    }
}
