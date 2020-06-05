package be.ac.umons.gui;

import be.ac.umons.HexaMaze;
import be.ac.umons.controller.AutomaticController;
import be.ac.umons.game.AutomaticGame;
import be.ac.umons.game.Editor;
import be.ac.umons.game.Extension;
import be.ac.umons.game.GameLevel;
import be.ac.umons.util.BackgroundActor;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Screen used for the Main Menu
 */
public class MainMenuScreen implements Screen {

	private Game libGDXGame;
	private Stage stage;
	private Table table;

	/**
	 * Constructor
	 * @param libGDXGame		Game (LibGDX) used to switch between screens
	 */
	public MainMenuScreen(Game libGDXGame) {
		this.libGDXGame = libGDXGame;
	}

	/**
	 * Creates a TextButton from a string using an uniformed style
	 * @param name				Text to be used in the TextButton
	 * @return					A TextButton
	 */
	private TextButton getTextButton(String name) {
		TextButton textButton = new TextButton(name, HexaMaze.skin, "text");
		textButton.getLabel().setFontScale(0.5f);
		return textButton;
	}

	/**
	 * Creates an TextButton and attaches a listener to it
	 * @param name				Text to be used in the TextButton
	 * @param listener			Listener to be attached to the TextButton
	 */
	private void addButton(String name, ChangeListener listener) {
		TextButton textButton = getTextButton(name);
		textButton.addListener(listener);
		table.add(textButton).space(5);
		table.row();
	}

	@Override
	public void render(float delta) {
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		BackgroundActor bg = new BackgroundActor(new Texture("img/MainBackground.png"));
		stage = new Stage(new ExtendViewport(HexaMaze.WIDTH, HexaMaze.HEIGHT));
		Gdx.input.setInputProcessor(stage);
		table = new Table().pad(10);
		table.setFillParent(true);
		table.top();

		Texture title = new Texture("img/MainTitle.png");
		TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(title, title.getWidth(), title.getHeight()));
		table.add(new ImageButton(drawable)).fill().padBottom(10);
		table.row();
		// Add play button
		addButton(HexaMaze.bundle.format("play"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				libGDXGame.setScreen(new LevelSelectionScreen(libGDXGame));
			}
		});
		if (HexaMaze.settings.isExtensionActive(Extension.LEVEL_CHECKER)) {
			// Add demonstration mode button
			addButton(HexaMaze.bundle.format("demonstrationMode"), new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					libGDXGame.setScreen(new AutomaticGameScreen<>(libGDXGame,
							new AutomaticGame(GameLevel.createLevelArray().get(0)), AutomaticController.Mode.MANUAL, MainMenuScreen.this, stage));
				}
			});
		}
		if (HexaMaze.settings.isExtensionActive(Extension.EDITOR)) {
			// Add editor button
			addButton(HexaMaze.bundle.format("editor"), new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					libGDXGame.setScreen(new EditorScreen(libGDXGame, new Editor()));
				}
			});
		}
		// Add settings button
		addButton(HexaMaze.bundle.format("settings"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				libGDXGame.setScreen(new SettingScreen(libGDXGame));
			}
		});
		if(HexaMaze.settings.isExtensionActive(Extension.USERS)) {
			addButton(HexaMaze.bundle.format("hallOfFame"), new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					libGDXGame.setScreen(new HallOfFame(libGDXGame, HexaMaze.bundle.format("hallOfFame")));
				}
			});
		}

		// Add exit button
		addButton(HexaMaze.bundle.format("exit"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		stage.addActor(bg);
		stage.addActor(table);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}