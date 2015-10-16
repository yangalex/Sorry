package yangalex_CSCI201_Assignment3;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

interface GamePanelDelegate {
	public void gameEnded();
}

public class GamePanel extends JPanel {
	public static final long serialVersionUID = 3114141;
	
	GamePanelDelegate delegate;
	
	// declare constants
	final int YELLOW_START_TILE = 4;
	final int GREEN_START_TILE = 19;
	final int RED_START_TILE = 34;
	final int BLUE_START_TILE = 49;
	final int YELLOW_SAFE_ZONE_TILE = 2;
	final int GREEN_SAFE_ZONE_TILE = 17;
	final int RED_SAFE_ZONE_TILE = 32;
	final int BLUE_SAFE_ZONE_TILE = 47;
	
	Tile[] tilesArray;
	Bot[] bots;
	ArrayList<Card> cardDeck;
	Color playerColor;
	int numPlayers;
	
	JButton cardButton;
	JLabel yStartLabel;
	JLabel gStartLabel;
	JLabel rStartLabel;
	JLabel bStartLabel;
	JLabel yHomeLabel;
	JLabel gHomeLabel;
	JLabel rHomeLabel;
	JLabel bHomeLabel;
	JLabel logo;
	
	BufferedImage yellowPanelImage;
	BufferedImage greenPanelImage;
	BufferedImage redPanelImage;
	BufferedImage bluePanelImage;
	BufferedImage logoImage;
	BufferedImage backCardImage;
	
	Font customFont;
	
	// Game logic related variables
	Card currentCard;
	Player selectedPlayer;
	int gameState = 0;
	final static int IDLE = 0;
	final static int SELECTING_PLAYER = 1;
	final static int SELECTING_MOVE = 2;
	final static int CARD_SEVEN = 3;
	final static int SORRY = 4;
	
	boolean waitingForMove = false;
	int yellowStartCount = 0;
	int greenStartCount = 0;
	int blueStartCount = 0;
	int redStartCount = 0;
	int yellowHomeCount = 0;
	int greenHomeCount = 0;
	int redHomeCount = 0;
	int blueHomeCount = 0;
	
	// for card 7 logic
	int card7State;
	final static int PICKING_FIRST_PAWN = 0;
	final static int PICKING_FIRST_MOVE = 1;
	final static int PICKING_SECOND_PAWN = 2;
	final static int PICKING_SECOND_MOVE = 3;
	int firstMoveSteps;
	int secondMoveSteps;
	Player firstPawn;
	
	
	// Arrays to store pawns in play for each color
	ArrayList<Player> yellowPawns = new ArrayList<Player>();
	ArrayList<Player> greenPawns = new ArrayList<Player>();
	ArrayList<Player> bluePawns = new ArrayList<Player>();
	ArrayList<Player> redPawns = new ArrayList<Player>();
	
