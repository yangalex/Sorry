package yangalex_CSCI201_Assignment3;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ColorSelectPanel extends JPanel {
	public static final long serialVersionUID = 232131;
	
	BufferedImage bgImage;
	
	public static int RED = 0;
	public static int BLUE = 1;
	public static int GREEN = 2;
	public static int YELLOW = 3;
	
	public ColorSelectPanel(int color) {
		
		try {
			if (color == RED) {
				bgImage = ImageIO.read(new File("images/red_button00.png"));
			} else if (color == BLUE) {
				bgImage = ImageIO.read(new File("images/blue_button00.png"));
			} else if (color == GREEN) {
				bgImage = ImageIO.read(new File("images/green_button00.png"));
			} else if (color == YELLOW) {
				bgImage = ImageIO.read(new File("images/yellow_button00.png"));
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
	}
}
