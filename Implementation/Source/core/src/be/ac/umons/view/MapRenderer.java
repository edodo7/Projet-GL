package be.ac.umons.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;

import be.ac.umons.HexaMaze;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.util.Pos;


public abstract class MapRenderer extends Actor {


	protected OrthographicCamera camera;
	protected TiledMap map;
	protected int mapXCells;
	protected int mapYCells;
	protected float mapWidth;
	protected float mapHeight;
	protected float unitWidth;
	protected float unitHeight;
	protected float actorWidth;
	protected float actorHeight;
	protected float actorX;
	protected float actorY;
	protected MapProperties prop;
	protected boolean menu = false;
	protected Pos currentMenu;
	protected Rectangle[] menus;
	protected Texture[] icons;
	protected ArrayList<Integer> enabledIcons;
	protected float ratioX;
	protected float ratioY;
	protected Camera screenCamera;
	//DEBUG
	private float origWidth = 0;
	private float origHeight = 0;
	private Vector3 translate;
	private boolean goodSize = false;
	private boolean debug = false;


	public MapRenderer(TiledMap _map) {
		map = _map;
		setUpICons();
		prop = map.getProperties();
		mapXCells = prop.get("width", Integer.class);
		mapYCells = prop.get("height", Integer.class);
		ratioX = 1;
		ratioY = 1;

		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * Chooses which icons should be enable in the actions menu for a certain hexagon
	 * @param hex				Hexagon where the actions menu will be shown
	 */
	public abstract void setEnabledIcons(Hexagon hex);

	/**
	 * Initializes the array containing the textures for actions menu icons
	 */
	public void setUpICons() {
		icons = new Texture[11];
		icons[0] = new Texture("img/remove.png");
		icons[1] = new Texture("img/turn-left.png");
		icons[2] = new Texture("img/turn-right.png");
		icons[3] = new Texture("img/move_locked.png");
		icons[4] = new Texture("img/turn_locked.png");
		icons[5] = new Texture("img/turn_red.png");
		icons[6] = new Texture("img/turn_green.png");
		icons[7] = new Texture("img/turn_blue.png");
		icons[8] = new Texture("img/turn_color.png");
		icons[9] = new Texture("img/apply_constraint.png");
		icons[10] = new Texture("img/add_remove_way.png");
	}
	/**
	 * Hides the actions menu
	 */
	public void hideMenu() {
		menu = false;
	}

	/**
	 * Returns true if the actions menu is shown
	 * @return 				True if the actions menu is shown
	 */
	public boolean isMenuShown() {
		return menu;
	}

	/**
	 * Checks if the (x,y) coordinates correspond to an actions menu icon
	 * @param x 			X component
	 * @param y 			Y component
	 * @return 				A code corresponding to an action.
	 */
	public int checkMenuIcons(float x, float y) {
		Vector3 projPos = screenCamera.project(new Vector3(x, y, 0));
		Vector3 pos3 = camera.unproject(projPos);
		Vector2 pos = new Vector2(pos3.x, pos3.y);
		for (int i = 0; i < enabledIcons.size(); i++) {
			if (menus[i].contains(pos)) {
				return enabledIcons.get(i);
			}
		}
		return -1;
	}

	/**
	 * Shows the actions menu in pos position
	 * @param pos 			Position where the actions menu will be shown
	 */
	public abstract void setMenu(Pos pos);

	/**
	 * Initializes the size and position of this actor
	 */
	public void initConfig() {
		setGoodSize(getX(), getY(), getWidth(), getHeight());
		camera = new OrthographicCamera();
		initCamera();
		setGoodPosition();
		goodSize = true;
	}


	/**
	 * Gives this actor the camera of the instance containing it
	 * @param cam 			Camera of the instance containing it
	 */
	public void setScreenCamera(Camera cam) {
		screenCamera = cam;
	}

	/**
	 * Initializes the camera
	 */
	public void initCamera() {
		float mapWidthCam = (mapWidth / actorWidth) * HexaMaze.WIDTH;
		float mapHeightCam = (mapHeight / actorHeight) * HexaMaze.HEIGHT;
		camera.setToOrtho(false, mapWidthCam, mapHeightCam);
		camera.update();
	}

	/**
	 * Sets the good size for this actor
	 * @param _x      		X component of the new position
	 * @param _y      		Y component of the new position
	 * @param _width  		New width
	 * @param _height 		New height
	 */
	public void setGoodSize(float _x, float _y, float _width, float _height) {
		actorWidth = _width;
		actorHeight = _height;
		actorX = _x;
		actorY = _y;
		if (origWidth == 0 && origHeight == 0) {
			origWidth = actorWidth;
			origHeight = actorHeight;
		}
		// Case when x or y goes beyond the window (So nice LibGDX !)
		if (actorX < 0)
			actorX = 0;
		else if (actorX > HexaMaze.WIDTH)
			actorX = HexaMaze.WIDTH;
		if (actorY < 0)
			actorY = 0;
		else if (actorY > HexaMaze.HEIGHT)
			actorY = HexaMaze.HEIGHT;
		//showSizes();
	}

	/**
	 * Searches and sets the good position for this actor
	 */
	public void setGoodPosition() {
		Vector3 actorPoint = camera.unproject(screenCamera.project(new Vector3(actorX, actorY, 0)));
		Vector3 origin = camera.unproject(screenCamera.project(new Vector3(0, 0, 0)));
		Vector3 actorMinusOrigin = new Vector3(actorPoint.x - origin.x, actorPoint.y - origin.y, 0);
		translate = new Vector3(-1 * Math.abs(actorMinusOrigin.x), -1 * Math.abs(actorMinusOrigin.y), 0);
		camera.translate(translate);
		camera.update();
		float gapX = (camera.unproject(screenCamera.project(new Vector3(actorX + actorWidth, 0, 0))).x - mapWidth * ratioX) / 2;
		float gapY = (camera.unproject(screenCamera.project(new Vector3(0, HexaMaze.HEIGHT - (actorY + actorHeight), 0))).y - mapHeight * ratioY) / 2;
		camera.translate(-gapX, -gapY);
		camera.update();
		//showPosition();
	}

	/**
	 * Returns the position of the clicked cell
	 * @param mousePos 		Clicked position
	 * @return 				The coordinates of the clicked cell or null if there's any cell in mousePos
	 */
	public abstract Pos getClickedCell(Vector3 mousePos);

	/**
	 * Draws the tilemap and the objects
	 */
	public abstract void drawMap();

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		// goodSize is used to avoid drawing the tiledMap while the actor does not have its size yet
		if (getWidth() != 0 && getHeight() != 0 && !goodSize) {
			initConfig();
		}
		if (goodSize) {
			drawMap();
			if (debug)
				drawDebug();
			if (menu) {
				HexaMaze.spriteBatch.begin();
				HexaMaze.spriteBatch.setProjectionMatrix(camera.combined);
				for (int i = 0; i < enabledIcons.size(); i++) {
					HexaMaze.spriteBatch.draw(icons[enabledIcons.get(i)], menus[i].getX(), menus[i].getY(), menus[i].getWidth(), menus[i].getHeight());
				}
				HexaMaze.spriteBatch.end();
			}

		}

		batch.begin();
	}