	public GamePanel(int numPlayers, Color playerColor) {
		setLayout(new GridBagLayout());
		
		this.playerColor = playerColor;
		initializeStartCountForColor(playerColor);
		this.numPlayers = numPlayers;
		
		initializeFont();
		initializeImages();
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeFont() {
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(10f);
		} catch (FontFormatException ffe) {
			System.out.println(ffe.getMessage());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private void initializeImages() {
		try {
			yellowPanelImage = ImageIO.read(new File("images/yellow_panel.png"));
			greenPanelImage = ImageIO.read(new File("images/green_panel.png"));
			redPanelImage = ImageIO.read(new File("images/red_panel.png"));
			bluePanelImage = ImageIO.read(new File("images/blue_panel.png"));
			logoImage = ImageIO.read(new File("images/sorry.png"));
			backCardImage = ImageIO.read(new File("images/cardBack_red.png"));
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private void initializeComponents() {
		buildTiles();
		buildBots();
		buildCards();
		cardButton = new JButton("");
		cardButton.requestFocus();
		buildLabels();
		
		logo = new JLabel("");
		logo.setIcon(new ImageIcon(logoImage.getScaledInstance(240, 90, Image.SCALE_SMOOTH)));
	}
	
	private void buildLabels() {
		yStartLabel = new JLabel(Integer.toString(yellowStartCount), JLabel.CENTER);
		gStartLabel = new JLabel(Integer.toString(greenStartCount), JLabel.CENTER);
		rStartLabel = new JLabel(Integer.toString(redStartCount), JLabel.CENTER);
		bStartLabel = new JLabel(Integer.toString(blueStartCount), JLabel.CENTER);
		
		yHomeLabel = new JLabel(Integer.toString(yellowHomeCount), JLabel.CENTER);
		gHomeLabel = new JLabel(Integer.toString(greenHomeCount), JLabel.CENTER);
		rHomeLabel = new JLabel(Integer.toString(redHomeCount), JLabel.CENTER);
		bHomeLabel = new JLabel(Integer.toString(blueHomeCount), JLabel.CENTER);
	}
	
	private void buildTiles() {
		// tiles will be ordered 0 to 59, starting with the top left tile and going clockwise
		// indexes 2 (yellow), 17 (green), 32 (red), and 47 (blue) will be SafeZoneEntryTiles
		tilesArray = new Tile[60];
		
		// create arrays to organize indexes with colors
		int[] blackTiles = {0, 5, 6, 7, 8, 14, 15, 20, 21, 22, 23, 29, 30, 35, 36, 37, 38, 44, 45, 50, 51, 52, 53, 59};
		int[] yellowTiles = {1, 2, 3, 4, 9, 10, 11, 12, 13};
		int[] greenTiles = {16, 17, 18, 19, 24, 25, 26, 27, 28};
		int[] redTiles = {31, 32, 33, 34, 39, 40, 41, 42, 43};
		int[] blueTiles = {46, 47, 48, 49, 54, 55, 56, 57, 58};
		
		// create black tiles
		for (int index : blackTiles) {
			tilesArray[index] = new Tile(Color.black, false);
			tilesArray[index].tileId = index;
		}
		
		// create yellow tiles
		for (int index : yellowTiles) {
			if (index == YELLOW_SAFE_ZONE_TILE) {
				tilesArray[index] = new SafeZoneEntryTile(Color.yellow, index);
			} else {
				tilesArray[index] = new Tile(Color.yellow, true);
				tilesArray[index].tileId = index;
			}
			
		}
		
		// create green tiles
		for (int index : greenTiles) {
			if (index == GREEN_SAFE_ZONE_TILE) {
				tilesArray[index] = new SafeZoneEntryTile(Color.green, index);
			} else {
				tilesArray[index] = new Tile(Color.green, true);
				tilesArray[index].tileId = index;
			}
		}
		
		// create red tiles
		for (int index : redTiles) {
			if (index == RED_SAFE_ZONE_TILE) {
				tilesArray[index] = new SafeZoneEntryTile(Color.red, index);
			} else {
				tilesArray[index] = new Tile(Color.red, true);
				tilesArray[index].tileId = index;
			}
			
		}
		
		// create blue tiles
		for (int index : blueTiles) {
			if (index == BLUE_SAFE_ZONE_TILE) {
				tilesArray[index] = new SafeZoneEntryTile(Color.blue, index);
			} else {
				tilesArray[index] = new Tile(Color.blue, true);
				tilesArray[index].tileId = index;
			}
			
		}
		
		// for loop to connect every tile 
		tilesArray[0].nextTile = tilesArray[1];
		tilesArray[0].previousTile = tilesArray[59];
		tilesArray[59].previousTile = tilesArray[58];
		tilesArray[59].nextTile = tilesArray[0];
		
		for (int i=1; i < 59; i++) {
			tilesArray[i].nextTile = tilesArray[i+1];
			tilesArray[i].previousTile = tilesArray[i-1];
		}
		
	}
	
	private void buildBots() {
		
		bots = new Bot[numPlayers-1];
		
		if (numPlayers == 2) {
			if (playerColor.equals(Color.yellow)) {
				bots[0] = new Bot(Color.red);
			} else if (playerColor.equals(Color.green)) {
				bots[0] = new Bot(Color.blue);
			} else if (playerColor.equals(Color.red)) {
				bots[0] = new Bot(Color.yellow);
			} else if (playerColor.equals(Color.blue)) {
				bots[0] = new Bot(Color.green);
			}
			initializeStartCountForColor(bots[0].color);
		} else if (numPlayers == 3) {
			if (playerColor.equals(Color.yellow)) {
				bots[0] = new Bot(Color.green);
				bots[1] = new Bot(Color.red);
			} else if (playerColor.equals(Color.green)) {
				bots[0] = new Bot(Color.red);
				bots[1] = new Bot(Color.yellow);
			} else if (playerColor.equals(Color.red)) {
				bots[0] = new Bot(Color.green);
				bots[1] = new Bot(Color.yellow);
			} else if (playerColor.equals(Color.blue)) {
				bots[0] = new Bot(Color.yellow);
				bots[1] = new Bot(Color.green);
			} 
			initializeStartCountForColor(bots[0].color);
			initializeStartCountForColor(bots[1].color);
		} else if (numPlayers == 4) {
			if (playerColor.equals(Color.yellow)) {
				Color[] botColors = {Color.green, Color.red, Color.blue};
				for (int i=0; i < bots.length; i++) {
					bots[i] = new Bot(botColors[i]);
				}
			} else if (playerColor.equals(Color.green)) {
				Color[] botColors = {Color.yellow, Color.red, Color.blue};
				for (int i=0; i < bots.length; i++) {
					bots[i] = new Bot(botColors[i]);
				}
			} else if (playerColor.equals(Color.blue)) {
				Color[] botColors = {Color.yellow, Color.red, Color.green};
				for (int i=0; i < bots.length; i++) {
					bots[i] = new Bot(botColors[i]);
				}
			} else if (playerColor.equals(Color.red)) {
				Color[] botColors = {Color.yellow, Color.blue, Color.green};
				for (int i=0; i < bots.length; i++) {
					bots[i] = new Bot(botColors[i]);
				}
			}
			initializeStartCountForColor(bots[0].color);
			initializeStartCountForColor(bots[1].color);
			initializeStartCountForColor(bots[2].color);
		}
	}
	
	private void buildCards() {
		cardDeck = new ArrayList<Card>();
		
		for (int i=1; i <= 13; i++) {
			if (i != 6 && i != 9) {
				cardDeck.add(new Card(i));
			}
		}
		
		Collections.shuffle(cardDeck);
	}
	
	private void rebuildCards() {
		for (int i=1; i <= 13; i++) {
			if (i != 6 && i != 9) {
				cardDeck.add(new Card(i));
			}
		}
		
		Collections.shuffle(cardDeck);
	}

	private void createGUI() {
		JLabel startLabel = new JLabel("Start");
		startLabel.setFont(new Font("Arial", Font.BOLD, 8));
		
		GridBagConstraints gb = new GridBagConstraints();
		gb.weightx = 3;
		gb.weighty = 3;
		
		// top row
		gb.gridx = 0;
		gb.gridy = 0;
		gb.fill = GridBagConstraints.BOTH;
		for (int i=0; i < 16; i++) {
			if (i == YELLOW_SAFE_ZONE_TILE) {
				add(tilesArray[i], gb);
				SafeZoneEntryTile ySafeZoneTile = (SafeZoneEntryTile)tilesArray[i];
				Tile[] ySafeZone = ySafeZoneTile.safeZone;
				for (int j=0; j < ySafeZone.length; j++) {
					gb.gridy += 1;
					ySafeZone[j].setPreferredSize(tilesArray[0].getPreferredSize());
					add(ySafeZone[j], gb);
				}
				gb.gridy = 0;
				gb.gridx += 1;
			} else if (i == YELLOW_START_TILE) {
				add(tilesArray[i], gb);
				
				gb.gridy = 1;
				JLabel yellowStartLabel = new JLabel("Start", new ImageIcon(yellowPanelImage), JLabel.CENTER);
				yellowStartLabel.setHorizontalTextPosition(JLabel.CENTER);
				yellowStartLabel.setFont(customFont);
				yellowStartLabel.setPreferredSize(tilesArray[0].getPreferredSize());
				add(yellowStartLabel, gb);
				
				gb.gridy = 0;
				gb.gridx += 1;
			} else {
				add(tilesArray[i], gb);
				gb.gridx += 1;
			}
		}
		
		// right column
		gb.gridx = 15;
		gb.gridy = 1;
		for (int i=16; i < 31; i++) {
			if (i == GREEN_SAFE_ZONE_TILE) {
				add(tilesArray[i], gb);
				SafeZoneEntryTile gSafeZoneTile = (SafeZoneEntryTile)tilesArray[i];
				Tile[] gSafeZone = gSafeZoneTile.safeZone;
				for (int j=0; j < gSafeZone.length; j++) {
					gb.gridx -= 1;
					gSafeZone[j].setPreferredSize(tilesArray[0].getPreferredSize());
					add(gSafeZone[j], gb);
				}
				gb.gridx = 15;
				gb.gridy += 1;
			} else if (i == GREEN_START_TILE) {
				add(tilesArray[i], gb);
				
				gb.gridx -= 1;
				JLabel greenStartLabel = new JLabel("Start", new ImageIcon(greenPanelImage), JLabel.CENTER);
				greenStartLabel.setHorizontalTextPosition(JLabel.CENTER);
				greenStartLabel.setFont(customFont);
				greenStartLabel.setPreferredSize(tilesArray[0].getPreferredSize());
				add(greenStartLabel, gb);
				
				gb.gridx = 15;
				gb.gridy += 1;
			} else {
				add(tilesArray[i], gb);
				gb.gridy += 1;
			}
		}
		
		// bottom row
		gb.gridx = 14;
		gb.gridy = 15;
		for (int i=31; i < 46; i++) {
			if (i == RED_SAFE_ZONE_TILE) {
				add(tilesArray[i], gb);
				SafeZoneEntryTile rSafeZoneTile = (SafeZoneEntryTile)tilesArray[i];
				Tile[] rSafeZone = rSafeZoneTile.safeZone;
				for (int j=0; j < rSafeZone.length; j++) {
					gb.gridy -= 1;
					rSafeZone[j].setPreferredSize(tilesArray[0].getPreferredSize());
					add(rSafeZone[j], gb);
				}
				gb.gridy = 15;
				gb.gridx -= 1;
			} else if (i == RED_START_TILE) {
				add(tilesArray[i], gb);
				
				gb.gridy -= 1;
				JLabel redStartLabel = new JLabel("Start", new ImageIcon(redPanelImage), JLabel.CENTER);
				redStartLabel.setHorizontalTextPosition(JLabel.CENTER);
				redStartLabel.setFont(customFont);
				redStartLabel.setPreferredSize(tilesArray[0].getPreferredSize());
				add(redStartLabel, gb);
				
				gb.gridy = 15;
				gb.gridx -= 1;
			} else { 
				add(tilesArray[i], gb);
				gb.gridx -= 1;
			}
		}
		
		// left column
		gb.gridx = 0;
		gb.gridy = 14;
		for (int i=46; i < 60; i++) {
			if (i == BLUE_SAFE_ZONE_TILE) {
				add(tilesArray[i], gb);
				SafeZoneEntryTile bSafeZoneTile = (SafeZoneEntryTile)tilesArray[i];
				Tile[] bSafeZone = bSafeZoneTile.safeZone;
				for (int j=0; j < bSafeZone.length; j++) {
					gb.gridx +=1;
					bSafeZone[j].setPreferredSize(tilesArray[0].getPreferredSize());
					add(bSafeZone[j], gb);
				}
				gb.gridx = 0;
				gb.gridy -= 1;
			} else if (i == BLUE_START_TILE) {
				add(tilesArray[i], gb);
				
				gb.gridx += 1;
				JLabel blueStartLabel = new JLabel("Start", new ImageIcon(bluePanelImage), JLabel.CENTER);
				blueStartLabel.setHorizontalTextPosition(JLabel.CENTER);
				blueStartLabel.setFont(customFont);
				blueStartLabel.setPreferredSize(tilesArray[0].getPreferredSize());
				add(blueStartLabel, gb);
				
				gb.gridx = 0;
				gb.gridy -= 1;
			} else {
				add(tilesArray[i], gb);
				gb.gridy -= 1;
			}
		}
		

		// add cards button
		gb.fill = GridBagConstraints.BOTH;
		gb.gridx = 7;
		gb.gridy = 7;
		gb.gridwidth = 1;
		gb.gridheight = 1;
		JLabel cardsLabel = new JLabel("Cards:");
		cardsLabel.setFont(customFont.deriveFont(9f));
		cardsLabel.setPreferredSize(tilesArray[0].getPreferredSize());
		add(cardsLabel, gb);
		gb.gridx = 8;
		cardButton.setPreferredSize(tilesArray[0].getPreferredSize());
		cardButton.setIcon(new ImageIcon(backCardImage.getScaledInstance(40, -1, Image.SCALE_SMOOTH)));
		cardButton.setContentAreaFilled(false);
		cardButton.setBorderPainted(false);
		cardButton.setOpaque(false);
		add(cardButton, gb);
		
		// add start and home labels
		gb.gridx = 4;
		gb.gridy = 2;
		add(yStartLabel, gb);
		gb.gridx = 2;
		gb.gridy = 7;
		add(yHomeLabel, gb);
		gb.gridx = 8;
		gb.gridy = 2;
		add(gHomeLabel, gb);
		gb.gridx = 13;
		gb.gridy = 4;
		add(gStartLabel, gb);
		gb.gridx = 13;
		gb.gridy = 8;
		add(rHomeLabel, gb);
		gb.gridx = 11;
		gb.gridy = 13;
		add(rStartLabel, gb);
		gb.gridx = 7;
		gb.gridy = 13;
		add(bHomeLabel, gb);
		gb.gridx = 2;
		gb.gridy = 11;
		add(bStartLabel, gb);
	}
	
	private void skipTurn() {
		if (currentCard.cardNumber != 2) {
			JOptionPane.showMessageDialog(this, "No moves available! Skipping turn");
		} else {
			JOptionPane.showMessageDialog(this, "No moves for Card 2, draw again");
		}
		
		finishTurn();
	}
	
	private void addEvents() {
		// cardButton events
		cardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cardButton.setEnabled(false);
				pickCard(playerColor);
				System.out.println("Player picked card: " + currentCard.cardNumber);
				
				evaluateGameState();
				
				// check if there is any move available, if not then skip turn
				boolean moveAvailable = checkMoves(playerColor);
				if (moveAvailable == false) {
					skipTurn();
				}
			}
		});
		
		// tile events
		for (Tile tile : tilesArray) {
			tile.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					if (SwingUtilities.isRightMouseButton(me)) {
						rightClick();
						return;
					}
					
					if (gameState == GamePanel.SELECTING_PLAYER) {
						selectPlayer((Tile)me.getSource());
					} else if (gameState == GamePanel.SELECTING_MOVE) {
						selectMove((Tile)me.getSource());
					} else if (gameState == GamePanel.CARD_SEVEN) {
						if (card7State == GamePanel.PICKING_FIRST_PAWN) {
							selectPlayer((Tile)me.getSource());
						} else if (card7State == GamePanel.PICKING_FIRST_MOVE) {
							selectMove((Tile)me.getSource());
						} else if (card7State == GamePanel.PICKING_SECOND_PAWN) {
							selectPlayer((Tile)me.getSource());
						} else if (card7State == GamePanel.PICKING_SECOND_MOVE) {
							selectMove((Tile)me.getSource());
						}
					} else if (gameState == GamePanel.SORRY) {
						selectSorryEnemy((Tile)me.getSource());
					}
				}
				
				public void mouseEntered(MouseEvent me) {
					if (gameState == GamePanel.SELECTING_PLAYER) {
						// highlight tile if own pawn
						Tile currentTile = (Tile)me.getSource();
						if (currentTile.hasPlayer()) {
							if (currentTile.getPlayer().color.equals(playerColor)) {
								currentTile.highlightTile(playerColor);
							}
						}
					}
					else if (gameState == GamePanel.SELECTING_MOVE) {
						// check if move is valid and highlight if so
						highlightPath((Tile)me.getSource());
					} else if (gameState == GamePanel.CARD_SEVEN) {
						if (card7State == GamePanel.PICKING_FIRST_PAWN) {
							Tile currentTile = (Tile)me.getSource();
							if (currentTile.hasPlayer() && currentTile.getPlayer().color.equals(playerColor)) {
								currentTile.highlightTile(playerColor);
							}
						} else if (card7State == GamePanel.PICKING_FIRST_MOVE) {
							highlightSeven((Tile)me.getSource());
						} else if (card7State == GamePanel.PICKING_SECOND_PAWN) {
							Tile currentTile = (Tile)me.getSource();
							if (currentTile.hasPlayer() && currentTile.getPlayer().color.equals(playerColor) && currentTile.getPlayer() != firstPawn) {
								currentTile.highlightTile(playerColor);
							}
						} else if (card7State == GamePanel.PICKING_SECOND_MOVE) {
							highlightSeven((Tile)me.getSource());
						}
					} else if (gameState == GamePanel.SORRY) {
						Tile currentTile = (Tile)me.getSource();
						if (currentTile.hasPlayer() && isEnemy(currentTile.getPlayer().color, playerColor)) {
							currentTile.highlightTile(playerColor);
						}
					}
				}
				
				public void mouseExited(MouseEvent me) {
					if (gameState == GamePanel.SELECTING_PLAYER) {
						Tile currentTile = (Tile)me.getSource();
						currentTile.unHighlightTile();
					} else if (gameState == GamePanel.SELECTING_MOVE) {
						unhighlightAllTiles();
					} else if (gameState == GamePanel.CARD_SEVEN) {
						if (card7State == GamePanel.PICKING_FIRST_PAWN || card7State == GamePanel.PICKING_SECOND_PAWN) {
							Tile currentTile = (Tile)me.getSource();
							if (currentTile.highlighted) {
								currentTile.unHighlightTile();
							}
						} else if (card7State == GamePanel.PICKING_FIRST_MOVE || card7State == GamePanel.PICKING_SECOND_MOVE) {
							unhighlightAllTiles();
						}
					} else if (gameState == GamePanel.SORRY) {
						Tile currentTile = (Tile)me.getSource();
						if (currentTile.highlighted) {
							currentTile.unHighlightTile();
						}
					}
				}
			});
			
