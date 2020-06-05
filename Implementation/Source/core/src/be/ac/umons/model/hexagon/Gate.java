package be.ac.umons.model.hexagon;


import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that lets the laser pass only by two opposite edges.
 * It behaves like a {@link GenericBridge} with one bridge.
 */
public class Gate extends Hexagon {

	private static final int tileId = 6;
	private static final int distinctRotationsNumber = 3;

	public Gate(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public Gate(Edge rotation) {
		super(rotation);
	}

	public Gate() {
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		if (edge == Edge.NORTH || edge == Edge.SOUTH)
			return new Edge[]{edge.getOpp()};
		return new Edge[]{};
	}

	@Override
	public int getDistinctRotationsNumber() {
		return distinctRotationsNumber;
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		return new Tile[]{new Tile(Edge.NORTH, tileId)};
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Gate)) {
			return false;
		}
		Gate other = (Gate) o;
		return super.equals(other);
	}

	@Override
	public Gate clone() throws CloneNotSupportedException {
		return (Gate) super.clone();
	}
}
