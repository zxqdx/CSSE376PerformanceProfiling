package objects;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import toolObjects.Coordinate;
import toolObjects.MusicPlayer;
import toolObjects.Node;

/**
 * An abstract class the constructs an Enemy object that follows the player.
 * 
 * @author Mark Hays and his students. Created Feb 9, 2015.
 */
public abstract class Enemy {
	private final int STARTING_X = 18; // The enemy's starting X position.
	private final int STARTING_Y = 18; // The enemy's starting Y position.
	protected final int SCALE = 32; // The enemy's pixel scale.

	protected int tilePositionX; // The enemy's current tile position in the X
									// direction.
	protected int tilePositionY; // The enemy's current tile position in the Y
									// direction.
	private int playerPositionX; // The player's current tile position in the X
									// direction.
	private int playerPositionY; // The player's current tile position in the Y
									// direction.

	protected BufferedImage sprite; // The enemy's image.
	protected Level level; // The level the enemy is on.
	private Digger player; // The target player to follow.
	protected int[][] map; // The map data from the enemy level.
	protected Node[][] nodeMap; // A node version of the map.

	protected ArrayList<Node> open; // The arraylist of Nodes to check.
	protected ArrayList<Node> close; // The arraylist of Nodes already checked.
	protected ArrayList<Coordinate> path; // A coordinate arraylist of the path
											// direction.
	private boolean pauseStatus; // The pause status of the enemy.
	protected Rectangle2D.Double collisionBox; // The collision box of the
												// enemy.
												// Mainly used to check if the
												// player is hit.
	private int delayCounter; // The movement delay of the Enemy.
	private int waitTime; // The spawning wait time of the Enemy.
	private MusicPlayer enemyDeath; // Enemy Death sound.

	/**
	 * Constructs an Enemy with a specified level and target player.
	 * 
	 * @param level
	 *            the specified level.
	 * @param player
	 *            the target player.
	 */
	public Enemy(Level level, Digger player) {
		try {
			this.sprite = ImageIO.read(getClass().getResourceAsStream("/enemy/nobbin.png"));
		} catch (IOException e) {
			System.out.println("Error: File not found.");
			e.printStackTrace();
			System.exit(0);
		}

		// Set initial pauseStatus.
		this.pauseStatus = false;

		// Obtains the player, the tracking object.
		this.player = player;
		this.level = level;
		if (this.level != null) {
			this.map = this.level.getMap();
		}

		// List of Nodes that needs to be checked or has been checked.
		this.open = new ArrayList<Node>();
		this.close = new ArrayList<Node>();
		
		this.enemyDeath = new MusicPlayer("/music/enemyDeathSound.mp3");


		// Initial conditions of the enemy.
		this.tilePositionX = this.STARTING_X;
		this.tilePositionY = this.STARTING_Y;
		this.collisionBox = new Rectangle2D.Double(this.tilePositionX
				* this.SCALE, this.tilePositionY * this.SCALE, this.SCALE,
				this.SCALE);
		this.delayCounter = 0;
		this.waitTime = 0;

		// Set in another code. -1 is a temp value.
		this.map = null;
		this.playerPositionX = -1;
		this.playerPositionY = -1;
		// Creates the Path array list of coordinates to keep track of where to
		// go.
		this.path = new ArrayList<Coordinate>();
	}

	/**
	 * Draws the enemy as it moves on a specified Graphics2D object.
	 * 
	 * @param g2
	 *            the specified Graphics2D object to be drawn on.
	 */
	public void draw(Graphics2D g2) {
		// If the level is null, do not draw.
		if (this.level == null) {
			return;
		}

		// If the Enemy has a waitTime, do not draw.
		if (this.waitTime != 0) {
			this.waitTime--;
			return;
		}

		// Move the Enemy on the path.
		move();

		// Draw the Enemy.
		g2.translate(this.tilePositionX * this.SCALE, this.tilePositionY
				* this.SCALE);

		g2.drawImage(this.sprite, 0, 0, null);

		g2.translate(-this.tilePositionX * this.SCALE, -this.tilePositionY
				* this.SCALE);

		// Find path.
		run();
	}

