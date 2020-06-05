package be.ac.umons.model.levelChecker;

import com.badlogic.gdx.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

import be.ac.umons.model.Container;
import be.ac.umons.model.Laser;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.Modifier;
import be.ac.umons.model.hexagon.Source;
import be.ac.umons.model.hexagon.Target;
import be.ac.umons.util.Edge;
import be.ac.umons.util.Pair;
import be.ac.umons.util.Pos;

/**
 * The LevelChecker can find the solutions of a map
 * with objects being placed on the map and other in the game container.
 * Basically, it recursively places distinct hexagons and modifiers and
 * checks every position and orientation of the available hexagons
 * and only place the hexagons on the path of the laser.
 * It does not support the color and intensity extensions.
 */
public class LevelChecker {

	private static final Logger logger;

	static {
		logger = new Logger("LC");
		logger.setLevel(Logger.DEBUG);
	}

	private Map map;
	private Container container;
	private volatile ArrayList<Solution> solutions = new ArrayList<>();
	private int maxSolutions;
	private boolean[][] reallyRotatable;
	private boolean[][] traversed;
	private double loading;
	private boolean stopped;

	public LevelChecker(Map map, Container container) {
		this.map = map;
		this.container = container;
		this.reallyRotatable = new boolean[map.getHeight()][map.getWidth()];
		this.traversed = new boolean[map.getHeight()][map.getWidth()];
	}

	/**
	 * Modify the map so that all movable hexagons goes to the container
	 *
	 * @return a pair with the first element being a list of all hexagons and their initial position of the map
	 * and the second element being the initial size of the container
	 */
	private Pair<List<HexagonPos>, Integer> setMovablesToContainer() {
		List<HexagonPos> moved = new ArrayList<>();
		Pair<List<HexagonPos>, Integer> movedAndSize = new Pair<>(moved, container.size());
		map.forEachUsedPos(pos -> {
			Hexagon hex = map.getHexagon(pos);
			if (hex.getModifier() != null) {
				container.addHexagon(hex.getModifier());
				moved.add(new HexagonPos(hex.getModifier(), pos, true));
				hex.setModifier(null);
			}
			if (!(hex instanceof Empty) && hex.isMovable()) {
				map.removeHexagon(pos);
				map.setHexagon(new Empty(), pos);
				container.addHexagon(hex);
				moved.add(new HexagonPos(hex, pos));
			}
		});
		return movedAndSize;
	}

	/**
	 * Revert the action of {@link #setMovablesToContainer()}.
	 * It is supposed that the given argument is the output of {@link #setMovablesToContainer()}
	 *
	 * @param movedAndSize a pair with the first element being a list of all hexagons and their initial position of the map
	 *                     and the second element being the initial size of the container
	 */
	private void setOnMap(Pair<List<HexagonPos>, Integer> movedAndSize) {
		for (int i = container.size() - 1; i >= movedAndSize.getSecond(); --i) {
			container.removeHexagon(i);
		}
		List<HexagonPos> moved = movedAndSize.getFirst();
		for (int i = moved.size() - 1; i >= 0; --i) {
			HexagonPos hexPos = moved.get(i);
			if (hexPos.isModifier()) {
				map.getHexagon(hexPos.getPos()).setModifier((Modifier) hexPos.getHexagon());
			} else {
				map.setHexagon(hexPos.getHexagon(), hexPos.getPos());
			}
		}
	}

	/**
	 * Try to place each of the available sources or none on a position of the map
	 * If all positions have been calculated, call {@link #placeHexagons(double)}
	 * It uses a {@link PermutationTree} instance that shows the available distinct sources at each recursion
	 *
	 * @param permTree the permutation tree
	 * @param pos      the current position of the map
	 * @param weight   the weight of the recursion
	 */
	private void placeSource(PermutationTree<Source> permTree, Pos pos, double weight) {
		if (stopped) {
			loading += weight;
			return;
		}
		if (pos.getY() == map.getHeight()) {
			placeHexagons(weight);
			return;
		}

		boolean endOfRow = (pos.getX() == map.getWidth() - 1);
		Pos nextPos = new Pos(endOfRow ? 0 : pos.getX() + 1, pos.getY() + (endOfRow ? 1 : 0));

		List<Source> choice = permTree.getChoice();
		int choiceNumber = 1;
		if (map.getHexagon(pos) instanceof Empty) {
			choiceNumber += choice.stream().filter(Objects::nonNull).count();
		}

		// Don't place a source on this position
		placeSource(permTree, nextPos, weight / choiceNumber);

		for (int i = 0; i < choice.size(); ++i) {
			Source source = choice.get(i);
			if (source != null && map.getHexagon(pos) instanceof Empty) {
				permTree.choose(i);
				Hexagon oldHex = map.getHexagon(pos);
				map.setHexagon(source, pos);

				// Place the source, it will be rotated after in rotateHexagon
				placeSource(permTree, nextPos, weight / choiceNumber);

				map.setHexagon(oldHex, pos);
				permTree.reconsider(i);
			}
		}
	}

