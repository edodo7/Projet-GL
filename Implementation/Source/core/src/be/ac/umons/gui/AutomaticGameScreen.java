package be.ac.umons.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import be.ac.umons.HexaMaze;
import be.ac.umons.controller.AutomaticController;
import be.ac.umons.game.AutomaticGame;
import be.ac.umons.game.GameLevel;
import be.ac.umons.game.Level;
import be.ac.umons.util.BackgroundDrawable;
import be.ac.umons.view.LeftBar;

/**
 * Screen used to show an automatic game
 * @param <T>				Game to be shown
 */
public class AutomaticGameScreen<T extends AutomaticGame> extends GameScreen<T> implements AutomaticController.EndLevelListener {

	private ProgressBar progressBar;
	private TextButton modeButton;
	private TextButton restartButton;
	private TextButton nextSolutionButton;
	private TextButton previousSolutionButton;
	private Screen backScreen;
	private Stage backStage;

	/**
	 * Constructor
	 * @param libGDXGame			Game (LibGDX) used to switch between screens
	 * @param game					AutomaticGame to be shown
	 * @param mode					The mode of the AutomaticGame
	 * @param backScreen			The screen used after exiting this screen
	 * @param backStage             The stage of which the input will be processed after exiting this screen
	 */
	public AutomaticGameScreen(Game libGDXGame, T game, AutomaticController.Mode mode, Screen backScreen, Stage backStage) {
		super(libGDXGame, game);
		this.backScreen = backScreen;
		this.backStage = backStage;
		game.getController().setMode(mode);
	}

	@Override
	public void show() {
		super.show();
		game.getController().addEndLevelListener(this);
	}

	@Override
	public void hide() {
		super.hide();
		game.getController().removeEndLevelListener(this);
	}

	@Override
	public void initGameTable() {
		gameTable = new Table();
		gameTable.add(game.getRenderer()).fill().height(HexaMaze.HEIGHT * 4 / 5f);
		gameTable.row();
		gameTable.add(game.getBotRenderer()).fill().expand();
		Texture text = new Texture("colors/dark_gray.png");
		gameTable.setBackground(new BackgroundDrawable(text));
	}

	@Override
	public void initLeft() {
		leftBar = new LeftBar();

		leftBar.addLabelContainer(HexaMaze.bundle.format("level"), game.getLevel().getName());
		leftBar.addLabelContainer(HexaMaze.bundle.format("moves"), "");
		leftBar.addLabelContainer(HexaMaze.bundle.format("solution"), "");
		modeButton = leftBar.addButton("", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (game.getController().getMode() == AutomaticController.Mode.AUTOMATIC) {
					game.getController().setMode(AutomaticController.Mode.MANUAL);
				} else {
					game.getController().setMode(AutomaticController.Mode.AUTOMATIC);
					game.getController().showSolution();
				}
			}
		});
		restartButton = leftBar.addButton(HexaMaze.bundle.format("restart"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.getController().showSolution();
			}
		});
		nextSolutionButton = leftBar.addButton(HexaMaze.bundle.format("nextSolution"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.getController().nextSolution();
			}
		});
		previousSolutionButton = leftBar.addButton(HexaMaze.bundle.format("previousSolution"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.getController().previousSolution();
			}
		});
		TextButton nextLevelButton = leftBar.addButton(HexaMaze.bundle.format("nextLevel"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				quit();
				setNextLevel();
			}
		});
		nextLevelButton.setDisabled(game.getLevel().getNextLevel() == null);
		TextButton previousLevelButton = leftBar.addButton(HexaMaze.bundle.format("previousLevel"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				quit();
				setPreviousLevel();
			}
		});
		previousLevelButton.setDisabled(game.getLevel().getPreviousLevel() == null);
		leftBar.addButton(HexaMaze.bundle.format("quit"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				quit();
				Gdx.input.setInputProcessor(backStage);
				gameLibGDX.setScreen(backScreen);
			}
		});
		progressBar = new ProgressBar(0.0f, 1.0f, 0.001f, false, HexaMaze.skin);
		leftBar.addActorAtBottom(progressBar);
		stage.addActor(leftBar);
		updateLeftBar();
	}

	/**
	 * To call when quitting the current game screen
	 */
	private void quit() {
		game.getController().stopLoading();
	}

	/**
	 * Switches to the next level
	 */
	private void setNextLevel() {
		changeLevel(game.getLevel().getNextLevel());
	}

	/**
	 * Switches to the previous level
	 */
	private void setPreviousLevel() {
		changeLevel(game.getLevel().getPreviousLevel());
	}

	/**
	 * Switch to another level
	 * @param level the level to switch to
	 */
	private void changeLevel(GameLevel level) {
		if (level != null)
			gameLibGDX.setScreen(new AutomaticGameScreen<>(gameLibGDX,
					new AutomaticGame(level), game.getController().getMode(), backScreen, backStage));
	}

	/**
	 * Switches to the next level when a level is finished
	 */
	public void onEndLevel() {
		setNextLevel();
	}

	/**
	 * Updates the data shown in the left bar
	 */
	private void updateLeftBar() {
		boolean loading = game.getState() == AutomaticGame.State.LOADING;
		boolean manualMode = game.getController().getMode() == AutomaticController.Mode.MANUAL;
		modeButton.setText(manualMode ? HexaMaze.bundle.format("automaticMode") : HexaMaze.bundle.format("manualMode"));
		restartButton.setDisabled(loading);
		nextSolutionButton.setDisabled(loading);
		previousSolutionButton.setDisabled(loading);
		leftBar.updateLabel(HexaMaze.bundle.format("solution"),
				String.format("%d / %d", game.getController().getSolutionNumber(), game.getController().getFoundSolutions().size()));
		leftBar.updateLabel(HexaMaze.bundle.format("moves"), String.valueOf(game.getMoves()));
		progressBar.setValue((float) game.getController().getLoading());
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		updateLeftBar();
	}
}
