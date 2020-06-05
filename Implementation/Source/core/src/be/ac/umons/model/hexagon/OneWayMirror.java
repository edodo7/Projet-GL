package be.ac.umons.model.hexagon;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that contains a single flat mirror with one side of the mirror reflecting the lasers
 */
public class OneWayMirror extends Mirror {

	private static final int tileId = 7;

	public OneWayMirror(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public OneWayMirror(Edge rotation) {
		super(rotation);
	}

	public OneWayMirror() {
		super();
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		if (isOnRotationSide(edge))
			return new Edge[]{getFlatMirrorReflection(edge)};
		return new Edge[]{};
	}

	@Override
	protected boolean containsLaserWithoutRot(Edge edge, Laser laser) {
		if (!isOnRotationSide(edge))
			return false;
		for (Edge e : new Edge[]{Edge.NORTHWEST, Edge.NORTH, Edge.NORTHEAST}) {
			if (visited.get(e))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof OneWayMirror)) {
			return false;
		}
		OneWayMirror other = (OneWayMirror) o;
		return super.equals(other);
	}

	@Override
	public OneWayMirror clone() throws CloneNotSupportedException {
		return (OneWayMirror) super.clone();
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		return new Tile[]{new Tile(Edge.NORTH, tileId)};
	}
}