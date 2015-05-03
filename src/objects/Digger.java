package objects;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import toolObjects.MusicPlayer;

/**
 * A Digger Player object that can move through Dirt blocks, pick up Emerald
 * blocks, and move Gold blocks.
 * 
 * @author Mark Hays and his students. Created Feb 12, 2015.
 */
public class Digger {
	// Constants.
	private final int STARTING_X = 3; // The starting tile position.
	private final int STARTING_Y = 3; // The starting tile position.
	private final int SCALE = 32; // The size of a tile in pixels.
	private final int EMERALD_POINT = 100; // Points gained from each emerald.
	private final int TREASURE_POINT = 500; // Points gained from each broken
											// gold bag.
	private final double MOVE_SPEED = 1; // The move speed in pixels.
	public final int MAX_LIFE = 5; // The max amount of lives a player can
									// gain.
	private final int TREASURE = 20;

	// Variables
	private double tilePositionX; // Digger's tile position in the X plane.
	private double tilePositionY; // Digger's tile position in the Y plane.
	private BufferedImage sprite; // Digger's player image.
	private Level level; // The current map.
	private int score; // The player's score.
	private int life; // The player's remaining lives.
	private Rectangle2D.Double collisionBox; // The Digger's collisionBox.
	private boolean isGodModeEnabled; // GodMode on/off(true/false).
	private String direction; // Indicates the direction the player was last
								// moving.
	private int delayCounter; // The movement delay.
	private MusicPlayer pointGain; // Pointgain sound.

