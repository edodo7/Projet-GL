package be.ac.umons.model.hexagon;

import be.ac.umons.game.Extension;
import be.ac.umons.model.Laser;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Tile;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * Base class for all classes representing an hexagon on the map
 */
public abstract class Hexagon implements Cloneable, Serializable {

	protected boolean movable;
	protected boolean rotatable;
	protected Modifier modifier;
	protected boolean canHaveModifier = true;
	protected EnumMap<Edge, Boolean> visited;
	private Edge rotation;

	/**
	 * Create an hexagon
	 * @param rotation  the rotation
	 * @param rotatable whether it is rotatable
	 * @param movable   whether it is movable
	 * @param modifier  its modifier
	 */
	public Hexagon(Edge rotation, Boolean rotatable, Boolean movable, Modifier modifier) {
		this.rotatable = rotatable;
		this.rotation = getLowestEqualsEdge(rotation);
		this.movable = movable;
		this.modifier = modifier;
		this.visited = new EnumMap<>(Edge.class);
		for (Edge e : Edge.values())
			this.visited.put(e, false);
	}

	public Hexagon(Edge rotation) {
		this(rotation, true, true, null);
	}

	public Hexagon() {
		this(Edge.NORTH);
	}

	/**
	 * Get the outgoing edges of a laser entering by a certain edge taking into account the rotation of the Hexagon object
	 *
	 * @param edge  the edge through which the laser enters
	 * @param laser the laser
	 *
	 * @return      an array containing the outgoing edges
	 */
	public final Edge[] getNextEdges(Edge edge, Laser laser) {
		Edge rotatedEdge = (edge == null ? null : edge.rotateCounterClockwise(rotation));
		Edge[] nextRotatedEdges = getNextEdgesWithoutRot(rotatedEdge, laser);
		for (int i = 0; i < nextRotatedEdges.length; i++)
			nextRotatedEdges[i] = nextRotatedEdges[i].rotateClockwise(rotation);
		return nextRotatedEdges;
	}

	/**
	 * Get the outgoing edges of a laser entering by a certain edge assuming that the hexagon has its default orientation
	 *
	 * @param edge  the edge through which the laser enters
	 * @param laser the laser
	 *
	 * @return      an array containing the outgoing edges
	 */
	protected abstract Edge[] getNextEdgesWithoutRot(Edge edge, Laser laser);

	/**
	 * Get the laser modified after traversing the Hexagon using the method {@link #getNextLaserWithoutRot(Edge, Laser)}
	 *
	 * @param edge  the edge by which the laser enters in the hexagon
	 * @param laser the laser traversing the Hexagon
	 * @return the laser modified
	 */
	public final Laser getNextLaser(Edge edge, Laser laser) {
		Edge rotatedEdge = (edge == null ? null : edge.rotateCounterClockwise(rotation));
		return getNextLaserWithoutRot(rotatedEdge, laser);
	}

	/**
	 * Get the laser modified after traversing the Hexagon without taking into account the rotation of the hexagon
	 *
	 * @param edge  the edge by which the laser enters in the hexagon
	 * @param laser the laser traversing the Hexagon
	 * @return the modified laser
	 */
	protected Laser getNextLaserWithoutRot(Edge edge, Laser laser) {
		Laser nextLaser = laser.clone();
		nextLaser.reduceIntensity();
		return nextLaser;
	}

	/**
	 * Get the laser modified after traversing the modifier of the hexagon if it exists
	 *
	 * @param laser the laser traversing the modifier of the hexagon
	 * @return the modified laser
	 */
	public final Laser getNextLaserModifier(Laser laser) {
		if (getModifier() != null)
			laser = getModifier().getNextLaser(null, laser);
		return laser;
	}

	/**
	 * Helper that traverses the hexagon taking into account the rotation of the hexagon
	 *
	 * @param edge   the edge through which the hexagon is traversed
	 * @param laser  the laser traversing the hexagon
	 * @param active whether we to set the hexagon as traversed or not traversed
	 */
	private void traverseHelper(Edge edge, Laser laser, boolean active) {
		Edge inEdge = null;
		if (edge != null) {
			inEdge = edge.rotateCounterClockwise(rotation);
			visited.put(inEdge, active);
		}
		for (Edge outEdge : this.getNextEdgesWithoutRot(inEdge, laser)) {
			visited.put(outEdge, active);
		}
	}

