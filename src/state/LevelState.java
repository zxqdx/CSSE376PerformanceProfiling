package state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import main.GamePanel;
import objects.Bullet;
import objects.Digger;
import objects.Enemy;
import objects.Gold;
import objects.Hobbin;
import objects.Level;
import objects.Nobbin;
import toolObjects.FreeTTS;
import toolObjects.MusicPlayer;

/**
 * LevelState manages the level and player interation for a specified
 * StateManager.
 * 
 * @author Mark Hays and his students. Created Feb 6, 2015.
 */
public class LevelState extends State {
	// Field Constants.
	private final int TARGET_WAIT_TIME = 20; // The amount of times update must
												// be called for 3 seconds to
												// pass.
	private final int BULLET_CHARGE_LIMIT = 40; // The bullet charge limit.
												// Allows the bullet to shoot if
												// bulletCharge has reach this
												// limit.

	// Field Variables.
	protected Level level; // The map the LevelState is representing.
	private Digger player; // The player the LevelState is tracking.
	private int levelNumber; // The level file indictor.
	private int nextLevelIndex; // The level to progress to.
	private int levelLimit; // The limit of the states arraylist of the
							// stateManager.
	private boolean isShooting; // The boolean value indicating to shoot a
								// Bullet.
	private Bullet bullet; // The passed Bullet object.
	private int enemyWaitTime; // Delay Enemy spawn rate.
	private int bulletCharge; // Delay rapid bullet fire.
	private ArrayList<Enemy> enemies; // ArrayList of Enemies.
	// Music.
	private MusicPlayer backgroundMusic;
	private MusicPlayer chestCrush;
	private MusicPlayer gameOverDeathSound;
	private MusicPlayer deathSound;

	/**
	 * Constructs a LevelState which manages the player's position and
	 * interation with the level.
	 * 
	 * @param stateManager
	 *            the stateManager managing this state.
	 * @param levelNumber
	 *            used to access the correct level file.
	 */
	public LevelState(StateManager stateManager, int levelNumber) {
		// Sets the stateManger and levelNumber.
		this.stateManager = stateManager;
		this.levelNumber = levelNumber;
		this.levelLimit = this.stateManager.getStatesSize();

		// Gets bullet and initialize boolean.
		this.bullet = this.stateManager.getBullet();
		this.isShooting = false;
		this.bulletCharge = this.BULLET_CHARGE_LIMIT;

		// Set enemyWaitTime to 0.
		this.enemyWaitTime = 0;

		this.enemies = new ArrayList<Enemy>();

		// Set up background music loop.
		this.backgroundMusic = new MusicPlayer("/music/levelMusic.mp3");
		this.backgroundMusic.playLoop();
		
		this.chestCrush = new MusicPlayer(
				"/music/chestDeathSound.mp3");
		this.gameOverDeathSound = new MusicPlayer(
				"/music/gameOverDeathSound.mp3");
		this.deathSound = new MusicPlayer(
				"/music/deathSound.mp3");

		// Initialize the variable.
		initialize();
	}

	@Override
	public void initialize() {
		// Gets the passed player and enemy objects.
		this.player = this.stateManager.getPlayer();

		// Add Enemies to enemies array list.
		for (int i = 0; i < this.levelNumber + 3; i++) {
			this.enemies.add(new Nobbin(this.level, this.player));
		}

		// Get the correct file String for level.
		String fileName = "/text/level/Level " + this.levelNumber
				+ ".txt";

		// Set level.
		this.level = new Level(fileName, 32);
	}

	@Override
	public Level getLevel() {
		return this.level;
	}

	@Override
	public void draw(Graphics2D g2) {
		// Clears the frame.
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, GamePanel.GAMEPANEL_WIDTH, GamePanel.GAMEPANEL_HEIGHT);

		// Draws the level.
		this.level.draw(g2);

		// Draws the player.
		this.player.incrementDelayCounter();
		this.player.draw(g2);

		// Draws Gold.
		ArrayList<Gold> goldObjects = this.level.getGoldObjects();
		for (int i = 0; i < goldObjects.size(); i++) {
			goldObjects.get(i).draw(g2);
		}

