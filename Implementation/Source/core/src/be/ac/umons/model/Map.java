package be.ac.umons.model;


import be.ac.umons.model.hexagon.*;
import be.ac.umons.model.levelChecker.HexagonPos;
import be.ac.umons.model.levelChecker.Solution;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pos;
import be.ac.umons.util.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A {@link Model} implementation that contains an array of hexagons
 */
public class Map implements Model, Cloneable {

	protected Hexagon[][] hexagons;
	protected Hexagon[][] constraints;
	protected List<Pos> sources;
	protected List<Pos> targets;
	protected int height, width;
	private List<Segment> segments;
	private boolean lost = false;
	private int targetsReached = 0;

	/**
	 * Constructor
	 * @param width					Width of the map
	 * @param height				Height of the map
	 */
	public Map(int width, int height) {
		this(width, height, false);
	}

	/**
	 * Constructor
	 * @param width					Width of the map
	 * @param height				Height of the map
	 * @param fill					True if the map should be filled with Empty Hexagons
	 */
	public Map(int width, int height, boolean fill) {
		init();
		hexagons = new Hexagon[height][width];
		this.height = height;
		this.width = width;
		if (fill)
			fill();
		afterConstructor();
	}

	/**
	 * Constructor
	 * @param hexagons				Content of the map
	 */
	public Map(Hexagon[][] hexagons) {
		init();
		this.height = hexagons.length;
		assert height > 0;
		this.width = hexagons[0].length;

		setHexagons(hexagons);
		afterConstructor();
	}

	/**
	 * Constructor
	 * @param solution				Solutions
	 */
	public Map(Solution solution) {
		this(solution.getWidth(), solution.getHeight());
		for (HexagonPos hexPos : solution.getHexagons()) {
			this.setHexagon(hexPos.getHexagon(), hexPos.getPos());
		}
		afterConstructor();
	}

	/**
	 * Initialize the map
	 */
	private void init() {
		segments = new CopyOnWriteArrayList<>();
		sources = new ArrayList<>();
		targets = new ArrayList<>();
	}

	/**
	 * Fills the map with Empty Hexagons
	 */
	public void fill() {
		for (int y = 0; y < this.height; ++y) {
			for (int x = 0; x < this.width; ++x) {
				if (this.hexagons[y][x] == null)
					this.hexagons[y][x] = new Empty();
			}
		}
	}

	/**
	 * Initializes the constraints
	 */
	private void afterConstructor() {
		constraints = new Hexagon[this.height][this.width];
	}

	@Override
	public Hexagon getHexagon(Pos pos) {
		if (!isValid(pos))
			return null;
		return hexagons[pos.getY()][pos.getX()];
	}

	@Override
	public Hexagon[][] getHexagons() {
		return hexagons;
	}

	@Override
	public void setHexagons(Hexagon[][] hexagons) {
		assert hexagons.length >= 1;
		reset(width,height);
		this.hexagons = new Hexagon[this.height][this.width];
		for (int y = 0; y < this.height; ++y) {
			for (int x = 0; x < this.width; ++x) {
				setHexagon(hexagons[y][x], new Pos(x, y));
			}
		}
	}

	/**
	 * Returns a List containing the position of all the sources
	 * @return						List containing the position of all the sources
	 */
	public List<Pos> getSourcesPos() {
		return sources;
	}

	/**
	 * Returns a List containing the position of all the targets
	 * @return						List containing the position of all the targets
	 */
	public List<Pos> getTargetsPos() {
		return targets;
	}

	/**
	 * Returns the height of the map
	 * @return						The height of the map
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the width of the map
	 * @return						The width of the map
	 */
	public int getWidth() {
		return width;
	}

	@Override
	public void setHexagon(Hexagon hexagon, Pos pos) {
		removeHexagon(pos);
		hexagons[pos.getY()][pos.getX()] = hexagon;
		if (hexagon != null) {
			if (hexagon.getClass() == Source.class)
				sources.add(pos);
			if (hexagon.getClass() == Target.class)
				targets.add(pos);
		}
	}

