package com.primus.view;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.deck.ImageLoader;
import com.primus.utils.GameState;
import com.primus.utils.PlayerSetupData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Graphical implementation of {@link GameView} using Java Swing. It provides a user interface for the Primus game,
 * displaying the players, their hands, the central table with the deck and discard pile, and status messages.
 */
public final class PrimusGameView extends JFrame implements GameView {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimusGameView.class);

    // Constants for layout and sizing
    private static final float SCREEN_PERCENTAGE = 0.75F;
    private static final int GAP_BETWEEN_CARDS = 5;
    private static final int SCROLL_UNIT_INCREMENT = 16;

    // Constants for fonts and styling
    private static final int BTN_WIDTH = 100;
    private static final int BTN_HEIGHT = 60;
    private static final int FONT_SIZE_STD = 14;
    private static final int BOT_CARD_W = 40;
    private static final int BOT_CARD_H = 60;
    private static final int TABLE_BORDER_BOT = 20;
    private static final int TABLE_GAP = 40;

    //Costant for font
    private static final String FONT_NAME = "SansSerif";

    //Constants for colors
    private static final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(50, 50, 50);
    private static final java.awt.Color COLOR_RED_ALERT = new java.awt.Color(255, 0, 0, 128);
    private static final java.awt.Color COLOR_GOLD = new java.awt.Color(255, 215, 0);
    private static final java.awt.Color TABLE_COLOR = new java.awt.Color(34, 139, 34);
    private static final java.awt.Color TEXT_COLOR = java.awt.Color.WHITE;

    //Card color constants
    private static final java.awt.Color COLOR_RED = new java.awt.Color(220, 20, 60);
    private static final java.awt.Color COLOR_BLUE = new java.awt.Color(0, 100, 200);
    private static final java.awt.Color COLOR_GREEN = new java.awt.Color(34, 139, 34);
    private static final java.awt.Color COLOR_YELLOW = new java.awt.Color(218, 165, 32);

    private final transient ImageLoader imageLoader;
    private transient Consumer<Card> cardPlayedListener;
    private transient Consumer<Boolean> newMatchListener;
    private transient Runnable drawListener;

    private Integer humanPlayerID;
    private final Map<Integer, PlayerPanel> panelMap = new HashMap<>();

    private final PlayerPanel playerNorth;
    private final PlayerPanel playerSouth;
    private final PlayerPanel playerWest;
    private final PlayerPanel playerEast;
    private final TablePanel tablePanel;

    /**
     * Constructor sets up the main game window and initializes the UI components. It configures the layout to have
     * a central table and four player panels around it, and applies a simple styling.
     *
     * @param imageLoader the ImageLoader instance used to load card images for rendering; must not be null
     */
    public PrimusGameView(final ImageLoader imageLoader) {
        super("Primus - The Game");
        Objects.requireNonNull(imageLoader);
        LOGGER.info("Initializing PrimusGameView");

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.imageLoader = imageLoader;

        // Dynamic sizing based on screen dimensions
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = (int) (screenSize.width * SCREEN_PERCENTAGE);
        final int height = (int) (screenSize.height * SCREEN_PERCENTAGE);
        LOGGER.debug("Setting window size to {}x{}", width, height);

        this.setSize(width, height);
        this.setMinimumSize(new Dimension(width / 2, height / 2));
        this.setLayout(new BorderLayout());

        // Look and Feel of the system for better integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException | InstantiationException
                       | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            LOGGER.warn("Could not set System LookAndFeel. Using default");
            // If we can't set the system look and feel, we just use the default one
        }

        // Player panels
        this.playerNorth = new PlayerPanel("Bot Top", FlowLayout.CENTER);
        this.playerSouth = new PlayerPanel("Human Player", FlowLayout.CENTER);
        this.playerWest = new PlayerPanel("Bot Left", -1);
        this.playerEast = new PlayerPanel("Bot Right", -1);

        this.tablePanel = new TablePanel();

        this.add(playerNorth, BorderLayout.NORTH);
        this.add(playerSouth, BorderLayout.SOUTH);
        this.add(playerWest, BorderLayout.WEST);
        this.add(playerEast, BorderLayout.EAST);
        this.add(tablePanel, BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
        LOGGER.info("View visible");
    }

    @Override
    public void initGame(final List<PlayerSetupData> players) {
        SwingUtilities.invokeLater(() -> {
            Objects.requireNonNull(players);
            LOGGER.info("Starting visual game setup for {} players", players.size());

            panelMap.clear();
            humanPlayerID = null;

            resetPanel(playerSouth);
            resetPanel(playerNorth);
            resetPanel(playerWest);
            resetPanel(playerEast);

            int humanIndex = -1;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).isHuman()) {
                    humanIndex = i;
                    break;
                }
            }

            if (humanIndex == -1) {
                LOGGER.error("No human player found in setup data");
                throw new IllegalArgumentException("At least one human player is required");
            }

            //South is always the human player
            assignPlayerToPanel(players.get(humanIndex), playerSouth);

            //The other players are assigned in clockwise order starting from the left of the human player
            assignPlayerToPanel(players.get((humanIndex + 1) % players.size()), playerWest);
            assignPlayerToPanel(players.get((humanIndex + 2) % players.size()), playerNorth);
            assignPlayerToPanel(players.get((humanIndex + 3) % players.size()), playerEast);

            this.revalidate();
            this.repaint();
        });
    }

    private void assignPlayerToPanel(final PlayerSetupData p, final PlayerPanel panel) {
        if (p.isHuman()) {
            this.humanPlayerID = p.id();
            panel.setPlayerName(p.name());
        } else {
            panel.setPlayerName(p.name());
            panel.updateHandBot(0);
        }
        panelMap.put(p.id(), panel);
        LOGGER.info("Assigned player {} ({}) to panel position", p.id(), p.name());
    }

    /**
     * Helper method to reset a player panel to its default state (empty name, no cards, inactive).
     *
     * @param p the PlayerPanel to reset
     */
    private void resetPanel(final PlayerPanel p) {
        Objects.requireNonNull(p);
        p.setPlayerName("");
        p.updateHandBot(0);
        p.setActive(false);
    }

    @Override
    public void setCardPlayedListener(final Consumer<Card> listener) {
        Objects.requireNonNull(listener);
        cardPlayedListener = listener;
        LOGGER.debug("CardPlayedListener registered");
    }

    @Override
    public void setDrawListener(final Runnable listener) {
        Objects.requireNonNull(listener);
        drawListener = listener;
        LOGGER.debug("DrawListener registered");
    }

    @Override
    public void setNewMatchListener(final Consumer<Boolean> listener) {
        Objects.requireNonNull(listener);
        newMatchListener = listener;
        LOGGER.debug("NewMatchListener registered");
    }

    @Override
    public void updateView(final GameState gameState) {
        SwingUtilities.invokeLater(() -> {
            Objects.requireNonNull(gameState);
            final int currentId = gameState.playerId();
            LOGGER.debug("Updating view. Active Player ID: {}", currentId);
            final boolean isHumanTurn = Objects.equals(currentId, this.humanPlayerID);

            showCurrentPlayer(currentId);
            // Obtain the active panel based on the current player ID
            final PlayerPanel humanPanel = panelMap.get(this.humanPlayerID);

            if (humanPanel != null) {
                humanPanel.updateHand(gameState.humanHand(), isHumanTurn);
            } else {
                LOGGER.error("Received update for unknown Player ID: {}", currentId);
                throw new IllegalArgumentException("Unknown Player ID in GameState: " + currentId);
            }

            gameState.playersCardCounts().forEach((playerId, count) -> {
                if (!Objects.equals(playerId, this.humanPlayerID)) {
                    final PlayerPanel botPanel = panelMap.get(playerId);
                    if (botPanel != null) {
                        botPanel.updateHandBot(count);
                    } else {
                        LOGGER.error("Received card count update for unknown Player ID: {}", playerId);
                        throw new IllegalArgumentException("Unknown Player ID in GameState: " + playerId);
                    }
                }
            });

            tablePanel.setTopCard(gameState.topCard());

            if (gameState.isMalusActive() && isHumanTurn) {
                tablePanel.setAlertMode(true);
                showMessage("Attenzione! Malus attivo: pesca o difenditi");
            } else {
                tablePanel.setAlertMode(false);
            }
        });
    }

    @Override
    public void showCurrentPlayer(final int currentPlayerID) {
        SwingUtilities.invokeLater(() -> {
            playerNorth.setActive(false);
            playerSouth.setActive(false);
            playerWest.setActive(false);
            playerEast.setActive(false);

            if (Objects.equals(currentPlayerID, humanPlayerID)) {
                playerSouth.setActive(true);
            } else {
                panelMap.get(currentPlayerID).setActive(true);
            }
        });
    }

    @Override
    public void showMessage(final String message) {
        SwingUtilities.invokeLater(() -> {
            Objects.requireNonNull(message);
            tablePanel.setStatusMessage(message);
        });
    }

    @Override
    public void showError(final String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            Objects.requireNonNull(errorMessage);
            LOGGER.warn("Showing UI Error: {}", errorMessage);
            JOptionPane.showMessageDialog(this, errorMessage, "Attention", JOptionPane.WARNING_MESSAGE);
        });
    }

    @Override
    public void showGameOverMessage(final String winnerName) {
        SwingUtilities.invokeLater(() -> {
            Objects.requireNonNull(winnerName);
            final Object[] options = {"Nuova Partita", "Esci dal Gioco"};
            final int choice = JOptionPane.showOptionDialog(
                    this,
                    "La partita è terminata!\nIl vincitore è: " + winnerName + "\n\nCosa vuoi fare?",
                    "Fine Partita",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (newMatchListener != null) {
                LOGGER.debug("User selected option {} in game over dialog", choice);
                // If choice is 0, the user wants to start a new match; any other choice, including closing the
                // dialog is treated as exiting the game
                newMatchListener.accept(choice == 0);
            }
        });
    }

    /**
     * Maps the card's color (which is an enum) to an actual Color object for rendering.
     *
     * @param enumColor the colour of the card as an enum value; expected to contain keywords like "RED", "BLUE", etc.
     * @return the corresponding {@link Color} object for rendering the card; returns grey if the colour is unrecognised
     */
    private java.awt.Color mapColor(final Object enumColor) {
        if (enumColor == null) {
            return java.awt.Color.GRAY;
        }

        final String s = enumColor.toString().toUpperCase(Locale.ROOT);

        if (s.contains("RED") || s.contains("ROSSO")) {
            return COLOR_RED;
        }
        if (s.contains("BLUE") || s.contains("BLU")) {
            return COLOR_BLUE;
        }
        if (s.contains("GREEN") || s.contains("VERDE")) {
            return COLOR_GREEN;
        }
        if (s.contains("YELLOW") || s.contains("GIALLO")) {
            return COLOR_YELLOW;
        }
        if (s.contains("BLACK") || s.contains("NERO")) {
            return java.awt.Color.BLACK;
        }

        return java.awt.Color.GRAY;
    }

    private Color promptForColor() {
        final Color[] options = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        final Color[] selection = {null};
        final JButton[] buttons = new JButton[options.length];

        for (int i = 0; i < options.length; i++) {
            final Color c = options[i];
            final JButton btn = new JButton(c.name());

            btn.setBackground(mapColor(c));
            btn.setForeground(TEXT_COLOR);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
            btn.setFont(new Font("Arial", Font.BOLD, FONT_SIZE_STD));

            btn.addActionListener(e -> {
                selection[0] = c;
                SwingUtilities.getWindowAncestor(btn).dispose();
            });

            buttons[i] = btn;
        }

        JOptionPane.showOptionDialog(
                this, "Scegli un colore per il jolly:", "Jolly - Scegli Colore",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]
        );

        return selection[0];
    }

    // Graphical components definitions

    /**
     * {@link JPanel} which represents a player in the game, showing their name and their hand of cards.
     * It can be configured to display either the front of the cards (for the human player) or the back of the
     * cards (for the bots), and to highlight itself when it's the active player's turn.
     */
    private final class PlayerPanel extends JPanel {
        @Serial
        private static final long serialVersionUID = 1L;

        private final JLabel nameLabel;
        private final JPanel cardsContainer;
        private final JScrollPane scrollPane;
        private final boolean isVertical;

        /**
         * Constructor for PlayerPanel.
         *
         * @param defaultName the name to display for the player (e.g., "Player 1", "Bot 1")
         * @param flowAlign   the alignment for the card layout; if -1, a vertical layout is used
         *                    otherwise a horizontal FlowLayout with the specified alignment
         */
        PlayerPanel(final String defaultName, final int flowAlign) {
            this.isVertical = flowAlign == -1;
            this.setLayout(new BorderLayout());
            this.setBackground(BACKGROUND_COLOR);
            this.setBorder(new EmptyBorder(GAP_BETWEEN_CARDS, GAP_BETWEEN_CARDS, GAP_BETWEEN_CARDS, GAP_BETWEEN_CARDS));

            nameLabel = new JLabel(defaultName, SwingConstants.CENTER);
            nameLabel.setForeground(java.awt.Color.WHITE);
            nameLabel.setFont(new Font(FONT_NAME, Font.BOLD, FONT_SIZE_STD));
            this.add(nameLabel, BorderLayout.NORTH);

            cardsContainer = new JPanel();
            cardsContainer.setOpaque(false);

            if (isVertical) {
                cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
            } else {
                cardsContainer.setLayout(new FlowLayout(FlowLayout.CENTER, GAP_BETWEEN_CARDS, GAP_BETWEEN_CARDS));
            }
            scrollPane = new JScrollPane(cardsContainer);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);

            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);
            this.add(scrollPane, BorderLayout.CENTER);
        }

        /**
         * Highlights the panel to indicate that it's the active player's turn. When active, it
         * shows a golden border and changes the name color.
         *
         * @param active true to activate the highlight, false to deactivate it
         */
        public void setActive(final boolean active) {
            if (active) {
                this.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_GOLD, 3),
                        new EmptyBorder(2, 2, 2, 2)
                ));
                nameLabel.setForeground(COLOR_GOLD);
            } else {
                this.setBorder(new EmptyBorder(GAP_BETWEEN_CARDS, GAP_BETWEEN_CARDS, GAP_BETWEEN_CARDS, GAP_BETWEEN_CARDS));
                nameLabel.setForeground(java.awt.Color.WHITE);
            }
        }

        public void setPlayerName(final String name) {
            nameLabel.setText(name);
        }

        /**
         * Updates the hand of the player by displaying the front of the cards. If interactable is true
         * the cards will be clickable.
         *
         * @param hand         the list of cards in the player's hand to be displayed
         * @param interactable true if the cards should be clickable
         */
        public void updateHand(final List<Card> hand, final boolean interactable) {
            Objects.requireNonNull(hand);
            cardsContainer.removeAll();

            for (final Card c : hand) {
                final CardComponent cc = new CardComponent(c);
                if (interactable) {
                    cc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    cc.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(final MouseEvent e) {
                            if (cardPlayedListener != null) {
                                if (c.getColor() == Color.BLACK) {
                                    final Color newColor = promptForColor();
                                    if (newColor != null) {
                                        LOGGER.debug("User selected color {} for wild card", newColor);
                                        final Card playedCard = c.withColor(newColor);
                                        cardPlayedListener.accept(playedCard);
                                    } else {
                                        LOGGER.debug("User cancelled color selection for wild card");
                                    }
                                } else {
                                    LOGGER.debug("User clicked card: {}", c);
                                    cardPlayedListener.accept(c);
                                }
                            }
                        }
                    });
                }
                cardsContainer.add(cc);
            }
            cardsContainer.revalidate();
            cardsContainer.repaint();
        }

        /**
         * Updates the hand of a bot player by displaying the back of the cards.
         *
         * @param count the number of cards in the bot's hand to be displayed
         */
        public void updateHandBot(final int count) {
            cardsContainer.removeAll();
            for (int i = 0; i < count; i++) {
                final CardComponent cc = new CardComponent(null);
                if (isVertical) {
                    cc.setPreferredSize(new Dimension(BOT_CARD_W, BOT_CARD_H));
                }
                cardsContainer.add(cc);
            }
            cardsContainer.revalidate();
            cardsContainer.repaint();
        }
    }

    /**
     * {@link JPanel} which represents the central table of the game, showing the top card of the discard pile
     * and the deck for drawing.
     */
    private final class TablePanel extends JPanel {
        @Serial
        private static final long serialVersionUID = 1L;

        private final JLabel statusLabel;
        private final JPanel centerZone;
        private final CardComponent deckView;
        private CardComponent discardView;

        TablePanel() {
            this.setLayout(new BorderLayout());
            this.setBackground(TABLE_COLOR);

            statusLabel = new JLabel("Welcome in Primus", SwingConstants.CENTER);
            statusLabel.setForeground(java.awt.Color.WHITE);
            statusLabel.setFont(new Font(FONT_NAME, Font.ITALIC, 16));
            statusLabel.setBorder(new EmptyBorder(10, 0, TABLE_BORDER_BOT, 0));
            this.add(statusLabel, BorderLayout.SOUTH);

            centerZone = new JPanel(new FlowLayout(FlowLayout.CENTER, TABLE_GAP, TABLE_GAP));
            centerZone.setOpaque(false);

            deckView = new CardComponent(null); //Back of the card for the deck
            deckView.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deckView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (drawListener != null) {
                        LOGGER.debug("User clicked deck to draw.");
                        drawListener.run();
                    }
                }
            });

            discardView = new CardComponent(null);
            discardView.setVisible(false); // Initially hidden until the first card is played

            centerZone.add(deckView);
            centerZone.add(discardView);

            this.add(centerZone, BorderLayout.CENTER);
        }

        /**
         * Updates the top card displayed on the table. It removes the old card component and replaces it with a new one.
         *
         * @param c the new top card to be displayed
         */
        public void setTopCard(final Card c) {
            centerZone.remove(discardView);
            discardView = new CardComponent(c);
            discardView.setVisible(true);
            centerZone.add(discardView);
            centerZone.revalidate();
            centerZone.repaint();
        }

        /**
         * Updates the status message displayed at the bottom of the table,
         * which can be used to show various game-related messages to the user.
         *
         * @param msg the new status message to be displayed
         */
        public void setStatusMessage(final String msg) {
            statusLabel.setText(msg);
        }

        public void setAlertMode(final boolean active) {
            if (active) {
                deckView.setBorder(BorderFactory.createLineBorder(COLOR_RED_ALERT, 3));
                statusLabel.setForeground(COLOR_RED_ALERT);
                statusLabel.setFont(new Font(FONT_NAME, Font.BOLD, FONT_SIZE_STD + 4));
            } else {
                deckView.setBorder(null);
                statusLabel.setForeground(TEXT_COLOR);
                statusLabel.setFont(new Font(FONT_NAME, Font.ITALIC, FONT_SIZE_STD));
            }
        }
    }

    /**
     * {@link JPanel} which represents a single card in the game. It can display either the front
     * of a card (with its colour and value)
     */
    private final class CardComponent extends JPanel {
        @Serial
        private static final long serialVersionUID = 1L;

        private static final int HUMAN_W = 80;
        private static final int HUMAN_H = 120;
        private final Card card;

        /**
         * Constructor for CardComponent.
         *
         * @param card the card to be displayed; if {@code null}, the component will display the back of the card
         */
        CardComponent(final Card card) {
            this.card = card;
            this.setPreferredSize(new Dimension(HUMAN_W, HUMAN_H));
            this.setOpaque(false);
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g;
            // Enable antialiasing for smoother edges and better image quality
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            final Image img;

            if (card != null) {
                img = imageLoader.getImage(card).orElse(null);
            } else {
                img = imageLoader.getBackImage().orElse(null);
            }

            if (img != null) {
                g2.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
            } else {
                if (card != null) {
                    LOGGER.error("Missing image for card: {} {}", card.getColor(), card.getValue());
                } else {
                    LOGGER.error("Missing image for card back");
                }
            }
        }
    }
}
