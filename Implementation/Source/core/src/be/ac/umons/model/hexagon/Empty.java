package be.ac.umons.model.hexagon;

import java.util.Map.Entry;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * An empty hexagon that doesn't modify the laser
 */
public class Empty extends Hexagon {

	private static final int tileId = 2;
	private static final int distinctRotationsNumber = 1;

	public Empty() {
		super();
		init();
	}

	public Empty(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
		init();
	}

	public Empty(Edge rotation) {
		super(rotation);
		init();
	}

	private void init() {
		canHaveModifier = false;
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		return new Edge[]{edge.getOpp()};
	}

	@Override
	protected boolean containsLaserWithoutRot(Edge edge, Laser laser) {
		boolean empty = true;
		for (Entry<Edge, Boolean> entry : visited.entrySet()) {
			if (entry.getValue())
				empty = false;
		}
		return !empty;
	}


	@Override
	public int getDistinctRotationsNumber() {
		return distinctRotationsNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Empty)) {
			return false;
		}
		Empty other = (Empty) o;
		return super.equals(other);
	}

	@Override
	public Empty clone() throws CloneNotSupportedException {
		return (Empty) super.clone();
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		return new Tile[]{new Tile(Edge.NORTH, tileId)};
	}


}