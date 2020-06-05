package be.ac.umons.model.hexagon;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that contains a triangular mirror reflecting the lasers by 60
 */
public class ThreeWayMirror extends Mirror {

	private static final int tileId = 9;
	private static final int distinctRotationsNumber = 2;

	public ThreeWayMirror(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public ThreeWayMirror(Edge rotation) {
		super(rotation);
	}

	public ThreeWayMirror() {
		super();
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		return new Edge[] {(edge.toInt() % 2 == 0) ? edge.getPrev() : edge.getNext()};
	}

	@Override
	protected boolean containsLaserWithoutRot(Edge edge, Laser laser) {
		return visited.get(edge) || visited.get((edge.toInt() % 2 == 0) ? edge.getPrev() : edge.getNext());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ThreeWayMirror)) {
			return false;
		}
		ThreeWayMirror other = (ThreeWayMirror) o;
		return super.equals(other);
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		return new Tile[]{new Tile(Edge.NORTH, tileId)};
	}

	@Override
	public ThreeWayMirror clone() throws CloneNotSupportedException {
		return (ThreeWayMirror) super.clone();
	}

	@Override
	public int getDistinctRotationsNumber() {
		return distinctRotationsNumber;
	}
}