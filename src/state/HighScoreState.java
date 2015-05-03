package state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * A High Score State the displays the top ten scores stored in a text file.
 * 
 * @author Mark Hays and his students. Created Feb 15, 2015.
 */
public class HighScoreState extends MenuState {
	private TreeMap<Integer, String> topScores; // A TreeMap used to store
												// topScores. TreeMap is used
												// due to its pre-sorted
												// arrangment.

	/**
	 * Constructs a HighScoreState.
	 * 
	 * @param stateManager
	 */
	public HighScoreState(StateManager stateManager) {
		// Call the super class constructor.
		super(stateManager);

		// Constants.
		final int NAME = 0;
		final int SCORE = 1;

		// Alter the title color.
		this.titleColor = Color.RED;

		// Set topScores TreeMap. Reverse its natural ordering.
		this.topScores = new TreeMap<Integer, String>(
				Collections.reverseOrder());

		// Populate topScores from file.
		try {
//			File fileLocation = new File("Resources/text/highscore.txt");
			File fileLocation = new File(getClass().getResource("/text/highscore.txt").getFile());
			Scanner fileScanner = new Scanner(fileLocation);

			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();
				String[] lineArray = line.split(" ");
				String name = lineArray[NAME];
				int score = Integer.parseInt(lineArray[SCORE]);
				this.topScores.put(score, name);
			}

			fileScanner.close();

			// Logging.
			// for (int key : this.topScores.keySet()) {
			// System.out.printf("\n%15s", this.topScores.get(key) + " : " +
			// key);
			// }
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		// Variable.
		String name = null;

		// Background
		this.background.draw(g2);

		// Title
		g2.setColor(this.titleColor);
		g2.setFont(this.titleFont);
		g2.drawString("High Scores", 125, 100);
		g2.drawString("__________", 125, 110);

		// Options
		g2.setFont(this.normalFont);
		g2.setColor(Color.BLACK);

		// Counter to halt the limit for loop to 10 runs.
		int counter = 0;

		// Loop through topScores key set and draw the string onto given
		// Graphics2D object.
		for (int key : this.topScores.keySet()) {
			if (counter == 10) {
				break;
			}

			if (this.topScores.get(key).length() >= 3) {
				name = this.topScores.get(key).substring(0, 3);
			} else {
				name = this.topScores.get(key);
			}

			g2.drawString(name, 125, 180 + counter * 35);
			g2.drawString(Integer.toString(key), 450, 180 + counter * 35);
			counter++;
		}

		// Draw the option to return to the menu. Given the illusion of
		// selection.
		g2.setColor(Color.MAGENTA);
		g2.drawString("<  Back To Menu", 175, 600);
	}

	@Override
	public void keyPressed(int key) {
		final int MENU_STATE = 0;

		// Return to the Menu State and end background music.
		if (key == KeyEvent.VK_ENTER) {
			this.menuBackgroundMusic.close();
			this.stateManager.setState(MENU_STATE);
		}
	}

}