	/**
	 * Notify the hexagon that a laser has traversed it by keeping the incoming and outgoing edges.
	 * Note : this information can be used later to check if a laser will cross another laser with the method {@link #containsLaser(Edge, Laser)}.
	 *
	 * @param edge  the edge through which the hexagon is traversed
	 * @param laser the laser traversing the hexagon
	 */
	public final void traverse(Edge edge, Laser laser) {
		traverseHelper(edge, laser, true);
	}

	/**
	 * Undo the effect of {@link #traverse(Edge, Laser)}
	 *
	 * @param edge  the edge through which the hexagon will not be traversed anymore
	 * @param laser the laser for which the effect has to be undone
	 */
	public final void traverseBack(Edge edge, Laser laser) {
		traverseHelper(edge, laser, false);
	}

	/**
	 * Set the hexagon as not traversed by any laser
	 */
	public final void reset() {
		for (Edge edge : Edge.values()) {
			visited.put(edge, false);
		}
	}

	/**
	 * Check if a laser will cross another laser when traversing the hexagon
	 *
	 * @param edge      the edge by which the hexagon is traversed
	 * @param laser     the laser that will traverse the hexagon
	 * @return          whether the laser will cross another laser
	 */
	public final boolean containsLaser(Edge edge, Laser laser) {
		if (edge == null)
			return false;
		return containsLaserWithoutRot(edge.rotateCounterClockwise(rotation), laser);
	}

	/**
	 * Check if a laser will cross another laser when traversing the hexagon assuming that the hexagon has its default orientation
	 *
	 * @param edge the edge by which the hexagon is traversed
	 * @param laser     the laser that will traverse the hexagon
	 * @return whether the laser will cross another laser
	 */
	protected boolean containsLaserWithoutRot(Edge edge, Laser laser) {
		return visited.get(edge);
	}

	/**
	 * Rotate the hexagon if it is rotatable
	 * Rotate to the lowest equivalent orientation
	 *
	 * @param edge the orientation of the hexagon
	 */
	public final void rotate(Edge edge) {
		if (isRotatable())
			rotation = getLowestEqualsEdge(edge);
	}

	/**
	 * Get the edge that is equals to a given edge with the smallest integer value using {@link Edge#toInt()}
	 *
	 * @param edge the given edge
	 * @return the edge that is equals to a given edge with the smallest integer value
	 */
	private Edge getLowestEqualsEdge(Edge edge) {
		return Edge.fromInt(edge.toInt() % getDistinctRotationsNumber());
	}

	/**
	 * Get the tiles representing the hexagon graphically
	 * @return  an array of {@link Tile} representing the hexagon graphically
	 */
	public final Tile[] getTiles() {
		Tile[] tiles = getTilesWithoutRotation();
		for (Tile tile : tiles) {
			tile.setEdge(tile.getEdge().rotateClockwise(rotation));
		}
		return tiles;
	}

	/**
	 * Get the tiles of the hexagon assuming it has its default rotation
	 * @return  the tiles of the hexagon assuming it has its default rotation
	 */
	protected abstract Tile[] getTilesWithoutRotation();

	/**
	 * Get the rotation of the hexagon
	 *
	 * @return the rotation of the hexagon
	 */
	public final Edge getRotation() {
		return rotation;
	}

	/**
	 * Get the number of distinct rotations of the hexagon
	 *
	 * @return the number of distinct rotations of the hexagon
	 */
	public int getDistinctRotationsNumber() {
		return Edge.size();
	}

	/**
	 * Get the distinct edges to which the hexagon can be rotated
	 *
	 * @return the distinct edges to which the hexagon can be rotated
	 */
	public Edge[] getDistinctEdges() {
		if (!this.isRotatable())
			return new Edge[]{this.getRotation()};
		Edge[] edges = new Edge[this.getDistinctRotationsNumber()];
		for (int i = 0; i < edges.length; ++i)
			edges[i] = Edge.fromInt(i);
		return edges;
	}

