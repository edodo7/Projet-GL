package be.ac.umons.model.hexagon;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that the lasers have to reach for completing the level
 */
public class Target extends Hexagon {

	private static final int tileId = 5;

	public Target() {
		super();
	}

	public Target(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public Target(Edge rotation) {
		super(rotation);
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		return new Edge[]{};
	}

	@Override
	protected Laser getNextLaserWithoutRot(Edge edge, Laser laser) {
		Laser nextLaser = laser.clone();
		nextLaser.reduceIntensity();
		if (edge == Edge.NORTH)
			nextLaser.setReachedTarget(true);
		return nextLaser;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Target)) {
			return false;
		}
		Target other = (Target) o;
		return super.equals(other);
	}

	@Override
	public Target clone() throws CloneNotSupportedException {
		return (Target) super.clone();
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		return new Tile[]{new Tile(Edge.NORTH, tileId)};
	}
}