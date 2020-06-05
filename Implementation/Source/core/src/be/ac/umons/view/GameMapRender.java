package be.ac.umons.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.Extension;
import be.ac.umons.game.Game;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.hexagon.Modifier;
import be.ac.umons.util.Color;
import be.ac.umons.util.Pos;
import be.ac.umons.util.Segment;
import be.ac.umons.util.Tile;

/**
 * MapRenderer used to show the main table
 */
public class GameMapRender extends MapRenderer {

	private HexagonalTiledMapRenderer renderer;
	private ArrayList<Pos> modifiers;
	private Game game;
	private String staggerIndex;
	private Sprite sp;
	private Sprite moveLocked;
	private Sprite turnLocked;
	private boolean editor = false;


	public GameMapRender(TiledMap _map, Game _game) {
		this(_map, _game, false);
	}

	public GameMapRender(TiledMap _map, Game _game, boolean _editor) {
		super(_map);
		editor = _editor;
		float unitScale = 1 / 128f;
		mapWidth = mapXCells * (3f / 4f) + (1 / 4f);
		mapHeight = mapYCells + (1 / 2f);
		if (mapWidth * prop.get("tilewidth", Integer.class) > mapHeight * prop.get("tileheight", Integer.class)) {
			ratioY = prop.get("tileheight", Integer.class).floatValue() / prop.get("tilewidth", Integer.class).floatValue();
			unitScale = 1 / 148f;
		} else {
			ratioX = prop.get("tilewidth", Integer.class).floatValue() / prop.get("tileheight", Integer.class).floatValue();

		}
		unitHeight = prop.get("tileheight", Integer.class);
		unitWidth = prop.get("tilewidth", Integer.class) * (3 / 4f);
		renderer = new HexagonalTiledMapRenderer(map, unitScale);
		game = _game;
		staggerIndex = (String) map.getProperties().get("staggerindex");
		sp = new Sprite();
		moveLocked = new Sprite(new Texture("img/move_locked.png"));
		turnLocked = new Sprite(new Texture("img/turn_locked.png"));
	}

	@Override
	public Pos getClickedCell(Vector3 mousePos) {
		Vector3 projectedMousePos = screenCamera.project(mousePos);
		Vector3 unprojectedMousePos = camera.unproject(mousePos);
		mousePos.x = mousePos.x / ratioX;
		mousePos.y = mousePos.y / ratioY;
		mousePos.x *= (4f / 3f);
		Vector2 relPos = new Vector2((mousePos.x) % 2f, mousePos.y % 1f);


		int x = (int) (mousePos.x - relPos.x);
		int y = (int) (mousePos.y - relPos.y);
		if (relPos.x < 1f / 3f) {
			if (relPos.y * 2 < relPos.x * 3)
				y -= 1;
			else if ((1 - relPos.y) * 2 > relPos.x * 3)
				x -= 1;
		} else if (relPos.x < 1f) {
			if (relPos.y < 0.5f)
				y -= 1;
		} else if (relPos.x < 4f / 3f) {
			float negX = 4f / 3f - relPos.x;
			if (relPos.y * 2 < negX * 3)
				y -= 1;
			else if ((1 - relPos.y) * 2 > negX * 3)
				x += 1;
		} else {
			x += 1;
		}

		if (x > mapXCells - 1 || x < 0 || y > mapYCells || y < 0)
			return null;
		return new Pos(x, y);

	}

	@Override
	public void setEnabledIcons(Hexagon hex) {
		enabledIcons = new ArrayList<>();
		if (hex.isMovable())
			enabledIcons.add(0);
		if (hex.isRotatable()) {
			enabledIcons.add(1);
			enabledIcons.add(2);
		}
		if (editor) {
			enabledIcons.add(3);
			enabledIcons.add(4);
			String className = hex.getClass().getSimpleName();
			if (hex.getExtension() == Extension.COLOR && !className.equals("GenericBridge") || hex.getClass().getSimpleName().equals("Source")) {
				enabledIcons.add(5);
				enabledIcons.add(6);
				enabledIcons.add(7);
			}

			if(className.equals("SpecificBridge"))
				enabledIcons.add(8);
			if(className.equals("GenericBridge"))
				enabledIcons.add(10);
			enabledIcons.add(9);
		}
	}

