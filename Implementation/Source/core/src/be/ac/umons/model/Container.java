package be.ac.umons.model;

import java.util.ArrayList;
import java.util.Objects;

import be.ac.umons.model.hexagon.Hexagon;

/**
 * Class containing the hexagons that can be used by an user during a game
 */
public class Container implements Cloneable {

	protected ArrayList<Hexagon> hexagons;

	/**
	 * Constructor
	 */
	public Container() {
		hexagons = new ArrayList<>();
	}

	/**
	 * Get the hexagon at the specified index
	 *
	 * @param index     the index of the hexagon
	 * @return          the hexagon at the specified index
	 */
	public Hexagon getHexagon(int index) {
		return hexagons.get(index);
	}

	/**
	 * Get an ArrayList containing the hexagons of the container
	 *
	 * @return an ArrayList containing the hexagons of the container
	 */
	public ArrayList<Hexagon> getHexagons() {
		return hexagons;
	}

	public void setHexagons(ArrayList<Hexagon> hexagons) {
		this.hexagons = hexagons;
	}

	/**
	 * Get the size of the container
	 *
	 * @return the size of the container
	 */
	public int size() {
		return hexagons.size();
	}

	/**
	 * Add an hexagon at an index of the container
	 *
	 * @param hexagon the hexagon that is added
	 * @param index   the index at which the hexagon is added
	 */
	public void setHexagon(Hexagon hexagon, int index) {
		hexagons.set(index, hexagon);
	}

	/**
	 * Add an hexagon at the end of the container
	 *
	 * @param hexagon the hexagon that is added
	 */
	public void addHexagon(Hexagon hexagon) {
		if (!hexagon.isEmpty())
			hexagons.add(hexagon);
	}

	public void addHexagons(ArrayList<Hexagon> hexagons) {
		for (int i = 0; i < hexagons.size(); i++) {
			addHexagon(hexagons.get(i));
		}
	}

	/**
	 * Removes all the hexagons
	 */
	public void removeAllHexagons() {
		hexagons.clear();
	}

	/**
	 * Remove an hexagon at a certain index of the container
	 *
	 * @param index     the index at which the hexagon is used
	 * @return          the hexagon that is removed
	 */
	public Hexagon removeHexagon(int index) {
		return hexagons.remove(index);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Container)) {
			return false;
		}
		Container other = (Container) o;
		return Objects.equals(hexagons, other.hexagons);
	}

	@Override
	public Container clone() throws CloneNotSupportedException {
		Container copy = (Container) super.clone();
		copy.hexagons = new ArrayList<>(hexagons.size());
		for (Hexagon hexagon : hexagons) {
			copy.hexagons.add(hexagon.clone());
		}
		return copy;
	}
}