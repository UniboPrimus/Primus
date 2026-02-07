package com.primus.view;

import com.primus.model.deck.Card;
import com.primus.utils.GameState;
import com.primus.utils.PlayerSetupData;

import java.util.Queue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Graphical implementation of {@link GameView} using Java Swing. It provides a user interface for the Primus game,
 * displaying the players, their hands, the central table with the deck and discard pile, and status messages.
 */
public final class PrimusGameView extends JFrame implements GameView {

    private static final float SCREEN_PERCENTAGE = 0.75F;
    private static final int START_CARDS = 7;

    private Consumer<Card> cardPlayedListener;
    private Runnable drawListener;

    private Integer humanPlayerID;
    private final Map<Integer, PlayerPanel> panelMap = new HashMap<>();

    private final PlayerPanel playerNorth;
    private final PlayerPanel playerSouth;
    private final PlayerPanel playerWest;
    private final PlayerPanel playerEast;
    private final TablePanel tablePanel;

    /**
     * Constuctor sets up the main game window and initializes the UI components. It configures the layout to have
     * a central table and four player panels around it, and applies a simple styling.
     */
    public PrimusGameView() {
        super("Primus - The Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dynamic sizing based on screen dimensions
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = (int) (screenSize.width * SCREEN_PERCENTAGE);
        final int height = (int) (screenSize.height * SCREEN_PERCENTAGE);

        this.setSize(width, height);
        this.setMinimumSize(new Dimension(width / 2, height / 2));
        this.setLayout(new BorderLayout());

        // Look and Feel of the system for better integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            // If we can't set the system look and feel, we just use the default one
        }

        // Plauer panels
        this.playerNorth = new PlayerPanel("Bot Top", FlowLayout.CENTER);
        this.playerSouth = new PlayerPanel("Human Player", FlowLayout.CENTER);
        this.playerWest = new PlayerPanel("Bot Left", -1); // -1 indica verticale custom
        this.playerEast = new PlayerPanel("Bot Right", -1);

        this.tablePanel = new TablePanel();

        this.add(playerNorth, BorderLayout.NORTH);
        this.add(playerSouth, BorderLayout.SOUTH);
        this.add(playerWest, BorderLayout.WEST);
        this.add(playerEast, BorderLayout.EAST);
        this.add(tablePanel, BorderLayout.CENTER);

        // Centra la finestra nello schermo
        this.setLocationRelativeTo(null);

        this.setVisible(true);
    }

    @Override
    public void initGame(final List<PlayerSetupData> players) {
        SwingUtilities.invokeLater(() -> {
            Objects.requireNonNull(players);

            panelMap.clear();
            humanPlayerID = null;

            resetPanel(playerSouth);
            resetPanel(playerNorth);
            resetPanel(playerWest);
            resetPanel(playerEast);

            final Queue<PlayerPanel> botSlots = new LinkedList<>(List.of(playerNorth, playerWest, playerEast));

            for (final PlayerSetupData p : players) {
                final PlayerPanel assignedPanel;

                if (p.isHuman()) {
                    // Human is always assigned to the South panel
                    assignedPanel = playerSouth;
                    assignedPanel.setName(p.id() + " (Tu)");
                    System.out.println("UMANO");
                    // Saving human player ID for turn management
                    this.humanPlayerID = p.id();
                } else {
                    // Bots are assigned to the remaining panels
                    assignedPanel = botSlots.poll();
                    if (assignedPanel != null) {
                        assignedPanel.setName(String.valueOf(p.id()));
                        assignedPanel.updateHandBot(START_CARDS);
                    } else {
                        throw new IllegalStateException("More players provided than available bot slots");
                        //TODO sarebbe da loggare un caso di forniti maggior numero di giocatori di quelli previsti
                    }
                }
                panelMap.put(p.id(), assignedPanel);
            }

            this.revalidate();
            this.repaint();
        });
    }

    /**
     * Helper method to reset a player panel to its default state (empty name, no cards, inactive).
     *
     * @param p the PlayerPanel to reset
     */
    private void resetPanel(final PlayerPanel p) {
        Objects.requireNonNull(p);

        p.setName("");
        p.updateHandBot(0);
        p.setActive(false);
    }

