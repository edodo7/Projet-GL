package be.ac.umons.game;

import be.ac.umons.controller.Controller;
import be.ac.umons.model.Container;
import be.ac.umons.model.Map;
import be.ac.umons.model.xml.XMLGameLoader;
import be.ac.umons.view.BotBarRenderer;
import be.ac.umons.view.GameMapRender;
import be.ac.umons.view.MapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.io.File;

/**
 * Class representing a game started by the user.
 * @param <T>   the type of controller used by the game
 */
public abstract class Game<T extends Controller> {

	protected Map map;
	protected Container container;
	protected GameLevel level;

	protected MapRenderer renderer;
	protected BotBarRenderer botRenderer;
	protected T controller;

	/**
	 * Constructor
	 * @param level				Level to be played
	 */
	public Game(Level level) {
		loadLevel(level);
	}

	/**
	 * Initializes the attributes with the given Level
	 * @param _level		Level used to initialize the attributes
	 */
	public void loadLevel(Level _level) {
		level = (GameLevel) _level;
		String folderToOpen = "levels";
		XMLGameLoader xmlGameLoader = new XMLGameLoader(new File(folderToOpen+File.separator+level.getXml()));
		map = xmlGameLoader.getGameData().getMap();
		container = xmlGameLoader.getGameData().getContainer();
		renderer = new GameMapRender(new TmxMapLoader().load("levels" + File.separator + level.getTmx()), this);
		botRenderer = new BotBarRenderer(new TmxMapLoader().load("levels" + File.separator + "newBotBar.tmx"), this, container);
	}

	/**
	 * Returns the Map attribute
	 * @return				A Map Object
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Replaces the Map attribute
	 * @param map			A Map Object
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * Returns the Container attribute
	 * @return				A Container Object
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * Replaces the Container attribute
	 * @param container			A Container Object
	 */
	public void setContainer(Container container) {
		this.container = container;
	}

	/**
	 * Returns the MapRenderer attribute
	 * @return					A MapRenderer Object
	 */
	public MapRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Returns the BotBarRenderer attribute
	 * @return					A BotBarRenderer Object
	 */
	public BotBarRenderer getBotRenderer() {
		return botRenderer;
	}

	/**
	 * Returns the Controller attribute
	 * @return					A Controller Object
	 */
	public T getController() {
		return controller;
	}

	/**
	 * Returns the GameLevel attribute
	 * @return					A GameLevel Object
	 */
	public GameLevel getLevel() {
		return level;
	}
}