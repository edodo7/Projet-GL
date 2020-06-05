package be.ac.umons.model;

import be.ac.umons.util.Color;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A laser is a represented by colors, an intensity and whether it has reached a target
 */
public class Laser implements Cloneable,Serializable {

	private static final float reduction = 0.1f;
	private EnumMap<Color, Boolean> colors;
	private float intensity;
	private boolean reachedTarget = false;
	private boolean enableIntensity;
	private boolean enableColor;

	/**
	 * Constructor
	 * @param enableIntensity				True if intensity should be taken into consideration
	 * @param enableColor					True if color should be taken into consideration
	 */
	public Laser(boolean enableIntensity, boolean enableColor) {
		this(Color.values(), 1.0f, enableIntensity, enableColor);
	}

	/**
	 * Constructor
	 * @param activeColors					Array of active colors
	 * @param intensity						Intensity of the laser
	 * @param enableIntensity				True if intensity should be taken into consideration
	 * @param enableColor					True if color should be taken into consideration
	 */
	public Laser(Color[] activeColors, float intensity, boolean enableIntensity, boolean enableColor) {
		this.enableIntensity = enableIntensity;
		this.enableColor = enableColor;
		initColors();
		setColors(activeColors);
		this.intensity = intensity;
	}

	/**
	 * Creates an empty laser
	 * @return								An empty laser
	 */
	public static Laser emptyLaser() {
		return new Laser(new Color[]{}, 0.0f, true, true);
	}

	public static float getReduction() {
		return reduction;
	}

	/**
	 * Initialize the colors of the laser as false
	 */
	private void initColors() {
		this.colors = new EnumMap<>(Color.class);
		for (Color color : Color.values())
			this.colors.put(color, false);
	}

	/**
	 * Set the color of the laser to active or inactive
	 *
	 * @param color  the color to change
	 * @param active whether the color must be active
	 */
	public void setColor(Color color, Boolean active) {
		if (enableColor)
			colors.put(color, active);
	}

	/**
	 * Get whether a color is active in the laser
	 *
	 * @param color the color to check
	 * @return whether the color is active
	 */
	public Boolean hasColor(Color color) {
		return colors.get(color);
	}

	/**
	 * Get an EnumMap containing the colors of the laser
	 *
	 * @return an EnumMap containing the colors of the laser
	 */
	public EnumMap<Color, Boolean> getColors() {
		return colors;
	}

	/**
	 * Set the colors in an array of colors to active
	 *
	 * @param colors the colors to set active
	 */
	private void setColors(Color[] colors) {
		for (Color color : colors)
			this.colors.put(color, true);
	}

	/**
	 * Get the intensity of the laser
	 *
	 * @return the intensity of the laser
	 */
	public float getIntensity() {
		return this.intensity;
	}

	/**
	 * Set the intensity of the laser
	 *
	 * @param intensity the intensity to set
	 */
	public void setIntensity(float intensity) {
		if (enableIntensity)
			this.intensity = Math.max(0, Math.min(intensity, 1));
	}

	/**
	 * Reduce the intensity of the laser
	 */
	public void reduceIntensity() {
		setIntensity(getIntensity() - reduction);
	}

	public void setReachedTarget(boolean reached) {
		this.reachedTarget = reached;
	}

	public boolean hasReachedTarget() {
		return reachedTarget;
	}

	/**
	 * The laser disappears if it has not enough intensity or no color leftBar
	 *
	 * @return whether the laser has disappeared
	 */
	public boolean hasDisappeared() {
		boolean noColor = true;
		for (Entry<Color, Boolean> entry : colors.entrySet()) {
			if (entry.getValue())
				noColor = false;
		}
		return noColor || intensity <= 0.1f + 1e-6;
	}

	@Override
	public Laser clone() {
		Laser copy;
		try {
			copy = (Laser) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException();
		}
		copy.colors = colors.clone();
		copy.intensity = intensity;
		copy.reachedTarget = reachedTarget;
		copy.enableIntensity = enableIntensity;
		copy.enableColor = enableColor;
		return copy;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Laser)) {
			return false;
		}
		Laser l = (Laser) o;
		return Objects.equals(colors, l.colors) && Math.abs(intensity - l.intensity) < 1e-6 && reachedTarget == l.reachedTarget;
	}

	/**
	 * Returns true if the laser is white
	 * @return						True if the laser is white
	 */
	public boolean isWhite(){
		return colors.get(Color.RED) && colors.get(Color.GREEN) && colors.get(Color.BLUE);
	}

	/**
	 * Returns true if the checked color is the only one active
	 * @param _color				Color to check
	 * @return						True if the checked color is the only one active
	 */
	public boolean isOnlyColor(Color _color){
		for(Color color : colors.keySet()){
			if(colors.get(color) && color!=_color){
				return false;
			}
		}
		return true;
	}
}