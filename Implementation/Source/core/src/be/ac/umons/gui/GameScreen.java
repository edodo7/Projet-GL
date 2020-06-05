package be.ac.umons.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

import be.ac.umons.HexaMaze;
import be.ac.umons.view.LeftBar;

/**
 * Screen model used to show/play a game
 * @param <T>			Game to be shown/played
 */
public abstract class GameScreen<T extends be.ac.umons.game.Game> extends ScreenAdapter {
	protected Game gameLibGDX;
	protected T game;
	protected Stage stage;
	protected Table table;
	protected Table gameTable;
	protected LeftBar leftBar;

	/**
	 * Constructor
	 * @param gameLibGDX		Game (LibGDX) used to switch between screens
	 * @param game				Game to be shown
	 */
	public GameScreen(Game gameLibGDX, T game) {
		this.gameLibGDX = gameLibGDX;
		this.game = game;
		stage = new Stage();
		stage.setViewport(new FitViewport(HexaMaze.WIDTH, HexaMaze.HEIGHT));
		stage.getCamera().translate(HexaMaze.WIDTH / 2, HexaMaze.HEIGHT / 2, 0);
		stage.getCamera().update();
		this.game.getRenderer().setScreenCamera(stage.getCamera());
		this.game.getBotRenderer().setScreenCamera(stage.getCamera());
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.top().setFillParent(true);

		initGameTable();
		initLeft();

		table.add(leftBar).fill().width(HexaMaze.WIDTH / 3);
		table.add(gameTable).fill().expand();
		stage.addActor(table);
	}

	/**
	 * Initializes the needed instances of MapRenderer
	 */
	protected abstract void initGameTable();

	/**
	 * Initializes the left bar elements
	 */
	protected abstract void initLeft();

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
		stage.getCamera().update();
	}

	@Override
	public void dispose() {

	}
}
