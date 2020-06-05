package be.ac.umons.model.hexagon;

import be.ac.umons.util.Edge;

/**
 * Base class of {@link OneWayMirror}, {@link TwoWayMirror} and {@link ThreeWayMirror}
 */
public abstract class Mirror extends Hexagon {

	public Mirror() {
		super();
	}

	public Mirror(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public Mirror(Edge rotation) {
		super(rotation);
	}

	/**
	 * Get whether the edge is on the rotation side of the hexagon
	 *
	 * @param edge  the edge
	 * @return      whether the edge is on the rotation side of the hexagon
	 */
	protected boolean isOnRotationSide(Edge edge) {
		return edge.toInt() <= 1 || edge.toInt() >= 5;
	}

	/**
	 * Get the edge after it has been reflecting in a flat horizontal mirror
	 *
	 * @param edge  the edge
	 * @return      the edge after it has been reflecting in a flat horizontal mirror
	 */
	protected Edge getFlatMirrorReflection(Edge edge) {
		return Edge.fromInt((-edge.toInt() + Edge.size()) % Edge.size());
	}
}