	/**
	 * Get whether the hexagon is movable
	 *
	 * @return whether    the hexagon is movable
	 */
	public final boolean isMovable() {
		return movable;
	}

	/**
	 * Set whether the hexagon is movable
	 *
	 * @param movable whether the hexagon is movable
	 */
	public final void setMovable(boolean movable) {
		this.movable = movable;
	}

	/**
	 * Get whether the hexagon is rotatable
	 *
	 * @return whether the hexagon is rotatable
	 */
	public final boolean isRotatable() {
		return rotatable;
	}

	/**
	 * Set whether the hexagon is rotatable
	 *
	 * @param rotatable whether the hexagon is rotatable
	 */
	public final void setRotatable(boolean rotatable) {
		this.rotatable = rotatable;
	}

	/**
	 * Get whether the hexagon can have a modifier
	 *
	 * @return whether the hexagon can have a modifier
	 */
	public final Boolean canHaveModifier() {
		return canHaveModifier;
	}

	/**
	 * Get the modifier of the hexagon
	 *
	 * @return a modifier or null if the Hexagon has no modifier
	 */
	public final Modifier getModifier() {
		return modifier;
	}

	/**
	 * Set the modifier of the hexagon
	 *
	 * @param modifier the modifier to set
	 */
	public final void setModifier(Modifier modifier) {
		if (modifier != null && (!canHaveModifier() || modifier.getModifier() != null))
			throw new IllegalArgumentException();
		this.modifier = modifier;
	}

	/**
	 * Get whether the hexagon is empty
	 *
	 * @return  whether the hexagon is empty
	 */
	public boolean isEmpty() {
		return this instanceof Empty;
	}

	/**
	 * Get the extension of the hexagon
	 *
	 * @return  null if it belongs to no extension, else an {@link Extension}
	 */
	public Extension getExtension() {
		return null;
	}

	/**
	 * Get whether the hexagon is equal to another object without taking into account if it is rotatable, movable and its rotation
	 *
	 * @param o the other object
	 * @return  whether the hexagon is equal to the other object without taking into account if it is rotatable, movable and its rotation
	 */
	public boolean equalsExceptAttributes(Object o) {
		if (!(o instanceof Hexagon))
			return false;
		Hexagon other = (Hexagon) o;
		boolean initRotatable = other.rotatable;
		boolean initMovable = other.movable;
		other.setRotatable(this.rotatable);
		other.setMovable(this.movable);
		boolean equals = equalsExceptRotation(other);
		other.setRotatable(initRotatable);
		other.setMovable(initMovable);
		return equals;
	}

	/**
	 * Get whether the hexagon is equal to another object without taking into account its rotation
	 *
	 * @param o the other object
	 * @return  whether it is equal to the other object without taking into account its rotation
	 */
	public boolean equalsExceptRotation(Object o) {
		if (!(o instanceof Hexagon))
			return false;
		Hexagon other = (Hexagon) o;
		Edge initRotation = other.rotation;
		other.rotate(this.rotation);
		boolean equals = equals(other);
		other.rotate(initRotation);
		return equals;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Hexagon)) {
			return false;
		}
		Hexagon other = (Hexagon) o;
		return movable == other.movable && rotatable == other.rotatable && rotation == ((Hexagon) o).rotation;
	}

	@Override
	public String toString() {
		return String.format("%10.10s ", this.getClass().getSimpleName()) + this.getRotation().toInt();
	}

	@Override
	public Hexagon clone() throws CloneNotSupportedException {
		Hexagon copy = (Hexagon) super.clone();
		copy.movable = movable;
		copy.rotatable = rotatable;
		copy.rotation = rotation;
		copy.modifier = (modifier == null) ? null : modifier.clone();
		copy.canHaveModifier = canHaveModifier;
		copy.visited = visited.clone();
		return copy;
	}
}