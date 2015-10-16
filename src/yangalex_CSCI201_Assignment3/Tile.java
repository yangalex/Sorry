package yangalex_CSCI201_Assignment3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Tile extends JPanel {
	public static final long serialVersionUID = 434141;
	
	public Color tileColor;
	private Player player;
	public boolean isSlide;
	public boolean highlighted = false;
	int tileId;
	Tile nextTile;
	Tile previousTile;
	BufferedImage bgImage;
	BufferedImage highlightedBG;
	
	// keep highlights for all colors so it doesn't need to keep opening file multiple times
	BufferedImage greenHighlight;
	BufferedImage blueHighlight;
	BufferedImage yellowHighlight;
	BufferedImage redHighlight;
	
	// images for pawns
	BufferedImage greenPawn;
	BufferedImage yellowPawn;
	BufferedImage bluePawn;
	BufferedImage redPawn;
	
	// images for slide
	BufferedImage slideImage;
	
	public Tile(Color tileColor, boolean isSlide) {
		this.tileColor = tileColor;
		this.isSlide = isSlide;
		
		initializeBG();
		initializeHighlights();
		initializePawns();
	}
	
	private void initializeBG() {
		try {
			if (tileColor.equals(Color.red)) {
				bgImage = ImageIO.read(new File("images/red_tile.png"));
				slideImage = ImageIO.read(new File("images/red_slide.png"));
			} else if (tileColor.equals(Color.green)) {
				bgImage = ImageIO.read(new File("images/green_tile.png"));
				slideImage = ImageIO.read(new File("images/green_slide.png"));
			} else if (tileColor.equals(Color.yellow)) {
				bgImage = ImageIO.read(new File("images/yellow_tile.png"));
				slideImage = ImageIO.read(new File("images/yellow_slide.png"));
			} else if (tileColor.equals(Color.blue)) {
				bgImage = ImageIO.read(new File("images/blue_tile.png"));
				slideImage = ImageIO.read(new File("images/blue_slide.png"));
			} else {
				bgImage = ImageIO.read(new File("images/grey_tile.png"));
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private void initializeHighlights() {
		try {
			redHighlight = ImageIO.read(new File("images/red_panel.png"));
			greenHighlight = ImageIO.read(new File("images/green_panel.png"));
			blueHighlight = ImageIO.read(new File("images/blue_panel.png"));
			yellowHighlight = ImageIO.read(new File("images/yellow_panel.png"));
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private void initializePawns() {
		try {
			greenPawn = ImageIO.read(new File("images/green_pawn.png"));
			bluePawn = ImageIO.read(new File("images/blue_pawn.png"));
			redPawn = ImageIO.read(new File("images/red_pawn.png"));
			yellowPawn = ImageIO.read(new File("images/yellow_pawn.png"));
		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
		}
	}
	
	public void setPlayer(Player newPlayer) {
		player = newPlayer;
		newPlayer.setLocation(tileId);
		newPlayer.currentTile = this;
		
		repaint();
		
	}
	
	public void highlightTile(Color highlightColor) {
		highlighted = true;
		
		if (highlightColor.equals(Color.red)) {
			highlightedBG = redHighlight; 
		} else if (highlightColor.equals(Color.green)) {
			highlightedBG = greenHighlight; 
		} else if (highlightColor.equals(Color.blue)) {
			highlightedBG = blueHighlight;
		} else if (highlightColor.equals(Color.yellow)) {
			highlightedBG = yellowHighlight;
		} 
		repaint();
	}
	
	public void unHighlightTile() {
		highlighted = false;
		repaint();
	}
	
	public void removePlayer() {
		player = null;
		repaint();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean hasPlayer() {
		if (player == null) {
			return false;
		} else {
			return true;
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	
		if (!highlighted) {
			g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
		} else {
			g.drawImage(highlightedBG, 0, 0, getWidth(), getHeight(), null);
		}
		
		if (isSlide) {
			int slideWidth = 10;
			int slideHeight =  10;
			g.drawImage(slideImage, getWidth()/2-slideWidth/2, getHeight()/2-slideHeight/2, slideWidth, slideHeight, null);
		}
		
		if (player != null) {
			int playerWidth = 15;
			int playerHeight = 15;

			if (player.color.equals(Color.green)) {
				g.drawImage(greenPawn, getWidth()/2-playerWidth/2, getHeight()/2-playerHeight/2, playerWidth, playerHeight, null);
			} else if (player.color.equals(Color.blue)) {
				g.drawImage(bluePawn, getWidth()/2-playerWidth/2, getHeight()/2-playerHeight/2, playerWidth, playerHeight, null);
			} else if (player.color.equals(Color.red)) {
				g.drawImage(redPawn, getWidth()/2-playerWidth/2, getHeight()/2-playerHeight/2, playerWidth, playerHeight, null);
			} else if (player.color.equals(Color.yellow)) {
				g.drawImage(yellowPawn, getWidth()/2-playerWidth/2, getHeight()/2-playerHeight/2, playerWidth, playerHeight, null);
			}
		}
	}
}



