package be.ac.umons.model.hexagon;

import be.ac.umons.game.Extension;
import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;

/**
 * Hexagon that amplify the intensity of an incoming laser
 */
public class Amplifier extends Modifier {

	private static final float amplification = 0.5f;
	private static final int tileId = 20;

	public Amplifier() {
		super();
	}

	public Amplifier(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public Amplifier(Edge rotation) {
		super(rotation);
	}

	@Override
	protected int[] getTileIds() {
		return new int[]{tileId};
	}

	@Override
	protected void modifyLaser(Laser laser) {
		laser.setIntensity(laser.getIntensity() + amplification);
	}

	@Override
	public Extension getExtension() {
		return Extension.INTENSITY;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Amplifier)) {
			return false;
		}
		Amplifier other = (Amplifier) o;
		return super.equals(other);
	}

	@Override
	public Amplifier clone() throws CloneNotSupportedException {
		return (Amplifier) super.clone();
	}
}