	/**
	 * Calculate each tiles Manhattan Distance.
	 * 
	 */
	public void calculateHeuristic() {
		this.map = this.level.getMap(); // Gets the map data as it is modified
										// by the player.
		this.nodeMap = new Node[this.map.length][this.map[0].length]; // 0 is an
																		// arbitrary
																		// row
																		// to
																		// get
																		// column
																		// length.

		// Obtains the player's position.
		this.playerPositionX = (int) Math.round(this.player.getPositionX());
		this.playerPositionY = (int) Math.round(this.player.getPositionY());

		// Variable
		Node rcNode;

		// Loop through the map and calculate the Heuristic values and generate
		// a node for the corresponding nodeMap.
		for (int r = 0; r < this.map.length; r++) {
			for (int c = 0; c < this.map[r].length; c++) {
				Coordinate rcCoordinate = new Coordinate(r, c);
				int heuristic = Math.abs(this.tilePositionX
						- this.playerPositionX)
						+ Math.abs(this.tilePositionY - this.playerPositionY);
				rcNode = new Node(rcCoordinate, heuristic);
				this.nodeMap[r][c] = rcNode;
			}
		}
	}

	/**
	 * Calculates the cost and moveCost of each tile surrounding the specified
	 * current Node.
	 * 
	 * @param nodeCurrent
	 *            the specified current Node.
	 * 
	 */
	public void calculateCost(Node nodeCurrent) {
		// Obtains the current cost of the current Node and its X and Y
		// coordinates.
		int currentPositionX = nodeCurrent.getCoordinate().getX();
		int currentPositionY = nodeCurrent.getCoordinate().getY();
		
		// Obtains the tile ID on the map.		
		if (currentPositionY - 1 == -1) {
			return;
		}
		int mapLeft = this.map[currentPositionX - 1][currentPositionY];
		int mapRight = this.map[currentPositionX + 1][currentPositionY];
		int mapUp = this.map[currentPositionX][currentPositionY - 1];
		int mapDown = this.map[currentPositionX][currentPositionY + 1];

		// Optains the Node data on the nodeMap.
		Node nodeLeft = this.nodeMap[currentPositionX - 1][currentPositionY];
		Node nodeRight = this.nodeMap[currentPositionX + 1][currentPositionY];
		Node nodeUp = this.nodeMap[currentPositionX][currentPositionY - 1];
		Node nodeDown = this.nodeMap[currentPositionX][currentPositionY + 1];

		// Checks that the map is CLEAR and is not already on the checked Node
		// list.
		// If not, link the specified Node to the nodeCurrent and add it to the
		// open list.
		canMoveTo(nodeCurrent, mapLeft, nodeLeft);
		canMoveTo(nodeCurrent, mapRight, nodeRight);
		canMoveTo(nodeCurrent, mapUp, nodeUp);
		canMoveTo(nodeCurrent, mapDown, nodeDown);

		// Double checks if the the current Node is in the close list before
		// adding it.
		if (!this.close.contains(nodeCurrent)) {
			this.close.add(nodeCurrent);
		}
	}

	/**
	 * Checks if the given Node is moveable. If so, it adjusts the appropriate
	 * fields and adds it to the open Node array list.
	 * 
	 * @param nodeCurrent
	 *            the current Node to point back to.
	 * @param mapPosition
	 *            the position within the map 2D array.
	 * @param nodePosition
	 *            the Node to check.
	 */
	protected void canMoveTo(Node nodeCurrent, int mapPosition,
			Node nodePosition) {
		// Constants.
		final int CLEAR = this.level.CLEAR;
		final int TREASURE = this.level.TREASURE;

		// Checks if the Enemy can move to the position.
		if ((mapPosition == CLEAR || mapPosition == TREASURE)
				&& !this.close.contains(nodePosition)) {
			if (nodePosition.getNext() == null) {
				nodePosition.setNext(nodeCurrent);
				this.open.add(nodePosition);
			}
		}
	}

