

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeMap;

import state.MenuState;
import state.StateManager;


/**
 * This does nothing in the game. It is a testing class for alternative methods for HighScoreState.
 *
 * @author Mark Hays and his students.
 *         Created Feb 15, 2015.
 */
public class testState extends MenuState {
	private TreeMap<Integer, String> topScores;

	public testState(StateManager stateManager) {
		super(stateManager);
		
		this.titleColor = Color.RED;
		
		final int NAME = 0;
		final int SCORE = 1;
		
		this.topScores = new TreeMap(Collections.reverseOrder());
		
		try {
			File fileLocation = new File("res/highscore.txt");
			Scanner fileScanner = new Scanner(fileLocation);

			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();
				String[] lineArray = line.split(" ");
				String name = lineArray[NAME];
				int score = Integer.parseInt(lineArray[SCORE]);
				this.topScores.put(score, name);
			}

			for (int key : this.topScores.keySet()) {
				System.out.printf("\n%15s", this.topScores.get(key) + " : " + key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics2D g2) {
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
		
		int counter = 0;
		
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
		
		g2.setColor(Color.MAGENTA);
		g2.drawString("<  Back To Menu", 175, 600);
	}
	
	@Override
	public void keyPressed(int key) {
		final int MENU_STATE = 0;
		
		if (key == KeyEvent.VK_ENTER) {
			this.menuBackgroundMusic.close();
			this.stateManager.setState(MENU_STATE);
		}
	}

}
