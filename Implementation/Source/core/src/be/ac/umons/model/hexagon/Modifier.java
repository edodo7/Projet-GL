package be.ac.umons.model.hexagon;


import java.util.ArrayList;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

public abstract class Modifier extends Hexagon {

	private final static int gateTileId = 6;
	private static final int distinctRotationNumber = 3;

	public Modifier() {
		super();
	}

	public Modifier(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
	}

	public Modifier(Edge rotation) {
		super(rotation);
	}

	@Override
	protected final Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		if (edge == Edge.NORTH || edge == Edge.SOUTH)
			return new Edge[]{edge.getOpp()};
		return new Edge[]{};
	}

	@Override
	protected final Laser getNextLaserWithoutRot(Edge edge, Laser laser) {
		Laser nextLaser = laser.clone();
		if (edge != Edge.NORTH && edge != Edge.SOUTH && edge != null)
			return nextLaser;
		modifyLaser(nextLaser);
		return nextLaser;
	}

	/**
	 * Modify the given laser
	 * @param laser     the laser
	 */
	abstract protected void modifyLaser(Laser laser);

	@Override
	protected final Tile[] getTilesWithoutRotation() {
		ArrayList<Tile> tiles = new ArrayList<>();
		tiles.add(new Tile(Edge.NORTH, gateTileId));
		for (int tileId : getTileIds()) {
			tiles.add(new Tile(Edge.NORTH, tileId));
			tiles.add(new Tile(Edge.SOUTH, tileId));
		}
		Tile[] tilesArray = new Tile[tiles.size()];
		return tiles.toArray(tilesArray);
	}

	/**
	 * Get the tiles of the modifier when it is placed as a modifier
	 * @return  the tiles of the modifier
	 */
	public final Tile[] getModifierTiles() {
		ArrayList<Tile> tiles = new ArrayList<>();
		for (Edge edge : Edge.values()) {
			for (int tileId : getTileIds()) {
				tiles.add(new Tile(edge, tileId));
			}
		}
		Tile[] tilesArray = new Tile[tiles.size()];
		return tiles.toArray(tilesArray);
	}

	@Override
	public int getDistinctRotationsNumber() {
		return distinctRotationNumber;
	}

	@Override
	public Modifier clone() throws CloneNotSupportedException {
		return (Modifier) super.clone();
	}

	/**
	 * Get an array of tiles representing the id of the tiles on one side of the modifier when it is placed as a modifier
	 * @return  an array of tiles representing the id of the tiles on one side of the modifier when it is placed as a modifier
	 */
	abstract protected int[] getTileIds();
}