	@Override
	public void setMenu(Pos pos) {
		setEnabledIcons(game.getMap().getHexagon(pos));
		float x = pos.getX() * ratioX * 3 / 4f;
		float y = pos.getY() * ratioY + 1 / 2f;
		if (pos.getX() % 2 == 0)
			y = y + ratioY / 2f;
		currentMenu = pos;
		menus = new Rectangle[icons.length];
		int nbIcons = enabledIcons.size();
		if(nbIcons > 5) {
			float size = (ratioX*4/5) / 5f;
			for (int i = 0; i < nbIcons; i++) {
				if (i < 5)
					menus[i] = new Rectangle(x +(ratioX/10) + (i * size), y, size, size);
				else
					menus[i] = new Rectangle(x +(ratioX/10) + (i % 5f * size), y - size, size, size);

			}
		}
		else{
			float size = (ratioX*4/5) / (float) nbIcons;
			for (int i = 0; i < nbIcons; i++) {
					menus[i] = new Rectangle(x +(ratioX/10) + (i * size), y - ((size / ratioY) / 2f), size, size);
			}
		}
		menu = true;

		//modifiers.add(pos);

	}

	@Override
	public void drawMap() {

		renderer.setView(camera);
		renderer.render();
		//DRAWS BACKGROUND IF NEEDED
		if(editor){
			drawBackgroundTile();
		}
		drawLaser();
		HexaMaze.spriteBatch.begin();
		HexaMaze.spriteBatch.setProjectionMatrix(camera.combined);
		for (int y = 0; y < game.getMap().getHeight(); y++) {
			for (int x = 0; x < game.getMap().getWidth(); x++) {
				Pos pos = new Pos(x,y);
				Hexagon hex = game.getMap().getHexagon(pos);
				float gapY = 0;
				boolean test;
				test = staggerIndex.equals("odd");
				if ((x % 2 == 0) == test)
					gapY = ratioY / 2f;

				setSpriteGoodPosition(sp,pos,gapY);


				//DRAWS THE OBJECT
				if (hex != null && !hex.isEmpty()) {
					Tile[] tiles = hex.getTiles();
					for (Tile tile : tiles) {
						drawTile(tile);
					}
					if (hex.getModifier() != null) {
						Modifier modifier = hex.getModifier();
						tiles = modifier.getModifierTiles();
						for (Tile tile : tiles) {
							drawTile(tile);
						}
					}
					drawStateIcons(pos, gapY, hex);
				}
				//DRAWS THE CONSTRAINT
				else {
					Hexagon constr = game.getMap().getConstraint(pos);
					if (constr != null) {
						float initAlpha = sp.getColor().a;
						sp.setAlpha(0.3f);
						Tile[] constrTiles = constr.getTiles();
						for (Tile tile : constrTiles) {
							drawTile(tile);
						}
						sp.setAlpha(initAlpha);
					}
				}
			}
		}
		HexaMaze.spriteBatch.end();
	}

	/**
	 * Places the sprite at the good position with the good size to draw a tile
	 * @param sprite					Sprite to be drawn
	 * @param pos						Position where the tile should be drawn
	 * @param gapY						Vertical gap
	 */
	public void setSpriteGoodPosition(Sprite sprite, Pos pos, float gapY){
		sprite.setPosition((float) pos.getX() * ratioX * 3 / 4f, ((pos.getY() * ratioY) + gapY));
		sprite.setSize(ratioX, ratioY);
		sprite.setOrigin(ratioX / 2f, ratioY / 2f);
	}

	/**
	 * Draws empty hexagons where needed (only for Editor)
	 */
	public void drawBackgroundTile(){
		HexaMaze.spriteBatch.begin();
		HexaMaze.spriteBatch.setProjectionMatrix(camera.combined);
		for(int y =0; y <game.getMap().getHeight(); y++){
			for(int x = 0; x < game.getMap().getWidth(); x++){
				Pos pos = new Pos(x,y);
				Hexagon hex = game.getMap().getHexagon(pos);
				float gapY = 0;
				boolean test;
				test = staggerIndex.equals("odd");
				if ((x % 2 == 0) == test)
					gapY = ratioY / 2f;

				setSpriteGoodPosition(sp,pos,gapY);
				if (hex != null || game.getMap().getConstraint(pos) != null) {
					TextureRegion texRegion = map.getTileSets().getTileSet(0).getTile(2).getTextureRegion();
					sp.setRegion(texRegion);
					sp.setRotation(0);
					sp.draw(HexaMaze.spriteBatch);
				}
			}
		}
		HexaMaze.spriteBatch.end();

	}




