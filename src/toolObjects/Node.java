package toolObjects;

/**
 * A Node represents characteristics of a particular position on the level.
 *
 * @author Mark Hays and his students.
 *         Created Feb 17, 2015.
 */
public class Node {
	private Coordinate coordinate; // The map coordinate of the Node.
	private int heuristic; // The heuristic (Manhattan Distance) of the Node.
	private Node next; // Node to point towards.
	
	/**
	 * Constructs a Node object.
	 *
	 * @param coordinate The map coordinate of the Node.
	 * @param heuristic The Manhattan Distance from the Player.
	 */
	public Node(Coordinate coordinate, int heuristic) {
		this.coordinate = coordinate;
		this.heuristic = heuristic;
		this.next = null;
	}
	
	/**
	 * Links this Node with a specified next Node.
	 *
	 * @param next the specfied next Node.
	 */
	public void setNext(Node next) {
		this.next = next;
	}
	
	/**
	 * Returns the Node this Node is linked to.
	 *
	 * @return the Node this Node is linked to.
	 */
	public Node getNext() {
		return this.next;
	}
	
	/**
	 * Sets the Coordinate of this Node to the specified coordinate.
	 *
	 * @param coordinate the specified Coordinate.
	 */
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	/**
	 * Returns the Coordinate of this Node.
	 *
	 * @return the Coordinate of this Node.
	 */
	public Coordinate getCoordinate() {
		return this.coordinate;
	}
	
	/**
	 * Sets the Manhattan Distance of this Node to a specific position.
	 *
	 * @param heuristic the Manhattan Distance.
	 */
	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}
	
	/**
	 * Returns the Manhattan Distance of this Node.
	 *
	 * @return the Manhattan Distance of this Node.
	 */
	public int getHeuristic() {
		return this.heuristic;
	}
	
	@Override
	public String toString() {
		int x = this.coordinate.getX();
		int y = this.coordinate.getY();
		String coordinate = "[" + x + "][" + y +"]" ;
		return coordinate;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.coordinate == null) ? 0 : this.coordinate.hashCode());
		result = prime * result + this.heuristic;
		result = prime * result
				+ ((this.next == null) ? 0 : this.next.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object check) {
		if (check == null) return false;
		if (check == this) return true;

		if (!(check instanceof Node)) return false;

		// Check relevant members.
	
		int checkX = ((Node) check).getCoordinate().getX();
		int checkY = ((Node) check).getCoordinate().getY();
		int thisX = this.getCoordinate().getX();
		int thisY = this.getCoordinate().getY();
		if (thisX == checkX && thisY == checkY) { 
			return true;
		}
		
		return false;
	}
}