	/**
	 * Calculates the shortest path to the player using Bread-First Search path
	 * finding algorithm
	 * 
	 * @param nodeCurrent
	 *            the current node to check.
	 * 
	 */
	public void calculateShortest(Node nodeCurrent) {
		// System.out.println("calculating");

		// Calculate the maps Heuristic.
		calculateHeuristic();

		// Clear the open and closed list before running main loop.
		this.open.clear();
		this.close.clear();

		// Add the given done to the open list. Mainly done so that the main
		// loop can operate.
		this.open.add(nodeCurrent);

		while (true) {
			// Removes the starting node from the open list and set nodeCurrent
			// to the removed node.
			nodeCurrent = this.open.remove(0);

			// Calculate the cost of the adjecent tiles.
			calculateCost(nodeCurrent);

			// Remove the current Node.
			this.open.remove(nodeCurrent);

			// Logging.
			// System.out.println("Destination: " + "[" + this.playerPositionX
			// + "][" + this.playerPositionY + "]");
			// System.out.println("Current: " + nodeCurrent.toString());
			// System.out.println(this.open.toString());
			// System.out.println(this.close.toString());

			// If the current Node is the where the player is, end the main loop
			// and process movement.
			if (nodeCurrent.getCoordinate().getX() == this.playerPositionX
					&& nodeCurrent.getCoordinate().getY() == this.playerPositionY) {
				setPath(nodeCurrent);
				break;
			}

			// If the open list is empty, break the loop.
			if (this.open.isEmpty()) {
				break;
			}
		}
	}

	/**
	 * Sets the path to the player.
	 * 
	 * @param nodeCurrent
	 *            the starting node of the path.
	 * 
	 */
	public void setPath(Node nodeCurrent) {
		// If pauseStatus is true, do not setPath.
		if (this.pauseStatus) {
			return;
		}

		// Set the current Node to the specified Node.
		Node current = nodeCurrent;

		// Clear the path array list.
		this.path.clear();

		// Loop through and add coordinates to the path.
		while (true) {
			if (current.getNext() == null) {
				break;
			}

			this.path.add(current.getCoordinate());
			current = current.getNext();
		}

		// Add current position to the path.
		this.path.add(new Coordinate(this.tilePositionX, this.tilePositionY));
	}

	/**
	 * Moves the player to a position on the set Path.
	 * 
	 */
	public void move() {
		// Constants.
		final int THRESHOLD = 15;

		// Do not move if certain conditions are met.
		if (this.path.size() == 0 || this.pauseStatus
				|| this.delayCounter <= THRESHOLD) {
			return;
		}

		// Reset the delayCounter.
		this.delayCounter = 0;

		// Set positionInPath to be -1 at first.
		int positionInPath = -1;

		// Find the appropriate positonInPath of the Enemy.
		for (int i = this.path.size() - 1; i >= 0; i--) {
			if (this.path.get(i).getX() == this.tilePositionX
					&& this.path.get(i).getY() == this.tilePositionY) {
				positionInPath = i;
				break;
			}
			this.path.remove(i);
		}

		// If it cannot find its position or its position is 0 (at the end of
		// the path), then return.
		if (positionInPath <= 0) {
			return;
		}
		
		// Last position is the closest coordinate to the player.
		Coordinate moveTo = this.path.get(positionInPath - 1);

		// Store the old position of the Enemy.
		int oldX = this.tilePositionX;
		int oldY = this.tilePositionY;

		// Alter the Enemy's tile position.
		this.tilePositionX = moveTo.getX();
		this.tilePositionY = moveTo.getY();

		// Update the collisionBox.
		this.collisionBox.setRect(this.tilePositionX * this.SCALE,
				this.tilePositionY * this.SCALE, this.SCALE, this.SCALE);

		// Check collision.
		collideWithOthers(this.map);
		collideWithGold(oldX, oldY);
	}

	/**
	 * Sets the Enemy's level data to the specified level.
	 * 
	 * @param level
	 *            the specified level to change to.
	 */
	public void setLevel(Level level) {
		this.level = level;
		this.path.clear();

		// If the level is null, reset the Enemy's position, clear both open and
		// closed array list, and set the map to null.
		if (this.level == null) {
			resetPosition();
			this.open.clear();
			this.close.clear();
			this.map = null;
			return;
		}

		// Calculate the Heuristic of the map.
		calculateHeuristic();
	}

	/**
	 * Sets the pauseStatus to the opposite boolean value it is currently.
	 * 
	 */
	public void setPause() {
		this.pauseStatus = !this.pauseStatus;
	}

	/**
	 * Gets the pauseStatus.
	 * 
	 * @return the pauseStatus.
	 */
	public boolean getPause() {
		return this.pauseStatus;
	}

	/**
	 * Calculates path by running core calculation methods.
	 * 
	 */
	public void run() {
		if (this.level == null || this.pauseStatus) {
			return;
		}

		Node nodeCurrent = this.nodeMap[this.tilePositionX][this.tilePositionY];
		calculateShortest(nodeCurrent);
	}

