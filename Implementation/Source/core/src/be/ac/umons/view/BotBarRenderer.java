package be.ac.umons.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.Extension;
import be.ac.umons.game.Game;
import be.ac.umons.model.Container;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.util.Pos;
import be.ac.umons.util.Tile;

/**
 * MapRenderer used to show the bottom table
 */
public class BotBarRenderer extends MapRenderer {

	private OrthogonalTiledMapRenderer renderer;
	private Game game;
	private Sprite sp;
	private boolean editor;
	private Container container;

	public BotBarRenderer(TiledMap _map, Game _game, Container _container) {
		this(_map, _game, _container, false);
	}

	public BotBarRenderer(TiledMap _map, Game _game, Container _container, boolean _editor) {
		super(_map);
		container = _container;
		editor = _editor;
		game = _game;
		ratioY = prop.get("tileheight", Integer.class).floatValue() / prop.get("tilewidth", Integer.class).floatValue();
		float unitScale = 1 / 148f;
		mapWidth = mapXCells;
		mapHeight = mapYCells;
		unitWidth = prop.get("tilewidth", Integer.class);
		unitHeight = prop.get("tileheight", Integer.class);
		renderer = new OrthogonalTiledMapRenderer(map, unitScale);
		sp = new Sprite();
	}

	@Override
	public Pos getClickedCell(Vector3 mousePos) {
		Vector3 projectedMousePos = screenCamera.project(mousePos);
		Vector3 unprojectedMousePos = camera.unproject(mousePos);
		unprojectedMousePos.y = unprojectedMousePos.y / ratioY;
		Pos pos = new Pos((int) unprojectedMousePos.x, (int) unprojectedMousePos.y);
		if (pos.getX() > mapXCells - 1 || pos.getX() < 0 || pos.getY() > mapYCells - 1 || pos.getY() < 0)
			return null;
		return pos;

	}

	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public void setEnabledIcons(Hexagon hex) {
		enabledIcons = new ArrayList<>();
		enabledIcons.add(1);
		enabledIcons.add(2);
		if (editor) {
			String className = hex.getClass().getSimpleName();
			if (hex.getExtension() == Extension.COLOR && !className.equals("GenericBridge")) {
				enabledIcons.add(5);
				enabledIcons.add(6);
				enabledIcons.add(7);
			}
			if(className.equals("SpecificBridge"))
				enabledIcons.add(8);
			if(className.equals("GenericBridge"))
				enabledIcons.add(10);
		}
	}

	@Override
	public void setMenu(Pos pos) {
		setEnabledIcons(container.getHexagon(getIndexFromPos(pos)));
		float x = pos.getX();
		float y = pos.getY() * ratioY;
		currentMenu = pos;
		menus = new Rectangle[icons.length];
		int nbIcons = enabledIcons.size();
		if (nbIcons <= 5) {
			float size = 1 / (float) nbIcons;
			for (int i = 0; i < nbIcons; i++) {
				menus[i] = new Rectangle(x + (i * size), y + 1 / 2f - size / 2f, size, size);
			}
		} else {
			float size = 1 / 5f;
			for (int i = 0; i < nbIcons; i++) {
				if (i < 5)
					menus[i] = new Rectangle(x + (i * size), y + 1 / 2f, size, size);
				else
					menus[i] = new Rectangle(x + (i % 5f * size), y + 1 / 2f - size, size, size);

			}
		}
		menu = true;
	}


	@Override
	public void drawMap() {
		renderer.setView(camera);
		renderer.render();
		HexaMaze.spriteBatch.begin();
		HexaMaze.spriteBatch.setProjectionMatrix(camera.combined);
		int i = 0;
		int l = 1;


		while (i < container.getHexagons().size() && i < 20) {
			Tile[] tiles = container.getHexagons().get(i).getTiles();
			TextureRegion texRegion;
			float y = ratioY;
			if (i >= 10)
				y = 0;
			sp.setPosition((i % 10) * ratioX, y);
			sp.setSize(ratioX, ratioY);
			sp.setOrigin(ratioX / 2f, ratioY / 2f);
			//sp.setAlpha(0.2f);
			for (Tile tile : tiles) {
				texRegion = map.getTileSets().getTileSet(0).getTile(tile.getId()).getTextureRegion();
				sp.setRegion(texRegion);
				sp.setRotation(-60 * tile.getEdge().toInt());
				sp.draw(HexaMaze.spriteBatch);
			}

			i++;
		}
		HexaMaze.spriteBatch.end();
		camera.update();
		drawBorders();
	}

	/**
	 * Draws the borders of this actor
	 */
	public void drawBorders() {
		HexaMaze.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		HexaMaze.shapeRenderer.setProjectionMatrix(camera.combined);
		Vector3 actorPoint = camera.unproject(screenCamera.project(new Vector3(actorX, HexaMaze.HEIGHT - actorY, 0)));
		actorPoint = new Vector3(Math.max(actorPoint.x, 0), Math.max(actorPoint.y, 0), 0);
		Vector3 actorOppPoint = camera.unproject(screenCamera.project(new Vector3(actorX + actorWidth, HexaMaze.HEIGHT - actorY - actorHeight, 0)));
		actorOppPoint = new Vector3(Math.max(actorOppPoint.x, 0), Math.max(actorOppPoint.y, 0), 0);
		HexaMaze.shapeRenderer.setColor(Color.BLACK);
		HexaMaze.shapeRenderer.line(0, 0, actorPoint.x, actorPoint.y);
		HexaMaze.shapeRenderer.line(actorPoint.x, actorPoint.y, actorPoint.x, actorOppPoint.y);
		HexaMaze.shapeRenderer.line(actorPoint.x, actorOppPoint.y, actorOppPoint.x, actorOppPoint.y);
		HexaMaze.shapeRenderer.line(actorOppPoint.x, actorOppPoint.y, actorPoint.x, actorOppPoint.y);
		HexaMaze.shapeRenderer.line(actorPoint.x, actorOppPoint.y, actorPoint.x, actorPoint.y);
		HexaMaze.shapeRenderer.end();
	}

	@Override
	public void setGoodPosition() {
		super.setGoodPosition();
	}

	/**
	 * Converts the position on this actor into the corresponding index on the Container object related to this instance
	 * @param pos			Position
	 * @return				Index on the Container object related to this instance
	 */
	public int getIndexFromPos(Pos pos) {
		return Math.abs(pos.getY() - 1) * 10 + pos.getX();
	}
}
