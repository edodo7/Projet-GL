package be.ac.umons.model.hexagon;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that contains a single flat mirror with the two sides of the mirror reflecting the lasers
 */
public class TwoWayMirror extends Mirror {

	private static final int tileId = 8;
	private static final int distinctRotationsNumber = 3;

	public TwoWayMirror(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public TwoWayMirror(Edge rotation) {
		super(rotation);
	}

	public TwoWayMirror() {
		super();
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		return new Edge[]{getFlatMirrorReflection(edge)};
	}

	@Override
	protected boolean containsLaserWithoutRot(Edge edge, Laser laser) {
		Edge rotationSide = isOnRotationSide(edge) ? Edge.NORTH : Edge.SOUTH; // contains the side parallel to the mirror and in the part of edge
		for (Edge e : new Edge[]{rotationSide.getPrev(), rotationSide, rotationSide.getNext()}) {
			if (visited.get(e))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TwoWayMirror)) {
			return false;
		}
		TwoWayMirror other = (TwoWayMirror) o;
		return super.equals(other);
	}

	@Override
	public TwoWayMirror clone() throws CloneNotSupportedException {
		return (TwoWayMirror) super.clone();
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