package objects;

import toolObjects.Coordinate;

/**
 * A Nobbin, an enemy that follows path already dugged out to the player.
 * 
 * @author Mark Hays and his students. Created Feb 8, 2015.
 */
public class Nobbin extends Enemy {

	/**
	 * Constructs a Nobbin with a specified level and target player.
	 * 
	 * @param level
	 *            the specified level.
	 * @param player
	 *            the target player.
	 */
	public Nobbin(Level level, Digger player) {
		super(level, player);
	}

	/**
	 * Constructs an "evolved" Nobbin (Hobbin -> Nobbin).
	 * 
	 * @param level
	 *            the level data of the Nobbin.
	 * @param player
	 *            the target player of the Nobbin.
	 * @param tileCoordinate
	 *            the Coordinate when it was a Nobbin.
	 */
	public Nobbin(Level level, Digger player, Coordinate tileCoordinate) {
		super(level, player);
		calculateHeuristic();
		this.tilePositionX = tileCoordinate.getX();
		this.tilePositionY = tileCoordinate.getY();
	}
}