	/**
	 * Draws lines representing the coordinate system
	 */
	public void drawDebug() {
		HexaMaze.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		HexaMaze.shapeRenderer.setProjectionMatrix(camera.combined);
		HexaMaze.shapeRenderer.setColor(Color.RED);
		for (int i = 0; i < mapWidth; i++) {
			for (int j = 0; j < mapHeight; j++) {
				HexaMaze.shapeRenderer.rect(i * ratioX, j * ratioY, ratioX, ratioY);
			}
		}
		HexaMaze.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
		HexaMaze.shapeRenderer.setColor(Color.GREEN);
		HexaMaze.shapeRenderer.line(0, 0, actorWidth, 0);
		HexaMaze.shapeRenderer.line(0, 0, 0, actorHeight);
		HexaMaze.shapeRenderer.end();
	}

	/**
	 * Prints the size of the actor
	 */
	public void showSizes() {
		// Can be useful
		System.out.println("RESOLUTION : " + HexaMaze.WIDTH + "x" + HexaMaze.HEIGHT);
		System.out.println("WIDTH : " + actorWidth);
		System.out.println("HEIGHT : " + actorHeight);
	}

	/**
	 * Prints the position of the actor
	 */
	public void showPosition() {
		System.out.println("ACTOR POINT : (" + actorX + "," + actorY + ")");
		System.out.println("CAMERA POINT : (" + camera.position.x + "," + camera.position.y + ")");
		Vector3 projected = camera.project(new Vector3(camera.position.x, camera.position.y, 0));
		Vector3 orig = camera.project(new Vector3(0, 0, 0));
		System.out.println("PROJECTED CAMERA POINT : (" + projected.x + "," + projected.y + ")");
		System.out.println("ORIGIN POINT : (" + orig.x + "," + orig.y + ")");
	}

	/**
	 * If enabled is true, draws the coordinates system
	 * @param enabled 		True to draw the coordinates system
	 */
	public void setDebug(boolean enabled) {
		debug = enabled;
	}

	/**
	 * Returns the camera used by this instance
	 * @return				Camera used by this instance
	 */
	public OrthographicCamera getCamera() {
		return camera;
	}

	/**
	 * Returns the absolute X position of this actor
	 * @return 				X component of the position of this actor
	 */
	public float getActorX() {
		return actorX;
	}

	/**
	 * Returns the absolute Y position of this actor
	 * @return 				Y component of the position of this actor
	 */
	public float getActorY() {
		return actorY;
	}

	/**
	 * Returns the TileMap used by the instance
	 * @return 				TileMap used by the instance
	 */
	public TiledMap getMap() {
		return map;
	}

	/**
	 * Gets the width of an hexagon in this actor's camera coordinate system
	 * @return				Width of an hexagon in this actor's camera coordinate system
	 */
	public float getRatioX() {
		return ratioX;
	}

	/**
	 * Gets the height of an hexagon in this actor's camera coordinate system
	 * @return				Height of an hexagon in this actor's camera coordinate system
	 */
	public float getRatioY() {
		return ratioY;
	}
}

