package yangalex_CSCI201_Assignment3;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartPanel extends JPanel {
	public final static long serialVersionUID = 23123213;
	CustomButton startButton;
	JLabel sorryTitle;
	BufferedImage greyBG;
	BufferedImage logo;
	BufferedImage buttonImage;
	
	public StartPanel() {
		setLayout(new GridBagLayout());
		
		initializeImages();
		initializeComponents();
		createGUI();
		
	}
	
	private void initializeImages() {
		try {
			greyBG = ImageIO.read(new File("images/grey_panel.png"));
			logo = ImageIO.read(new File("images/sorry.png"));
			buttonImage = ImageIO.read(new File("images/grey_button00.png"));
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private void initializeComponents() {
		sorryTitle = new JLabel("");
		sorryTitle.setIcon(new ImageIcon(logo));
		
		startButton = new CustomButton("START", new ImageIcon(buttonImage));
		startButton.setActionCommand("startClicked");
	}
	
	private void createGUI() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		gbc.insets = new Insets(0, 0, 30, 0);
		sorryTitle.setAlignmentX(Box.CENTER_ALIGNMENT);
		add(sorryTitle, gbc);
		
		gbc.gridy = 1;
//		startButton.setFont(new Font("Arial", Font.BOLD, 15));
		startButton.setAlignmentX(Box.CENTER_ALIGNMENT);
		add(startButton, gbc);
	}
	
	public void addController(ActionListener controller) {
		startButton.addActionListener(controller);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(greyBG, 0, 0, this.getWidth(), this.getHeight(), null);
		
	}
}
