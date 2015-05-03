package toolObjects;

/**
 * An object that represents a Coordinate pair in the mathematical sense.
 *
 * @author Mark Hays and his students.
 *         Created Feb 9, 2015.
 */
public class Coordinate {
	private int x; // The X-coordinate.
	private int y; // The Y-coordinate.
	
	/**
	 * Default constructor of a Coordinate.
	 *
	 */
	public Coordinate() {
		this(0, 0);
	}
	
	/**
	 * Primary constructor of a Coordinate
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the X-coordinate of the Coordinate pair.
	 *
	 * @return the X-coordinate.
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Returns the Y-coordinate of the Coordinate pair.
	 *
	 * @return the Y-coordinate.
	 */
	public int getY() {
		return this.y;
	}
	
	@Override
	public String toString() {
		String result = "[" + this.x + "][" + this.y + "]";
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.x;
		result = prime * result + this.y;
		return result;
	}

	@Override
	public boolean equals(Object check) {
		// Main check.
		if (check == null) {
			return false;
		}
		if (check == this) {
			return true;
		}

		if (!(check instanceof Coordinate)) {
			return false;
		}
		
		// Ultimate check.
		int checkX = ((Coordinate) check).getX();
		int checkY = ((Coordinate) check).getY();
		int thisX = this.getX();
		int thisY = this.getY();
		
		if (thisX == checkX && thisY == checkY) {
			return true;
		}
		return false;
	}
}
