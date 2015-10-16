package yangalex_CSCI201_Assignment3;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class CardDialog extends JDialog {
	public final static long serialVersionUID = 2131;
	
	private Card card;
	private BufferedImage bg;
	private JLabel cardNum;
	private JTextArea cardMessage;
	private Font customFont;
	private JFrame owner;
	
	public CardDialog(JFrame owner, Card card) {
		super(owner, "Card");
		setSize(300, 450);
		setLocationRelativeTo(null);
		
		this.owner = owner;
		this.card = card;
		initializeBG();
		initializeComponents();
		addEvents();
		createGUI();

		setModalityType(ModalityType.APPLICATION_MODAL);
		repaint();
	}
	
	private void initializeBG() {
		Random randomGenerator = new Random();
		int randomNum = randomGenerator.nextInt(4); 
		// randomize card color
		try {
			if (randomNum == 0) {
				bg = ImageIO.read(new File("images/card_beige.png"));
			} else if (randomNum == 1) {
				bg = ImageIO.read(new File("images/card_beigeLight.png"));
			} else if (randomNum == 2) {
				bg = ImageIO.read(new File("images/card_blue.png"));
			} else if (randomNum == 3) {
				bg = ImageIO.read(new File("images/card_brown.png"));
			}
		} catch (IOException ioe) {
			System.out.println("IOException " + ioe.getMessage());
		}
	}
	
	private void initializeComponents() {
		cardNum = new JLabel(card.cardNumber + "");

		cardMessage = new JTextArea(card.message);
		cardMessage.setColumns(20);
		cardMessage.setLineWrap(true);
		cardMessage.setWrapStyleWord(true);
		cardMessage.setOpaque(false);
		cardMessage.setEditable(false);
	}
	
	private void createGUI() {
		// initialize custom font
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(40f);
		} catch (FontFormatException ffe) {
			System.out.println(ffe.getMessage());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		
		JPanel jp = new JPanel() {
			public final static long serialVersionUID = 21414;
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bg, 0, 0, this.getWidth(), this.getHeight(), null);
			}
		};
		jp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(30, 0, 0, 0);
		gbc.anchor = GridBagConstraints.PAGE_START;
		cardNum.setFont(customFont);
		cardNum.setAlignmentX(CENTER_ALIGNMENT);
		jp.add(cardNum, gbc);
		
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 20, 0, 20);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		cardMessage.setFont(customFont.deriveFont(20f));
//		cardMessage.setAlignmentX(CENTER_ALIGNMENT);
		jp.add(cardMessage, gbc);
		
		add(jp, BorderLayout.CENTER);

	}
	
	private void addEvents() {
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				closeDialog();
			}
		});
		
		cardNum.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				closeDialog();
			}
		});
		
		cardMessage.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				closeDialog();
			}
			
		});
	}
	
	private void closeDialog() {
		owner.getRootPane().setCursor(Sorry.c);
		dispose();
	}
}
