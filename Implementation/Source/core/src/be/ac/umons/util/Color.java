package be.ac.umons.util;

/**
 * An enumeration of colors
 */
public enum Color {
	RED(com.badlogic.gdx.graphics.Color.RED),
	GREEN(com.badlogic.gdx.graphics.Color.GREEN),
	BLUE(com.badlogic.gdx.graphics.Color.BLUE);

	private com.badlogic.gdx.graphics.Color libGDXColor;

	Color(com.badlogic.gdx.graphics.Color libGDXColor) {
		this.libGDXColor = libGDXColor;
	}

	/**
	 * Get the number of possible colors
	 *
	 * @return 				The number of possible colors
	 */
	public static int size() {
		return Color.values().length;
	}

	/**
	 * Returns the corresponding LibGDX Color
	 * @return				The corresponding LibGDX Color
	 */
	public com.badlogic.gdx.graphics.Color getLibGDXColor() {
		return libGDXColor;
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}