    @Override
    public void setCardPlayedListener(final Consumer<Card> listener) {
        cardPlayedListener = listener;
    }

    @Override
    public void setDrawListener(final Runnable listener) {
        drawListener = listener;
    }

    @Override
    public void updateView(final GameState gameState) {
        SwingUtilities.invokeLater(() -> {

            final int currentId = gameState.playerId();

            final boolean isHumanTurn = currentId == this.humanPlayerID;

            // Obtain the active panel based on the current player ID
            final PlayerPanel activePanel = panelMap.get(currentId);

            if (activePanel != null) {
                if (isHumanTurn) {
                    activePanel.updateHand(gameState.activeHand(), true);
                } else {
                    activePanel.updateHandBot(gameState.activeHand().size());
                }
            } else {
                throw new IllegalArgumentException();
                //TODO sarebbe da loggare che è stato fornito un id sconosciuto
            }

            tablePanel.setTopCard(gameState.topCard());
        });
    }

    @Override
    public void showCurrentPlayer(final int currentPlayerID) {
        SwingUtilities.invokeLater(() -> {
            playerNorth.setActive(false);
            playerSouth.setActive(false);
            playerWest.setActive(false);
            playerEast.setActive(false);

            if (currentPlayerID == humanPlayerID) {
                playerSouth.setActive(true);
            } else {
                panelMap.get(currentPlayerID).setActive(true);
            }
        });
    }

    @Override
    public void showMessage(final String message) {
        SwingUtilities.invokeLater(() -> tablePanel.setStatusMessage(message));
    }

