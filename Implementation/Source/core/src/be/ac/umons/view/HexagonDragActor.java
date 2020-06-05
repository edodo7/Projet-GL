package be.ac.umons.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

import be.ac.umons.HexaMaze;
import be.ac.umons.util.Tile;

public class HexagonDragActor extends Actor {

	private Tile[] tiles;
	private Sprite sp;
	private float width;
	private float height;
	private TiledMapTileSet tileset;
	private Camera cam;

	public HexagonDragActor(Tile[] tiles, float width, float height, TiledMapTileSet tileset, Camera cam) {
		this.tileset = tileset;
		this.tiles = tiles;
		this.width = width;
		this.height = height;
		this.cam = cam;
		sp = new Sprite();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		HexaMaze.spriteBatch.begin();
		HexaMaze.spriteBatch.setProjectionMatrix(cam.combined);
		Vector3 camPos = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		sp.setPosition(camPos.x, camPos.y);
		sp.setSize(width, height);
		sp.setOrigin(width / 2f, height / 2f);
		for (Tile tile : tiles) {
			sp.setRegion(tileset.getTile(tile.getId()).getTextureRegion());
			sp.setRotation(-60 * tile.getEdge().toInt());
			sp.draw(HexaMaze.spriteBatch);
		}
		HexaMaze.spriteBatch.end();
	}
}
