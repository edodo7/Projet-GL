package be.ac.umons.util;

/**
 * An enumeration of the edges of an hexagon
 */
public enum Edge {
	NORTH("north"),
	NORTHEAST("northeast"),
	SOUTHEAST("southeast"),
	SOUTH("south"),
	SOUTHWEST("southwest"),
	NORTHWEST("northwest");

	private String name;

	private Edge(String name){
		this.name = name;
	}

	/**
	 * Get the number of different edges
	 *
	 * @return the number of different edges
	 */
	public static int size() {
		return Edge.values().length;
	}

	/**
	 * Get an Edge object from its integer value
	 *
	 * @param n the integer value of the Edge object
	 * @return the new Edge object
	 */
	public static Edge fromInt(int n) {
		if (n < 0 || n >= size())
			throw new IllegalArgumentException();
		return Edge.values()[n];
	}

	public String toSting() {
		switch (toInt()) {
			case 0:
				return "north";
			case 1:
				return "northeast";
			case 2:
				return "southeast";
			case 3:
				return "south";
			case 4:
				return "southwest";
			case 5:
				return "northwest";
		}
		return null;
	}

	/**
	 * Convert the edge to an integer value
	 *
	 * @return an integer value indicating the edge
	 */
	public int toInt() {
		return this.ordinal();
	}

	/**
	 * Get the opposite edge
	 *
	 * @return the opposite edge
	 */
	public Edge getOpp() {
		return Edge.values()[(this.ordinal() + size() / 2) % size()];
	}

	/**
	 * Get the adjacent edge clockwise
	 *
	 * @return the adjacent edge clockwise
	 */
	public Edge getNext() {
		return Edge.values()[(this.ordinal() + 1) % size()];
	}

	/**
	 * Get the adjacent edge counterclockwise
	 *
	 * @return the adjacent edge counterclockwise
	 */
	public Edge getPrev() {
		return Edge.values()[(this.ordinal() + size() - 1) % size()];
	}

	/**
	 * Get the actual edge rotated by the value of another edge clockwise
	 *
	 * @param rot the value by which the actual edge is rotated
	 * @return the rotated edge
	 */
	public Edge rotateClockwise(Edge rot) {
		return Edge.fromInt((this.toInt() + rot.toInt()) % size());
	}

	/**
	 * Get the actual edge rotated by the value of another edge counterclockwise
	 *
	 * @param rot the value by which the actual edge is rotated
	 * @return the rotated edge
	 */
	public Edge rotateCounterClockwise(Edge rot) {
		return Edge.fromInt((this.toInt() - rot.toInt() + size()) % size());
	}

	public String getName(){
		return name;
	}
}