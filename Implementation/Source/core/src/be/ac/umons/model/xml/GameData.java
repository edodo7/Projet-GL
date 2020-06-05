package be.ac.umons.model.xml;


import be.ac.umons.game.GameLevel;
import be.ac.umons.model.Container;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.util.Pos;

/**
 * Data loaded by {@link XMLGameLoader}
 */
public class GameData {
	private GameLevel level;
	private Map map;
	private Container container;

	/**
	 * Constructor
	 * @param level						Level
	 * @param map						Map
	 * @param container					Container
	 */
	public GameData(GameLevel level, Map map, Container container) {
		this.level = level;
		this.map = map;
		this.container = container;
	}

	/**
	 * Returns the level
	 * @return							The Level
	 */
	public GameLevel getLevel() {
		return level;
	}

	/**
	 * Updates the level
	 * @param level						New Level
	 */
	public void setLevel(GameLevel level) {
		this.level = level;
	}

	/**
	 * Returns the Map
	 * @return							The Map
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Updates the Map
	 * @param map						New Map
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * Returns the Container
	 * @return							The Container
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * Updates the Container
	 * @param container						New Container
	 */
	public void setContainer(Container container) {
		this.container = container;
	}

	/**
	 * Checks if the level can be played
	 * @return  whether the level can be played
	 */
	public boolean isValid() {
		return checkExtensions() && checkConstraints();
	}

	private boolean checkExtensions() {
		Pos invalidPos = map.testForEachUsed(pos -> !checkHexagonExtension(map.getHexagon(pos), level) ||
				!checkHexagonExtension(map.getHexagon(pos).getModifier(), level));
		if (invalidPos != null)
			return false;
		boolean invalid = container.getHexagons().stream().anyMatch(hex -> !checkHexagonExtension(hex, level));
		return !invalid;
	}

	/**
	 * Check if the level has the hexagon extension
	 * @param hex   the hexagon
	 * @param level the level
	 * @return      whether the hexagon is valid
	 */
	private boolean checkHexagonExtension(Hexagon hex, GameLevel level) {
		if (hex == null)
			return true;
		return level.isExtensionActive(hex.getExtension());
	}

	/**
	 * Check if the constraints are valid on the initial map
	 * @return  whether the constraints are valid on the initial map
	 */
	private boolean checkConstraints() {
		Pos invalidPos = map.testForEachUsed(pos -> !map.respectConstraint(map.getHexagon(pos), pos));
		return invalidPos == null;
	}
}