	/**
	 * Start the tree traversal
	 *
	 * @param weight the weight of the recursion
	 */
	private void placeHexagons(double weight) {
		Stack<LaserPosEdge> laserSources = new Stack<>();
		for (Pos sourcePos : map.getSourcesPos()) {
			Source source = (Source) map.getHexagon(sourcePos);
			laserSources.add(new LaserPosEdge(source.getLaser(), sourcePos, null));
		}
		List<Hexagon> hexagons = container.getHexagons().stream().filter(hex -> !(hex instanceof Source)).collect(Collectors.toList());
		placeHexagon(new PermutationTree<>(hexagons, true), laserSources, 0, weight);
	}

	/**
	 * Try to place each of the available hexagons or none on the last position of the laser
	 * The function {@link #placeModifier(LaserPosEdge, Stack, PermutationTree, Hexagon, int, double)} is called with each hexagon
	 * If there is no more possible position or if it is the last hexagon, add the solution if the game is won
	 * It uses a {@link PermutationTree} instance that shows the available distinct hexagons at each recursion
	 *
	 * @param permTree     the permutation tree
	 * @param laserSources stack of PosEdge that maintains the last position and direction of each laser
	 * @param depth        the depth of the recursion
	 * @param weight       the weight of the recursion
	 */
	private void placeHexagon(PermutationTree<Hexagon> permTree, Stack<LaserPosEdge> laserSources, int depth, double weight) {
		if (solutions.size() >= maxSolutions || stopped) {
			loading += weight;
			return;
		}
		// All lasers are computed, check win
		if (laserSources.isEmpty()) {
			if (map.hasWon()) {
				addSolution();
			}
			loading += weight;
			return;
		}
		// Get the actual source
		LaserPosEdge laserSource = laserSources.pop();
		Laser laser = laserSource.getLaser();
		Hexagon oldHex = map.getHexagon(laserSource.getPos());

		logger.debug(spaceString(depth) + "======== " + laserSource.getPos() + " ========");
		// If the position is not in the map,
		// search the next possible position of the laser
		if (oldHex == null || laserSource.getLaser().hasDisappeared()) {
			if (oldHex == null) {
				logger.debug(spaceString(depth) + "OUT OF MAP");
			} else {
				logger.debug(spaceString(depth) + "LASER DISAPPEARED : " + "intensity " + laser.getIntensity()
						+ ", colors " + laser.getColors().values().stream().filter(value -> value).count());
			}

			placeHexagon(permTree, laserSources, depth, weight);
		} else if (!oldHex.containsLaser(laserSource.getEdge(), laser)) {
			// Stop the recursion if a laser is crossed
			List<Hexagon> choice = permTree.getChoice();
			int choiceNumber = 1; // We can always choose to not place any hexagon
			if (oldHex instanceof Empty) {
				choiceNumber += choice.stream().filter(Objects::nonNull).count();
			}
			logger.debug(spaceString(depth) + "Don't place hexagon on position " + laserSource.getPos());
			// Recursion without placing the new hexagon
			placeModifier(laserSource, laserSources, permTree, oldHex, depth, weight / choiceNumber);
			// Check if we can place a new hexagon
			if (oldHex instanceof Empty) {
				for (int i = 0; i < choice.size(); ++i) {
					Hexagon newHex = choice.get(i);
					if (newHex != null) {
						if (map.respectConstraint(newHex, laserSource.getPos())) {
							permTree.choose(i);
							map.setHexagon(newHex, laserSource.getPos());
							reallyRotatable[laserSource.getPos().getY()][laserSource.getPos().getX()] = newHex.isRotatable();
							logger.debug(spaceString(depth) + "Place hexagon " + newHex + " on position " + laserSource.getPos() +
									", laser coming from " + laserSource.getEdge());
							// Recursion after placing the new hexagon
							placeModifier(laserSource, laserSources, permTree, newHex, depth, weight / choiceNumber);
							reallyRotatable[laserSource.getPos().getY()][laserSource.getPos().getX()] = oldHex.isRotatable();
							map.setHexagon(oldHex, laserSource.getPos());
							permTree.reconsider(i);
						} else {
							loading += weight / choiceNumber;
						}
					}
				}
			}
		} else {
			logger.debug("---- LOSE -----");
			logger.debug(map.toString() + "---------------");
			loading += weight;
		}
		laserSources.add(laserSource);
	}