	/**
	 * Constructs a Digger object to represent the player.
	 * 
	 * @param level
	 *            the level the player is on.
	 */
	public Digger(Level level) {
		// Location on 20 x 20 board.
		this.tilePositionX = this.STARTING_X;
		this.tilePositionY = this.STARTING_Y;

		// Initialize score and level.
		this.score = 0;
		this.level = level;

		// Sets Initial Conditions.
		this.isGodModeEnabled = false;
		this.life = 0;
		this.collisionBox = new Rectangle2D.Double(this.tilePositionX
				* this.SCALE, this.tilePositionY * this.SCALE, this.SCALE,
				this.SCALE);

		// Set initial direction and delayCounter.
		this.direction = "right";
		this.delayCounter = 0;
		
		this.pointGain = new MusicPlayer(
				"/music/pointsGainSound.mp3");

		// Assign the player's image.
		try {
			this.sprite = ImageIO.read(getClass().getResourceAsStream("/digger/digger.png"));
		} catch (IOException e) {
			System.out.println("Error: File not found.");
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Draws the player on a given Graphics2D object.
	 * 
	 * @param g2
	 *            the the given Graphics2D object.
	 */
	public void draw(Graphics2D g2) {
		g2.translate(this.tilePositionX * this.SCALE, this.tilePositionY
				* this.SCALE);

		g2.drawImage(this.sprite, 0, 0, null);

		g2.translate(-this.tilePositionX * this.SCALE, -this.tilePositionY
				* this.SCALE);
	}

	/**
	 * Moves the player object accordingly.
	 * 
	 * @param direction
	 *            a string indicating direction.
	 */
	public void move(String direction) {
		// Constants.
		final int THRESHOLD = 5;

		// Delay player movement.
		if (this.delayCounter <= THRESHOLD) {
			return;
		}

		// Reset delay.
		this.delayCounter = 0;

		// Set references.
		double oldX = this.tilePositionX;
		double oldY = this.tilePositionY;
		this.direction = direction;
		int[][] map = this.level.getMap();

		// Move the player accordingly.
		if (direction.equals("left")) {
			this.tilePositionX = this.tilePositionX - this.MOVE_SPEED;
		}
		if (direction.equals("right")) {
			this.tilePositionX = this.tilePositionX + this.MOVE_SPEED;
		}
		if (direction.equals("up")) {
			this.tilePositionY = this.tilePositionY - this.MOVE_SPEED;
		}
		if (direction.equals("down")) {
			this.tilePositionY = this.tilePositionY + this.MOVE_SPEED;
		}

		// Updates the collisionBox.
		this.collisionBox.setRect(this.tilePositionX * this.SCALE,
				this.tilePositionY * this.SCALE, this.SCALE, this.SCALE);

		// Checks if it is colliding with regular or Gold tiles.
		collideWithOthers(map);
		collideWithGold(map, oldX, oldY);

		// Checks if it is colliding with the barrier.
		if (collideWithBarrier()) {
			this.tilePositionX = oldX;
			this.tilePositionY = oldY;
		}

		// Generates a new regualrCollisionBoxes array list.
		this.level.generateRegularCollisionBoxes();
	}

	/**
	 * Resets the player's position to its starting point. Mainly used after
	 * levels are completed.
	 * 
	 */
	public void resetPlayerPosition() {
		this.tilePositionX = this.STARTING_X;
		this.tilePositionY = this.STARTING_Y;
	}

	/**
	 * Returns the Digger X-Coordinate position.
	 * 
	 * @return the X-Coordinate position.
	 */
	public double getPositionX() {
		return this.tilePositionX;
	}

	/**
	 * Returns the Digger Y-Coordinate position.
	 * 
	 * @return the Y-Coordinate position.
	 */
	public double getPositionY() {
		return this.tilePositionY;
	}

	/**
	 * Updates Digger's level field. Mainly used after levels are completed.
	 * 
	 * @param level
	 *            a new level file
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * Returns the Digger's level data.
	 * 
	 * @return level data.
	 */
	public Level getLevel() {
		return this.level;
	}

	/**
	 * Adds the specified points to the score.
	 * 
	 * @param pointsToAdd
	 *            the specified amount of points.
	 */
	public void addScore(int pointsToAdd) {
		this.score = this.score + pointsToAdd;
	}

	/**
	 * Resets the score to 0.
	 * 
	 */
	public void resetScore() {
		this.score = 0;
	}

	/**
	 * Returns a boolean value of whether or not the Digger is colliding with
	 * the Barriers.
	 * 
	 * @return true if colliding with barrier false if not.
	 */
	public boolean collideWithBarrier() {
		// Gets the barrierCollisionBoxes array list from the level.
		ArrayList<Rectangle2D.Double> barrierCollisonBoxes = this.level
				.getBarrierCollisonBoxes();

		// Loop through and check if the Digger is colliding with a
		// barrierCollisonBox.
		for (int i = 0; i < barrierCollisonBoxes.size(); i++) {
			if (this.collisionBox.intersects(barrierCollisonBoxes.get(i))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if the Digger is colliding with non-barrier blocks. If it can dig
	 * through, Digger will move through.
	 * 
	 * @param map
	 *            the level tileID map.
	 */
	public void collideWithOthers(int[][] map) {
		// Constants.
		final int DIRT = this.level.DIRT;
		final int EMERALD = this.level.EMERALD;
		final int CLEAR = this.level.CLEAR;

		// Gets the regularCollisionBoxes from the array list from the level.
		ArrayList<Rectangle2D.Double> regularCollisionBoxes = this.level
				.getRegularCollisionBoxes();

		// Loop through and do specific actions depending on the tile block.
		for (int i = 0; i < regularCollisionBoxes.size(); i++) {
			if (this.collisionBox.intersects(regularCollisionBoxes.get(i))) {
				int column = (int) regularCollisionBoxes.get(i).getX()
						/ this.SCALE;
				int row = (int) regularCollisionBoxes.get(i).getY()
						/ this.SCALE;
				if (map[column][row] == DIRT) {
					this.level.updateTile(column, row, CLEAR);
				}
				if (map[column][row] == EMERALD) {
					this.level.updateTile(column, row, CLEAR);
					this.score = this.score + this.EMERALD_POINT;
					this.pointGain.play();
					// System.out.println(this.score);
				}
				// if (map[column][row] == this.GOLD) {
				// this.die();
				// }
				if (map[column][row] == this.TREASURE) {
					this.level.updateTile(column, row, CLEAR);
					this.score = this.score + this.TREASURE_POINT;
					// System.out.println(this.score);
				}
			}
		}
	}

	/**
	 * Checks to see if the gold box and player are at the same spot.
	 * 
	 * @return true if they are at the same spot. False if not.
	 */
	public boolean sameAsGold() {
		if (this.level == null) {
			return false;
		}

		ArrayList<Gold> goldObjects = this.level.getGoldObjects();

		for (int i = 0; i < goldObjects.size(); i++) {
			if (this.tilePositionX == goldObjects.get(i).getPositionX()
					&& this.tilePositionY == goldObjects.get(i).getPositionY()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if it is colliding with the gold object.
	 * 
	 * @param map
	 *            the map data.
	 * @param oldX
	 *            the old x position the Digger was at.
	 * @param oldY
	 *            the old y position the Digger was at.
	 */
	public void collideWithGold(int[][] map, double oldX, double oldY) {
		// Constants.
		final int CLEAR = this.level.CLEAR;

		// Gets the goldObjects array list from the level.
		ArrayList<Gold> goldObjects = this.level.getGoldObjects();

		// Loops through to check for collision.
		for (int i = 0; i < goldObjects.size(); i++) {
			// Moves the Gold in the horizontal position if applicable.
			if (this.collisionBox.intersects(goldObjects.get(i)
					.getCollisionBox())) {
				goldObjects.get(i).move(this.direction);

				// If the Digger moves from above, the Gold and the Digger will
				// not move.
				if (this.direction.equals("down")) {
					this.tilePositionX = oldX;
					this.tilePositionY = oldY;
				}

				if (this.direction.equals("up")) {
					this.tilePositionX = oldX;
					this.tilePositionY = oldY;
				}

				// Updates the tileID below the Digger.
				this.level.updateTile((int) this.getPositionX(),
						(int) this.getPositionY(), CLEAR);
			}
		}
	}

	/**
	 * Returns the Digger's collisionBox.
	 * 
	 * @return the collisionBox.
	 */
	public Rectangle2D.Double getCollisionBox() {
		return this.collisionBox;
	}

	/**
	 * Kills the Digger and resets its position.
	 * 
	 */
	public void die() {
		if (this.isGodModeEnabled) {
			return;
		}

		this.life--;
		resetPlayerPosition();
		this.collisionBox.setRect(this.tilePositionX, this.tilePositionY,
				this.SCALE, this.SCALE);
	}

	/**
	 * Returns the life count of the Digger.
	 * 
	 * @return the life count.
	 */
	public int getLife() {
		return this.life;
	}

	/**
	 * Sets the Diggers life count to the specified paramenter.
	 * 
	 * @param life
	 *            the specified life parameter.
	 */
	public void setLife(int life) {
		this.life = life;
	}

	/**
	 * Checks for bonuses to add for the Digger if prerequisites are met.
	 * 
	 */
	public void checkScore() {
		if (this.score != 0 && this.score % 5000 == 0
				&& this.life < this.MAX_LIFE) {
			this.life++;
		}
	}

	/**
	 * Returns the score of the Digger.
	 * 
	 * @return the score.
	 */
	public int getScore() {
		return this.score;
	}

	/**
	 * Toggles GodMode. (Makes the player unkillable).
	 * 
	 */
	public void toggleGodMode() {
		this.isGodModeEnabled = !this.isGodModeEnabled;
		if (this.isGodModeEnabled) {
			System.out.println("GodMode Enabled.");
		} else {
			System.out.println("GodMode Disabled.");
		}
	}

	/**
	 * Returns the direction the Digger was moving.
	 * 
	 * @return String of the direction.
	 */
	public String getDirection() {
		return this.direction;
	}

	/**
	 * Increases the delayCounter by 1.
	 * 
	 */
	public void incrementDelayCounter() {
		this.delayCounter++;
	}

}