	/**
	 * Resets the position of the Enemy to its starting position. Clears path,
	 * open, and close array list.
	 * 
	 */
	public void resetPosition() {
		// Resets tile position.
		this.tilePositionX = this.STARTING_X;
		this.tilePositionY = this.STARTING_Y;

		// Reset collisionBox.
		this.collisionBox.setRect(this.tilePositionX * this.SCALE,
				this.tilePositionY * this.SCALE, this.SCALE, this.SCALE);

		// Clear array lists.
		this.path.clear();
		this.open.clear();
		this.close.clear();
	}

	/**
	 * Returns a boolean indicating if the Enemy has collided with the player.
	 * 
	 * @return boolean value indicating collision with player.
	 */
	public boolean collideWithPlayer() {
		if (this.collisionBox.intersects(this.player.getCollisionBox())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the level data of the enemy.
	 * 
	 * @return the level data.
	 */
	public Level getLevel() {
		return this.level;
	}

	/**
	 * Increments the delayCounter by 1.
	 * 
	 */
	public void incrementDelayCounter() {
		this.delayCounter++;
	}

	/**
	 * Returns a Coordinate of the enemy's X and Y tile position.
	 * 
	 * @return Coordinate of the enemy's tile position.
	 */
	public Coordinate getTileCoordinate() {
		return new Coordinate(this.tilePositionX, this.tilePositionY);
	}

	/**
	 * Kills the player and adds 250 points to the player's score.
	 * 
	 */
	public void die() {
		// Constants.
		final int enemyPoint = 250;
		
		// Plays Enemy death sound.
		this.enemyDeath.play();
		
		// Reset position.
		this.resetPosition();

		// Add score to player and check if bonus can be given.
		this.player.addScore(enemyPoint);
		this.player.checkScore();

		// Set wait time.
		this.waitTime = 20;
	}

	/**
	 * Returns the enemy's collisionBox.
	 * 
	 * @return the enemy's collisionBox.
	 */
	public Rectangle2D.Double getCollisionBox() {
		return this.collisionBox;
	}

	/**
	 * Checks if the Enemy is colliding with non-barrier blocks. If it can dig
	 * through, the Enemy will move through.
	 * 
	 * @param map
	 *            the level tileID map.
	 */
	public void collideWithOthers(int[][] map) {
		// Constants.
		final int TREASURE = this.level.TREASURE;
		final int CLEAR = this.level.CLEAR;

		// Get the regularCollisionBoxes from the level.
		ArrayList<Rectangle2D.Double> regularCollisionBoxes = this.level
				.getRegularCollisionBoxes();

		// Check if the Enemy collides with Treasure tiles.
		for (int i = 0; i < regularCollisionBoxes.size(); i++) {
			if (this.collisionBox.intersects(regularCollisionBoxes.get(i))) {
				int column = (int) regularCollisionBoxes.get(i).getX()
						/ this.SCALE;
				int row = (int) regularCollisionBoxes.get(i).getY()
						/ this.SCALE;
				if (map[column][row] == TREASURE) {
					this.level.updateTile(column, row, CLEAR);
				}
			}
		}
	}

	/**
	 * Checks if the Enemy collides with a Gold object.
	 * 
	 * @param oldX
	 *            the old x tile position of the Enemy.
	 * @param oldY
	 *            the old y tile position of the Enemy.
	 */
	public void collideWithGold(int oldX, int oldY) {
		// Initialize variable.
		String moveDirection = null;

		// Get the goldObjects array list from level.
		ArrayList<Gold> goldObjects = this.level.getGoldObjects();

		// Calculate horizontal displacement and set moveDirection accordingly.
		if (oldX - this.tilePositionX == 1) {
			moveDirection = "left";
		} else {
			moveDirection = "right";
		}

		// Loop through the goldObjects array list and see if the Enemy is
		// colliding with one. If so, move it in the direction of moveDirection.
		for (int i = 0; i < goldObjects.size(); i++) {
			if (this.collisionBox.intersects(goldObjects.get(i)
					.getCollisionBox())) {
				goldObjects.get(i).move(moveDirection);
			}
		}

	}

	/**
	 * Checks to see if the gold box and enemy are at the same spot.
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
}
