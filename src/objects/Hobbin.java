package objects;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import toolObjects.Coordinate;
import toolObjects.Node;

/**
 * A Hobbin, an enemy that can dig its own tunnels to the player object.
 * 
 * @author Mark Hays and his students. Created Feb 10, 2015.
 */
public class Hobbin extends Enemy {
	/**
	 * Constructor of a Hobbin
	 * 
	 * @param level
	 *            the level data of the Hobbin.
	 * @param player
	 *            the target player of the Hobbin.
	 */
	public Hobbin(Level level, Digger player) {
		super(level, player);
		try {
			this.sprite = ImageIO.read(getClass().getResourceAsStream("/enemy/hobbin.png"));
		} catch (IOException e) {
			System.out.println("Error: File not found.");
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Constructs an "evolved" Hobbin (Nobbin -> Hobbin).
	 * 
	 * @param level
	 *            the level data of the Hobbin.
	 * @param player
	 *            the target player of the Hobbin.
	 * @param tileCoordinate
	 *            the Coordinate when it was a Nobbin.
	 */
	public Hobbin(Level level, Digger player, Coordinate tileCoordinate) {
		super(level, player);
		try {
			this.sprite = ImageIO.read(getClass().getResourceAsStream("/enemy/hobbin.png"));
		} catch (IOException e) {
			System.out.println("Error: File not found.");
			e.printStackTrace();
			System.exit(0);
		}
		calculateHeuristic();
		this.tilePositionX = tileCoordinate.getX();
		this.tilePositionY = tileCoordinate.getY();
	}

	@Override
	protected void canMoveTo(Node nodeCurrent, int mapPositionID,
			Node nodePosition) {
		// Constants.
		final ArrayList<Integer> BARRIERS = this.level.getBarriersIDList();
		final int GOLD = this.level.GOLD;

		if (!BARRIERS.contains(mapPositionID) && mapPositionID != GOLD
				&& !this.close.contains(nodePosition)) {
			if (nodePosition.getNext() == null) {
				nodePosition.setNext(nodeCurrent);
				this.open.add(nodePosition);
			}
		}
	}

	@Override
	public void move() {
		super.move();
	}

	@Override
	public void collideWithOthers(int[][] map) {
		super.collideWithOthers(map);

		// Constants.
		final int CLEAR = this.level.CLEAR;
		final int EMERALD = this.level.EMERALD;
		final int DIRT = this.level.DIRT;
		final int TREASURE = this.level.TREASURE;

		// Gets the level's regularCollisionBoxes.
		ArrayList<Rectangle2D.Double> regularCollisionBoxes = this.level
				.getRegularCollisionBoxes();

		// Loop through and update the blocks to clear as the Hobbin moves
		// through the level.
		for (int i = 0; i < regularCollisionBoxes.size(); i++) {
			if (this.collisionBox.intersects(regularCollisionBoxes.get(i))) {
				int column = (int) regularCollisionBoxes.get(i).getX() / this.SCALE;
				int row = (int) regularCollisionBoxes.get(i).getY() / this.SCALE;
				if (map[column][row] == DIRT) {
					this.level.updateTile(column, row, CLEAR);
				}
				if (map[column][row] == EMERALD) {
					this.level.updateTile(column, row, CLEAR);
				}
				if (map[column][row] == TREASURE) {
					this.level.updateTile(column, row, CLEAR);
				}
			}
		}
		
		// Generate a new level of regularCollisionBoxes.
		this.level.generateRegularCollisionBoxes();
	}
}
