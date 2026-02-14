package com.primus.model.core;

import com.primus.model.deck.Card;
import com.primus.model.deck.CardEffect;
import com.primus.model.deck.Deck;
import com.primus.model.deck.DropPile;
import com.primus.model.deck.GameEvent;
import com.primus.model.deck.PrimusDeck;
import com.primus.model.deck.PrimusDropPile;
import com.primus.model.player.Player;
import com.primus.model.player.bot.BotFactory;
import com.primus.model.player.HumanPlayer;
import com.primus.model.rules.Sanctioner;
import com.primus.model.rules.SanctionerImpl;
import com.primus.model.rules.Scheduler;
import com.primus.model.rules.SchedulerImpl;
import com.primus.model.rules.Validator;
import com.primus.model.rules.ValidatorImpl;
import com.primus.model.player.bot.BotFactoryImpl;
import com.primus.utils.GameState;
import com.primus.utils.PlayerSetupData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Objects;

/**
 * Implementation of {@link GameManager} to manage the game flow. It offers an API
 * to initialize the game, advance turns, execute player actions, and check for game completion to the
 * {@link com.primus.controller.GameController}
 */
public final class GameManagerImpl implements GameManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameManagerImpl.class);
    private static final int CARD_NUMBER = 7;

    private final Map<Integer, Player> players;
    private final Sanctioner sanctioner;
    private final Validator validator;
    private Deck deck;
    private DropPile discardPile;
    private Scheduler scheduler;
    private boolean isInitialized;
    private GameEvent currentEvent; //NOPMD - This field is necessary to keep track of the current game event

    /**
     * Constructor initialises the game manager with necessary components.
     */
    public GameManagerImpl() {
        deck = new PrimusDeck();
        sanctioner = new SanctionerImpl();
        validator = new ValidatorImpl();
        players = new HashMap<>();
    }

    @Override
    public void init() {
        LOGGER.info("Initializing Game Manager");

        isInitialized = true;

        currentEvent = GameEvent.getRandomEvent();
        LOGGER.info("Selected Game Event: {} - {}", currentEvent, currentEvent.getDescription());

        discardPile = new PrimusDropPile();
        final PrimusDeck primusDeck = new PrimusDeck();
        primusDeck.setGameEvent(this.currentEvent);
        primusDeck.init();
        this.deck = primusDeck;
        players.clear();
        sanctioner.reset();
        final BotFactory botFactory = new BotFactoryImpl();

        // Create players and add them to the map using their own ID as key
        final Player humanPlayer = new HumanPlayer(1, "You");
        players.put(humanPlayer.getId(), humanPlayer);

        final Player bot1 = botFactory.createFortuitus(2);
        players.put(bot1.getId(), bot1);

        final Player bot2 = botFactory.createImplacabilis(3);
        players.put(bot2.getId(), bot2);

        final Player bot3 = botFactory.createFallax(4, humanPlayer);
        players.put(bot3.getId(), bot3);

        LOGGER.info("Players created: {}", players.keySet());

        // Create the scheduler by passing the players IDs to it
        scheduler = new SchedulerImpl(players.keySet());

        // Distribute cards
        LOGGER.debug("Distributing {} cards to each player", CARD_NUMBER);
        for (final Player p : players.values()) {
            for (int i = 0; i < CARD_NUMBER; i++) {
                final Card c = drawDeckCard();
                if (c != null) {
                    p.addCards(List.of(c));
                }
            }
        }

        // Draw the start card
        final Card startCard = deck.drawStartCard();
        discardPile.addCard(startCard);
        LOGGER.info("Game initialized. Start card: {}", startCard);
    }

    @Override
    public GameState getGameState() {
        ensureInitialized();
        final Map<Integer, Integer> cardCounts = new HashMap<>();

        players.values().forEach(player -> cardCounts.put(player.getId(), player.getHand().size()));

        final List<Card> humanCards = players.values().stream()
                .filter(p -> !p.isBot())
                .findFirst()
                .map(Player::getHand)
                .orElse(List.of());

        return new GameState(
                discardPile.peek(),
                humanCards,
                cardCounts,
                scheduler.getCurrentPlayer(),
                sanctioner.isActive()
        );
    }

    @Override
    public List<PlayerSetupData> getGameSetup() {
        ensureInitialized();
        return scheduler.getPlayersDisposition().stream()
                .map(id -> {
                    final Player p = players.get(id);
                    return new PlayerSetupData(p.getId(), p.getName(), !p.isBot());
                })
                .toList();
    }

    @Override
    public Player nextPlayer() {
        ensureInitialized();
        final int nextId = scheduler.nextPlayer();
        LOGGER.debug("Scheduler advanced. Next player ID: {}", nextId);
        return players.get(nextId);
    }

    @Override
    public boolean executeTurn(final Card card) {
        ensureInitialized();
        final Player activePlayer = getActivePlayer();
        LOGGER.debug("Executing turn for Player {}. Card played: {}", activePlayer.getId(), card);

        // If there's an active sanction, the player must resolve instead of playing a normal turn
        if (sanctioner.isActive()) {
            LOGGER.debug("Sanction is active. Delegating to handleMalus.");
            return handleMalus(activePlayer, card);
        }

        // User chooses to draw a card
        if (card == null) {
            LOGGER.info("Player {} chose to draw a card.", activePlayer.getId());
            drawCardForPlayer(activePlayer);
            return true;
        }

        // User plays a card, so it must be validated
        if (!validator.isValidCard(discardPile.peek(), card)) {
            LOGGER.warn("Invalid move attempted by player {}: {} on {}", activePlayer.getId(), card, discardPile.peek());
            activePlayer.notifyMoveResult(card, false);
            return false;
        }

        // Confirm the move and apply effects
        LOGGER.info("Player {} played valid card: {}", activePlayer.getId(), card);
        activePlayer.notifyMoveResult(card, true);
        discardPile.addCard(card);

        applyCardEffects(card);

        return true;
    }

    @Override
    public Optional<Integer> getWinner() {
        ensureInitialized();
        final Optional<Integer> winner = players.values().stream().filter(
                p -> p.getHand().isEmpty()).map(Player::getId).findFirst();
        winner.ifPresent(integer -> LOGGER.info("Winner found. Player ID: {}", integer));
        return winner;
    }

    /**
     * @return the player whose turn it is, based on the scheduler's current player ID
     */
    private Player getActivePlayer() {
        return Objects.requireNonNull(players.get(scheduler.getCurrentPlayer()));
    }

    /**
     * Ensures that the game manager has been initialized before performing operations.
     *
     * @throws IllegalStateException if the game manager is not initialized
     */
    private void ensureInitialized() {
        if (!isInitialized) {
            LOGGER.error("Attempted to perform operation on uninitialized GameManager");
            throw new IllegalStateException("GameManager must be initialized before performing this operation. "
                    + "Call init() first.");
        }
    }

    /**
     * Handles the situation where a player is under a sanction (malus). The player can either
     * <ul>
     * <li>defend against the sanction by playing a valid {@link Card}</li>
     * <li>accept the sanction by drawing the required number of {@link Card}</li>
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
            LOGGER.info("Player {} accepts malus. Drawing {} cards.", player.getId(), amount);

            // Apply malus
            for (int i = 0; i < amount; i++) {
                drawCardForPlayer(player);
            }
            sanctioner.reset();

            return true;
        }

        // Player is defending against an active sanction
        if (sanctioner.isActive() && validator.isValidDefense(discardPile.peek(), card)) {
            LOGGER.info("Player {} successfully defended with {}", player.getId(), card);
            player.notifyMoveResult(card, true);
            discardPile.addCard(card);
            applyCardEffects(card);
            return true;
        }

        // Invalid defense attempt
        LOGGER.warn("Player {} failed to defend with invalid card: {}", player.getId(), card);
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
            LOGGER.info("Deck is empty. Refilling from discard pile.");
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
        } else {
            LOGGER.error("Deck is empty even after refill attempt. Player {} cannot draw.", player.getId());
            throw new IllegalStateException("Deck is empty and cannot be refilled. No cards available to draw.");
        }
    }

    /**
     * Applies the effects of the played card to the game state.
     *
     * @param card the card whose effects are to be applied
     */
    private void applyCardEffects(final Card card) {
        Objects.requireNonNull(card);

        if (card.hasEffect(CardEffect.SKIP_NEXT)) {
            LOGGER.debug("Applying SKIP_NEXT effect (triggered by {})", card.getValue());
            scheduler.skipTurn();
        }

        if (card.hasEffect(CardEffect.REVERSE_TURN)) {
            LOGGER.debug("Applying REVERSE_TURN effect.");
            scheduler.reverseDirection();
        }

        // Accumulate sanctions if the card has any effect that triggers them (e.g., Draw Two, Wild Draw Four)
        sanctioner.accumulate(card);
    }
}