    @Override
    public void showError(final String errorMessage) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, errorMessage, "Attention", JOptionPane.WARNING_MESSAGE)
        );
    }

    // Graphical components definitions

    /**
     * {@link JPanel} which represents a player in the game, showing their name and their hand of cards.
     * It can be configured to display either the front of the cards (for the human player) or the back of the
     * cards (for the bots), and to highlight itself when it's the active player's turn.
     */
    private class PlayerPanel extends JPanel {
        private final JLabel nameLabel;
        private final JPanel cardsContainer;
        private final boolean isVertical;

        /**
         * Constructor for PlayerPanel.
         *
         * @param defaultName the name to display for the player (e.g., "Player 1", "Bot 1")
         * @param flowAlign the alignment for the card layout; if -1, a vertical layout is used
         *                  otherwise a horizontal FlowLayout with the specified alignment
         */
        PlayerPanel(final String defaultName, final int flowAlign) {
            this.isVertical = flowAlign == -1;
            this.setLayout(new BorderLayout());
            this.setBackground(new Color(50, 50, 50));
            this.setBorder(new EmptyBorder(5, 5, 5, 5));

            nameLabel = new JLabel(defaultName, SwingConstants.CENTER);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            this.add(nameLabel, BorderLayout.NORTH);

            cardsContainer = new JPanel();
            cardsContainer.setOpaque(false);

            if (isVertical) {
                cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
            } else {
                cardsContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            }
            this.add(cardsContainer, BorderLayout.CENTER);
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
                        new LineBorder(new Color(255, 215, 0), 3),
                        new EmptyBorder(2, 2, 2, 2)
                ));
                nameLabel.setForeground(new Color(255, 215, 0));
            } else {
                this.setBorder(new EmptyBorder(5, 5, 5, 5));
                nameLabel.setForeground(Color.WHITE);
            }
        }

        /**
         * Updates the hand of the player by displaying the front of the cards. If interactable is true
         * the cards will be clickable.
         *
         * @param hand the list of cards in the player's hand to be displayed
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
                                cardPlayedListener.accept(c);
                            }
                        }
                    });
                }
                cardsContainer.add(cc);
            }
            cardsContainer.revalidate();
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
                    cc.setPreferredSize(new Dimension(60, 40));
                }
                cardsContainer.add(cc);
            }
            cardsContainer.revalidate();
        }
    }

    /**
     * {@link JPanel} which represents the central table of the game, showing the top card of the discard pile
     * and the deck for drawing.
     */
    private class TablePanel extends JPanel {
        private final JLabel statusLabel;
        private final JPanel centerZone;
        private CardComponent discardView;
        private CardComponent deckView;

        TablePanel() {
            this.setLayout(new BorderLayout());
            this.setBackground(new Color(34, 139, 34));

            statusLabel = new JLabel("Welcome in Primus", SwingConstants.CENTER);
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            statusLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
            this.add(statusLabel, BorderLayout.SOUTH);

            centerZone = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
            centerZone.setOpaque(false);

            deckView = new CardComponent(null); //Back of the card for the deck
            deckView.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deckView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (drawListener != null) {
                        drawListener.run();
                    }
                }
            });

            discardView = new CardComponent(null); // todo Vuoto inizialmente?

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
    }

    /**
     * {@link JPanel} which represents a single card in the game. It can display either the front
     * of a card (with its colour and value)
     */
    private class CardComponent extends JPanel {
        private final Card card;
        private final int W = 80;
        private final int H = 120;

        /**
         * Constructor for CardComponent.
         *
         * @param card the card to be displayed; if {@code null}, the component will display the back of the card
         */
        CardComponent(final Card card) {
            this.card = card;
            this.setPreferredSize(new Dimension(W, H));
            this.setOpaque(false);
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g;
            // Enable anti-aliasing for smoother edges
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (card != null) {

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, W-1, H-1, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, W-1, H-1, 12, 12);

                g2.setColor(mapColor(card.getColor()));
                g2.fillRoundRect(6, 6, W-13, H-13, 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 28));

                final String val = formatValue(card.getValue());
                final FontMetrics fm = g2.getFontMetrics();
                final int txtW = fm.stringWidth(val);
                final int txtH = fm.getAscent();

                g2.drawString(val, (W - txtW) / 2, (H + txtH) / 2 - 4);

                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(val, 10, 20);

            } else {
                g2.setColor(new Color(60, 60, 60));
                g2.fillRoundRect(0, 0, W-1, H-1, 12, 12);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, W-1, H-1, 12, 12);
                g2.drawOval(W/4, H/4, W/2, H/2);
            }
        }

        /**
         * Maps the card's color (which is an enum) to an actual Color object for rendering.
         *
         * @param enumColor the colour of the card as an enum value; expected to contain keywords like "RED", "BLUE", etc.
         * @return the corresponding {@link Color} object for rendering the card; returns grey if the
         *                  colour is unrecognised
         */
        private Color mapColor(final Object enumColor) {
            final String s = enumColor.toString().toUpperCase();
            if (s.contains("RED") || s.contains("ROSSO")) return new Color(220, 20, 60);
            if (s.contains("BLUE") || s.contains("BLU")) return new Color(0, 100, 200);
            if (s.contains("GREEN") || s.contains("VERDE")) return new Color(34, 139, 34);
            if (s.contains("YELLOW") || s.contains("GIALLO")) return new Color(218, 165, 32);
            if (s.contains("BLACK") || s.contains("NERO")) return Color.BLACK;
            return Color.GRAY;
        }

        /**
         * Formats the card's value (which is an enum) into a string representation for display.
         *
         * @param enumValue the value of the card as an enum
         * @return a string representation of the card's value for display
         */
        private String formatValue(final Object enumValue) {
            final String s = enumValue.toString();
            if (s.contains("SKIP")) return "Ø";
            if (s.contains("REVERSE")) return "⇄";
            if (s.contains("TWO")) return "+2";
            if (s.contains("FOUR")) return "+4";
            if (s.contains("WILD")) return "★";

            return switch (s) {
                case "ZERO" -> "0";
                case "ONE" -> "1";
                case "TWO" -> "2";
                case "THREE" -> "3";
                case "FOUR" -> "4";
                case "FIVE" -> "5";
                case "SIX" -> "6";
                case "SEVEN" -> "7";
                case "EIGHT" -> "8";
                case "NINE" -> "9";
                default -> s.substring(0, 1);
            };
        }
    }
}
