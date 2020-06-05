package be.ac.umons.model.hexagon;

import java.util.ArrayList;
import java.util.Arrays;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that contains two or three bridges which let pass any laser
 */
public class GenericBridge extends Bridge {

	private static final int tileId = 6;
	private static final int distinctRotationsNumber = 3;
	private boolean[] canPass;

	public GenericBridge(boolean[] canPass) {
		super();
		init(canPass);
	}

	public GenericBridge(boolean[] canPass, Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
		init(canPass);
	}

	public GenericBridge(boolean[] canPass, Edge rotation) {
		super(rotation);
		init(canPass);
	}

	private void init(boolean[] canPass) {
		assert canPass.length == 3;
		int nbOfCrossing = 0;
		for (int i = 0; i < 3; i++) {
			if (canPass[i])
				nbOfCrossing++;
		}
		assert nbOfCrossing >= 2;
		this.canPass = canPass;
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		int targetEdge = edge.toInt() % (Edge.size() / 2);
		if (canPass[targetEdge])
			return new Edge[]{edge.getOpp()};
		return new Edge[]{};
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		ArrayList<Tile> tiles = new ArrayList<>();
		for (int i = 0; i < 3; ++i) {
			if (canPass[i])
				tiles.add(new Tile(Edge.fromInt(i), tileId));
		}
		Tile[] tilesArray = new Tile[tiles.size()];
		return tiles.toArray(tilesArray);
	}

	@Override
	public int getDistinctRotationsNumber() {
		return distinctRotationsNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GenericBridge)) {
			return false;
		}
		GenericBridge other = (GenericBridge) o;
		return super.equals(other) && Arrays.equals(canPass, other.canPass);
	}

	@Override
	public GenericBridge clone() throws CloneNotSupportedException {
		GenericBridge copy = (GenericBridge) super.clone();
		copy.canPass = canPass.clone();
		return copy;
	}

	public boolean[] getCanPass() {
		return canPass;
	}
}
