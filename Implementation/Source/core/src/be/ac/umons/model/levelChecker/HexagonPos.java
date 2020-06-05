package be.ac.umons.model.levelChecker;


import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.util.Pos;

/**
 * Contains an hexagon, its position on the map and whether it is a modifier
 */
public class HexagonPos {
	private Hexagon hex;
	private Pos pos;
	private boolean modifier;

	public HexagonPos(Hexagon hex, Pos pos) {
		this(hex, pos, false);
	}

	public HexagonPos(Hexagon hex, Pos pos, boolean modifier) {
		this.hex = hex;
		this.pos = pos;
		this.modifier = modifier;
	}

	/**
	 * Get the hexagon
	 *
	 * @return  the hexagon
	 */
	public Hexagon getHexagon() {
		return hex;
	}

	/**
	 * Set the hexagon
	 *
	 * @param hex   the hexagon
	 */
	public void setHexagon(Hexagon hex) {
		this.hex = hex;
	}

	/**
	 * Get the position
	 *
	 * @return  the position
	 */
	public Pos getPos() {
		return pos;
	}

	/**
	 * Set the position
	 *
	 * @param pos   the position
	 */
	public void setPos(Pos pos) {
		this.pos = pos;
	}

	/**
	 * Get whether the hexagon is a modifier
	 *
	 * @return      whether the hexagon is a modifier
	 */
	public boolean isModifier() {
		return modifier;
	}

	/**
	 * Set whether the hexagon is a modifier
	 *
	 * @param modifier      whether the hexagon is a modifier
	 */
	public void setModifier(boolean modifier) {
		this.modifier = modifier;
	}
}