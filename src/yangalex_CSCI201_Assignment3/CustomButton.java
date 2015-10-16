package yangalex_CSCI201_Assignment3;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JButton;

public class CustomButton extends JButton {
	public static final long serialVersionUID = 31231;
	
	public CustomButton(String text, Icon icon) {
		super(text, icon);
		setContentAreaFilled(false);
		setOpaque(false);
		setBorderPainted(false);
		setHorizontalTextPosition(JButton.CENTER);
		setVerticalTextPosition(JButton.CENTER);
		
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(15f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			setFont(font);
		} catch (FontFormatException ffe) {
			System.out.println("FontFormatException: " + ffe.getMessage());
		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
		}
	}
}
