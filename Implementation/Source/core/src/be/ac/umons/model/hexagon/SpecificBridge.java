package be.ac.umons.model.hexagon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import be.ac.umons.model.Laser;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

/**
 * Hexagon that contains two or three bridges which let pass lasers of a specific color
 */
public class SpecificBridge extends Bridge {

	private static final int distinctRotationsNumber = 3;
	private static final int gateTileId = 6;
	private static final EnumMap<Color, Integer> colorTileMap;

	static {
		colorTileMap = new EnumMap<>(Color.class);
		colorTileMap.put(Color.RED, 17);
		colorTileMap.put(Color.GREEN, 18);
		colorTileMap.put(Color.BLUE, 19);
	}

	private Color[] crossColors; // A null color means that the bridge can't be crossed

	public SpecificBridge(Color[] crossColors) {
		super();
		init(crossColors);
	}

	public SpecificBridge(Color[] crossColors, Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
		init(crossColors);
	}

	public SpecificBridge(Color[] crossColors, Edge rotation) {
		super(rotation);
		init(crossColors);
	}

	private void init(Color[] crossColors) {
		assert crossColors.length == 3;
		int nbOfCrossing = 0;
		for (int i = 0; i < 3; i++) {
			if (crossColors[i] != null)
				nbOfCrossing++;
		}
		assert nbOfCrossing >= 2;
		this.crossColors = crossColors;
	}

	@Override
	protected Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser) {
		int targetEdge = edge.toInt() % (Edge.size() / 2);
		Color targetColor = crossColors[targetEdge];
		if (targetColor == null)
			return new Edge[]{};
		boolean containsOnlyTargetColor = true;
		for (Color color : Color.values()) {
			if (color == targetColor) {
				if (!laser.hasColor(color))
					containsOnlyTargetColor = false;
			} else {
				if (laser.hasColor(color))
					containsOnlyTargetColor = false;
			}
		}
		if (containsOnlyTargetColor)
			return new Edge[]{edge.getOpp()};
		return new Edge[]{};
	}

	@Override
	public Tile[] getTilesWithoutRotation() {
		ArrayList<Tile> tiles = new ArrayList<>();
		for (int i = 0; i < 3; ++i) {
			if (crossColors[i] != null) {
				tiles.add(new Tile(Edge.fromInt(i), gateTileId));
				tiles.add(new Tile(Edge.fromInt(i), colorTileMap.get(crossColors[i])));
				tiles.add(new Tile(Edge.fromInt(i).getOpp(), colorTileMap.get(crossColors[i])));
			}
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
		if (!(o instanceof SpecificBridge)) {
			return false;
		}
		SpecificBridge other = (SpecificBridge) o;
		return super.equals(other) && Arrays.equals(crossColors, other.crossColors);
	}

	@Override
	public SpecificBridge clone() throws CloneNotSupportedException {
		SpecificBridge copy = (SpecificBridge) super.clone();
		copy.crossColors = crossColors.clone();
		return copy;
	}

	public Color[] getCrossColors() {
		return crossColors;
	}

	public void modifyColor(Color color, int index) {
		crossColors[index] = color;
	}
}
