package yangalex_CSCI201_Assignment3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


public class Sorry extends JFrame implements ActionListener, GamePanelDelegate {
	public final static long serialVersionUID = 1;
	StartPanel startPanel;
	NumPlayersPanel numPlayersPanel;
	ColorPanel colorPanel;
	GamePanel gamePanel;
	
	int numberOfPlayers;
	Color selectedColor;
	BufferedImage cursorImage;
	public static Cursor c;
	JDialog helpMenu;
	Font customFont;
	JMenuItem helpItem;
	JMenuItem scoreItem;
	
	public static JDialog topScoresWindow;
	public static DefaultTableModel topScoresModel;
	
	
	public Sorry() {
		super("Sorry");
		setMaximumSize(new Dimension(960, 640));
		initializeFont();
		initializeImages();
		loadTopScores();
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeFont() {
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(12f);
		} catch (FontFormatException ffe) {
			System.out.println(ffe.getMessage());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private void initializeImages() {
		try {
			cursorImage = ImageIO.read(new File("images/cursorHand_blue.png"));
		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
		}
	}

	private void initializeComponents() {
		// create custom cursor
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		c = toolkit.createCustomCursor(cursorImage, new Point(this.getRootPane().getX(), this.getRootPane().getY()), "cursor");
		getRootPane().setCursor(c);
		
		// initial panel
		startPanel = new StartPanel();
		startPanel.addController(this);
		startPanel.setVisible(true);
		
		// select number of players panel
		numPlayersPanel = new NumPlayersPanel();
		numPlayersPanel.addController(this);
		numPlayersPanel.setVisible(false);
		
		// color panel
		colorPanel = new ColorPanel();
		colorPanel.addController(this);
		colorPanel.setVisible(false);
		
		helpMenu = new JDialog(this, false);	
		helpMenu.setSize(300,  400);
		helpMenu.setLocationRelativeTo(null);
		
		// Menu Bar
		JMenuBar jmb = new JMenuBar();
		helpItem = new JMenuItem("Help");
		helpItem.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.CTRL_MASK));
		scoreItem = new JMenuItem("Top Scores");
		scoreItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
		jmb.add(helpItem);
		jmb.add(scoreItem);
		this.setJMenuBar(jmb);
		
		// Top Scores Table
		topScoresWindow = new JDialog(this, false);
		topScoresWindow.setSize(200, 300);
		topScoresWindow.setLocationRelativeTo(null);
		
	}
	
	private void createGUI() {
		setSize(640, 480);
		setLocation(400, 100);
		
		// add start panel
		add(startPanel, BorderLayout.CENTER);
		
		// create help menu
		String helpText = "Sorry!\n\n"
				+ "Starting the Game\n"
				+ "\tPress 'Start'\n"
				+ "\tSelect the number of players\n"
				+ "\tSelect your color\n\n"
				+ "Playing the game:\n"
				+ "\tDraw a card\n"
				+ "\tSelect valid pawn\n"
				+ "\tRight click any tile to deselect\n"
				+ "\tValid tiles will highlight path when hovered over\n"
				+ "\tSelect valid tile to move\n\n"
				+ "Starting a pawn\n"
				+ "\tClick on tile in front of 'Start' tile\n\n"
				+ "Special cards instructions\n"
				+ "\tCard 7:\n"
				+ "\t\tYou can split 7 moves between two different pawns\n"
				+ "\t\tFirst pawn can move anywhere from 1 to 7 moves\n"
				+ "\t\tSecond pawn will have the remaining amount of moves\n"
				+ "\tCard 11\n"
				+ "\t\tCan choose between moving forward 11 or switching with enemy pawn\n"
				+ "\t\tTo switch: select one of your pawns, then click enemy pawn\n"
				+ "\tSorry! Card\n"
				+ "\t\tAllows you to put one pawn from your start on any enemy pawn\n"
				+ "\t\tSimply select an enemy pawn on board";
		JTextArea jta = new JTextArea(helpText);
		jta.setFont(customFont);
		jta.setBackground(new Color(212, 192, 140));
		JScrollPane jsp = new JScrollPane(jta);
		helpMenu.add(jsp);
		
		
		// creating model and table
		JTable scoresTable = new JTable(topScoresModel);

		// row sorter
		TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(topScoresModel);
		rowSorter.setSortsOnUpdates(true);
		rowSorter.toggleSortOrder(1);
		rowSorter.toggleSortOrder(1);
		scoresTable.setRowSorter(rowSorter);	

		scoresTable.setGridColor(Color.black);
		JScrollPane tableScrollPane = new JScrollPane(scoresTable);
		topScoresWindow.add(tableScrollPane);
	}
	