	/**
	 * Draws a tile
	 * @param tile			Tile to be drawn
	 */
	public void drawTile(Tile tile) {
		TextureRegion texRegion = map.getTileSets().getTileSet(0).getTile(tile.getId()).getTextureRegion();
		sp.setRegion(texRegion);
		sp.setRotation(-60 * tile.getEdge().toInt());
		sp.draw(HexaMaze.spriteBatch);
	}

	/**
	 * Draws the state icons of an hexagon
	 * @param pos			Position of the Hexagon
	 * @param gapY			Vertical gap
	 * @param hex			The Hexagon
	 */
	public void drawStateIcons(Pos pos, float gapY, Hexagon hex) {
		float width = ratioX / 4f;
		float height = ratioX / 4f;
		float xCoord = ((float) pos.getX() * ratioX * (3 / 4f)) + (ratioX * 1 / 4f);
		float yCoord = ((pos.getY() * ratioY) + gapY + (ratioY - ratioX * 3 / 4f));
		if (!hex.isMovable()) {
			drawStateIcon(moveLocked, xCoord, yCoord, width, height);
		}
		if (!hex.isRotatable()) {
			drawStateIcon(turnLocked, xCoord + ratioX * 1 / 4f, yCoord, width, height);
		}
	}

	/**
	 * Draws a state icon on the position of an hexagon
	 * @param sprite		Sprite corresponding to the state icon
	 * @param x				X coordinate where the icon will be drawn
	 * @param y				Y coordinate where the icon will be drawn
	 * @param width			Width of the icon
	 * @param height		Height of the icon
	 */
	public void drawStateIcon(Sprite sprite, float x, float y, float width, float height) {
		sprite.setSize(width, height);
		sprite.setPosition(x, y);
		sprite.draw(HexaMaze.spriteBatch);
	}

	/**
	 * Draws the laser given by the model
	 */
	public void drawLaser() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		HexaMaze.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		HexaMaze.shapeRenderer.setProjectionMatrix(camera.combined);
		List<Segment> segments = game.getMap().getSegments();
		boolean odd = false;
		if (staggerIndex.equals("odd"))
			odd = true;
		for (Segment segment : segments) {
			Pos from = segment.getFrom();
			Pos to = segment.getTo();
			float gapY1 = 0;
			float gapY2 = 0;
			if ((from.getX() % 2 == 0) == odd)
				gapY1 = ratioY / 2f;
			if ((to.getX() % 2 == 0) == odd)
				gapY2 = ratioY / 2f;
			com.badlogic.gdx.graphics.Color fromColor = new com.badlogic.gdx.graphics.Color(0, 0, 0, 0);
			com.badlogic.gdx.graphics.Color toColor = new com.badlogic.gdx.graphics.Color(0, 0, 0, 0);
			for (Map.Entry<Color, Boolean> entry : segment.getFromColors().entrySet()) {
				if (entry.getValue()) {
					fromColor.add(entry.getKey().getLibGDXColor());
				}
			}
			for (Map.Entry<Color, Boolean> entry : segment.getToColors().entrySet()) {
				if (entry.getValue()) {
					toColor.add(entry.getKey().getLibGDXColor());
				}
			}
			fromColor.a = segment.getFromIntensity();
			toColor.a = segment.getToIntensity();
			Vector2 p1 = new Vector2(from.getX() * ratioX * (3 / 4f) + (ratioX / 2f), (from.getY() * ratioY) + (ratioY / 2f) + gapY1);
			Vector2 p2 = new Vector2(to.getX() * ratioX * (3 / 4f) + (ratioX / 2f), to.getY() * ratioY + (ratioY / 2f) + gapY2);
			Vector2 middle = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
			HexaMaze.shapeRenderer.rectLine(p1.x, p1.y, middle.x, middle.y, 0.03f, fromColor, fromColor);
			HexaMaze.shapeRenderer.rectLine(middle.x, middle.y, p2.x, p2.y, 0.03f, toColor, toColor);
		}
		HexaMaze.shapeRenderer.end();
	}

	@Override
	public void initCamera() {
		float max = Math.max(mapWidth, mapHeight);
		float mapWidthCam = (max / actorWidth) * HexaMaze.WIDTH;
		float mapHeightCam = (max / actorHeight) * HexaMaze.HEIGHT;
		camera.setToOrtho(false, mapWidthCam*ratioX, mapHeightCam*ratioY);
		camera.update();
	}

	/**
	 * Returns true if this instance is being used by an Editor
	 * @return				True if this instance is being used by an Editor
	 */
	public boolean isEditor() {
		return editor;
	}
}
