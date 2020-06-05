package be.ac.umons.model.hexagon;

import java.util.EnumMap;

import be.ac.umons.game.Extension;
import be.ac.umons.model.Laser;
import be.ac.umons.util.Color;
import be.ac.umons.util.Edge;

/**
 * Hexagon that converts the color of an incoming laser
 */
public class Converter extends Modifier {

	private static final EnumMap<Color, Integer> tileMap;

	static {
		tileMap = new EnumMap<>(Color.class);
		tileMap.put(Color.RED, 14);
		tileMap.put(Color.GREEN, 15);
		tileMap.put(Color.BLUE, 16);
	}

	private Color convertColor;

	public Converter(Color convertColor) {
		super();
		init(convertColor);
	}

	public Converter(Color convertColor, Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		super(rotation, rotatable, movable, modifier);
		init(convertColor);
	}

	public Converter(Color convertColor, Edge rotation) {
		super(rotation);
		init(convertColor);
	}

	private void init(Color convertColor) {
		this.convertColor = convertColor;
	}

	@Override
	protected void modifyLaser(Laser laser) {
		laser.reduceIntensity();
		for (Color color : Color.values()) {
			laser.setColor(color, false);
		}
		laser.setColor(convertColor, true);
	}


	@Override
	protected int[] getTileIds() {
		return new int[]{tileMap.get(convertColor)};
	}

	public Color getConvertColor() {
		return convertColor;
	}

	public void setConvertColor(Color convertColor) {
		this.convertColor = convertColor;
	}

	@Override
	public Extension getExtension() {
		return Extension.COLOR;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Converter)) {
			return false;
		}
		Converter hex = (Converter) o;
		return super.equals(hex) && convertColor == hex.convertColor;
	}

	@Override
	public Converter clone() throws CloneNotSupportedException {
		Converter copy = (Converter) super.clone();
		copy.convertColor = convertColor;
		return copy;
	}
}