			// add listener to safe zones
			if (tile instanceof SafeZoneEntryTile) {
				SafeZoneTile[] safeZone = ((SafeZoneEntryTile)tile).safeZone;
				for (Tile safeZoneTile : safeZone) {
					safeZoneTile.addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent me) {
							
							if (SwingUtilities.isRightMouseButton(me)) {
								rightClick();
								return;
							}
							
							if (gameState == GamePanel.SELECTING_PLAYER) {
								selectPlayer((Tile)me.getSource());
							} else if (gameState == GamePanel.SELECTING_MOVE) {
								selectMove((Tile)me.getSource());
							} else if (gameState == GamePanel.CARD_SEVEN) {
								if (card7State == GamePanel.PICKING_FIRST_PAWN) {
									selectPlayer((Tile)me.getSource());
								} else if (card7State == GamePanel.PICKING_FIRST_MOVE) {
									selectMove((Tile)me.getSource());
								} else if (card7State == GamePanel.PICKING_SECOND_PAWN) {
									selectPlayer((Tile)me.getSource());
								} else if (card7State == GamePanel.PICKING_SECOND_MOVE) {
									selectMove((Tile)me.getSource());
								}
							} 
						}
						
						public void mouseEntered(MouseEvent me) {
							if (gameState == GamePanel.SELECTING_PLAYER) {
								// highlight tile if own pawn
								Tile currentTile = (Tile)me.getSource();
								if (currentTile.hasPlayer()) {
									if (currentTile.getPlayer().color.equals(playerColor)) {
										currentTile.highlightTile(playerColor);
									}
								}
							}
							else if (gameState == GamePanel.SELECTING_MOVE) {
								// check if move is valid and highlight if so
								highlightPath((Tile)me.getSource());
							} else if (gameState == GamePanel.CARD_SEVEN) {
								if (card7State == GamePanel.PICKING_FIRST_PAWN) {
									Tile currentTile = (Tile)me.getSource();
									if (currentTile.hasPlayer() && currentTile.getPlayer().color.equals(playerColor)) {
										currentTile.highlightTile(playerColor);
									}
								} else if (card7State == GamePanel.PICKING_FIRST_MOVE) {
									highlightSeven((Tile)me.getSource());
								} else if (card7State == GamePanel.PICKING_SECOND_PAWN) {
									Tile currentTile = (Tile)me.getSource();
									if (currentTile.hasPlayer() && currentTile.getPlayer().color.equals(playerColor) && currentTile.getPlayer() != firstPawn) {
										currentTile.highlightTile(playerColor);
									}
								} else if (card7State == GamePanel.PICKING_SECOND_MOVE) {
									highlightSeven((Tile)me.getSource());
								}
							}
						}
						
						public void mouseExited(MouseEvent me) {
							if (gameState == GamePanel.SELECTING_PLAYER) {
								Tile currentTile = (Tile)me.getSource();
								currentTile.unHighlightTile();
							} else if (gameState == GamePanel.SELECTING_MOVE) {
								unhighlightAllTiles();
							} else if (gameState == GamePanel.CARD_SEVEN) {
								if (card7State == GamePanel.PICKING_FIRST_PAWN || card7State == GamePanel.PICKING_SECOND_PAWN) {
									Tile currentTile = (Tile)me.getSource();
									if (currentTile.highlighted) {
										currentTile.unHighlightTile();
									}
								} else if (card7State == GamePanel.PICKING_FIRST_MOVE || card7State == GamePanel.PICKING_SECOND_MOVE) {
									unhighlightAllTiles();
								}
							} 
						}
					});

				}
			}
		}
	}
	
	private void evaluateGameState() {
		if (currentCard.cardNumber == 7) {
			gameState = GamePanel.CARD_SEVEN;
			card7State = GamePanel.PICKING_FIRST_PAWN;
		} else if (currentCard.cardNumber == 13) {
			gameState = GamePanel.SORRY;
		} else {
			gameState = GamePanel.SELECTING_PLAYER;
		}
	}
	
	private void rightClick() {
		if (gameState == GamePanel.SELECTING_MOVE) {
			selectedPlayer.currentTile.unHighlightTile();
			gameState = GamePanel.SELECTING_PLAYER;
		} else if (gameState == GamePanel.CARD_SEVEN) {
			if (card7State == GamePanel.PICKING_FIRST_MOVE) {
				selectedPlayer.currentTile.unHighlightTile();
				card7State = GamePanel.PICKING_FIRST_PAWN;
			} else if (card7State == GamePanel.PICKING_SECOND_MOVE) {
				selectedPlayer.currentTile.unHighlightTile();
				card7State = GamePanel.PICKING_SECOND_PAWN;
			}
		}
		
		unhighlightAllTiles();
	}
	
	private void pickCard(Color pawnColor) {
		currentCard = cardDeck.get(0);
		cardDeck.remove(0);
		
		// only show message if player picked card
		if (pawnColor.equals(playerColor)) {
			JFrame currentFrame = (JFrame)SwingUtilities.getRoot(this);
			CardDialog cd = new CardDialog(currentFrame, currentCard);
			cd.setVisible(true);
		}
		
		if (cardDeck.isEmpty()) {
			rebuildCards();
		}
		
	}
	
	// checks if there is own player on tile or if should start a player (cards 1 and 2)
	private void selectPlayer(Tile tile) {
	
		if (gameState == GamePanel.CARD_SEVEN) {
			if (card7State == GamePanel.PICKING_FIRST_PAWN) {
				if (tile.hasPlayer() && tile.getPlayer().color.equals(playerColor)) {
					firstPawn = tile.getPlayer();
					selectedPlayer = tile.getPlayer();
					tile.highlightTile(playerColor);
					card7State = GamePanel.PICKING_FIRST_MOVE;
				}
			} else if (card7State == GamePanel.PICKING_SECOND_PAWN) {
				if (tile.hasPlayer() && tile.getPlayer().color.equals(playerColor) && tile.getPlayer() != firstPawn) {
					selectedPlayer = tile.getPlayer();
					tile.highlightTile(playerColor);
					card7State = GamePanel.PICKING_SECOND_MOVE;
				}
			}
		} else {

			// check for start tile
			if (currentCard.cardNumber == 1 || currentCard.cardNumber == 2) {
				Tile startTile = tilesArray[startTileForColor(playerColor)];
				if (tile == startTile) {
					if (canStartPawn(playerColor)) {
						if (!startTile.hasPlayer())  {
							startNewPawn(playerColor);
							finishTurn();
							return;
						} else if (startTile.hasPlayer() && isEnemy(playerColor, startTile.getPlayer().color)) {
							startNewPawn(playerColor);
							finishTurn();
							return;
						}
					}
				}
			} 
			

			if (tile.hasPlayer() && tile.getPlayer().color.equals(playerColor)) {
				selectedPlayer = tile.getPlayer();
				tile.highlightTile(playerColor);
				gameState = GamePanel.SELECTING_MOVE;
			}
		}
	}
	
	private void selectMove(Tile tile) {
		Tile destTile = null;
		boolean moveSuccess = false;
		// get dest tiles for cards
		if (currentCard.cardNumber == 1) {
			destTile = getDestTileForward(selectedPlayer, 1);
			if (tile == destTile) {
				moveSuccess = movePawn(selectedPlayer, 1);
			}
		} else if (currentCard.cardNumber == 2) {
			destTile = getDestTileForward(selectedPlayer, 2);
			if (tile == destTile) {
				moveSuccess = movePawn(selectedPlayer, 2);
			}
		} else if (currentCard.cardNumber == 3) {
			destTile = getDestTileForward(selectedPlayer, 3);
			if (tile == destTile) {
				moveSuccess = movePawn(selectedPlayer, 3); 
			}
		} else if (currentCard.cardNumber == 4) {
			destTile = getDestTileBack(selectedPlayer, 4);
			if (tile == destTile) {
				moveSuccess = movePawnBack(selectedPlayer, 4);
			}
		} else if (currentCard.cardNumber == 5) {
			destTile = getDestTileForward(selectedPlayer, 5);
			if (tile == destTile) {
				moveSuccess = movePawn(selectedPlayer, 5);
			}
		} else if (currentCard.cardNumber == 7) {
			Tile[] destTiles = new Tile[7];
			destTiles[0] = getDestTileForward(selectedPlayer, 1);
			destTiles[1] = getDestTileForward(selectedPlayer, 2);
			destTiles[2] = getDestTileForward(selectedPlayer, 3);
			destTiles[3] = getDestTileForward(selectedPlayer, 4);
			destTiles[4] = getDestTileForward(selectedPlayer, 5);
			destTiles[5] = getDestTileForward(selectedPlayer, 6);
			destTiles[6] = getDestTileForward(selectedPlayer, 7);	
			
			if (card7State == GamePanel.PICKING_FIRST_MOVE) {
				for (int i=0; i < destTiles.length; i++) {
					if (tile == destTiles[i] && firstMoveValid(selectedPlayer, i+1)) {
						moveSuccess = movePawn(selectedPlayer, i+1);
						if (moveSuccess) {
							firstMoveSteps = i+1;
							if (firstMoveSteps == 7) {
								unhighlightAllTiles();
								card7State = GamePanel.PICKING_FIRST_PAWN;
								finishTurn();
								return;
							}
							secondMoveSteps = 7-firstMoveSteps;
						}
						break;
					}
				}
			} else if (card7State == GamePanel.PICKING_SECOND_MOVE) {
				if (tile == destTiles[secondMoveSteps-1]) {
					moveSuccess = movePawn(selectedPlayer, secondMoveSteps);
				}
			}
			
		} else if (currentCard.cardNumber == 8) {
			destTile = getDestTileForward(selectedPlayer, 8);
			if (tile == destTile) {
				moveSuccess = movePawn(selectedPlayer, 8);
			}
		} else if (currentCard.cardNumber == 10) {
			destTile = getDestTileForward(selectedPlayer, 10);
			Tile destTileBack = getDestTileBack(selectedPlayer, 1);
			
			if (tile == destTile) {
				moveSuccess = movePawn(selectedPlayer, 10);
			} else if (tile == destTileBack) {
				moveSuccess = movePawnBack(selectedPlayer, 1);
			}
		} else if (currentCard.cardNumber == 11) {
			destTile = getDestTileForward(selectedPlayer, 11);
			
			if (tile == destTile) {
				moveSuccess = movePawn(selectedPlayer, 11);
			} else {
				if (isEnemy(tile.getPlayer().color, playerColor) && !(tile instanceof SafeZoneTile) && !(selectedPlayer.currentTile instanceof SafeZoneTile)) {
					Tile sourceTile = selectedPlayer.currentTile;
					sourceTile.setPlayer(tile.getPlayer());
					tile.setPlayer(selectedPlayer);
					moveSuccess = true;
				}
			}
		} else if (currentCard.cardNumber == 12) {
			destTile = getDestTileForward(selectedPlayer, 12);
			if (tile == destTile) {
				moveSuccess = movePawn(selectedPlayer, 12);
			}
		}
		
		if (moveSuccess) {
			if (gameState == GamePanel.CARD_SEVEN) {
				if (card7State == GamePanel.PICKING_FIRST_MOVE) {
					card7State = GamePanel.PICKING_SECOND_PAWN;
					selectedPlayer.currentTile.unHighlightTile();
					unhighlightAllTiles();
				} else if (card7State == GamePanel.PICKING_SECOND_MOVE) {
					unhighlightAllTiles();
					finishTurn();
				}
			} else {
				unhighlightAllTiles();
				card7State = GamePanel.PICKING_FIRST_PAWN;
				finishTurn();
			}
		}
	}
	
	private void selectSorryEnemy(Tile tile) {
		if (tile.hasPlayer()) {
			if (isEnemy(tile.getPlayer().color, playerColor) && !(tile instanceof SafeZoneTile)) {
				bumpPlayer(tile.getPlayer());

				Player newPlayer = new Player(playerColor);
				getPawns(playerColor).add(newPlayer);
				tile.setPlayer(newPlayer);
				
				decreaseStartCountForColor(playerColor);
				
				tile.unHighlightTile();
				finishTurn();
			}
		}
	}
	
	private void finishTurn() {
		gameState = GamePanel.IDLE;
		cardButton.setEnabled(true);
		cardButton.requestFocus();
		if (selectedPlayer != null) {
			selectedPlayer.currentTile.unHighlightTile();
			selectedPlayer = null;
		}
		
		if (currentCard.cardNumber != 2) {
			moveBots();
		}
	}
	
	private Tile getDestTileForward(Player pawn, int spaces) {
		Tile destTile = pawn.currentTile;
		if (destTile instanceof HomeTile) {
			return null;
		}
		
		for (int i=0; i < spaces; i++) {
			if (destTile instanceof SafeZoneEntryTile && destTile.tileColor.equals(pawn.color)) {
				destTile = ((SafeZoneEntryTile)destTile).firstSafeZoneTile;
			} else {
				destTile = destTile.nextTile;
			}
			
			if (destTile instanceof HomeTile) {
				return destTile;
			}
		}
		
		return destTile;
	}
	
	private Tile getDestTileBack(Player pawn, int spaces) {
		Tile destTile = pawn.currentTile;
		for (int i=0; i < spaces; i++) {
			destTile = destTile.previousTile;
		}
		
		return destTile;
	}
	
	private void highlightPath(Tile hoverTile) {
		Tile destTile = null;
		Tile destTileBack = null;
		
		if (currentCard.cardNumber == 1) {
			destTile = getDestTileForward(selectedPlayer, 1);
		} else if (currentCard.cardNumber == 2) {
			destTile = getDestTileForward(selectedPlayer, 2);
		} else if (currentCard.cardNumber == 3) {
			destTile = getDestTileForward(selectedPlayer, 3);
		} else if (currentCard.cardNumber == 4) {
			destTile = getDestTileBack(selectedPlayer, 4);
		} else if (currentCard.cardNumber == 5) {
			destTile = getDestTileForward(selectedPlayer, 5);
		}  else if (currentCard.cardNumber == 8) {
			destTile = getDestTileForward(selectedPlayer, 8);
		} else if (currentCard.cardNumber == 10) {
			destTile = getDestTileForward(selectedPlayer, 10);
			destTileBack = getDestTileBack(selectedPlayer, 1);
		} else if (currentCard.cardNumber == 11) {
			destTile = getDestTileForward(selectedPlayer, 11);
		} else if (currentCard.cardNumber == 12) {
			destTile = getDestTileForward(selectedPlayer, 12);
		}
		
		if (currentCard.cardNumber == 10) {
			if (hoverTile == destTile) {
				if (isValidTile(hoverTile)) { 
					highlightHelper(selectedPlayer.currentTile, hoverTile);
				}
			} else if (hoverTile == destTileBack) {
				if (isValidTile(hoverTile)) {
					highlightBackHelper(selectedPlayer.currentTile, hoverTile);
				}
			}
		} else {
			if (hoverTile == destTile) {
				if (isValidTile(hoverTile)) {
					if (currentCard.cardNumber == 4) {
						highlightBackHelper(selectedPlayer.currentTile, hoverTile);
					} else {
						highlightHelper(selectedPlayer.currentTile, hoverTile);
					}
				}
			}
		}
	}
	
	private void highlightSeven(Tile hoverTile) {
		Tile[] destTiles = new Tile[7];
		destTiles[0] = getDestTileForward(selectedPlayer, 1);
		destTiles[1] = getDestTileForward(selectedPlayer, 2);
		destTiles[2] = getDestTileForward(selectedPlayer, 3);
		destTiles[3] = getDestTileForward(selectedPlayer, 4);
		destTiles[4] = getDestTileForward(selectedPlayer, 5);
		destTiles[5] = getDestTileForward(selectedPlayer, 6);
		destTiles[6] = getDestTileForward(selectedPlayer, 7);
		
		if (card7State == GamePanel.PICKING_FIRST_MOVE) {
			for (int i=0; i < destTiles.length; i++) {
				if (hoverTile == destTiles[i] && isValidTile(hoverTile) && firstMoveValid(selectedPlayer, i+1)) {
					highlightHelper(selectedPlayer.currentTile, hoverTile);
				}
			}
		} else if (card7State == GamePanel.PICKING_SECOND_MOVE) {
			if (hoverTile == destTiles[secondMoveSteps-1] && isValidTile(hoverTile)) {
				highlightHelper(selectedPlayer.currentTile, hoverTile);
			}
		}
		
	}
	
	private void highlightHelper(Tile startTile, Tile endTile) {
		Tile tempTile;
		if (startTile instanceof SafeZoneEntryTile && startTile.tileColor.equals(selectedPlayer.color)) {
			tempTile = ((SafeZoneEntryTile)startTile).firstSafeZoneTile;
		} else {
			tempTile = startTile.nextTile;
		}
		while (tempTile != endTile) {
			tempTile.highlightTile(playerColor);
			
			if (tempTile instanceof SafeZoneEntryTile && tempTile.tileColor.equals(selectedPlayer.color)) {
				tempTile = ((SafeZoneEntryTile)tempTile).firstSafeZoneTile;
			} else {
				tempTile = tempTile.nextTile;
			}
		}
		tempTile.highlightTile(playerColor);
		
	}
	
	private void highlightBackHelper(Tile startTile, Tile endTile) {
		Tile tempTile = startTile.previousTile;
		while (tempTile != endTile) {
			tempTile.highlightTile(playerColor);
			tempTile = tempTile.previousTile;
		}
		tempTile.highlightTile(playerColor);

	}
	
	private void unhighlightAllTiles() {
		for (Tile tile : tilesArray) {
			if (tile != selectedPlayer.currentTile) {
				if (tile.highlighted) {
					tile.unHighlightTile();
				}
			} 
			
			if (tile instanceof SafeZoneEntryTile) {
				SafeZoneTile[] safeZone = ((SafeZoneEntryTile)tile).safeZone;
				for (Tile safeZoneTile : safeZone) {
					if (safeZoneTile != selectedPlayer.currentTile && safeZoneTile.highlighted) {
						safeZoneTile.unHighlightTile();
					}
				}
			}
		}
	}
	/*
	private void tileClicked(Tile tile) {
		boolean moveMade = false;
		
		if (currentCard.cardNumber == 1) {
			Tile startTile = tilesArray[startTileForColor(playerColor)];
			if (tile == startTile) {
				if (!startTile.hasPlayer()) {
					moveMade = startNewPawn(playerColor);
				} else if (startTile.hasPlayer()) {
					// own pawn
					if (isNotBot(startTile.getPlayer())) {
						moveMade = movePawn(startTile.getPlayer(), 1);
					} else {		// enemy pawn
						moveMade = startNewPawn(playerColor);
					}
				}
			} else {
				if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
					moveMade = movePawn(tile.getPlayer(), 1);
				}
			}
		} else if (currentCard.cardNumber == 2) { 
			Tile startTile = tilesArray[startTileForColor(playerColor)];
			if (tile == startTile) {
				if (!startTile.hasPlayer()) {
					moveMade = startNewPawn(playerColor);
				} else if (startTile.hasPlayer()) {
					if (isNotBot(startTile.getPlayer())) {
						moveMade = movePawn(startTile.getPlayer(), 2);
					} else {
						moveMade = startNewPawn(playerColor);
					}
				}
			} else {
				if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
					moveMade = movePawn(tile.getPlayer(), 2);
				}
			}
		} else if (currentCard.cardNumber == 3) {
			if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
				moveMade = movePawn(tile.getPlayer(), 3);
			}
		} else if (currentCard.cardNumber == 4) { 
			if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
				moveMade = movePawnBack(tile.getPlayer(), 4);
			}
		} else if (currentCard.cardNumber == 5) {
			if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
				moveMade = movePawn(tile.getPlayer(), 5);
			} 
		} else if (currentCard.cardNumber == 7) {
			if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
				
				if (pickingSecondPawn && !(tile.getPlayer() == firstPawnPicked)) {
					if (splitChoice == SIX) {
						if (canMoveForward(firstPawnPicked, 6) && canMoveForward(tile.getPlayer(), 1)) {
							movePawn(firstPawnPicked, 6);
							movePawn(tile.getPlayer(), 1);
							moveMade = true;
						} 
					} else if (splitChoice == FIVE) {
						if (canMoveForward(firstPawnPicked, 5) && canMoveForward(tile.getPlayer(), 2)) {
							movePawn(firstPawnPicked, 5);
							movePawn(tile.getPlayer(), 2);
							moveMade = true;
						}
					} else if (splitChoice == FOUR) {
						if (canMoveForward(firstPawnPicked, 4) && canMoveForward(tile.getPlayer(), 3)) {
							movePawn(firstPawnPicked, 4);
							movePawn(tile.getPlayer(), 3);
							moveMade = true;
						}
					}
					
					pickingSecondPawn = false;
					firstPawnPicked = null;
					splitChoice = -1;
					
				} else {
					// show options
					Object[] options = {"Move forward 7", "Split 6 and 1", "Split 5 and 2", "Split 4 and 3"};
					int choice = JOptionPane.showOptionDialog(this, "Choose option", "Card 7", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					
					if (choice == 0) {
						moveMade = movePawn(tile.getPlayer(), 7);
					} else if (choice == 1) {
						if (canSplit(tile.getPlayer(), 6)) {
							JOptionPane.showMessageDialog(this, "Now pick pawn to move 1 forward");
							firstPawnPicked = tile.getPlayer();
							splitChoice = SIX;
							pickingSecondPawn = true;
						} else {
							JOptionPane.showMessageDialog(this, "Not possible to split this way, try again.");
						}
					} else if (choice == 2) {
						if (canSplit(tile.getPlayer(), 5)) {
							JOptionPane.showMessageDialog(this, "Now pick pawn to move 2 forward");
							firstPawnPicked = tile.getPlayer();
							splitChoice = FIVE;
							pickingSecondPawn = true;
						} else {
							JOptionPane.showMessageDialog(this, "Not possible to split this way, try again.");
						}
					} else if (choice == 3) {
						if (canSplit(tile.getPlayer(), 4)) {
							JOptionPane.showMessageDialog(this, "Not pick pawn to move 3 forward");
							firstPawnPicked = tile.getPlayer();
							splitChoice = FOUR;
							pickingSecondPawn = true;
						} else {
							JOptionPane.showMessageDialog(this, "Not possible to split this way, try again.");
						}
					}
				}
			}
		} else if (currentCard.cardNumber == 8) {
			if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
				moveMade = movePawn(tile.getPlayer(), 8);
			}
		} else if (currentCard.cardNumber == 10) {
			if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
				// show option dialog
				Object[] options = {"Move forward 10", "Move back 1"};
				int choice = JOptionPane.showOptionDialog(this, "What would you like to do?", "Card 10", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				
				if (choice == 0) {
					moveMade = movePawn(tile.getPlayer(), 10);
				} else {
					moveMade = movePawnBack(tile.getPlayer(), 1);
				}	
			}
		} else if (currentCard.cardNumber == 11) {
			if (pickingSwitchPawn) {
				if (tile.hasPlayer() && isEnemy(playerColor, tile.getPlayer().color) && !(tile instanceof SafeZoneTile)) {
					Tile sourceTile = sourcePawn.currentTile;
					sourceTile.setPlayer(tile.getPlayer());
					tile.setPlayer(sourcePawn);
					moveMade = true;
				}
					
				pickingSwitchPawn = false;
				sourcePawn = null;
			} else {
				if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
					Object[] options = {"Move forward 11", "Switch with enemy pawn"};
					int choice = JOptionPane.showOptionDialog(this, "What would you like to do?", "Card 11", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				
					if (choice == 0) {
						moveMade = movePawn(tile.getPlayer(), 11);
					} else {
						if (canSwitch(playerColor)) {
							pickingSwitchPawn = true;
							sourcePawn = tile.getPlayer();
						} else {
							JOptionPane.showMessageDialog(this, "No available enemy pawns to switch with!");
						}
					}
				}
			}
		} else if (currentCard.cardNumber == 12) {
			if (tile.hasPlayer() && isNotBot(tile.getPlayer())) {
				moveMade = movePawn(tile.getPlayer(), 12);
			}
		} else if (currentCard.cardNumber == 13) {
			if (tile.hasPlayer() && isEnemy(playerColor, tile.getPlayer().color) && !(tile instanceof SafeZoneTile)) {
				bumpPlayer(tile.getPlayer());

				Player newPlayer = new Player(playerColor);
				getPawns(playerColor).add(newPlayer);
				tile.setPlayer(newPlayer);
				
				decreaseStartCountForColor(playerColor);
				moveMade = true;
			}
		} 
			
		if (!pickingSecondPawn && !pickingSwitchPawn) {
			if (moveMade) {
				if (currentCard.cardNumber != 2) {
					moveBots();
				}
				
				waitingForMove = false;
				cardButton.setEnabled(true);
				cardButton.requestFocus();
			} else {
				JOptionPane.showMessageDialog(this, "Invalid move!");
			}
		}
	} */
	
	private boolean checkMoves(Color pawnColor) {
		boolean moveAvailable = false;
		ArrayList<Player> pawns = getPawns(pawnColor);
		
		if (currentCard.cardNumber == 1) {
			if (canStartPawn(pawnColor)) {
				moveAvailable = true;
			} else {
				for (Player pawn : pawns) {
					if (canMoveForward(pawn, 1)) {
						moveAvailable = true;
						break;
					}
				}
			}
		} else if (currentCard.cardNumber == 2) {
			if (canStartPawn(pawnColor)) {
				moveAvailable = true;
			} else {
				for (Player pawn : pawns) {
					if (canMoveForward(pawn, 2)) {
						moveAvailable = true;
						break;
					}
				}
			}
		} else if (currentCard.cardNumber == 3) {
			for (Player pawn : pawns) {
				if (canMoveForward(pawn, 3)) {
					moveAvailable = true;
					break;
				}
			}
		} else if (currentCard.cardNumber == 4) {
			for (Player pawn : pawns) {
				if (canMoveBack(pawn, 4)) {
					moveAvailable = true;
					break;
				}
			}
		} else if (currentCard.cardNumber == 5) {
			for (Player pawn : pawns) {
				if (canMoveForward(pawn, 5)) {
					moveAvailable = true;
					break;
				}
			}
		} else if (currentCard.cardNumber == 7) {
			ArrayList<Player> possiblePawns;
			for (Player pawn : pawns) {
				if (canMoveForward(pawn, 7)) {
					moveAvailable = true;
					break;
				} else if (canMoveForward(pawn, 6)) {
					possiblePawns = new ArrayList<Player>(pawns);
					possiblePawns.remove(pawn);
					for (Player remainingPawn : possiblePawns) {
						if (canMoveForward(remainingPawn, 1)) {
							moveAvailable = true;
							break;
						}
					}
					if (moveAvailable == true) { break; }
				} else if (canMoveForward(pawn, 5)) {
					possiblePawns = new ArrayList<Player>(pawns);
					possiblePawns.remove(pawn);
					for (Player remainingPawn : possiblePawns) {
						if (canMoveForward(remainingPawn, 2)) {
							moveAvailable = true;
							break;
						}
					}
					if (moveAvailable == true) { break; }
				} else if (canMoveForward(pawn, 4)) {
					possiblePawns = new ArrayList<Player>(pawns);
					possiblePawns.remove(pawn);
					for (Player remainingPawn : possiblePawns) {
						if (canMoveForward(remainingPawn, 3)) {
							moveAvailable = true;
							break;
						}
					}
					if (moveAvailable == true) { break; }
				} 
			}
		} else if (currentCard.cardNumber == 8) {
			for (Player pawn : pawns) {
				if (canMoveForward(pawn, 8)) {
					moveAvailable = true;
					break;
				}
			}
		} else if (currentCard.cardNumber == 10) {
			for (Player pawn : pawns) {
				if (canMoveForward(pawn, 10) || canMoveBack(pawn, 1)) {
					moveAvailable = true;
					break;
				}
			}
		} else if (currentCard.cardNumber == 11) {
			for (Player pawn : pawns) {
				if (canMoveForward(pawn, 11)) {
					moveAvailable = true;
					break;
				}
			}
			
			if (!getPawns(pawnColor).isEmpty()) {
				ArrayList<ArrayList<Player>> otherPawnSets = getOtherPawns(pawnColor);
				
				for (ArrayList<Player> pawnSet : otherPawnSets) {
					if (!pawnSet.isEmpty()) {
						for (Player otherPawn : pawnSet ) {
							if (!(otherPawn.currentTile instanceof SafeZoneTile)) {
								moveAvailable = true;
								break;
							}
						}
						if (moveAvailable) { break; }
					}
				}
			}
		} else if (currentCard.cardNumber == 12) {
			for (Player pawn : pawns) {
				if (canMoveForward(pawn, 12)) {
					moveAvailable = true;
					break;
				}
			}
		} else if (currentCard.cardNumber == 13) {
			if (startCountForColor(pawnColor) > 0) {
				
				ArrayList<ArrayList<Player>> otherPawnSets = getOtherPawns(pawnColor);
				for (ArrayList<Player> pawnSet : otherPawnSets) {
					if (!pawnSet.isEmpty()) {
						for (Player otherPawn : pawnSet) {
							if (!(otherPawn.currentTile instanceof SafeZoneTile)) {
								moveAvailable = true;
								break;
							}
						}
						if (moveAvailable) { break; }
					}
				}
			}
		}
		
		return moveAvailable;
	}
	
	private boolean startNewPawn(Color pawnColor) {
		if (canStartPawn(pawnColor)) {
			Tile startTile = tilesArray[startTileForColor(pawnColor)];
			if (startTile.hasPlayer() && isEnemy(pawnColor, startTile.getPlayer().color)) {
				bumpPlayer(startTile.getPlayer());
			}
			
			Player newPlayer = new Player(pawnColor);
			getPawns(pawnColor).add(newPlayer);
			
			startTile.setPlayer(newPlayer);
			decreaseStartCountForColor(pawnColor);
			return true;
		} else {
			return false;
		}
	}
	
	private boolean canStartPawn(Color pawnColor) {
		if (startCountForColor(pawnColor) > 0) {
			Tile startTile = tilesArray[startTileForColor(pawnColor)];
			if (startTile.hasPlayer() && isFriend(pawnColor, startTile.getPlayer().color)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	
	private boolean movePawn(Player pawn, int spaces) {
		
		Tile sourceTile = pawn.currentTile;
		Tile destTile = pawn.currentTile;
		
		if (canMoveForward(pawn, spaces)) {
			for (int i=0; i < spaces; i++) {
				if (destTile instanceof SafeZoneEntryTile && destTile.tileColor.equals(pawn.color)) {
					destTile = ((SafeZoneEntryTile)destTile).firstSafeZoneTile;
				} else {
					destTile = destTile.nextTile;
					
					if (destTile instanceof HomeTile) {
						homeReached(pawn.color);
						getPawns(pawn.color).remove(pawn);
						break;
					}
				}
			}	
			
			if (destTile.hasPlayer() && isEnemy(pawn.color, destTile.getPlayer().color)) {
				bumpPlayer(destTile.getPlayer());
			} 
			destTile.setPlayer(pawn);
			sourceTile.removePlayer();
			
			// check for slide
			if (destTile.isSlide && !destTile.tileColor.equals(pawn.color)) {
				slidePawn(pawn);
			}
			
			return true;
		} else {
			return false;
		}
	
	}
	
	private boolean canMoveForward(Player pawn, int spaces) {
		Tile destTile = pawn.currentTile;
		
		// pawn is in home already
		if (destTile instanceof HomeTile) {
			return false;
		}
		
		for (int i=0; i < spaces; i++) {
			if (destTile instanceof SafeZoneEntryTile && destTile.tileColor.equals(pawn.color)) {
				destTile = ((SafeZoneEntryTile)destTile).firstSafeZoneTile;
			} else {
				destTile = destTile.nextTile;
			}
			
			if (destTile instanceof HomeTile) {
				break;
			}
			// check if there is own player in it
			if (destTile.hasPlayer() && !isEnemy(pawn.color, destTile.getPlayer().color) && !(destTile instanceof HomeTile)) {
				return false;
			}
		}
		
		// check for slides
		if (destTile.isSlide && !destTile.tileColor.equals(pawn.color)) {
			while (destTile.isSlide) {
				destTile = destTile.nextTile;
			}
			
			if (destTile.hasPlayer() && isFriend(pawn.color, destTile.getPlayer().color)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean movePawnBack(Player pawn, int spaces) {
		Tile sourceTile = pawn.currentTile;
		Tile destTile = pawn.currentTile;
		
		if (canMoveBack(pawn, spaces)) {
			for (int i=0; i < spaces; i++) {
				destTile = destTile.previousTile;
			}
			
			if (destTile.hasPlayer()) {
				bumpPlayer(destTile.getPlayer());
			}
			destTile.setPlayer(pawn);
			sourceTile.removePlayer();
			
			// check for slide
			if (destTile.isSlide && !destTile.tileColor.equals(pawn.color)) {
				slidePawn(pawn);
			}
			
			return true;
		} else {
			return false;
		}
		
	}
	
	private boolean canMoveBack(Player pawn, int spaces) {
		Tile destTile = pawn.currentTile;
		
		// home tile
		if (destTile instanceof HomeTile) {
			return false;
		}
		
		for (int i=0; i < spaces; i++) {
			destTile = destTile.previousTile;
			
			if (destTile.hasPlayer() && isFriend(pawn.color, destTile.getPlayer().color)) {
				return false;
			}
		}
		
		// check for slides
		if (destTile.isSlide && !destTile.tileColor.equals(pawn.color)) {
			while (destTile.isSlide) {
				destTile = destTile.nextTile;
			}
			
			if (destTile.hasPlayer() && isFriend(pawn.color, destTile.getPlayer().color)) {
				return false;
			}
		}
		
		return true;
	}
	
	private void slidePawn(Player pawn) {
		Tile sourceTile = pawn.currentTile;
		Tile destTile = pawn.currentTile;
		
		while (destTile.isSlide) {
			destTile = destTile.nextTile;
			
			// bump enemies
			if (destTile.hasPlayer() && isEnemy(pawn.color, destTile.getPlayer().color)) {
				bumpPlayer(destTile.getPlayer());
			}
		}
		
		// reached first non sliding tile
		if (destTile.hasPlayer() && isEnemy(destTile.getPlayer().color, pawn.color)) {
			bumpPlayer(destTile.getPlayer());
		}
		destTile.setPlayer(pawn);
		sourceTile.removePlayer();
	}
	
	private void homeReached(Color color) {
		increaseHomeCountForColor(color);
		
		if (homeCountForColor(color) == 4) {
			JOptionPane.showMessageDialog(this, "Player " + colorName(color) + " is the winner!");

			int totalScore = calculateScoreForPlayer();
			String endText = "Your total score was: " + totalScore + ". Enter your name to save score.";
			String newEntry = JOptionPane.showInputDialog(endText);
			if (!newEntry.isEmpty()) {
				Sorry.addToTopScores(newEntry, totalScore);
			}
			
			if (delegate != null) {
				delegate.gameEnded();
			}
		}
	}
	
	private boolean isEnemy(Color pawnColor, Color otherColor) {
		if (pawnColor.equals(otherColor)) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean isFriend(Color pawnColor, Color otherColor) {
		if (pawnColor.equals(otherColor)) {
			return true;
		} else {
			return false;
		}
	}
	

	private void bumpPlayer(Player player) {
		player.currentTile.removePlayer();
		player.removeFromGame();
		
		getPawns(player.color).remove(player);
		
		increaseStartCountForColor(player.color);
	}
	
	private void moveBots() {
		for (Bot bot : bots) {
			ArrayList<Player> botPawns = getPawns(bot.color);
			// pick a card for the bot
			pickCard(bot.color);
			System.out.println(colorName(bot.color) + " picked card: " + currentCard.cardNumber);
			
			// check if there is any available move
			boolean moveAvailable = checkMoves(bot.color);
			
			if (moveAvailable) {
				// get most progressed pawn and least progressed pawn;
				Player mostProgressedPawn = null;
				Player leastProgressedPawn = null;
				for (Player pawn : botPawns) {
					if (mostProgressedPawn == null && !(pawn.currentTile instanceof HomeTile)) {
						mostProgressedPawn = pawn;
					} else {
						if (distanceToHome(pawn) < distanceToHome(mostProgressedPawn) && !(pawn.currentTile instanceof HomeTile)) {
							mostProgressedPawn = pawn;
						}
					}
					
					if (leastProgressedPawn == null && !(pawn.currentTile instanceof HomeTile)) {
						leastProgressedPawn = pawn;
					} else {
						if (distanceToHome(pawn) > distanceToHome(leastProgressedPawn) && !(pawn.currentTile instanceof HomeTile)) {
							leastProgressedPawn = pawn;
						}
					}
				}
			
				// paste back up below
				if (currentCard.cardNumber == 1) {
					// Check if we can start a pawn
					if (canStartPawn(bot.color)) {
						startNewPawn(bot.color);
					} else {	// find possible move
						movePawn(mostProgressedPawn, 1);
					}
				} else if (currentCard.cardNumber == 2) {
					// check if we can start a pawn
					if (canStartPawn(bot.color)) {
						startNewPawn(bot.color);
					} else {
						movePawn(mostProgressedPawn, 2);
					}
				} else if (currentCard.cardNumber == 3) {
					movePawn(mostProgressedPawn, 3);
				} else if (currentCard.cardNumber == 4) {
					movePawnBack(mostProgressedPawn, 4);
				} else if (currentCard.cardNumber == 5) {
					movePawn(mostProgressedPawn, 5);
				} else if (currentCard.cardNumber == 7) {
					for (Player pawn : botPawns) {
						if (canMoveForward(pawn, 7)) {
							movePawn(pawn, 7);
							break;
						} else if (canMoveForward(pawn, 6)) {
							boolean splitAccepted = false;
							ArrayList<Player> remainingPawns = new ArrayList<Player>(botPawns);
							remainingPawns.remove(pawn);
							for (Player otherPawn : remainingPawns) {
								if (canMoveForward(otherPawn, 1)) {
									movePawn(pawn, 6);
									movePawn(otherPawn, 1);
									splitAccepted = true;
									break;
								}
							}
							if (splitAccepted) { break; }
						} else if (canMoveForward(pawn, 5)) {
							boolean splitAccepted = false;
							ArrayList<Player> remainingPawns = new ArrayList<Player>(botPawns);
							remainingPawns.remove(pawn);
							for (Player otherPawn : remainingPawns) {
								if (canMoveForward(otherPawn, 2)) {
									movePawn(pawn, 5);
									movePawn(otherPawn, 2);
									splitAccepted = true;
									break;
								}
							}
							if (splitAccepted) { break; }
						} else if (canMoveForward(pawn, 4)) {
							boolean splitAccepted = false;
							ArrayList<Player> remainingPawns = new ArrayList<Player>(botPawns);
							remainingPawns.remove(pawn);
							for (Player otherPawn : remainingPawns) {
								if (canMoveForward(otherPawn, 3)) {
									movePawn(pawn, 4);
									movePawn(otherPawn, 3);
									splitAccepted = true;
									break;
								}
							}
							if (splitAccepted) { break; }
						}
					}
				} else if (currentCard.cardNumber == 8) {
					movePawn(mostProgressedPawn, 8);
				} else if (currentCard.cardNumber == 10) {
					
					if (canMoveBack(leastProgressedPawn, 1)) {
						movePawnBack(leastProgressedPawn, 1);
					} else {
						movePawn(mostProgressedPawn, 10);
					}
				} else if (currentCard.cardNumber == 11) {
					movePawn(mostProgressedPawn, 11);
				} else if (currentCard.cardNumber == 12) {
					movePawn(mostProgressedPawn, 12);
				} else if (currentCard.cardNumber == 13) {
					ArrayList<ArrayList<Player>> otherPawnSets = getOtherPawns(bot.color);
					
					boolean switched = false;
					for (ArrayList<Player> pawnSet : otherPawnSets) {
						for (Player enemyPawn : pawnSet) {
							if (!(enemyPawn.currentTile instanceof SafeZoneTile)) {
								// switch
								Tile enemyTile = enemyPawn.currentTile;
								bumpPlayer(enemyPawn);
								
								Player newPlayer = new Player(bot.color);
								getPawns(bot.color).add(newPlayer);
								enemyTile.setPlayer(newPlayer);
								decreaseStartCountForColor(bot.color);
								switched = true;
								break;
							}
						}
						if (switched) { break; }
					}
				}
			}
			
		}
		
		System.out.println("=======================");
		// pastback up above
	}
	
	private int startTileForColor(Color color) {
		if (color.equals(Color.yellow)) { 
			return YELLOW_START_TILE;
		} else if (color.equals(Color.green)) {
			return GREEN_START_TILE;
		} else if (color.equals(Color.red)) {
			return RED_START_TILE;
		} else if (color.equals(Color.blue)) {
			return BLUE_START_TILE;
		} else {
			return -1;
		}
	}
	
	private int startCountForColor(Color color) {
		if (color.equals(Color.yellow)) {
			return yellowStartCount;
		} else if (color.equals(Color.green)) {
			return greenStartCount;
		} else if (color.equals(Color.red)) {
			return redStartCount;
		} else if (color.equals(Color.blue)) {
			return blueStartCount;
		} else {
			return -1;
		}
	}
	
	private void decreaseStartCountForColor(Color color) {
		if (color.equals(Color.yellow)) {
			yellowStartCount--;
			yStartLabel.setText(Integer.toString(yellowStartCount));
		} else if (color.equals(Color.green)) {
			greenStartCount--;
			gStartLabel.setText(Integer.toString(greenStartCount));
		} else if (color.equals(Color.red)) {
			redStartCount--;
			rStartLabel.setText(Integer.toString(redStartCount));
		} else if (color.equals(Color.blue)) {
			blueStartCount--;
			bStartLabel.setText(Integer.toString(blueStartCount));
		}
	}
	
	private void increaseStartCountForColor(Color color) {
		if (color.equals(Color.yellow)) {
			yellowStartCount++;
			yStartLabel.setText(Integer.toString(yellowStartCount));
		} else if (color.equals(Color.green)) {
			greenStartCount++;
			gStartLabel.setText(Integer.toString(greenStartCount));
		} else if (color.equals(Color.red)) {
			redStartCount++;
			rStartLabel.setText(Integer.toString(redStartCount));
		} else if (color.equals(Color.blue)) {
			blueStartCount++;
			bStartLabel.setText(Integer.toString(blueStartCount));
		}
	}
	
	private void increaseHomeCountForColor(Color color) {
		if (color.equals(Color.yellow)) {
			yellowHomeCount++;
			yHomeLabel.setText(Integer.toString(yellowHomeCount));
		} else if (color.equals(Color.green)) {
			greenHomeCount++;
			gHomeLabel.setText(Integer.toString(greenHomeCount));
		} else if (color.equals(Color.red)) {
			redHomeCount++;
			rHomeLabel.setText(Integer.toString(redHomeCount));
		} else if (color.equals(Color.blue)) {
			blueHomeCount++;
			bHomeLabel.setText(Integer.toString(blueHomeCount));
		}
	}
	
	private int homeCountForColor(Color color) {
		if (color.equals(Color.yellow)) {
			return yellowHomeCount;
		} else if (color.equals(Color.green)) {
			return greenHomeCount;
		} else if (color.equals(Color.red)) {
			return redHomeCount;
		} else if (color.equals(Color.blue)) {
			return blueHomeCount;
		} else {
			return -1;
		}
	}
	
	private String colorName(Color color) {
		if (color.equals(Color.yellow)) {
			return "Yellow";
		} else if (color.equals(Color.red)) {
			return "Red";
		} else if (color.equals(Color.green)) {
			return "Green";
		} else if (color.equals(Color.blue)) {
			return "Blue";
		} else {
			return "NoColor";
		}
	}
	
	private ArrayList<Player> getPawns(Color color) {
		if (color.equals(Color.yellow)) {
			return yellowPawns;
		} else if (color.equals(Color.green)) {
			return greenPawns;
		} else if (color.equals(Color.red)) {
			return redPawns;
		} else if (color.equals(Color.blue)) {
			return bluePawns;
		} else {
			return null;
		}
	}
	
	private ArrayList<ArrayList<Player>> getOtherPawns(Color color) {
		ArrayList<ArrayList<Player>> otherPawnSets = new ArrayList<ArrayList<Player>>();
		switch (numPlayers) {
		case 2:
			if (color.equals(Color.yellow)) {
				otherPawnSets.add(redPawns);
			} else if (color.equals(Color.green)) {
				otherPawnSets.add(bluePawns);
			} else if (color.equals(Color.red)) {
				otherPawnSets.add(yellowPawns);
			} else if (color.equals(Color.blue)) {
				otherPawnSets.add(greenPawns);
			}
			break;
		case 3:
			if (color.equals(Color.yellow)) {
				otherPawnSets.add(greenPawns);
				otherPawnSets.add(redPawns);
			} else if (color.equals(Color.green)) {
				otherPawnSets.add(redPawns);
				otherPawnSets.add(yellowPawns);
			} else if (color.equals(Color.red)) {
				otherPawnSets.add(greenPawns);
				otherPawnSets.add(yellowPawns);
			} else if (color.equals(Color.blue)) {
				otherPawnSets.add(yellowPawns);
				otherPawnSets.add(greenPawns);
			}
			break;
		case 4:
			if (color.equals(Color.yellow)) {
				otherPawnSets.add(greenPawns);
				otherPawnSets.add(bluePawns);
				otherPawnSets.add(redPawns);
			} else if (color.equals(Color.green)) {
				otherPawnSets.add(bluePawns);
				otherPawnSets.add(yellowPawns);
				otherPawnSets.add(redPawns);
			} else if (color.equals(Color.red)) {
				otherPawnSets.add(bluePawns);
				otherPawnSets.add(yellowPawns);
				otherPawnSets.add(greenPawns);
			} else if (color.equals(Color.blue)) {
				otherPawnSets.add(greenPawns);
				otherPawnSets.add(yellowPawns);
				otherPawnSets.add(redPawns);
			}
			break;
		default:
			
		}
		
		return otherPawnSets;
	}
	
	private void initializeStartCountForColor(Color color) {
		if (color.equals(Color.yellow)) {
			yellowStartCount = 4;
		} else if (color.equals(Color.green)) {
			greenStartCount = 4;
		} else if (color.equals(Color.red)) {
			redStartCount = 4;
		} else if (color.equals(Color.blue)) {
			blueStartCount = 4;
		}
	}
	
	private boolean isValidTile(Tile tile) {
		if (!tile.hasPlayer() || tile instanceof HomeTile) {
			return true;
		} else {
			if (isEnemy(tile.getPlayer().color, playerColor)) {
				return true;
			} else {
				return false;
			}
		}
			
			
		
	}
	
	private boolean firstMoveValid(Player firstPawn, int steps) {
		boolean moveValid = false;
		
		if (steps == 7) {
			return true;
		}
		
		int remainingMoves = 7-steps;
		for (Player pawn : getPawns(playerColor)) {
			if (pawn != firstPawn) {
				if (canMoveForward(pawn, remainingMoves)) {
					moveValid = true;
					break;
				}
			}
		}
		return moveValid;
	}
	
	private int distanceToHome(Player pawn) {
		if (pawn == null) {
			return -1;
		}
		
		int distance = 0;
		Tile tempTile = pawn.currentTile;
		
		while (!(tempTile instanceof HomeTile)) {
			if ((tempTile instanceof SafeZoneEntryTile) && tempTile.tileColor.equals(pawn.color)) {
				tempTile = ((SafeZoneEntryTile)tempTile).firstSafeZoneTile;
			} else {
				tempTile = tempTile.nextTile;
			}
			distance++;
		}
		
		return distance;
	}
	
	private int calculateScoreForPlayer() {
		int totalScore = homeCountForColor(playerColor) * 5;
	
		for (Bot bot : bots) {
			totalScore += startCountForColor(bot.color);
			totalScore += (getPawns(bot.color).size()) * 3; 
		}
		
		return totalScore;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int x = 200 + (getWidth()-640)/5;
		int y = 100 + (getHeight()-458)/5;
		g.drawImage(logoImage, x, y, 240, 80, null);
	}
	
}
