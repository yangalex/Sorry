package yangalex_CSCI201_Assignment3;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class NumPlayersPanel extends JPanel {
	public static final long serialVersionUID = 23131241;
	
	JLabel titleLabel;
	JRadioButton twoButton;
	JRadioButton threeButton;
	JRadioButton fourButton;
	ButtonGroup buttonGroup;
	CustomButton confirmButton;
	Font customFont;
	
	BufferedImage uncheckedImage;
	BufferedImage checkedImage;
	BufferedImage greyBG;
	BufferedImage buttonImage;
	
	public NumPlayersPanel() {
		setLayout(new GridBagLayout());
		
		initializeFont();
		initializeImages();
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeFont() {
		// create custom font
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(25f);
		} catch (FontFormatException ffe) {
			System.out.println(ffe.getMessage());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private void initializeImages() {
		try {
			uncheckedImage = ImageIO.read(new File("images/grey_circle.png"));
			checkedImage = ImageIO.read(new File("images/grey_circleTick.png"));
			greyBG = ImageIO.read(new File("images/grey_panel.png"));
			buttonImage = ImageIO.read(new File("images/grey_button00.png"));
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private void initializeComponents() {
		titleLabel = new JLabel("Select the number of players");
		titleLabel.setFont(customFont);
		
		// crate radio buttons and set action commands for each
		twoButton = new JRadioButton("2", new ImageIcon(uncheckedImage));
		twoButton.setFont(customFont);
		twoButton.setActionCommand("2");
		threeButton = new JRadioButton("3", new ImageIcon(uncheckedImage));
		threeButton.setFont(customFont);
		threeButton.setActionCommand("3");
		fourButton = new JRadioButton("4", new ImageIcon(uncheckedImage));
		fourButton.setFont(customFont);
		fourButton.setActionCommand("4");
		
		// initialize ButtonGroup and add radio buttons
		buttonGroup = new ButtonGroup();
		buttonGroup.add(twoButton);
		buttonGroup.add(threeButton);
		buttonGroup.add(fourButton);
		
		confirmButton = new CustomButton("Confirm", new ImageIcon(buttonImage));
		confirmButton.setEnabled(false);
		confirmButton.setActionCommand("confirmPlayers");
		
	}
	
	private void createGUI() {
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 3;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.insets = new Insets(50, 0, 0, 0);
		add(titleLabel, gbc);
		
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		add(twoButton, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		add(threeButton, gbc);
		
		gbc.weightx = 0.1;
		gbc.gridx = 2;
		gbc.gridy = 1;
		add(fourButton, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 0, 30, 30);
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		add(confirmButton, gbc);
	}
	
	private void addEvents() {

		twoButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				enableConfirm();
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					twoButton.setIcon(new ImageIcon(checkedImage));
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					twoButton.setIcon(new ImageIcon(uncheckedImage));
				}
			}
 		});
		
		threeButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				enableConfirm();
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					threeButton.setIcon(new ImageIcon(checkedImage));
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					threeButton.setIcon(new ImageIcon(uncheckedImage));
				}
			}
 		});

		fourButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				enableConfirm();
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					fourButton.setIcon(new ImageIcon(checkedImage));
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					fourButton.setIcon(new ImageIcon(uncheckedImage));
				}
			}
 		});
	}
	
	public void addController(ActionListener controller) {
		confirmButton.addActionListener(controller);
	}
	
	private void enableConfirm() {
		confirmButton.setEnabled(true);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(greyBG, 0, 0, this.getWidth(), this.getHeight(), null);
	}
}
