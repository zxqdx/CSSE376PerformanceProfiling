package objects;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import toolObjects.MusicPlayer;

/**
 * Gold can be pushed horizontally, fall, kill people, and break up (if fallen
 * more than 1 tile) and be picked up as points.
 * 
 * @author Mark Hays and his students. Created Feb 13, 2015.
 */
public class Gold {
	// Constants.
	private final int SCALE = 32; // The size of a tile in pixels.

	// Variables.
	private int tilePositionX; // Gold tile position in the X plane.
	private int tilePositionY; // Gold tile position in the Y plane.
	private BufferedImage sprite; // Gold player image.
	private Level level; // The current level.

	private Rectangle2D.Double collisionBox; // The collision box for the Gold box.
	private int fallingWaitCounter; // The counter to make it wait before it falls.
	private int breakingCounter; // The counter to make it break when it falls more than one square.
	private boolean stopped; // States whether the box has stopped falling.
	private int delay; // This counter makes the gold box fall faster or slower, depending on
	 					//  how high the counter is set.

	private MusicPlayer chestOpen; // Chest open sound.
	/**
	 * Constructs a Gold object (gold bag).
	 * 
	 * @param level
	 *            the level the Gold object is on.
	 * @param tilePositionX
	 *            the X-Coordinate of the Gold object.
	 * @param tilePositionY
	 *            the Y-Coordinate of the Gold object.
	 */
	public Gold(Level level, int tilePositionX, int tilePositionY) {
		// Sets level and tile position.
		this.level = level;
		this.tilePositionX = tilePositionX;
		this.tilePositionY = tilePositionY;
		
		// Create collisionBox.
		this.collisionBox = new Rectangle2D.Double(this.tilePositionX
				* this.SCALE, this.tilePositionY * this.SCALE, this.SCALE,
				this.SCALE);
		
		this.chestOpen = new MusicPlayer("/music/chestOpenSound.mp3");
		
		// Set counters.
		this.fallingWaitCounter = 0;
		this.breakingCounter = 0;
		this.stopped = false;
		this.delay = 0;
		
		// Get image.
		try {
			this.sprite = ImageIO.read(getClass().getResourceAsStream("/tile/chest.png"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: File not found");
		}
	}

	/**
	 * Draws the Gold boxes on the specified Graphics2D object.
	 * 
	 * @param g2
	 *            the specified Graphics2D object.
	 */
	public void draw(Graphics2D g2) {
		// Sets the variable.
		final int TREASURE = 20;
		
		// Checks if the tile position if off the map. If it is,
		// it returns in order not to draw anything.
		if(this.tilePositionX == -1) {
			return;
		}
		
		// Checks to see if the gold box broke. If it did, updates the 
		// tile position to treasure which can be picked up for points,
		// and sets the the coordinates of the collisionBox and image
		// off the screen to enable it to be gone.
		if(this.breakingCounter > 1 && this.stopped) {
			this.level.updateTile(this.tilePositionX, this.tilePositionY, TREASURE);
			
			// Plays chestOpenSound when chest opens.
			this.chestOpen.play();
			
			this.tilePositionX = -1;
			this.tilePositionY = -1;
			this.collisionBox.setRect(this.tilePositionX, this.tilePositionY, 1, 1);
			return;
		}
		
		// Update the collisionBox.
		this.collisionBox.setRect(this.tilePositionX * this.SCALE,
				this.tilePositionY * this.SCALE, this.SCALE, this.SCALE);
		
		// Draw.
		g2.translate(this.tilePositionX * this.SCALE, this.tilePositionY
				* this.SCALE);
		g2.drawImage(this.sprite, 0, 0, null);
		g2.translate(-this.tilePositionX * this.SCALE, -this.tilePositionY
				* this.SCALE);		

		// Check below if it can fall.
		checkBelow();
	}

	/**
	 * Checks if the tile below is a clear and moves the Gold object downwards.
	 * 
	 */
	public void checkBelow() {
		// Constants.
		final int FALLING_WAIT_LIMIT = 30;
		
		// Setting variables.
		final int CLEAR = this.level.CLEAR;
		int[][] map = this.level.getMap();
		
		// Checks if the space below the box is clear. If it is, it falls.
		if (map[this.tilePositionX][this.tilePositionY + 1] == this.level.CLEAR) {
			// This enables the gold box to wait a little before it falls.
			if (this.fallingWaitCounter == FALLING_WAIT_LIMIT) {
				this.level.updateTile(this.tilePositionX, this.tilePositionY,
						CLEAR);
				if(this.delay == 5) {
					this.tilePositionY++;
					this.breakingCounter++;
					this.delay = 0;
				}
				this.delay++;
			} else {
				this.fallingWaitCounter++;
			}
		} else {
			// If the gold box breaks into treasure, returns.
			if(this.breakingCounter > 1) {
				this.stopped = true;
				return;
			}
			this.breakingCounter = 0;
			this.fallingWaitCounter = 0;
		}
	}

	/**
	 * Moves the treasure in response to other stimuli.
	 * 
	 * @param direction
	 *            the direction it is moving.
	 */
	public void move(String direction) {
		// Constants.
		final int LEFT_BOUND = 0;
		final int RIGHT_BOUND = 19;
		final int UPPER_BOUND = 0;
		final int LOWER_BOUND = 19;

		// Initialize moveDirections as 0.
		int moveDirectionX = 0;
		int moveDirectionY = 0;

		// System.out.println(direction);

		// Get proper moveDirection values.
		if (direction.equals("left")) {
			moveDirectionX = -1;
		}
		if (direction.equals("right")) {
			moveDirectionX = 1;
		}

		// Alters tile position.
		this.tilePositionX = this.tilePositionX + moveDirectionX;
		this.tilePositionY = this.tilePositionY + moveDirectionY;

		// Ensures it is within the bounds of play.
		if (this.tilePositionX == LEFT_BOUND) {
			this.tilePositionX = LEFT_BOUND + 1;
		}
		if (this.tilePositionX == RIGHT_BOUND) {
			this.tilePositionX = RIGHT_BOUND - 1;
		}

		if (this.tilePositionY == UPPER_BOUND) {
			this.tilePositionY = UPPER_BOUND + 1;
		}
		if (this.tilePositionY == LOWER_BOUND) {
			this.tilePositionY = LOWER_BOUND - 1;
		}

	}

	/**
	 * Returns the collisionBox of the Gold object.
	 * 
	 * @return the collisionBox.
	 */
	public Rectangle2D.Double getCollisionBox() {
		return this.collisionBox;
	}

	
	/**
	 * Returns the Gold X-Coordinate position.
	 * 
	 * @return the X-Coordinate position.
	 */
	public double getPositionX() {
		return this.tilePositionX;
	}

	/**
	 * Returns the Gold Y-Coordinate position.
	 * 
	 * @return the Y-Coordinate position.
	 */
	public double getPositionY() {
		return this.tilePositionY;
	}
}

// /**
// * Updates the level for the Gold class.
// *
// * @param lev
// */
// public void updateLevel(Level lev) {
// this.level = lev;
// }
// OLD
// CODE----------------------------------------------------------------------------------
// /**
// * Constructs the Gold object.
// *
// * @param The
// * StateManager
// */
// public Gold(StateManager man) {
//
// this.level = null;
// this.manager = man;
// }
// /**
// * Moves the Gold block the specified direction.
// *
// * @param direction
// * for gold to move.
// * @param the
// * x coordinate of the gold.
// * @param the
// * y coordinate of the gold.
// */
// public void move(String direction, int x, int y) {
// if (direction.equals("left")) {
// if (this.level.getType(x - 1, y) == this.GOLD) {
// this.level.updateTile(x - 2, y, this.GOLD);
// this.level.updateTile(x - 1, y, this.GOLD);
// this.level.updateTile(x, y, this.CLEAR);
// if (this.level.getType(x - 1, y + 1) == this.CLEAR) {
// fall(x - 1, y);
// }
// } else {
// this.level.updateTile(x - 1, y, this.GOLD);
// this.level.updateTile(x, y, this.CLEAR);
// if (this.level.getType(x - 1, y + 1) == this.CLEAR) {
// fall(x - 1, y);
// }
// }
// }
// if (direction.equals("right")) {
// if (this.level.getType(x + 1, y) == this.BARRIER) {
// return;
// } else if (this.level.getType(x + 1, y) == this.GOLD) {
// this.level.updateTile(x + 2, y, this.GOLD);
// this.level.updateTile(x + 1, y, this.GOLD);
// this.level.updateTile(x, y, this.CLEAR);
// if (this.level.getType(x + 1, y + 1) == this.CLEAR) {
// fall(x + 1, y);
// }
// } else {
// this.level.updateTile(x + 1, y, this.GOLD);
// this.level.updateTile(x, y, this.CLEAR);
// if (this.level.getType(x + 1, y + 1) == this.CLEAR) {
// fall(x + 1, y);
// }
// }
// }
// }

// /**
// * Makes the gold block fall and break if fallen more than 2 blocks.
// *
// * @param the
// * x coordinate of the gold.
// * @param the
// * y coordinate of the gold.
// */
// public void fall(int x, int y) {
// ArrayList<Enemy> enemies = this.manager.getEnemies();
// try {
// // Graphics g = getGraphics();
// // Graphics2D g2 = (Graphics2D) g;
// TimeUnit.MILLISECONDS.sleep(500);
// int counter = 0;
// while (true) {
// TimeUnit.MILLISECONDS.sleep(100);
// if (this.manager.getPlayer().getPositionX() == x
// && this.manager.getPlayer().getPositionY() == y) {
// this.manager.getPlayer().die();
// }
// for (Enemy enemy : enemies) {
// if (enemy.getTileCoordinate().getX() == x
// && enemy.getTileCoordinate().getY() == y) {
// enemy.die();
// }
// }
// if (this.level.getType(x, y + 1) == this.DIRT
// || this.level.getType(x, y + 1) == this.BARRIER) {
// if (counter > 1) {
// this.level.updateTile(x, y, this.TREASURE);
// }
// break;
// }
// this.level.updateTile(x, y + 1, this.GOLD);
// this.level.updateTile(x, y, this.CLEAR);
// y = y + 1;
// counter++;
// }
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// }
//
// /**
// * Checks the type of tile at specified coordinates
// *
// * @param x
// * coordinate
// * @param y
// * coordinate
// * @return the tileID
// */
// public int check(int x, int y) {
// if (this.level.getType(x, y) == this.BARRIER) {
// return this.BARRIER;
// }
// if (this.level.getType(x, y) == this.BARRIER) {
// return this.BARRIER;
// }
// if (this.level.getType(x, y + 1) == this.CLEAR) {
// return this.CLEAR;
// }
// // To satisfy the necessary return statement
// return -1;
// }

