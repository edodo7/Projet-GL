package be.ac.umons.model.hexagon;

import be.ac.umons.game.Extension;
import be.ac.umons.util.Edge;

/**
 * Base class of {@link GenericBridge} and {@link SpecificBridge}
 */
public abstract class Bridge extends Hexagon {

	public Bridge() {
		super();
	}

	public Bridge(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public Bridge(Edge rotation) {
		super(rotation);
	}

	@Override
	public Extension getExtension() {
		return Extension.COLOR;
	}
}