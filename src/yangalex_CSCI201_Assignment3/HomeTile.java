package yangalex_CSCI201_Assignment3;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

// subclass of Tile that accepts more than one player in it
public class HomeTile extends SafeZoneTile {
	public static final long serialVersionUID = 34141351;
	Player[] players;
	BufferedImage bg;
	Font customFont;
	
	public HomeTile(Color tileColor) {
		super(tileColor, 5);
		initializeBG();
		initializeFont();
		
		players = new Player[4];
		JLabel homeLabel = new JLabel("Home");
		homeLabel.setFont(customFont);
		homeLabel.setHorizontalTextPosition(JLabel.CENTER);
		homeLabel.setVerticalTextPosition(JLabel.CENTER);
		add(homeLabel);
	
	}
	
	private void initializeBG() {
		try {
			if (tileColor.equals(Color.red)) {
				bgImage = ImageIO.read(new File("images/red_panel.png"));
			} else if (tileColor.equals(Color.green)) {
				bgImage = ImageIO.read(new File("images/green_panel.png"));
			} else if (tileColor.equals(Color.yellow)) {
				bgImage = ImageIO.read(new File("images/yellow_panel.png"));
			} else if (tileColor.equals(Color.blue)) {
				bgImage = ImageIO.read(new File("images/blue_panel.png"));
			} 
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
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
	
	public void addPlayer(Player newPlayer) {
		for (int i=0; i < players.length; i++) {
			if (players[i] == null) {
				players[i] = newPlayer;
				break;
			}
		}
	}
	
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
	}
}
