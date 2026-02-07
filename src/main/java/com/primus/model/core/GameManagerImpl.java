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
import com.primus.utils.PlayerSetupData;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Objects;
// TODO aggiungere i veri import
// import com.primus.model.player.HumanPlayer;

/**
 * Implementation of {@link GameManager} to manage the game flow. It offers an API
 * to initialize the game, advance turns, execute player actions, and check for game completion to the
 * {@link com.primus.controller.GameController}
 */
public final class GameManagerImpl implements GameManager {
    private static final int CARD_NUMBER = 7;

    private final Map<Integer, Player> players;
    private final Sanctioner sanctioner;
    private final Validator validator;
    private Deck deck;
    private DropPile discardPile;
    private Scheduler scheduler;

    /**
     * Constructor initialises the game manager with necessary components.
     */
    public GameManagerImpl() {
        deck = new PrimusDeck();
        sanctioner = new SanctionerImpl();
        validator = new ValidatorImpl();
        players = new HashMap<>();
        init(); // Ensure the game is initialized upon creation
    }

    @Override
    public void init() {
        discardPile = new PrimusDropPile();
        deck = new PrimusDeck();
        deck.init();
        players.clear();
        sanctioner.reset();
        final BotFactory botFactory = new BotFactoryImpl();

        // TODO: creare il giocatore umano e poi fornirlo a bot Fallax

        players.put(1, botFactory.createFortuitus(1));
        players.put(2, botFactory.createImplacabilis(2));
        //this.players.add(botFactory.createFallax(3, HUMAN_PLAYER));

        // Create the scheduler by passing the players IDs to it
        scheduler = new SchedulerImpl(players.keySet());

        // Distribute cards
        for (final Player p : players.values()) {
            for (int i = 0; i < CARD_NUMBER; i++) {
                final Card c = drawDeckCard();
                p.addCards(List.of(c));
            }
        }

        // Draw the start card
        discardPile.addCard(drawDeckCard());
    }

    @Override
    public GameState getGameState() {
        return new GameState(discardPile.peek(), getActivePlayer().getHand(), scheduler.getCurrentPlayer());
    }

    @Override
    public List<PlayerSetupData> getGameSetup() {
        return players.values().stream()
                .map(p -> new PlayerSetupData(p.getId(), !p.isBot()))
                .toList();
    }

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "Player is a public API interface - intentional exposure"
    )
    @Override
    public Player nextPlayer() {
        return players.get(scheduler.nextPlayer());
    }

    @Override
    public boolean executeTurn(final Card card) {
        // If there's an active sanction, the player must resolve instead of playing a normal turn
        if (sanctioner.isActive()) {
            return handleMalus(getActivePlayer(), card);
        }

        // User chooses to draw a card
        if (card == null) {
            drawCardForPlayer(getActivePlayer());
            return true;
        }

        // User plays a card, so it must be validated
        if (!validator.isValidCard(discardPile.peek(), card)) {
            getActivePlayer().notifyMoveResult(card, false);
            return false;
        }

        // Confirm the move and apply effects
        getActivePlayer().notifyMoveResult(card, true);
        discardPile.addCard(card);

        applyCardEffects(card);

        return true;
    }

    @Override
    public Optional<Integer> getWinner() {
        return players.values().stream().filter(p -> p.getHand().isEmpty()).map(Player::getId).findFirst();
    }

    /**
     * @return the player whose turn it is, based on the scheduler's current player ID
     */
    private Player getActivePlayer() {
        return Objects.requireNonNull(players.get(scheduler.getCurrentPlayer()));
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
        if (deck.isEmpty()) {
            deck.refillFrom(discardPile);
        }
        return deck.drawCard();
    }

    /**
     * Draws a card from the deck and adds it to the player's hand.
     *
     * @param player the player drawing the card
     */
    private void drawCardForPlayer(final Player player) {
        final Card c = drawDeckCard();
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
