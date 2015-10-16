package yangalex_CSCI201_Assignment3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorPanel extends JPanel {
	public static final long serialVersionUID = 3213241;
	
	JLabel titleLabel;
	JPanel redPanel;
	JPanel bluePanel;
	JPanel greenPanel;
	JPanel yellowPanel;
	JButton confirmButton;
	Font customFont;
	
	JPanel selectedPanel;
	Color selectedColor;
	BufferedImage buttonImage;
	BufferedImage greyBG;
	
	public ColorPanel() {
		setLayout(new GridBagLayout());
		initializeFont();
		initializeImages();
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeFont() {
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(20f);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (FontFormatException ffe) {
			System.out.println(ffe.getMessage());
		}
	}
	
	private void initializeImages() {
		try {
			buttonImage = ImageIO.read(new File("images/grey_button00.png"));
			greyBG = ImageIO.read(new File("images/grey_panel.png"));
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		
	}
	
	private void initializeComponents() {
		titleLabel = new JLabel("Select your color");
		redPanel = getRedPanel();
		bluePanel = getBluePanel();
		greenPanel = getGreenPanel();
		yellowPanel = getYellowPanel();
		confirmButton = new CustomButton("CONFIRM", new ImageIcon(buttonImage));
		confirmButton.setEnabled(false);
		confirmButton.setActionCommand("confirmColor");
	}
	
	private void createGUI() {
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		titleLabel.setFont(customFont.deriveFont(40f));
		add(titleLabel, gbc);
		
		gbc.weighty = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 10, 10, 10);
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1.7;
		add(redPanel, gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1;
		add(bluePanel, gbc);
		
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.weightx = 1.7;
		gbc.insets = new Insets(10, 10, 0, 10);
		add(greenPanel, gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1;
		add(yellowPanel, gbc);
		
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 3;
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		add(confirmButton, gbc);
		
	}
	
	private void addEvents() {
		redPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				selectPanel(redPanel);
				selectedColor = Color.red;
			}
		});
		
		bluePanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				selectPanel(bluePanel);
				selectedColor = Color.blue;
			}
		});
		
		greenPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				selectPanel(greenPanel);
				selectedColor = Color.green;
			}
		});
		
		yellowPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				selectPanel(yellowPanel);
				selectedColor = Color.yellow;
			}
		});
	}
	
	private void selectPanel(JPanel jp) {
		confirmButton.setEnabled(true);
		getLabelFromPanel(jp).setForeground(Color.gray);
		
		if (selectedPanel != null) {
			getLabelFromPanel(selectedPanel).setForeground(Color.black);
		}
		
		selectedPanel = jp;
	}
	
	private JLabel getLabelFromPanel(JPanel jp) {
		for (Component comp : jp.getComponents()) {
			if (comp instanceof JLabel) {
				return (JLabel)comp;
			}
		}
		
		return null;
	}
	
	public void addController(ActionListener controller) {
		this.confirmButton.addActionListener(controller);
	}
	
	private JPanel getRedPanel() {
		JPanel jp = new ColorSelectPanel(ColorSelectPanel.RED);
		jp.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel jl = new JLabel("Red");
		jl.setFont(customFont);
		jp.add(jl, gbc);
		
		return jp;
	}
	
	private JPanel getBluePanel() {
		JPanel jp = new ColorSelectPanel(ColorSelectPanel.BLUE);
		jp.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel jl = new JLabel("Blue");
		jl.setFont(customFont);
		jp.add(jl, gbc);
		
		return jp;
	}
	
	private JPanel getGreenPanel() {
		JPanel jp = new ColorSelectPanel(ColorSelectPanel.GREEN);
		jp.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel jl = new JLabel("Green");
		jl.setFont(customFont);
		jp.add(jl, gbc);
		
		return jp;		
	}
	
	private JPanel getYellowPanel() {
		JPanel jp = new ColorSelectPanel(ColorSelectPanel.YELLOW);
		jp.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel jl = new JLabel("Yellow");
		jl.setFont(customFont);
		jp.add(jl, gbc);
		
		return jp;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(greyBG, 0, 0, getWidth(), getHeight(), null);
		
	}
}
