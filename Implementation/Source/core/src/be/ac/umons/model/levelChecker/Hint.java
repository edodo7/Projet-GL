package be.ac.umons.model.levelChecker;

import java.util.ArrayList;
import java.util.Objects;

import be.ac.umons.model.Container;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Empty;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.Modifier;
import be.ac.umons.util.Pos;

/**
 * A class that can give an hint to the nearest or a specific solution
 * An hint is seen here as a move that leads to a strictly closer solution
 */
public class Hint {

	private final Map map;
	private final Container container;

	private Hexagon hintHexagon;
	private Map solutionMap;
	private GlobalPos initPos;
	private GlobalPos finalPos;

	public Hint(Map map, Container container, boolean best) {
		this.map = map;
		this.container = container;
		if (best)
			this.solutionMap = new Map(getBestSolution());
		else
			this.solutionMap = new Map(new LevelChecker(map, container).getSolution());
	}

	public Hint(Map map, Container container, Map solutionMap) {
		this.map = map;
		this.container = container;
		this.solutionMap = solutionMap;
	}

	/**
	 * Get the solution that is the nearest from the actual map using {@link LevelChecker#getSolutions()}
	 *
	 * @return  the solution that is the nearest from the actual map
	 */
	private Solution getBestSolution() {
		// Find the best solutionMap
		// The best solutionMap is the solutionMap that has the least difference with the actual map
		ArrayList<Solution> solutions = new LevelChecker(map, container).getSolutions();
		if (solutions.isEmpty()) {
			throw new IllegalStateException("Hint : No hint can be found since there is no solution to the level");
		}
		Solution bestSolution = null;
		int bestSteps = Integer.MAX_VALUE;

		for (Solution solution : solutions) {
			int steps = 0;
			Map solutionMap = new Map(solution);
			for (int y = 0; y < map.getHeight(); ++y) {
				for (int x = 0; x < map.getWidth(); ++x) {
					Pos pos = new Pos(x, y);
					Hexagon actualHex = map.getHexagon(pos);
					Hexagon solutionHex = solutionMap.getHexagon(pos);
					if (!Objects.equals(actualHex, solutionHex)) {
						++steps;
					}
					if (actualHex == null && solutionHex == null)
						continue;
					if ((actualHex == null && solutionHex.getModifier() != null) ||
							(solutionHex == null && actualHex.getModifier() != null))
						++steps;
					if (actualHex == null || solutionHex == null)
						continue;
					Modifier actualMod = actualHex.getModifier();
					Modifier solutionMod = solutionHex.getModifier();
					if ((actualMod != null || solutionMod != null) &&
							(actualMod == null || !actualMod.equalsExceptRotation(solutionMod))) {
						++steps;
					}
				}
			}
			if (steps < bestSteps) {
				bestSteps = steps;
				bestSolution = solution;
			}
		}
		return bestSolution;
	}

	/**
	 * Find the final position of a good move on the map
	 *
	 * @return  the final position of the hexagon, null if not found
	 */
	private GlobalPos findFinalPosInMap() {
		boolean modifier = false;
		Pos finalMapPos = solutionMap.testForEachUsed(pos -> {
			Hexagon actualHex = map.getHexagon(pos);
			Hexagon solutionHex = solutionMap.getHexagon(pos);
			return !(solutionHex instanceof Empty) && !Objects.equals(actualHex, solutionHex);
		});
		if (finalMapPos == null) {
			modifier = true;
			finalMapPos = solutionMap.testForEachUsed(pos -> {
				Hexagon actualHex = map.getHexagon(pos);
				Hexagon solutionHex = solutionMap.getHexagon(pos);
				return solutionHex.getModifier() != null && !Objects.equals(actualHex.getModifier(), solutionHex.getModifier());
			});
		}
		return new GlobalPos(finalMapPos, modifier, null);
	}

	/**
	 * Find an hexagon that belongs to a good move by searching its initial position
	 *
	 * @return  an hexagon that belongs to a good move
	 */
	private Hexagon findInitialHexagon() {
		boolean modifier = false;
		Pos initMapPos = solutionMap.testForEachUsed(pos -> {
			Hexagon actualHex = map.getHexagon(pos);
			Hexagon solutionHex = solutionMap.getHexagon(pos);
			return !Objects.equals(actualHex, solutionHex) && solutionHex instanceof Empty;
		});
		if (initMapPos == null) {
			modifier = true;
			initMapPos = solutionMap.testForEachUsed(pos -> {
				Hexagon actualHex = map.getHexagon(pos);
				Hexagon solutionHex = solutionMap.getHexagon(pos);
				return !Objects.equals(actualHex.getModifier(), solutionHex.getModifier());
			});
		}
		return modifier ? map.getHexagon(initMapPos).getModifier() : map.getHexagon(initMapPos);
	}

	/**
	 * Find the final position of a good move
	 *
	 * @param solutionMap   the solution to which we want to approach
	 * @return              the final position of a good move
	 */
	private GlobalPos findFinalPos(Map solutionMap) {
		// Find the final position of the best move in all solutions
		// The best move is any move of a not empty and not null hexagon
		GlobalPos finalMapPos = findFinalPosInMap();
		if (finalMapPos.getMapPos() != null) {
			hintHexagon = finalMapPos.getHexagon(solutionMap, container);
			return finalMapPos;
		}
		// There is no position on the map where the hexagon of the solutionMap is not empty, not null and not equal to the hexagon of the actual map
		// Since the maps are not equals, there must be an hexagon in the container of the solutionMap that is not empty, not null
		// and not in the container of the actual map
		hintHexagon = findInitialHexagon();
		if (hintHexagon == null) {
			throw new IllegalStateException("Hint : Moved hexagon not found");
		}
		return new GlobalPos(null, false, container.size()); // Add at the end of the container
	}

