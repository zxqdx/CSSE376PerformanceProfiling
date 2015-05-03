package state;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import objects.Level;
import toolObjects.Background;
import toolObjects.MusicPlayer;


/**
 * MenuState represents the main menu of the Digger game.
 *
 * @author Mark Hays and his students.
 *         Created Feb 12, 2015.
 */
public class MenuState extends State {
	private final int PLAY = 0;
	private final int SURVIVAL = 1;
	private final int HIGHSCORES = 2;
	private final int QUIT = 3;
	private final int LEVEL_0 = 1;
	private final int HIGH_SCORE_STATE = 5;
	private final int SURVIVAl_STATE = 6;

	private String[] options = { "Play", "Survival", "Highscores", "Quit" };
	private int currentChoice = 0;
	protected Background background;
	protected Color titleColor;
	protected Font titleFont;
	protected Font normalFont;
	
	protected MusicPlayer menuBackgroundMusic;

	/**
	 * Constructs a Main menu.
	 * 
	 * @param stateManager
	 *            the StateManager that manages the various states.
	 */
	public MenuState(StateManager stateManager) {
		this.stateManager = stateManager;
		this.menuBackgroundMusic = new MusicPlayer("/music/mainMenuMusic.mp3");
		this.menuBackgroundMusic.playLoop();
		
		try {
			this.background = new Background("/background/menuBackground.gif", 1);
			this.background.setVector(-0.1, 0);

			// Title
			this.titleColor = Color.CYAN;
			this.titleFont = new Font("Times New Romans", Font.PLAIN, 72);

			// Font
			this.normalFont = new Font("Arial", Font.PLAIN, 32);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize() {
		// Not used in MenuState.
	}

	@Override
	public void update() {
		this.background.update();
	}

	@Override
	public void draw(Graphics2D g2) {
		// Constants.
		final int VERTICAL_DISPLACEMENT = 35;
		
		// Background.
		this.background.draw(g2);

		// Title.
		g2.setColor(this.titleColor);
		g2.setFont(this.titleFont);
		g2.drawString("Digger", 200, 150);

		// Options.
		g2.setFont(this.normalFont);

		// Set color of options and selected option.
		for (int i = 0; i < this.options.length; i++) {
			// Sets selected option color.
			if (i == this.currentChoice) {
				g2.setColor(Color.MAGENTA);
			} else {
				g2.setColor(Color.YELLOW);
			}
			// Draws the options in optimal positions 35 pixels apart starting
			// at (200, 180).
			g2.drawString(this.options[i], 200, 180 + i * VERTICAL_DISPLACEMENT);
		}
	}

	private void select() {
		// Variable.
		int selection = this.currentChoice % this.options.length;

		// If play, play game, highscores, see high scores, quit, quit game.
		if (selection == this.PLAY) {
			// Sets the level to 0, the first level.
			this.stateManager.setState(this.LEVEL_0);
			
			// Gets the level.
			Level level = this.stateManager.getLevel();
			
			// Sets the life of the player.
			this.stateManager.getPlayer().setLife(3);
						
			// Update player level.
			this.stateManager.player.setLevel(level);
			
			// Update the enemies level.
//			this.stateManager.setEnemyLevel(1, level);
		} else if (selection == this.SURVIVAL) {
			// Sets the level to 0, the first level.
			this.stateManager.setState(this.SURVIVAl_STATE);
			
			// Gets the level.
			Level level = this.stateManager.getLevel();
			
			// Sets the life of player.
			this.stateManager.getPlayer().setLife(3);
						
			// Update player level.
			this.stateManager.player.setLevel(level);		
		} else if (selection == this.HIGHSCORES) {
			// Sets the state to the high score state.
			this.stateManager.setState(this.HIGH_SCORE_STATE);
		} else if (selection == this.QUIT) {
			System.exit(0);
		}
		
		// Closes the menuBackgroundMusic.
		this.menuBackgroundMusic.close();
	}

	@Override
	public void keyPressed(int key) {
		if (key == KeyEvent.VK_ENTER) {
			select();
		}
		if (key == KeyEvent.VK_DOWN) {
			//this.currentChoice = Math.floorMod(this.currentChoice + 1,
			this.currentChoice = (this.currentChoice + 1)%
					this.options.length;
		}
		if (key == KeyEvent.VK_UP) {
			this.currentChoice = (this.currentChoice - 1)%
					this.options.length;
		}
	}

	@Override
	public void keyReleased(int key) {
		// Not used in MenuState.
	}

	@Override
	public Level getLevel() {
		return super.getLevel();
	}

}
