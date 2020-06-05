package be.ac.umons.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.io.File;

import be.ac.umons.HexaMaze;
import be.ac.umons.controller.EditorController;
import be.ac.umons.gui.EditorScreen;
import be.ac.umons.gui.TryScreen;
import be.ac.umons.model.Container;
import be.ac.umons.model.EditorContainer;
import be.ac.umons.model.EditorMap;
import be.ac.umons.model.Map;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.levelChecker.LevelChecker;
import be.ac.umons.model.xml.EditorFileHandler;
import be.ac.umons.model.xml.XMLGameLoader;
import be.ac.umons.util.Pos;
import be.ac.umons.view.BotBarRenderer;
import be.ac.umons.view.GameMapRender;

/**
 * Game used in the Editor
 */
public class Editor extends Game {

	private EditorLevel editorLevel;
	private EditorContainer editorContainer;
	private BotBarRenderer botEditorRenderer;

	/**
	 * Constructor
	 */
	public Editor() {
		super(new EditorLevel());
	}

	@Override
	public void loadLevel(Level _level) {
		editorLevel = (EditorLevel) _level;
		map = new EditorMap(editorLevel.getWidth(), editorLevel.getHeight());
		editorContainer = new EditorContainer();
		container = new Container();
		renderer = new GameMapRender(new TmxMapLoader().load("levels" + File.separator + editorLevel.getTmx()), this, true);
		botRenderer = new BotBarRenderer(new TmxMapLoader().load("levels" + File.separator + "newBotBar.tmx"), this, container, true);
		botEditorRenderer = new BotBarRenderer(new TmxMapLoader().load("levels" + File.separator + "EditorBotBar.tmx"), this, editorContainer);
		controller = new EditorController(this);
	}

	/**
	 * Rebuilds the GameMapRender after a change of size
	 * @param cam   the camera to set
	 */
	public void rebuildRenderer(Camera cam) {
		renderer = new GameMapRender(new TmxMapLoader().load("levels" + File.separator + editorLevel.getTmx()), this, true);
		renderer.setScreenCamera(cam);
		controller.update();
	}

	/**
	 * Saves both xml and tmx files
	 * @param levelName				Name of the level
	 * @param difficulty			Difficulty of the level
	 * @param timer					Limit time for he level
	 * @param temp					True if these files are just for a try
	 */
	public void saveFiles(String levelName, Difficulty difficulty, int timer, boolean temp) {
		String file;
		if (!temp) {
			int id = GameLevel.getLastLevelID() + 1;
			file = "g3_" + id;
		} else {
			file = "tempLevel";
		}
		EditorFileHandler.saveTMX((EditorMap) getMap(), file + ".tmx");
		EditorFileHandler.saveXML((EditorMap) getMap(), getContainer(), levelName, file + ".xml", difficulty, timer);
	}

	/**
	 * Saves both xml and tmx files
	 * @param levelName				Name of the level
	 * @param difficulty			Difficulty of the level
	 * @param timer					Limit time for the level
	 */
	public void saveFiles(String levelName, Difficulty difficulty, int timer) {
		saveFiles(levelName, difficulty, timer, false);
	}

	/**
	 * Changes the size of the Map
	 * @param h						New Height
	 * @param w						New Width
	 * @param cam					Camera of the Screen showing the editor
	 */
	public void changeSize(int h, int w, Camera cam) {
		editorLevel.update(w, h);
		rebuildRenderer(cam);
		((EditorMap) map).updateSize(w, h);
	}

	/**
	 * Loads a level
	 * @param selected				Level to be loaded
	 * @param cam					Camera of the Screen showing the editor
	 */
	public void loadLevel(GameLevel selected, Camera cam) {
		XMLGameLoader xmlLoader = new XMLGameLoader(new File("levels" + File.separator + selected.getXml()));
		Map newMap = xmlLoader.getGameData().getMap();
		Container newCont = xmlLoader.getGameData().getContainer();
		changeSize(newMap.getHeight(), newMap.getWidth(), cam);

		getRenderer().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getController().processInput(x, y, getRenderer(), false);
			}
		});
		((EditorMap)getMap()).replaceMap(newMap.getHexagons(), newMap.getSourcesPos(), newMap.getTargetsPos(), newMap.getConstraints());
		getContainer().removeAllHexagons();
		getContainer().addHexagons(newCont.getHexagons());

	}

	/**
	 * Allows the user to try a level
	 * @param levelName				Name of the level
	 * @param difficulty			Difficulty of the level
	 * @param timer					Limit time for the level
	 * @param gameLibGDX			Game (LibGDX Object) used to change the current Screen
	 * @param screen				Screen where the editor is being shown
	 */
	public void tryGame(String levelName, Difficulty difficulty, int timer, com.badlogic.gdx.Game gameLibGDX, EditorScreen screen) {
		saveFiles(levelName, difficulty, timer, true);
		GameLevel gameLevel = new GameLevel("tempLevel.tmx", "tempLevel.xml", HexaMaze.bundle.format("editorTest"), Difficulty.EASY);
		BasicGame basicGame = new BasicGame(gameLevel, Mode.PRACTICE, Mode.ONE_TIME);
		gameLibGDX.setScreen(new TryScreen(gameLibGDX, basicGame, screen));
	}




	/**
	 * Retuns the EditorContainer attribute
	 * @return						The EditorContainer attribute
	 */
	public EditorContainer getEditorContainer() {
		return editorContainer;
	}

	/**
	 * Returns the BotBarRenderer attribute
	 * @return						The BotBarRenderer attribute
	 */
	public BotBarRenderer getBotEditorRenderer() {
		return botEditorRenderer;
	}

	/**
	 * Returns the EditorLevel
	 * @return						The EditorLevel
	 */
	public EditorLevel getEditorLevel() {
		return editorLevel;
	}
}