		// Draws bullet if space is clicked.
		if (this.isShooting) {
//			MusicPlayer bulletSFX = new MusicPlayer("/music/bulletFX.mp3");
//			bulletSFX.play();
			this.stateManager.getBullet().draw(g2);
			if (this.stateManager.getBullet().getHasCollided()) {
				this.bullet.setIsFieldSet(false);
				this.isShooting = false;
			}
		}

		// If bulletCharge is not full, charge it by increments of 1.
		if (this.bulletCharge < 40) {
			this.bulletCharge++;
		}

		// Draws Enemies.
		drawEnemy(g2);
	}

	private void drawEnemy(Graphics2D g2) {
		for (int i = 0; i < this.enemies.size(); i++) {
			if (this.enemies.get(i).getLevel() != null) {
				this.enemies.get(i).incrementDelayCounter();
				this.enemies.get(i).draw(g2);
			}
		}
	}

	@Override
	public void update() {
		// Spawn Enemy.
		spawnEnemy();

		// Increment enemyWaitTime.
		this.enemyWaitTime++;

		// Random Enemy Transformation.
		transform();

		// Checks if the Enemy collides with a Gold object.
		playerCollideWithGold();

		// Checks if the Enemy collides with a Gold object.
		enemyCollideWithGold();

		// Checks if the Enemy collides with the player.
		enemyCollideWithPlayer();

		// Updates visual score counter.
		updateScoreCounter();

		// Updates the visual life counter.
		checkGameOverStatus(this.player.getLife());

		// Updates the visual life counter.
		updateLifeCounter();

		// Check emerald on board. If empty (0), progress.
		if (this.level.getEmeraldCount() == 0) {
			progressForward();
		}
	}

	/**
	 * Checks if the player has collided with a Gold object.
	 * 
	 */
	public void playerCollideWithGold() {
		if (this.player.sameAsGold()) {
			this.player.die();

			// Plays the chestDeathSound when chest kills player.
			this.chestCrush.play();

			resetAllEnemy();
		}
	}

	/**
	 * Checks if the game is over (life = 0)
	 * 
	 * @param currentLife
	 *            the number of lives the player has.
	 */
	public void checkGameOverStatus(int currentLife) {
		// Constants.
		final int HIGHSCORE_STATE = 5;

		// If the currentLife is 0, end the game and transition the player back
		// to MenuState.
		if (currentLife == 0) {
			// Closes the backgroundMusic so that other sounds can be heard.
			this.backgroundMusic.close();

			// Plays the gameOverDeathSound.
			this.gameOverDeathSound.play();

			// Allows the gameOverDeathSound to finish playing.
			try {
				Thread.sleep(6500);
			} catch (InterruptedException exception) {
				exception.printStackTrace();
			}

			// Request for player name.
			requestForName();

			// Sets the isPlayingSurvival status to false in both LevelState and
			// StateManager.
			this.stateManager.setIsPlayingSurvival(false);

			// Reset the player's score and bring them to the Digger screen.
			this.player.resetScore();
			this.stateManager.setState(HIGHSCORE_STATE);

			// Set player level to null and close gameOverDeathSound.
			this.player.setLevel(null);
			gameOverDeathSound.close();
		}
	}

	/**
	 * Spawns Enemies.
	 * 
	 */
	public void spawnEnemy() {
		// Variables.
		Random generator = new Random();

		for (int i = 0; i < this.enemies.size(); i++) {
			// Only spawn the last 3 Enemies after certain levels are completed.
			if (i == 5 && this.levelNumber >= 3) {
				if (this.enemies.get(i).getLevel() == null
						&& generator.nextInt(101) % (10 + i * 10) == 0
						&& this.enemyWaitTime >= i * this.TARGET_WAIT_TIME) {
					this.enemies.get(i).setLevel(this.level);
					continue;
				}
			}
			if (i == 4 && this.levelNumber >= 2) {
				if (this.enemies.get(i).getLevel() == null
						&& generator.nextInt(101) % (10 + i * 10) == 0
						&& this.enemyWaitTime >= i * this.TARGET_WAIT_TIME) {
					this.enemies.get(i).setLevel(this.level);
					continue;
				}
			}
			if (i == 3 && this.levelNumber >= 2) {
				if (this.enemies.get(i).getLevel() == null
						&& generator.nextInt(101) % (10 + i * 10) == 0
						&& this.enemyWaitTime >= i * this.TARGET_WAIT_TIME) {
					this.enemies.get(i).setLevel(this.level);
					continue;
				}
			}

			// Spawn the first 3 regardless of level as long as the conditions
			// are met.
			if (i < 3 && this.enemies.get(i).getLevel() == null
					&& generator.nextInt(101) % (10 + i * 10) == 0
					&& this.enemyWaitTime >= i * this.TARGET_WAIT_TIME) {
				this.enemies.get(i).setLevel(this.level);
			}

		}

		// Update StateManager Enemies Array.
		updateStateManagerEnemiesArray();

	}

	/**
	 * Attempt to randomly transform the Enemy to a another class.
	 * 
	 */
	public void transform() {
		// Variables.
		Random generator = new Random();
		int index;

		for (int i = 0; i < this.enemies.size(); i++) {
			index = i;
			Enemy currentEnemy = this.enemies.get(i);
			if (currentEnemy.getLevel() != null && generator.nextInt(300) == 1) {
				this.enemies.remove(i);
				if (currentEnemy instanceof Nobbin) {
					this.enemies.add(index, new Hobbin(this.level, this.player,
							currentEnemy.getTileCoordinate()));
				} else {
					this.enemies.add(index, new Nobbin(this.level, this.player,
							currentEnemy.getTileCoordinate()));
				}
			}
		}
	}

	/**
	 * Update the StateManager Enemies array list.
	 * 
	 */
	public void updateStateManagerEnemiesArray() {
		this.stateManager.setEnemies(this.enemies);
	}

	/**
	 * Checks if the Enemy has collided with the player.
	 */
	public void enemyCollideWithPlayer() {
		for (int i = 0; i < this.enemies.size(); i++) {
			if (this.enemies.get(i).collideWithPlayer()) {
				// Kills the player and resets position.
				this.player.die();

				// Play deathSound.mp3 when Enemy kills player.
				this.deathSound.play();

				resetAllEnemy();

				nullEnemyLevel();
			}

		}
	}

	/**
	 * Updates the visual score counter.
	 * 
	 */
	public void updateScoreCounter() {
		String currentScoreString = String.format("%05d",
				this.player.getScore());
		int newScore = Integer.parseInt(currentScoreString);
		int evaluate;

		// Updates the visual score counter.
		for (int i = 0; i < currentScoreString.length(); i++) {
			int x = (this.level.getMap().length - 1) - i;
			evaluate = newScore % 10;
			int tileID = this.level.convertToTileID(evaluate);
			this.level.updateTile(x, 0, tileID);
			newScore = newScore / 10;
		}

	}

	/**
	 * Updates the visual life counter.
	 * 
	 */
	public void updateLifeCounter() {
		int currentLife = this.player.getLife();
		int tileID = this.level.convertToTileID(currentLife);
		this.level.updateTile(5, 0, tileID);
	}

	/**
	 * Checks if the Enemy has collided with a Gold object.
	 * 
	 */
	public void enemyCollideWithGold() {
		for (int i = 0; i < this.enemies.size(); i++) {
			if (this.enemies.get(i).sameAsGold()) {
				this.enemies.get(i).die();
			}
		}
	}

	/**
	 * Request for player name to store data.
	 * 
	 */
	public void requestForName() {
		String playerName = JOptionPane.showInputDialog("Enter your name: ");
		int playerScore = this.player.getScore();

		// Reads the playerName on the speaker.
		FreeTTS readPlayerName = new FreeTTS("Congratulations " + playerName);
		readPlayerName.speak();

		try {
			File highscoreFile = new File(getClass().getResource("/text/highscore.txt").getFile());
			//File highscoreFile = new File("Resources/text/highscore.txt");
			PrintWriter writer = new PrintWriter(new FileWriter(highscoreFile, true), true);
			writer.append("\n" + playerName + " " + playerScore);
			writer.close();
		} catch (Exception e) {
			System.out.println("Error: File not found.");
		}
	}

	/**
	 * General code both progressFoward and progressBackward will run.
	 * 
	 */
	public void generalProgression() {
		// Checks if survival mode was selected. If so, engage in stage looping
		// until player death.
		if (this.stateManager.getIsPlayingSurvival()) {
			if (this.nextLevelIndex >= this.levelLimit) {
				this.nextLevelIndex = 1;
			}
		} else {
			// If the player wins the last level, ask for name.
			if (this.levelNumber == 3) {
				requestForName();
				this.player.resetScore();
			}
		}

		// Close the backgroundMusic.
		this.backgroundMusic.close();

		// Ensures the nextLevelIndex is within the level limits.
		if (this.nextLevelIndex >= this.levelLimit) {
			//this.nextLevelIndex = Math.floorMod(this.nextLevelIndex,
			this.nextLevelIndex = (this.nextLevelIndex %
					this.levelLimit);
		}

		// Set the currentState to a new level and reset the player's position.
		this.stateManager.setState(this.nextLevelIndex);
		this.player.resetPlayerPosition();

		// Sets the player's level accordingly.
		Level currentLevel = this.stateManager.getLevel();
		this.player.setLevel(currentLevel);
	}

	/**
	 * Nulls all enemy levels.
	 * 
	 */
	private void nullEnemyLevel() {
		for (int i = 0; i < this.enemies.size(); i++) {
			this.enemies.get(i).setLevel(null);
		}
	}

	/**
	 * Resets all Enemies position.
	 * 
	 */
	private void resetAllEnemy() {
		for (int i = 0; i < this.enemies.size(); i++) {
			this.enemies.get(i).resetPosition();
		}
	}

	public void setEnemyPause() {
		for (int i = 0; i < this.enemies.size(); i++) {
			this.enemies.get(i).setPause();
		}
	}

	/**
	 * Moves the currentState to the nextLevel.
	 * 
	 */
	protected void progressForward() {
		// Updates nextLevelIndex. Adds 2 due to levelNumber starting at 0, but
		// states Array starts level index at 1. Also, updates levelLimit if
		// stateManager has updated its arraylist.
		this.nextLevelIndex = this.levelNumber + 2;
		generalProgression();

	}

	/**
	 * Moves the currentState backwards.
	 * 
	 */
	public void progressBackWard() {
		// Wraps the nextLevelIndex if at level 0.
		System.out.println(this.levelNumber);
		if (this.levelNumber <= 0) {
			this.nextLevelIndex = this.levelLimit - 1;
		} else {
			this.nextLevelIndex = this.levelNumber;
		}
		generalProgression();
	}

	@Override
	public void keyPressed(int key) {
		// Constants.
		final int BULLET_CHARGE_LIMIT = 40;

		// Player Movement.
		if (key == KeyEvent.VK_LEFT) {
			this.player.move("left");
		}
		if (key == KeyEvent.VK_RIGHT) {
			this.player.move("right");
		}
		if (key == KeyEvent.VK_UP) {
			this.player.move("up");
		}
		if (key == KeyEvent.VK_DOWN) {
			this.player.move("down");
		}

		// Level Skipping.
		if (key == KeyEvent.VK_U) {
			progressForward();
		}
		if (key == KeyEvent.VK_D) {
			progressBackWard();
		}

		// Pause Enemies.
		if (key == KeyEvent.VK_P) {
			setEnemyPause();
		}

		// Toggle GodMode.
		if (key == KeyEvent.VK_G) {
			this.player.toggleGodMode();
		}

		// Shooting Bullets.
		if (key == KeyEvent.VK_SPACE) {
			if (this.bulletCharge >= BULLET_CHARGE_LIMIT) {
				this.isShooting = true;
				if (this.stateManager.bullet.getHasCollided()) {
					this.stateManager.bullet.setHasCollided(false);
				}
				this.bulletCharge = 0;
			}

		}
	}

	@Override
	public void keyReleased(int key) {
		// Not Used.

	}
}
