package be.ac.umons.util;

/**
 * A Couple of Pos and edge
 */
public class PosEdge {
	private Pos pos;
	private Edge edge;

	/**
	 * Constructor
	 * @param pos				The position
	 * @param edge				The edge
	 */
	public PosEdge(Pos pos, Edge edge) {
		this.pos = pos;
		this.edge = edge;
	}

	/**
	 * Returns the position
	 * @return					The position
	 */
	public Pos getPos() {
		return pos;
	}

	/**
	 * Updates the position
	 * @param pos				New position
	 */
	public void setPos(Pos pos) {
		this.pos = pos;
	}

	/**
	 * Returns the edge
	 * @return					The edge
	 */
	public Edge getEdge() {
		return edge;
	}

	/**
	 * Updates the edge
	 * @param edge				New edge
	 */
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
}