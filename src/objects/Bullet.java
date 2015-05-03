package objects;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import state.StateManager;
import toolObjects.MusicPlayer;

/**
 * A Bullet object represents a weapon a Digger can fire to defend itself.
 * 
 * @author Mark Hays and his students. Created Feb 12, 2015.
 */
public class Bullet {
	// Constants.
	private final int SCALE = 32;

	// Field Variables.
	private BufferedImage sprite; // The Bullet sprite image.
	private Rectangle2D.Double collisionBox; // The Bullet collision box.
	private double tilePositionX; // The Bullet's X tile position.
	private double tilePositionY; // The Bullet's Y tile position.
	private StateManager stateManager; // The stateManger managing the Bullet.
	private Digger player; // The Digger object wielding the Bullet.
	private String direction; // The direction of the Bullet's trajectory.
	private Level level; // The level file of the Bullet.
	private boolean hasCollided; // The boolean value indicating if the Bullet
									// has collided with an object.
	private boolean isFieldSet; // The boolean value indicating if the Bullet
								// has had its field set.

	private HashMap<String, BufferedImage> bulletMap; // Bullet Images.
	private MusicPlayer bulletSound; // Bullet sound.

	/**
	 * A bullet fired by the player. Constantly checks for collision with the
	 * enemy. Requires a StateManager to gather necessary data on the player and
	 * level.
	 * 
	 * @param stateManager
	 *            the required StateManger.
	 */
	public Bullet(StateManager stateManager) {
		// Set the StateManger field.
		this.stateManager = stateManager;

		// Gets the player and direction.
		this.player = this.stateManager.getPlayer();
		this.direction = this.player.getDirection();

		// Gets the level the player is on.
		this.level = this.player.getLevel();

		// Gets the player's positin.
		this.tilePositionX = this.player.getPositionX();
		this.tilePositionY = this.player.getPositionY();

		// Initialize collision and fieldSet booleans to false.
		this.hasCollided = false;
		this.isFieldSet = false;
		
		this.bulletSound = new MusicPlayer("/music/bulletFX.mp3");
		
		this.bulletMap = new HashMap<String, BufferedImage>();
		populateBulletMap();

		// Get sprite image.
		this.sprite = this.bulletMap.get(this.direction);

		// Set collisionBox.
		this.collisionBox = new Rectangle2D.Double(this.tilePositionX
				* this.SCALE, this.tilePositionY * this.SCALE, this.SCALE,
				this.SCALE);
	}

	/**
	 * Populates the bulletMap.
	 *
	 */
	public void populateBulletMap() {
		try {
			this.bulletMap.put("right", ImageIO.read(getClass().getResourceAsStream("/bullet/bulletRight.png")));
			this.bulletMap.put("left", ImageIO.read(getClass().getResourceAsStream("/bullet/bulletLeft.png")));
			this.bulletMap.put("up", ImageIO.read(getClass().getResourceAsStream("/bullet/bulletUp.png")));
			this.bulletMap.put("down", ImageIO.read(getClass().getResourceAsStream("/bullet/bulletDown.png")));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: A file was not found.");
		}
		
	}

