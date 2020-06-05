package be.ac.umons.util;

/**
 * Couple of an edge and an id corresponding to a tile
 */
public class Tile {
	private Edge edge;
	private int id;

	/**
	 * Constructor
	 * @param edge			Edge
	 * @param id			Tile id
	 */
	public Tile(Edge edge, int id) {
		this.edge = edge;
		this.id = id;
	}

	/**
	 * Returns the edge
	 * @return				The Edge
	 */
	public Edge getEdge() {
		return edge;
	}

	/**
	 * Updates the edge
	 * @param edge			New Edge
	 */
	public void setEdge(Edge edge) {
		this.edge = edge;
	}

	/**
	 * Returns the tile id
	 * @return				The tile id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Updates the tile id
	 * @param id			New tile id
	 */
	public void setId(int id) {
		this.id = id;
	}
}