	/**
	 * Try to place each possible modifier on the hexagon or nothing
	 * The function {@link #rotateHexagon(LaserPosEdge, Stack, PermutationTree, Hexagon, int, double)} is then called
	 *
	 * @param laserSource  the actual laser and its position and direction
	 * @param laserSources stack of LaserPosEdge that maintains the lasers that have been processed and their position and direction
	 * @param permTree     the permutation tree
	 * @param hexagon      the hexagon to place
	 * @param depth        the depth of the recursion
	 * @param weight       the weight of the recursion
	 */
	private void placeModifier(LaserPosEdge laserSource, Stack<LaserPosEdge> laserSources, PermutationTree<Hexagon> permTree, Hexagon hexagon, int depth, double weight) {
		int choiceNumber = 1;
		List<Hexagon> choice = permTree.getChoice();
		Pos currentPos = laserSource.getPos();
		boolean addModifier = hexagon.canHaveModifier() && hexagon.getModifier() == null && !traversed[currentPos.getY()][currentPos.getX()];
		boolean wasTraversed = traversed[currentPos.getY()][currentPos.getX()];
		traversed[currentPos.getY()][currentPos.getX()] = true;
		if (addModifier) {
			choiceNumber += (int) choice.stream().filter(hex -> hex instanceof Modifier).count();
		}

		logger.debug(spaceString(depth) + "Don't set Modifier");
		rotateHexagon(laserSource, laserSources, permTree, hexagon, depth, weight / choiceNumber);
		if (addModifier) {
			for (int i = 0; i < choice.size(); ++i) {
				if (choice.get(i) instanceof Modifier) {
					Modifier modifier = (Modifier) choice.get(i);
					permTree.choose(i);
					logger.debug(spaceString(depth) + "Set Modifier " + modifier);
					hexagon.setModifier(modifier);

					rotateHexagon(laserSource, laserSources, permTree, hexagon, depth, weight / choiceNumber);

					logger.debug(spaceString(depth) + "Remove Modifier" + modifier);
					hexagon.setModifier(null);
					permTree.reconsider(i);
				}
			}
		}
		traversed[laserSource.getPos().getY()][laserSource.getPos().getX()] = wasTraversed;
	}

	/**
	 * Rotate the hexagon in each available rotation
	 * After it has been rotated, it is marked as non rotatable
	 * Get the next laser and check that it does not disappear
	 * The hexagon is traversed and {@link #placeHexagon(PermutationTree, Stack, int, double)} is called with the next laserSources
	 *
	 * @param laserSource  the actual laser and its position and direction
	 * @param laserSources stack of LaserPosEdge that maintains the lasers that have been processed and their position and direction
	 * @param permTree     the permutation tree
	 * @param hexagon      the hexagon to place
	 * @param depth        the depth of the recursion
	 * @param weight       the weight of the recursion
	 */
	private void rotateHexagon(LaserPosEdge laserSource, Stack<LaserPosEdge> laserSources, PermutationTree<Hexagon> permTree, Hexagon hexagon, int depth, double weight) {
		Laser laser = laserSource.getLaser();

		Edge initRot = hexagon.getRotation();
		boolean isRotatable = hexagon.isRotatable();

		// Rotate directly in a good direction if it is a target
		if (hexagon instanceof Target) {
			hexagon.rotate(laserSource.getEdge());
			hexagon.setRotatable(false);
		}

		// Rotate in each possible rotations
		Edge[] distinctEdges = hexagon.getDistinctEdges();
		for (Edge edge : distinctEdges) {
			logger.debug(spaceString(depth) + "Rotate position " + laserSource.getPos() + " to " + edge);
			hexagon.rotate(edge);
			// Don't let the hexagon rotate again once its rotation has been defined
			hexagon.setRotatable(false);

			Laser modifiedLaser = hexagon.getNextLaserModifier(laser);

			// Check if the laser has disappeared after traversing the modifier
			if (modifiedLaser.hasDisappeared()) {
				placeHexagon(permTree, laserSources, depth + 1, weight / distinctEdges.length);
			} else {
				Laser nextLaser = hexagon.getNextLaser(laserSource.getEdge(), modifiedLaser);
				boolean hasReachedTarget = nextLaser.hasReachedTarget();
				if (hasReachedTarget) {
					logger.debug(spaceString(depth) + "-- Target reached at position " + laserSource.getPos() + " --");
					map.setReachedTarget(true);
				}
				// Notify that the hexagon has been traversed
				hexagon.traverse(laserSource.getEdge(), modifiedLaser);

				// Add the new positions of the laser to the stack
				Edge[] nextEdges = hexagon.getNextEdges(laserSource.getEdge(), modifiedLaser);
				for (Edge nextEdge : nextEdges) {
					laserSources.add(new LaserPosEdge(nextLaser, map.getNextPos(laserSource.getPos(), nextEdge), nextEdge.getOpp()));
				}

				placeHexagon(permTree, laserSources, depth + 1, weight / distinctEdges.length);

				for (Edge ignored : nextEdges) {
					laserSources.pop();
				}

				// Notify that the hexagon has not been traversed anymore
				hexagon.traverseBack(laserSource.getEdge(), modifiedLaser);
				if (hasReachedTarget) {
					map.setReachedTarget(false);
				}
			}
			hexagon.setRotatable(isRotatable);
			hexagon.rotate(initRot);
		}
	}