	@Override
	public void removeHexagon(Pos pos) {
		if (getHexagon(pos) == null)
			return;
		if (getHexagon(pos).getClass() == Source.class)
			sources.remove(pos);
		if (getHexagon(pos).getClass() == Target.class)
			targets.remove(pos);
		hexagons[pos.getY()][pos.getX()] = null;
	}

	@Override
	public void moveHexagon(Pos from, Pos to) {
		setHexagon(getHexagon(from), to);
		removeHexagon(from);
	}

	@Override
	public void swapHexagons(Pos from, Pos to) {
		Hexagon toHex = getHexagon(to);
		setHexagon(getHexagon(from), to);
		setHexagon(toHex, from);
	}

	/**
	 * Gets the constraint at a certain position of the map
	 * @param pos					Position
	 * @return						Hexagon representing the constraint
	 */
	public Hexagon getConstraint(Pos pos) {
		if (!isValid(pos))
			return null;
		return constraints[pos.getY()][pos.getX()];
	}

	/**
	 * Updates the constraint at a certain position of the map
	 * @param pos					Position
	 * @param constraint			Hexagon representing the constraint
	 */
	public void setConstraint(Hexagon constraint, Pos pos) {
		if (!isValid(pos))
			return;
		constraints[pos.getY()][pos.getX()] = constraint;
	}

	/**
	 * Checks if an hexagon respects a constraint
	 * @param hex					The Hexagon
	 * @param pos					Position of the constraint
	 * @return						True if the hexagon respects the constraint
	 */
	public boolean respectConstraint(Hexagon hex, Pos pos) {
		if (!isValid(pos))
			return false;
		Hexagon constraint = constraints[pos.getY()][pos.getX()];
		if (constraint == null || hex instanceof Empty || hex == null)
			return true;
		return constraint.equalsExceptAttributes(hex);
	}

	@Override
	public Pos getNextPos(Pos pos, Edge edge) {
		Pos nextPos = pos.clone();
		switch (edge) {
			case NORTH:
				nextPos.setY(nextPos.getY() + 1);
				break;
			case NORTHEAST:
				nextPos.setX(nextPos.getX() + 1);
				nextPos.setY(nextPos.getY() + 1);
				break;
			case SOUTHEAST:
				nextPos.setX(nextPos.getX() + 1);
				break;
			case SOUTH:
				nextPos.setY(nextPos.getY() - 1);
				break;
			case SOUTHWEST:
				nextPos.setX(nextPos.getX() - 1);
				break;
			case NORTHWEST:
				nextPos.setX(nextPos.getX() - 1);
				nextPos.setY(nextPos.getY() + 1);
				break;
		}
		if (Math.floorMod(pos.getX(), 2) == 1 && edge != Edge.NORTH && edge != Edge.SOUTH) {
			nextPos.setY(nextPos.getY() - 1);
		}
		return nextPos;
	}

	@Override
	public void sendLaser() {
		removeLaser();
		for (Pos sourcePos : sources) {
			Source source = (Source) getHexagon(sourcePos);
			sendLaserHelper(null, sourcePos, source.getLaser());
		}
	}