	/**
	 * Draws the Bullet onto the given Graphics2D object.
	 * 
	 * @param g2
	 *            the given Graphics2D object.
	 */
	public void draw(Graphics2D g2) {
		// If this is the first run, set the field.
		if (!(this.isFieldSet)) {
			setField();
			this.setIsFieldSet(true);
		}

		// Logging.
		// System.out.println("X: " + this.tilePositionX);
		// System.out.println("Y: " + this.tilePositionY);
		// System.out.println(this.hasCollided);

		// If Bullet has collided with something, do not draw.
		if (this.hasCollided) {
			return;
		}

		// Initialize moveDirections as 0.
		int moveDirectionX = 0;
		int moveDirectionY = 0;

		// Get proper moveDirection values.
		if (this.direction.equals("left")) {
			moveDirectionX = -1;
		}
		if (this.direction.equals("right")) {
			moveDirectionX = 1;
		}
		if (this.direction.equals("up")) {
			moveDirectionY = -1;
		}
		if (this.direction.equals("down")) {
			moveDirectionY = 1;
		}

		// Update tile position.
		this.tilePositionX = this.tilePositionX + moveDirectionX;
		this.tilePositionY = this.tilePositionY + moveDirectionY;

		// Check if it has collided with a non-clear block or enemy.
		collide();
		
		this.bulletSound.play();

		// Draw.
		g2.translate(this.tilePositionX * this.SCALE, this.tilePositionY
				* this.SCALE);

		g2.drawImage(this.sprite, 0, 0, null);

		g2.translate(-this.tilePositionX * this.SCALE, -this.tilePositionY
				* this.SCALE);

		// Update collisionBox.
		this.collisionBox.setRect(this.tilePositionX * this.SCALE,
				this.tilePositionY * this.SCALE, this.SCALE, this.SCALE);

		// Checks for collision again. Mainly a fail-safe.
		collide();
		
	}

	/**
	 * Sets the fields of the bullet when called. Can be called only once.
	 * 
	 */
	public void setField() {
		// Update the direction and level.
		this.direction = this.player.getDirection();
		this.level = this.player.getLevel();

		// If the level still returns null or hasCollided is true, do not run.
		if (this.level == null) {
			return;
		}

		// Get the tilePosition to shoot bullet.
		this.tilePositionX = this.player.getPositionX();
		this.tilePositionY = this.player.getPositionY();

		// Get the correct Sprite image.
		this.sprite = this.bulletMap.get(this.direction);

		// Updates the collisionBox.
		this.collisionBox.setRect(this.tilePositionX * this.SCALE,
				this.tilePositionY * this.SCALE, this.SCALE, this.SCALE);
	}

	/**
	 * Checks if the Bullet has collided with a non-clear block or Enemy.
	 * 
	 */
	public void collide() {
		// Get the collision boxes of the Barriers and regular blocks.
		ArrayList<Rectangle2D.Double> collisionBoxes = new ArrayList<Rectangle2D.Double>();
		collisionBoxes.addAll(this.level.getBarrierCollisonBoxes());
		collisionBoxes.addAll(this.level.getRegularCollisionBoxes());

		// Get the enemies array list.
		ArrayList<Enemy> enemies = this.stateManager.getEnemies();

		// Loop through the enemies array list and get the enemy collision
		// block. Kill enemy if collision occurs.
		for (int j = 0; j < enemies.size(); j++) {
			if (this.collisionBox.intersects(enemies.get(j).getCollisionBox())) {
				// System.out.println("Hit Enemy");
				enemies.get(j).die();
				this.hasCollided = true;
			}
		}

		// Loop through the blocks and check if collision occurs.
		for (int k = 0; k < collisionBoxes.size(); k++) {
			if (this.collisionBox.intersects(collisionBoxes.get(k))) {
				// System.out.println("Hit Block");
				this.hasCollided = true;
			}
		}
	}

	/**
	 * Returns the boolean value of hasCollided.
	 * 
	 * @return boolean value of hasCollided.
	 */
	public boolean getHasCollided() {
		return this.hasCollided;
	}

	/**
	 * Sets the current hasCollided boolean value to the specified boolean
	 * value. Preferable method of altering field data outside of class.
	 * 
	 * @param hasCollided
	 *            the specified boolean value.
	 */
	public void setHasCollided(boolean hasCollided) {
		this.hasCollided = hasCollided;
	}

	/**
	 * Returns the boolean value of isFieldSet. Preferable method of obtaining
	 * field data outside of class.
	 * 
	 * @return the boolean value of isFieldSet.
	 */
	public boolean getIsFieldSet() {
		return this.isFieldSet;
	}

	/**
	 * Sets the isFieldSet to the specified boolean value. Preferable method of
	 * altering field data.
	 * 
	 * @param isFieldSet
	 *            the specified boolean value
	 */
	public void setIsFieldSet(boolean isFieldSet) {
		this.isFieldSet = isFieldSet;
	}

}