	private void addEvents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				helpMenu.setVisible(true);
			}
		});
		
		scoreItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				topScoresWindow.setVisible(true);
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				saveTopScores();
			}
		});
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		// handle start button clicked
		if (ae.getActionCommand().equals("startClicked")) {
			// add numPlayersPanel
			add(numPlayersPanel, BorderLayout.CENTER);
			startPanel.setVisible(false);
			numPlayersPanel.setVisible(true);
		} else if (ae.getActionCommand().equals("confirmPlayers")) {
			String selectedNum = numPlayersPanel.buttonGroup.getSelection().getActionCommand();
			numberOfPlayers = Integer.parseInt(selectedNum);
			
			add(colorPanel, BorderLayout.CENTER);
			numPlayersPanel.setVisible(false);
			colorPanel.setVisible(true);
		} else if (ae.getActionCommand().equals("confirmColor")) {
			
			// set player color
			this.selectedColor = colorPanel.selectedColor;
			
			// initialize GamePanel
			gamePanel = new GamePanel(numberOfPlayers, selectedColor);
			gamePanel.delegate = this;
			add(gamePanel, BorderLayout.CENTER);
			colorPanel.setVisible(false);
			gamePanel.setVisible(true);
		}
	}
	
	public void gameEnded() {
		System.out.println("Game ended");
		getContentPane().remove(gamePanel);
			
		startPanel.setVisible(true);
		repaint();
	}
	
	public static void addToTopScores(String name, int score) {
		Object[] newEntry = {name, score};
		topScoresModel.addRow(newEntry);
		topScoresModel.fireTableDataChanged();
	}
	
	private void loadTopScores() {
		if (topScoresModel != null) {
			return;
		}
		ObjectInputStream ois;
		try {
			String[] columnNames = {"Name", "Score"};
			File scoresFile = new File("top_score_model.txt");
			if (scoresFile.exists()) {
				ois = new ObjectInputStream(new FileInputStream(scoresFile));
				topScoresModel = new DefaultTableModel() {
					public static final long serialVersionUID = 23131;
					@Override
					public Class<?> getColumnClass(int columnIndex) {
						if (columnIndex == 1) {
							return Integer.class;
						} else {
							return String.class;
						}
					}
				};
				topScoresModel.setDataVector((Vector<?>)ois.readObject(), new Vector<String>(Arrays.asList(columnNames)));
				ois.close();
			} else {
				topScoresModel = new DefaultTableModel(new Object[0][0], columnNames) {
					public static final long serialVersionUID = 23131;
					@Override
					public Class<?> getColumnClass(int columnIndex) {
						if (columnIndex == 1) {
							return Integer.class;
						} else {
							return String.class;
						}
					}
				};
			}
		} catch (IOException ioe) {
			System.out.println("IOException when trying to load top scores: " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Class not found exception: " + cnfe.getMessage());
		} 
	}
	
	private void saveTopScores() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("top_score_model.txt"));
			oos.writeObject(topScoresModel.getDataVector());
			oos.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Sorry mainFrame = new Sorry();
		mainFrame.setVisible(true);

	}
}
