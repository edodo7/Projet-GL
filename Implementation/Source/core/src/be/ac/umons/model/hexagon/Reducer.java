package be.ac.umons.model.hexagon;

import be.ac.umons.game.Extension;
import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;

/**
 * Hexagon that reduces the intensity of an incoming laser
 */
public class Reducer extends Modifier {

	private static final float reduction = 0.5f;
	private static final int tileId = 21;

	public Reducer() {
		super();
	}

	public Reducer(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public Reducer(Edge rotation) {
		super(rotation);
	}

	@Override
	protected void modifyLaser(Laser laser) {
		laser.setIntensity(laser.getIntensity() - reduction);
	}

	@Override
	public Extension getExtension() {
		return Extension.INTENSITY;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Reducer)) {
			return false;
		}
		Reducer other = (Reducer) o;
		return super.equals(other);
	}

	@Override
	public Reducer clone() throws CloneNotSupportedException {
		return (Reducer) super.clone();
	}

	@Override
	protected int[] getTileIds() {
		return new int[]{tileId};
	}
}