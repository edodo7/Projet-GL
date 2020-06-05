package be.ac.umons.model.hexagon;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Objects;

import be.ac.umons.game.Extension;
import be.ac.umons.model.Laser;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;

/**
 * Hexagon that only lets certain colors of the lasers traverse it
 */
public class Filter extends Modifier {

	private static final EnumMap<Color, Integer> tileMap;

	static {
		tileMap = new EnumMap<>(Color.class);
		tileMap.put(Color.RED, 17);
		tileMap.put(Color.GREEN, 18);
		tileMap.put(Color.BLUE, 19);
	}

	private EnumMap<Color, Boolean> filterColors;

	public Filter(Color[] filterColors) {
		super();
		init(filterColors);
	}

	public Filter(Color[] filterColors, Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
		init(filterColors);
	}

	public Filter(Color[] filterColors, Edge rotation) {
		super(rotation);
		init(filterColors);
	}

	private void init(Color[] filterColors) {
		this.filterColors = new EnumMap<>(Color.class);
		for (Color color : Color.values()) {
			this.filterColors.put(color, false);
		}
		for (Color color : filterColors) {
			this.filterColors.put(color, true);
		}
	}

	@Override
	protected void modifyLaser(Laser laser) {
		laser.reduceIntensity();
		for (Entry<Color, Boolean> entry : laser.getColors().entrySet()) {
			if (!filterColors.get(entry.getKey())) {
				laser.setColor(entry.getKey(), false);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Filter)) {
			return false;
		}
		Filter other = (Filter) o;
		return super.equals(other) && Objects.equals(filterColors, other.filterColors);
	}

	@Override
	public Filter clone() throws CloneNotSupportedException {
		Filter copy = (Filter) super.clone();
		copy.filterColors = filterColors.clone();
		return copy;
	}

	@Override
	protected int[] getTileIds() {
		ArrayList<Integer> tileIds = new ArrayList<>();
		for (Entry<Color, Boolean> entry : filterColors.entrySet()) {
			if (entry.getValue()) {
				tileIds.add(tileMap.get(entry.getKey()));
			}
		}
		return tileIds.stream().mapToInt(Integer::intValue).toArray();
	}

	public boolean hasColor(Color color) {
		return filterColors.get(color);
	}

	@Override
	public Extension getExtension() {
		return Extension.COLOR;
	}

	public EnumMap<Color, Boolean> getFilterColors() {
		return filterColors;
	}
}