package be.ac.umons.util;

import java.util.EnumMap;
import java.util.Objects;

/**
 * A segment contains initial and final positions, initial and final intensities and an array of colors
 */
public class Segment {

	private Pos from;
	private Pos to;
	private float fromIntensity;
	private float toIntensity;
	private EnumMap<Color, Boolean> fromColors;
	private EnumMap<Color, Boolean> toColors;

	public Segment(Pos from, Pos to, float fromIntensity, float toIntensity, EnumMap<Color, Boolean> fromColors, EnumMap<Color, Boolean> toColors) {
		this.from = from;
		this.to = to;
		this.fromIntensity = fromIntensity;
		this.toIntensity = toIntensity;
		this.fromColors = fromColors;
		this.toColors = toColors;
	}

	/**
	 * Get the initial position of the segment
	 *
	 * @return the initial position of the segment
	 */
	public Pos getFrom() {
		return from;
	}

	/**
	 * Set the initial position of the segment
	 *
	 * @param from the initial position of the segment
	 */
	public void setFrom(Pos from) {
		this.from = from;
	}

	/**
	 * Get the final position of the segment
	 *
	 * @return the final position of the segment
	 */
	public Pos getTo() {
		return to;
	}

	/**
	 * Set the final position of the segment
	 *
	 * @param to    the final position of the segment
	 */
	public void setTo(Pos to) {
		this.to = to;
	}

	/**
	 * Get the initial intensity of the segment
	 *
	 * @return the initial intensity of the segment
	 */
	public float getFromIntensity() {
		return fromIntensity;
	}

	/**
	 * Set the initial intensity of the segment
	 *
	 * @param fromIntensity the initial intensity of the segment
	 */
	public void setFromIntensity(float fromIntensity) {
		this.fromIntensity = fromIntensity;
	}

	/**
	 * Get the final intensity of the segment
	 *
	 * @return the final intensity of the segment
	 */
	public float getToIntensity() {
		return toIntensity;
	}

	/**
	 * Set the final intensity of the segment
	 *
	 * @param toIntensity the final intensity of the segment
	 */
	public void setToIntensity(float toIntensity) {
		this.toIntensity = toIntensity;
	}

	/**
	 * Get the initial colors of the segment
	 *
	 * @return the initial colors of the segment
	 */
	public EnumMap<Color, Boolean> getFromColors() {
		return fromColors;
	}

	/**
	 * Set the colors initial of the segment
	 *
	 * @param fromColors the initial colors of the segment
	 */
	public void setFromColors(EnumMap<Color, Boolean> fromColors) {
		this.fromColors = fromColors;
	}

	/**
	 * Get the initial colors of the segment
	 *
	 * @return the initial colors of the segment
	 */
	public EnumMap<Color, Boolean> getToColors() {
		return toColors;
	}

	/**
	 * Set the colors initial of the segment
	 *
	 * @param toColors the initial colors of the segment
	 */
	public void setToColor(EnumMap<Color, Boolean> toColors) {
		this.toColors = toColors;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Segment)) {
			return false;
		}
		Segment other = (Segment) o;
		return Objects.equals(from, other.from) && Objects.equals(to, other.to) &&
				Math.abs(fromIntensity - other.fromIntensity) < 1e-6 && Math.abs(toIntensity - other.toIntensity) < 1e-6 &&
				Objects.equals(fromColors, other.fromColors) && Objects.equals(toColors, other.toColors);
	}
}