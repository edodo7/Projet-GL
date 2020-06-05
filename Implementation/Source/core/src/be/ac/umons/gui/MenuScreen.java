package be.ac.umons.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import be.ac.umons.HexaMaze;

/**
 *Screen model used for the menus
 */
public class MenuScreen extends ScreenAdapter {
	private final int barHeight = 50;
	protected Game game;
	protected Stage stage;
	protected Table table;
	protected ImageButton backButton;

	/**
	 * Constructor
	 * @param game				Game (LibGDX) used to switch between screens
	 * @param title				Title of the menu screen
	 */
	public MenuScreen(Game game, String title) {
		//INIT
		this.game = game;
		stage = new Stage();
		stage.setViewport(new FitViewport(HexaMaze.WIDTH, HexaMaze.HEIGHT));
		stage.getCamera().update();
		Gdx.input.setInputProcessor(stage);
		table = new Table();
		table.setFillParent(true);
		table.top();

		//INIT BACK BUTTON
		Texture button = new Texture("img/BackButton.png");
		TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(button, button.getWidth(), button.getHeight()));
		backButton = new ImageButton(drawable);


		//INIT TITLE
		Label label = HexaMaze.widgetFactory.createTitleLabel(title);

		//ADDS TO TABLE
		Table topTable = new Table();
		topTable.add(backButton).align(Align.left).height(barHeight).width(barHeight).expandX();
		topTable.add(label).align(Align.left).width(stage.getWidth() - barHeight).height(barHeight);
		table.add(topTable);
		table.row();

		//ADDS TO STAGE
		stage.addActor(table);

		//BG
		Texture color = new Texture("colors/gray.png");
		TextureRegion region = new TextureRegion(color, HexaMaze.WIDTH, HexaMaze.HEIGHT);
		TextureRegionDrawable bgDrawable = new TextureRegionDrawable(region);
		table.setBackground(bgDrawable);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		HexaMaze.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		HexaMaze.shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
		HexaMaze.shapeRenderer.end();
		stage.act();
		stage.draw();
		HexaMaze.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		HexaMaze.shapeRenderer.setColor(Color.BLACK);
		HexaMaze.shapeRenderer.line(0, HexaMaze.HEIGHT - barHeight, HexaMaze.WIDTH, HexaMaze.HEIGHT - barHeight);
		HexaMaze.shapeRenderer.end();
	}

	public Stage getStage() {
		return stage;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {

	}
}