	/**
	 * An helper of the function {@link #sendLaser()} that sends the laser recursively
	 *
	 * @param inEdge the edge by which the laser enters
	 * @param hexPos the position of the hexagon in which the laser enters
	 * @param laser  the laser that is sent
	 */
	private void sendLaserHelper(Edge inEdge, Pos hexPos, Laser laser) {
		Hexagon hex = getHexagon(hexPos);
		// Out of map
		if (hex == null) {
			addSegment(inEdge, hexPos, laser, Laser.emptyLaser());
			return;
		}
		// Get the laser after traversing the modifier
		Laser modifiedLaser = hex.getNextLaserModifier(laser);
		// Check the laser after traversing the modifier too
		if (modifiedLaser.hasDisappeared()) {
			addSegment(inEdge, hexPos, laser, modifiedLaser);
			return;
		}
		// Check if the laser cross another laser
		if (hex.containsLaser(inEdge, modifiedLaser)) {
			lost = true;
			addSegment(inEdge, hexPos, laser, modifiedLaser);
			return;
		}
		// Notify that the hexagon has been crossed
		hex.traverse(inEdge, modifiedLaser);
		// Get the laser after traversing the hexagon itself
		Laser nextLaser = hex.getNextLaser(inEdge, modifiedLaser);
		// Check that the laser has no color or not enough intensity
		if (nextLaser.hasDisappeared()) {
			addSegment(inEdge, hexPos, laser, modifiedLaser);
			return;
		}
		// Check if the laser has reached a target
		if (nextLaser.hasReachedTarget()) {
			setReachedTarget(true);
		}
		addSegment(inEdge, hexPos, laser, nextLaser);
		// Recursive call on all next edges
		for (Edge outEdge : hex.getNextEdges(inEdge, modifiedLaser)) {
			Pos nextPos = getNextPos(hexPos, outEdge);
			sendLaserHelper(outEdge.getOpp(), nextPos, nextLaser);
		}
	}

	// Add a segment consisting of the previous position and the actual position if the previous position exists
	private void addSegment(Edge inEdge, Pos pos, Laser prevLaser, Laser laser) {
		if (inEdge != null) {
			Pos prevPos = getNextPos(pos, inEdge);
			segments.add(new Segment(prevPos, pos, prevLaser.getIntensity(), laser.getIntensity(),
					prevLaser.getColors(), laser.getColors()));
		}
	}

	/**
	 * Remove the laser information in each hexagon of the map
	 */
	public void removeLaser() {
		targetsReached = 0;
		lost = false;
		segments.clear();
		forEachUsedPos(pos -> getHexagon(pos).reset());
	}

	@Override
	public List<Segment> getSegments() {
		return segments;
	}

	@Override
	public void setReachedTarget(boolean reached) {
		if (reached)
			++targetsReached;
		else
			--targetsReached;
	}

	@Override
	public boolean hasWon() {
		return targets.size() == targetsReached;
	}

	@Override
	public boolean hasLost() {
		return lost;
	}

	/**
	 * Get whether the given position is in the bounds of the map
	 *
	 * @param pos the position
	 * @return whether the position is in the bounds of the map
	 */
	private boolean isValid(Pos pos) {
		return pos != null && 0 <= pos.getX() && pos.getX() < width & 0 <= pos.getY() && pos.getY() < height;
	}

	@Override
	public void reset(int width, int height) {
		removeLaser();
		hexagons = new Hexagon[height][width];
		sources = new ArrayList<>();
		targets = new ArrayList<>();
	}

	/**
	 * Call the method {@link Consumer#accept(Object)} of the given {@link Consumer} object on each position containing a not null hexagon
	 *
	 * @param consumer Consumer functional interface
	 */
	public void forEachUsedPos(Consumer<Pos> consumer) {
		for (int y = 0; y < hexagons.length; ++y) {
			for (int x = 0; x < hexagons[y].length; ++x) {
				Pos pos = new Pos(x, y);
				if (getHexagon(pos) != null) {
					consumer.accept(pos);
				}
			}
		}
	}

