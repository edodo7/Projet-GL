package be.ac.umons.model;

import java.util.List;

import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pos;
import be.ac.umons.util.Segment;

/**
 * The model of the game
 */
public interface Model {

	/**
	 * Get the hexagon located on a given position of the map
	 *
	 * @param pos   the position of the hexagon
	 * @return      the hexagon located on the given position of the map
	 */
	Hexagon getHexagon(Pos pos);

	/**
	 * Get the hexagons of the map as an array of hexagons
	 *
	 * @return an array containing the hexagons of the map
	 */
	Hexagon[][] getHexagons();

	/**
	 * Set the hexagons of the map
	 *
	 * @param hexagons an hexagon array containing the hexagons to add in the map
	 */
	void setHexagons(Hexagon[][] hexagons);

	/**
	 * Add an hexagon on a position in the map
	 *
	 * @param hexagon the hexagon to add
	 * @param pos     the position of the hexagon in the map
	 */
	void setHexagon(Hexagon hexagon, Pos pos);

	/**
	 * Remove an hexagon on a position in the map
	 *
	 * @param pos the position of the hexagon to remove
	 */
	void removeHexagon(Pos pos);

	/**
	 * Move an hexagon from a position in the map to another position
	 *
	 * @param from the original position of the hexagon
	 * @param to   the final position of the hexagon
	 */
	void moveHexagon(Pos from, Pos to);

	/**
	 * Swap the position of two hexagons in the map
	 *
	 * @param from the position of the first hexagon to swap
	 * @param to   the position of the second hexagon to swap
	 */
	void swapHexagons(Pos from, Pos to);

	/**
	 * Get the position relative to another position in the map and the edge between the two positions
	 *
	 * @param pos   the initial position
	 * @param edge  the edge between the two positions
	 * @return      the next position
	 */
	Pos getNextPos(Pos pos, Edge edge);

	/**
	 * Send the laser from each source in order to detect if the map is in a winning state with {@link #hasWon()}
	 * or in a losing state with {@link #hasLost()}
	 * The path of the laser can be later accessed with {@link #getSegments()} ()}
	 */
	void sendLaser();

	/**
	 * Get an ArrayList of Segment objects containing the path of the laser
	 *
	 * @return an ArrayList of Segment objects containing the path of the laser
	 */
	List<Segment> getSegments();

	/**
	 * Notify the map that a target has been reached or that a target is not reached anymore
	 *
	 * @param reached whether the target has been reached
	 */
	void setReachedTarget(boolean reached);

	/**
	 * Return whether the map is in a winning state
	 *
	 * @return whether the map is in a winning state
	 */
	boolean hasWon();

	/**
	 * Return whether the map is in a losing state
	 *
	 * @return whether the map is in a losing state
	 */
	boolean hasLost();

	/**
	 * Set the map as an empty map with a width and height
	 *
	 * @param width  the width of the map
	 * @param height the height of the map
	 */
	void reset(int width, int height);
}