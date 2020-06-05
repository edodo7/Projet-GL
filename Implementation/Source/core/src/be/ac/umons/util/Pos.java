package be.ac.umons.util;

/**
 * A position is represented by x and y values
 */
public class Pos implements Cloneable {
	private int x;
	private int y;

	/**
	 * Constructor
	 * @param x				X coordinate
	 * @param y				Y coordinate
	 */
	public Pos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the x value of the position
	 *
	 * @return the x value
	 */
	public int getX() {
		return x;
	}

	/**
	 * Set the x value of the position
	 *
	 * @param x the x value
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Get the y value of the position
	 *
	 * @return the y value
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set the y value of the position
	 *
	 * @param y the y value
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Translate the actual position by another position
	 *
	 * @param other the position by which the actual position is translated
	 * @return the translated position
	 */
	public Pos add(Pos other) {
		return new Pos(x + other.x, y + other.y);
	}

	/**
	 * Swap the x and y values of the position
	 *
	 * @return a new position with the x and y values swapped
	 */
	public Pos swap() {
		return new Pos(y, x);
	}

	@Override
	public Pos clone() {
		Pos copy = null;
		try {
			copy = (Pos) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException();
		}
		copy.x = x;
		copy.y = y;
		return copy;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public int hashCode() {
		return (x + y) * y + x;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pos))
			return false;
		Pos other = (Pos) o;

		return x == other.x && y == other.y;
	}
}