	/**
	 * Add the state of the game map to the solutions list
	 */
	private void addSolution() {
		Solution solution = new Solution(map.getWidth(), map.getHeight());
		map.forEachUsedPos(pos -> {
			Hexagon hex = map.getHexagon(pos);
			if (hex != null) {
				Hexagon hexCopy = null;
				try {
					hexCopy = hex.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				assert hexCopy != null;
				hexCopy.setRotatable(reallyRotatable[pos.getY()][pos.getX()]);
				hexCopy.reset();
				solution.addHexagon(new HexagonPos(hexCopy, pos.clone()));
			}
		});
		logger.debug("----- WIN -----");
		logger.debug(map.toString() + "---------------");
		solutions.add(solution);
	}

	/**
	 * Get all solutions
	 *
	 * @param maxSolutions the maximum number of solutions
	 * @return an ArrayList containing all solutions
	 */
	private ArrayList<Solution> getSolutions(int maxSolutions) {
		this.maxSolutions = maxSolutions;
		this.solutions = new ArrayList<>();
		this.loading = 0.0;
		this.stopped = false;
		this.map.removeLaser();

		Pair<List<HexagonPos>, Integer> moved = setMovablesToContainer();
		map.forEachUsedPos(pos -> reallyRotatable[pos.getY()][pos.getX()] = map.getHexagon(pos).isRotatable());
		map.forEachUsedPos(pos -> traversed[pos.getY()][pos.getX()] = false);
		List<Edge> initRotations = container.getHexagons().stream().map(Hexagon::getRotation).collect(Collectors.toList());
		// Rotate container objects to north so that the rotation will not make them distinct in the PermutationTree
		container.getHexagons().forEach(hex -> hex.rotate(Edge.NORTH));
		List<Source> sources = container.getHexagons().stream().filter(hex -> hex instanceof Source).map(hex -> (Source) hex).collect(Collectors.toList());
		placeSource(new PermutationTree<>(sources, true), new Pos(0, 0), 1.0); // Start the recursions
		for (int i = 0; i < initRotations.size(); ++i) {
			container.getHexagon(i).rotate(initRotations.get(i));
		}
		setOnMap(moved);
		return solutions;
	}

	/**
	 * Get all solutions
	 *
	 * @return an ArrayList containing all solutions
	 */
	public ArrayList<Solution> getSolutions() {
		return getSolutions(Integer.MAX_VALUE);
	}

	/**
	 * Get a solution
	 *
	 * @return a solution
	 */
	public Solution getSolution() {
		ArrayList<Solution> solutions = getSolutions(1);
		if (solutions.isEmpty())
			return null;
		return solutions.get(0);
	}

	public double getLoading() {
		return loading;
	}

	public void stop() {
		stopped = true;
	}

	public ArrayList<Solution> getFoundSolutions() {
		return solutions;
	}

	private String spaceString(int length) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < length; ++i)
			str.append(" ");
		return str.toString();
	}

	/**
	 * Container class that contains a laser, a position and an edge
	 */
	private static class LaserPosEdge {
		private Laser laser;
		private Pos pos;
		private Edge edge;

		private LaserPosEdge(Laser laser, Pos pos, Edge edge) {
			this.laser = laser;
			this.pos = pos;
			this.edge = edge;
		}

		public Laser getLaser() {
			return laser;
		}

		public Pos getPos() {
			return pos;
		}

		public Edge getEdge() {
			return edge;
		}
	}
}