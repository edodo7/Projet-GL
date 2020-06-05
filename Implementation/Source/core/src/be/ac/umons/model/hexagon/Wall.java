package be.ac.umons.model.hexagon;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that stops the lasers
 */
public class Wall extends Hexagon {

	private static final int tileId = 3;
	private static final int distinctRotationsNumber = 1;

	public Wall() {
		super();
	}

	public Wall(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public Wall(Edge rotation) {
		super(rotation);
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		return new Edge[]{};
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Wall)) {
			return false;
		}
		Wall other = (Wall) o;
		return super.equals(other);
	}

	@Override
	public Wall clone() throws CloneNotSupportedException {
		return (Wall) super.clone();
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		return new Tile[]{new Tile(Edge.NORTH, tileId)};
	}

	@Override
	public int getDistinctRotationsNumber() {
		return distinctRotationsNumber;
	}
}