	/**
	 * Return the first position that respects the method {@link Predicate#test(Object)} of the given {@link Predicate} object without counting
	 * positions containing a null hexagon
	 *
	 * @param predicate Predicate functional interface
	 * @return the first position that respects the predicate
	 */
	public Pos testForEachUsed(Predicate<Pos> predicate) {
		for (int y = 0; y < hexagons.length; ++y) {
			for (int x = 0; x < hexagons[y].length; ++x) {
				Pos pos = new Pos(x, y);
				Hexagon hex = getHexagon(pos);
				if (hex != null && predicate.test(pos)) {
					return pos;
				}
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Map)) {
			return false;
		}
		Map other = (Map) o;
		if (this.getHeight() != other.getHeight() || this.getWidth() != other.getWidth())
			return false;
		for (int y = 0; y < this.getHeight(); ++y) {
			for (int x = 0; x < this.getWidth(); ++x) {
				Pos pos = new Pos(x, y);
				Hexagon hex = this.getHexagon(pos);
				Hexagon otherHex = other.getHexagon(pos);
				if (!Objects.equals(hex, otherHex) || (hex != null && otherHex != null &&
						!Objects.equals(hex.getModifier(), otherHex.getModifier()))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Adds an Empty Hexagon
	 * @param pos					Position
	 */
	public void fillHole(Pos pos) {
		setHexagon(new Empty(), pos);
	}

	/**
	 * Gets the element on the top of a cell
	 * @param pos					Cell position
	 * @return						The element on the top
	 */
	public Hexagon getOnTop(Pos pos) {
		Hexagon baseHex = getHexagon(pos);
		if (baseHex == null)
			return null;
		if (baseHex.getModifier() != null)
			return baseHex.getModifier();
		return baseHex;
	}

	/**
	 * Removes the element on the top of a cell
	 * @param pos					Cell position
	 * @return						The element on the top
	 */
	private Hexagon removeOnTop(Pos pos) {
		Hexagon currentHex = this.getHexagon(pos);
		Hexagon removed;
		if (currentHex.getModifier() != null) {
			removed = currentHex.getModifier();
			currentHex.setModifier(null);
		} else {
			removed = currentHex;
			fillHole(pos);
		}
		return removed;
	}

	/**
	 * Replaces the element on the top of a cell
	 * @param hex					New element
	 * @param to					Cell position
	 * @return						The olf element on the top
	 */
	private Hexagon addAndRemoveOnTop(Hexagon hex, Pos to) {
		Hexagon currentTo = getHexagon(to);
		if (!canInsertTo(hex, to))
			return hex;
		else if (hex instanceof Modifier) {
			if(currentTo!=null && (currentTo.isEmpty() || !currentTo.canHaveModifier()) && respectConstraint(hex, to)){
				setHexagon(hex,to);
				return null;
			}
			else if (currentTo != null && currentTo.canHaveModifier()) {
				Hexagon removed = currentTo.getModifier();
				currentTo.setModifier((Modifier) hex);
				return removed;
			}
			else if (respectConstraint(hex, to)) {
				setHexagon(hex, to);
				return currentTo;
			} else {
				return hex;
			}
		} else if (respectConstraint(hex, to)) {
			setHexagon(hex, to);
			return currentTo;
		} else {
			return hex;
		}
	}

	/**
	 * Returns true if an element can be moved between two positions
	 * @param from					1st position
	 * @param to					2nd position
	 * @return						True if the move cant rake place
	 */
	public boolean canMoveTo(Pos from, Pos to) {
		boolean notNull = getHexagon(from) != null && getOnTop(to) != null;
		if(notNull){
			Hexagon src = getOnTop(from);
			Hexagon target = getOnTop(to);
			boolean isOK;
			if(src instanceof Modifier){
				isOK = target.isEmpty() || target.canHaveModifier();
			}
			else{
				isOK = respectConstraint(src, to);
			}
			return isOK;
		}
		else
			return false;
	}

	/**
	 * Returns true if an element from a container can be inserted into a cell
	 * @param container				The container
	 * @param to					The position of the cell
	 * @param index					The index of the element to be inserted
	 * @return						True if the element can be inserted into the cell
	 */
	public boolean canInsertTo(Container container, Pos to, int index) {
		boolean notNull = getHexagon(to) != null && index < container.size();
		if(notNull) {
			Hexagon src = container.getHexagon(index);
			Hexagon target = getHexagon(to);
			boolean isOK;
			if(src instanceof Modifier){
				isOK = target.canHaveModifier() || target.isEmpty();
			}
			else{
				isOK = respectConstraint(src, to);
			}
			return isOK;
		}
		else
			return false;
	}

	/**
	 * Returns true if an hexagon can be inserted into a cell
	 * @param hex					Hexagon to be inserted
	 * @param to					Position where the hexagon should be inserted
	 * @return						True if the hexagon can be inserted
	 */
	public boolean canInsertTo(Hexagon hex, Pos to) {
		boolean notNull = getHexagon(to) != null && hex != null && !hex.isEmpty();
		if(notNull) {
			Hexagon target = getHexagon(to);
			boolean isOK;
			if(hex instanceof Modifier){
				isOK = target.isEmpty() || target.canHaveModifier();
			}
			else{
				isOK = respectConstraint(hex,to);
			}
			return isOK;
		}
		else
			return false;

	}

	/**
	 * Moves an hexagon
	 * @param from					Initial position
	 * @param to					New position
	 */
	public void moveOnMap(Pos from, Pos to) {
		if (!canMoveTo(from, to))
			return;
		Hexagon currentFrom = removeOnTop(from);
		Hexagon removed = addAndRemoveOnTop(currentFrom, to);
		if(getHexagon(from)==null || getHexagon(from).isEmpty() || (removed instanceof Modifier) && getHexagon(from).canHaveModifier())
			addAndRemoveOnTop(removed, from);
	}

	/**
	 * Adds an Hexagon from a container
	 * @param container				The Container
	 * @param index					Index of hexagon
	 * @param mapPos				Position on the map
	 */
	public void moveFromContainer(Container container, int index, Pos mapPos) {
		if (!canInsertTo(container, mapPos, index))
			return;
		Hexagon currentFrom = container.removeHexagon(index);
		if (getHexagon(mapPos) != null && getHexagon(mapPos).getModifier() != null && !(currentFrom instanceof Modifier)) {
			container.addHexagon(currentFrom);
			return;
		}
		Hexagon removed = addAndRemoveOnTop(currentFrom, mapPos);
		if (removed != null && !(removed instanceof Empty))
			container.addHexagon(removed);
	}

	/**
	 * Moves an hexagon to a container
	 * @param mapPos				Position of the Hexagon
	 * @param container				The Container
	 */
	public void moveToContainer(Pos mapPos, Container container) {
		Hexagon currentFrom = removeOnTop(mapPos);
		if (currentFrom != null && !(currentFrom instanceof Empty))
			container.addHexagon(currentFrom);
	}

	@Override
	public Map clone() throws CloneNotSupportedException {
		Map copy = (Map) super.clone();
		copy.hexagons = new Hexagon[hexagons.length][hexagons[0].length];
		for (int y = 0; y < hexagons.length; ++y) {
			for (int x = 0; x < hexagons[y].length; ++x) {
				if (hexagons[y][x] != null)
					copy.hexagons[y][x] = hexagons[y][x].clone();
			}
		}
		copy.sources = new ArrayList<>(sources.size());
		for (Pos pos : sources) {
			copy.sources.add(pos.clone());
		}
		copy.targets = new ArrayList<>(targets.size());
		for (Pos pos : targets) {
			copy.targets.add(pos.clone());
		}
		return copy;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int y = this.getHeight() - 1; y >= 0; --y) {
			for (int x = 0; x < this.getWidth(); ++x) {
				str.append(this.getHexagon(new Pos(x, y)) == null ? "      null  " : this.getHexagon(new Pos(x, y))).append("  ");
			}
			str.append("\n");
			for (int x = 0; x < this.getWidth(); ++x) {
				Pos pos = new Pos(x, y);
				str.append((this.getHexagon(pos) == null || this.getHexagon(pos).getModifier() == null) ?
						"            " : this.getHexagon(pos).getModifier()).append("  ");
			}
			str.append("\n");
		}
		return str.toString();
	}

	/**
	 * Allows to declare the game lost
	 * @param lost					True if the game is lost
	 */
	public void setLost(boolean lost) {
		this.lost = lost;
	}

	/**
	 * Returns the array containing the constraints
	 * @return						An array containing the constraints
	 */
	public Hexagon[][] getConstraints() {
		return constraints;
	}
}