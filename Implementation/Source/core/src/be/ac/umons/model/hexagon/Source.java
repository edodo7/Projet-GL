package be.ac.umons.model.hexagon;

import java.util.Objects;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon emitting the lasers
 * It contains its starting laser
 */
public class Source extends Hexagon {

	private static final int tileId = 4;

	private Laser laser;

	public Source(Laser laser) {
		super();
		init();
		setLaser(laser);
	}

	public Source(Laser laser, Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
		init();
		setLaser(laser);
	}

	public Source(Laser laser, Edge rotation) {
		super(rotation);
		init();
		setLaser(laser);
	}

	private void init() {
		canHaveModifier = false;
	}

	public Laser getLaser() {
		return laser;
	}

	public void setLaser(Laser laser) {
		this.laser = laser;
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		if (edge == null)
			return new Edge[]{Edge.NORTH};
		return new Edge[]{};
	}

	@Override
	protected Laser getNextLaserWithoutRot(Edge edge, Laser laser) {
		return laser.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Source)) {
			return false;
		}
		Source other = (Source) o;
		return super.equals(other) && Objects.equals(laser, other.laser);
	}

	@Override
	public Source clone() throws CloneNotSupportedException {
		Source copy = (Source) super.clone();
		copy.laser = laser.clone();
		return copy;
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		return new Tile[]{new Tile(Edge.NORTH, tileId)};
	}
}