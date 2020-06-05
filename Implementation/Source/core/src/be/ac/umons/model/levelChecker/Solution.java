package be.ac.umons.model.levelChecker;

import java.util.ArrayList;

import be.ac.umons.model.hexagon.Empty;

/**
 * Class that stores the hexagons of the solution and the dimension of the solution
 */
public class Solution {

	private final ArrayList<HexagonPos> hexagonsPos = new ArrayList<>();
	private final int width;
	private final int height;

	public Solution(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Add an hexagon to the solution
	 *
	 * @param hexPos    the hexagon
	 */
	public void addHexagon(HexagonPos hexPos) {
		hexagonsPos.add(hexPos);
	}

	/**
	 * Get all hexagons of the solutions
	 *
	 * @return  the hexagons
	 */
	public ArrayList<HexagonPos> getHexagons() {
		return hexagonsPos;
	}

	/**
	 * Get the width of the solution
	 *
	 * @return  the width of the solution
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the solution
	 *
	 * @return  the height of the solution
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the number of hexagons of the solution that are not empty
	 *
	 * @return  the number of hexagons of the solution that are not empty
	 */
	public int countNotEmpty() {
		int count = 0;
		for (HexagonPos hexagonPos : hexagonsPos) {
			if (!(hexagonPos.getHexagon() instanceof Empty))
				++count;
		}
		return count;
	}
}