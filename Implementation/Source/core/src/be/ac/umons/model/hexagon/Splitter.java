package be.ac.umons.model.hexagon;

import java.util.HashMap;
import java.util.Map;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that divides the laser in a number of new lasers with reduced intensity
 */
public class Splitter extends Hexagon {

	private static final Map<Integer, Integer> tileMap;

	static {
		tileMap = new HashMap<>();
		tileMap.put(2, 10);
		tileMap.put(3, 11);
		tileMap.put(4, 12);
		tileMap.put(5, 13);
	}

	private int way;

	public Splitter(int way) {
		super();
		init(way);
	}

	public Splitter(int way, Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
		init(way);
	}

	public Splitter(int way, Edge rotation) {
		super(rotation);
		init(way);
	}

	private void init(int way) {
		if (way <= 1 || way >= Edge.size())
			throw new IllegalArgumentException();
		this.way = way;
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		if (edge == Edge.NORTH) {
			Edge[] edges = new Edge[way];
			if (way % 2 == 1) // Add the opposite edge if the number of division is odd
				edges[way - 1] = edge.getOpp();
			if (way >= 2) {
				edges[0] = edge.getOpp().getNext();
				edges[1] = edge.getOpp().getPrev();
			}
			if (way >= 4) {
				edges[2] = edge.getNext();
				edges[3] = edge.getPrev();
			}
			return edges;
		}
		return new Edge[]{};
	}

	@Override
	protected Laser getNextLaserWithoutRot(Edge edge, Laser laser) {
		Laser nextLaser = laser.clone();
		nextLaser.setIntensity((float) (laser.getIntensity() / Math.sqrt((double) way)));
		return nextLaser;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Splitter)) {
			return false;
		}
		Splitter other = (Splitter) o;
		return super.equals(other) && way == other.way;
	}

	@Override
	public Splitter clone() throws CloneNotSupportedException {
		Splitter copy = (Splitter) super.clone();
		copy.way = way;
		return copy;
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		return new Tile[]{new Tile(Edge.NORTH, tileMap.get(way))};
	}

	public int getWay() {
		return way;
	}
}