import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Color;
import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class Helper extends JFrame {
	/* define all of the labels on the helper */
	private JLabel lblCoords = new JLabel("Mouse Coordiantes: ", SwingConstants.CENTER); //SwingConstants.CENTER will center the label in column
	private JLabel lblColorRGB = new JLabel("Pixel Color (R,G,B): ", SwingConstants.CENTER);
	private JLabel lblColorINT = new JLabel("Pixel Color (INT): ", SwingConstants.CENTER);
	private JLabel lblKeyPressed = new JLabel("Pressed Key (INT): ", SwingConstants.CENTER);
	private JLabel lblEmpty = new JLabel("");

	/* define each of the text fields */
	private JTextField txtCoords = new JTextField();
	private JTextField txtColorRGB = new JTextField();
	private JTextField txtColorINT = new JTextField();
	private JTextField txtKeyPressed = new JTextField();

	/* define each of the buttons */
	private JButton btnHelp = new JButton("Help");
	private JButton btnExit = new JButton("Exit");
	private JButton btnCoordsCopy = new JButton("Copy to Clipboard");
	private JButton btnColorRGBCopy = new JButton("Copy to Clipboard");
	private JButton btnColorINTCopy = new JButton("Copy to Clipboard");
	private JButton btnKeyPressedCopy = new JButton("Copy to Clipboard");

	/* define each of the handlers for the buttons */
	private btnHelpHandler bhHandler;
	private btnExitHandler beHandler;
	private btnCoordsCopyHandler bccHandler;
	private btnColorRGBCopyHandler bcrHandler;
	private btnColorINTCopyHandler bciHandler;
	private btnKeyPressedCopyHandler bkpcHandler;

	/* define action handlers for capturing key presses */
	private keyPressedHandler kpHandler;
	private keyPressedSecondHandler kpsHandler;

	/* we start active -- not paused */
	private static boolean PAUSED = false;

	/* shortcut for when we want to display a message box */
	private void msgBox(String Text, String Title) {
		JOptionPane.showMessageDialog(null,Text,Title,JOptionPane.PLAIN_MESSAGE);
	}

	/* function to pause/unpause our updating of coordinates and colors */
	private void pause() {
		/* if it's already paused move the mouse back to where it started */
		if (PAUSED == true) {
			//get the coordinates in the text field
			String rawCoords = txtCoords.getText();
			rawCoords = rawCoords.replace("(","");
			rawCoords = rawCoords.replace(")","");
			String[] Coords = rawCoords.split("[,]+");
			int[] cursorPos = {Integer.parseInt(Coords[0]), Integer.parseInt(Coords[1])};

			//move mouse to position
			try {
				Robot r = new Robot();
				r.mouseMove(cursorPos[0], cursorPos[1]);
			} catch(AWTException e) {
				throw new RuntimeException(e);
			}
		} 
		/* otherwise display some info */
		else {
			System.out.println("Paused at: " + txtCoords.getText());
			System.out.println("Color int: " + txtColorINT.getText());
			System.out.println("Color RBG: " + txtColorRGB.getText());
			System.out.println("");
		}
		PAUSED = !PAUSED;
	}

	private class btnHelpHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			msgBox("To pause the helper make sure that the cursor is in the Mouse Coordinates field and press F8.\n\nTo get the integer value of a key make sure the cursor is in the Pressed Key field and press the key.\n\nFor further information visit the documentation.", "Help");
		}
	}

	private class btnExitHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	private class btnCoordsCopyHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String rawCoords = txtCoords.getText();
			rawCoords = rawCoords.replace("(","");
			rawCoords = rawCoords.replace(")","");
			String[] Coords = rawCoords.split("[,]+");
			String myString = Coords[0] + "," + Coords[1];
			StringSelection stringSelection = new StringSelection (myString);
			Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clpbrd.setContents (stringSelection, null);
		}
	}

	private class btnColorRGBCopyHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String myString = txtColorRGB.getText();
			StringSelection stringSelection = new StringSelection (myString);
			Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clpbrd.setContents (stringSelection, null);
		}
	}

	private class btnColorINTCopyHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String myString = txtColorINT.getText();
			StringSelection stringSelection = new StringSelection (myString);
			Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clpbrd.setContents (stringSelection, null);
		}
	}

	private class btnKeyPressedCopyHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String myString = txtKeyPressed.getText();
			StringSelection stringSelection = new StringSelection (myString);
			Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clpbrd.setContents (stringSelection, null);
		}
	}

	private class keyPressedHandler implements KeyListener {   
		public void keyTyped(KeyEvent e) {
			//do nothing
		}
		/* Handle the key pressed event from the text field. */
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case 119: pause(); break; //pause if F8 is typed
				default: break;
			}
		}
		public void keyReleased(KeyEvent e) {
			//do nothing (we have to override it though)
		}
	}

	private class keyPressedSecondHandler implements KeyListener {   
		public void keyTyped(KeyEvent e) {
			e.consume();
		}
		/* Handle the key pressed event from the text field. */
		public void keyPressed(KeyEvent e) {
			e.consume();
			System.out.println("Pressed key: " + e.getKeyCode());
			txtKeyPressed.setText(String.valueOf(e.getKeyCode()));
			if (e.getKeyCode() == 8) {
				e.consume();
				txtKeyPressed.setText("8");
			}
		}
		public void keyReleased(KeyEvent e) {
			//do nothing
		}
	}

	public void updateVals() {
		//function to update the Text Field vals

		//get mouse coordinates
		int X = MouseInfo.getPointerInfo().getLocation().x; //get the X coordinate of the mouse
		int Y = MouseInfo.getPointerInfo().getLocation().y; //get the Y coordinate of the mouse
		String coords = "(" + X + "," + Y + ")";

		//get the Color of a pixel at X, Y
		try {
			Robot r = new Robot();
			Color pixel = r.getPixelColor( X, Y );

			//get the RGB value
			String RGB = pixel.getRed() + "," + pixel.getGreen() + "," + pixel.getBlue();

			//get the int value
			int RGBint = pixel.getRGB();
			String RGB_str = String.valueOf(RGBint);

			//update the values
			txtCoords.setText(coords);
			txtColorRGB.setText(RGB);
			txtColorINT.setText(RGB_str);

		} catch(AWTException e) {
			throw new RuntimeException(e);
		}
	}

	public Helper() {
		//specify handlers for help button
		bhHandler = new btnHelpHandler();
		btnHelp.addActionListener(bhHandler);

		//specify handler for exit button
		beHandler = new btnExitHandler();
		btnExit.addActionListener(beHandler);

		//copy buttons
		bccHandler = new btnCoordsCopyHandler();
		btnCoordsCopy.addActionListener(bccHandler);
		bcrHandler = new btnColorRGBCopyHandler();
		btnColorRGBCopy.addActionListener(bcrHandler);
		bciHandler = new btnColorINTCopyHandler();
		btnColorINTCopy.addActionListener(bciHandler);
		bkpcHandler = new btnKeyPressedCopyHandler();
		btnKeyPressedCopy.addActionListener(bkpcHandler);

		//specify handler for key listener
		kpHandler = new keyPressedHandler();
		txtCoords.addKeyListener(kpHandler);
		kpsHandler = new keyPressedSecondHandler();
		txtKeyPressed.addKeyListener(kpsHandler);

		Container pane = getContentPane(); //get HWND for GUI frame
		pane.setLayout(new GridLayout(5, 3)); //set the layout type to a 3 x 4 grid

		//center the text fields
		txtCoords.setHorizontalAlignment(JLabel.CENTER);
		txtColorRGB.setHorizontalAlignment(JLabel.CENTER);
		txtColorINT.setHorizontalAlignment(JLabel.CENTER);
		txtKeyPressed.setHorizontalAlignment(JLabel.CENTER);

		//make the text fields uneditable
		txtColorRGB.setEditable(false);
		txtColorINT.setEditable(false);

		//add things to the pane in order of display (left to right top to bottom)
		pane.add(lblCoords);
		pane.add(txtCoords);
		pane.add(btnCoordsCopy);

		pane.add(lblColorRGB);
		pane.add(txtColorRGB);
		pane.add(btnColorRGBCopy);

		pane.add(lblColorINT);
		pane.add(txtColorINT);
		pane.add(btnColorINTCopy);

		pane.add(lblKeyPressed);
		pane.add(txtKeyPressed);
		pane.add(btnKeyPressedCopy);

		pane.add(btnHelp);
		pane.add(lblEmpty); //margin between help and exit
		pane.add(btnExit);


		//finish up
		setTitle("Javauto Helper"); //set title
		setSize(493,175); //set size
		setVisible(true); //make visible
		setDefaultCloseOperation(EXIT_ON_CLOSE); //define what the X does
		setAlwaysOnTop(true); //always on top
	}

	public static void launch() {
		try {
			Robot roboto = new Robot(); //create robot to facilitate delays & other actions
			Helper javautoHelper = new Helper(); //create GUI
			while (true) {
				roboto.delay(20);
				if (PAUSED == false) {
					javautoHelper.updateVals(); //update values
				}
			}          
		} catch(AWTException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