	/**
	 * Find the initial position of a good move
	 * The hexagon of the move is supposed to be found
	 *
	 * @param solutionMap       the map of the solution
	 * @return                  the initial position of a good move
	 */
	private GlobalPos findInitialPos(Map solutionMap) {
		// We have to find the initial position of the best move
		// On that position, the hexagon of the actual map must be able to rotate
		// so that it has the same rotation as the hexagon of the hint
		// Moreover, the hexagon of the actual map and the hexagon of the solutionMap
		// can not be equal else the map after applying the hint will not be closer to the solutionMap
		// That position can be on the map or in the container
		boolean modifier = true;
		Pos initMapPos = map.testForEachUsed(pos -> {
			Hexagon actualHex = map.getHexagon(pos);
			Hexagon solutionHex = solutionMap.getHexagon(pos);
			return actualHex.getModifier() != null && !Objects.equals(actualHex.getModifier(), solutionHex.getModifier()) && actualHex.getModifier().equalsExceptRotation(hintHexagon);
		});
		if (initMapPos == null) {
			modifier = false;
			initMapPos = map.testForEachUsed(pos -> {
				Hexagon actualHex = map.getHexagon(pos);
				Hexagon solutionHex = solutionMap.getHexagon(pos);
				return !Objects.equals(actualHex, solutionHex) && actualHex.equalsExceptRotation(hintHexagon);
			});
		}
		if (initMapPos != null) {
			return new GlobalPos(initMapPos, modifier, null);
		}
		// If the hexagon is not seen on the map, it must be in the container
		for (int i = 0; i < container.size(); ++i) {
			Hexagon actualHex = container.getHexagon(i);
			if (actualHex.equalsExceptRotation(hintHexagon)) {
				return new GlobalPos(null, false, i);
			}
		}

		throw new IllegalStateException("Hint : Initial hexagon position not found");
	}

	/**
	 * Find the initial and final position of the best move and the hexagon that is moved
	 */
	private void compute() {
		if (map.equals(solutionMap)) {
			throw new IllegalArgumentException("Hint : Can not find an hint because the map is already a solution");
		}
		finalPos = findFinalPos(solutionMap);
		try {
			hintHexagon = hintHexagon.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		initPos = findInitialPos(solutionMap);
		if ((initPos.isModifier() || finalPos.isModifier()) && !(hintHexagon instanceof Modifier)) {
			throw new IllegalStateException("Hint : The found hexagon is not a Modifier and was found on the position of a Modifier");
		}
	}

	/**
	 * Remove the moved hexagon from its initial position
	 */
	private void applyInitPos() {
		Pos initMap = initPos.getMapPos();
		if (initMap != null) {
			if (initPos.isModifier()) {
				map.getHexagon(initMap).setModifier(null);
			} else {
				if (map.getHexagon(initMap).getModifier() != null)
					map.setHexagon(map.getHexagon(initMap).getModifier(), initMap);
				else
					map.setHexagon(new Empty(), initMap);
			}
		} else {
			container.removeHexagon(initPos.getContainerIndex());
		}
	}

	/**
	 * Set the hexagon to its final position
	 */
	private void applyFinalPos() {
		Pos finalMap = finalPos.getMapPos();
		if (finalMap != null) {
			Hexagon replacedHex = finalPos.isModifier() ? map.getHexagon(finalMap).getModifier()
					: map.getHexagon(finalMap);
			if (replacedHex != null && !(replacedHex instanceof Empty)) {
				container.addHexagon(replacedHex);
			}
			if (finalPos.isModifier()) {
				map.getHexagon(finalPos.getMapPos()).setModifier((Modifier) hintHexagon);
			} else {
				hintHexagon.setModifier(map.getHexagon(finalPos.getMapPos()).getModifier());
				map.setHexagon(hintHexagon, finalPos.getMapPos());
			}
		} else {
			container.addHexagon(hintHexagon);
		}
	}

	/**
	 * Apply an hint by moving the hexagon of a good move
	 */
	public void apply() {
		compute();
		applyInitPos();
		applyFinalPos();
	}

	/**
	 * Apply an hint until the actual map is equal to the solution
	 */
	public void applyAll() {
		while (!map.equals(solutionMap)) {
			apply();
		}
	}

	public static class GlobalPos {
		boolean modifier;
		private Pos mapPos;
		private Integer containerIndex;

		public GlobalPos(Pos mapPos, boolean modifier, Integer containerIndex) {
			this.mapPos = mapPos;
			this.modifier = modifier;
			this.containerIndex = containerIndex;
		}

		public Pos getMapPos() {
			return mapPos;
		}

		public boolean isModifier() {
			return modifier;
		}

		public Integer getContainerIndex() {
			return containerIndex;
		}

		public Hexagon getHexagon(Map map, Container container) {
			if (getMapPos() != null) {
				return modifier ? map.getHexagon(mapPos).getModifier() : map.getHexagon(mapPos);
			}
			if (getContainerIndex() != null)
				return container.getHexagon(containerIndex);
			return null;
		}
	}
}
