package be.ac.umons.gui;

import be.ac.umons.HexaMaze;
import be.ac.umons.controller.AutomaticController;
import be.ac.umons.game.*;
import be.ac.umons.model.EditorMap;
import be.ac.umons.model.hexagon.Hexagon;
import be.ac.umons.model.levelChecker.LevelChecker;
import be.ac.umons.util.BackgroundDrawable;
import be.ac.umons.util.Pos;
import be.ac.umons.view.LeftBar;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import javax.naming.InvalidNameException;
import java.io.InvalidObjectException;
import java.util.ArrayList;

/**
 * Screen where the user can use the editor
 */
public class EditorScreen extends GameScreen<Editor> {

	TextButton updateSize;
	TextButton copyLevel;
	private TextField width;
	private TextField height;
	private TextField time;
	private TextField name;
	private SelectBox<Difficulty> difficultySelectBox;
	private SelectBox<GameLevel> gameLevelSelectBox;

	/**
	 * Constructor
	 * @param libGdxGame			Game (LibGDX) used to switch between screens
	 * @param editor				Editor to be shown
	 */
	public EditorScreen(com.badlogic.gdx.Game libGdxGame, Editor editor) {
		super(libGdxGame, editor);
		this.game.getBotEditorRenderer().setScreenCamera(stage.getCamera());
	}

	@Override
	protected void initGameTable() {
		gameTable = new Table();
		gameTable.add(game.getRenderer()).fill().height(HexaMaze.HEIGHT * 3 / 5f);
		gameTable.row();
		gameTable.add(game.getBotRenderer()).fill().height(HexaMaze.HEIGHT * 1 / 5f);
		gameTable.row();
		gameTable.add(game.getBotEditorRenderer()).fill().expand();
		game.getRenderer().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.getController().processInput(x, y, game.getRenderer(), false);
			}
		});
		game.getBotRenderer().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.getController().processInput(x, y, game.getBotRenderer(), true);
			}
		});
		Texture text = new Texture("colors/dark_gray.png");
		gameTable.setBackground(new BackgroundDrawable(text));

	}

	/**
	 * Initializes the elements to be contained in the left bar
	 */
	private void initLeftElements() {
		name = new TextField("", HexaMaze.skin);
		name.setAlignment(Align.center);
		time = new TextField("", HexaMaze.skin);
		time.setAlignment(Align.center);
		width = new TextField("", HexaMaze.skin);
		width.setAlignment(Align.center);
		height = new TextField("", HexaMaze.skin);
		height.setAlignment(Align.center);
		width.setText(String.valueOf(game.getEditorLevel().getWidth()));
		height.setText(String.valueOf(game.getEditorLevel().getHeight()));
		difficultySelectBox = HexaMaze.widgetFactory.createSelectBox();
		difficultySelectBox.setAlignment(Align.center);
		difficultySelectBox.setItems(Difficulty.values());
		gameLevelSelectBox = HexaMaze.widgetFactory.createSelectBox();
		ArrayList<GameLevel> levels = GameLevel.createLevelArray();
		Array<GameLevel> levelsGood = new Array<>();
		for (int i = 0; i < levels.size(); i++) {
			levelsGood.add(levels.get(i));
		}
		gameLevelSelectBox.setItems(levelsGood);
		updateSize = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("updateSize"));
		copyLevel = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("copyLevel"));

	}

	/**
	 * Initializes the listeners to be attached to elements in the left bar
	 */
	private void initLeftListeners() {
		updateSize.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				checkAndUpdateSize();
			}
		});
		copyLevel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				copyLevel();

			}
		});
	}

	@Override
	protected void initLeft() {
		initLeftElements();
		initLeftListeners();
		leftBar = new LeftBar();
		leftBar.addActorContainer(HexaMaze.bundle.format("levelName"), name);
		leftBar.addActorContainer(HexaMaze.bundle.format("limitTime"), time);
		leftBar.addActorContainer(HexaMaze.bundle.format("difficulty"), difficultySelectBox);
		leftBar.addActorContainer(HexaMaze.bundle.format("levels"), gameLevelSelectBox);
		leftBar.addActorWithoutLabel(copyLevel);
		leftBar.addActorContainer(HexaMaze.bundle.format("width"), width);
		leftBar.addActorContainer(HexaMaze.bundle.format("height"), height);
		leftBar.addActorWithoutLabel(updateSize);


		leftBar.addButton(HexaMaze.bundle.format("fill"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.getMap().fill();
			}
		});
		leftBar.addButton(HexaMaze.bundle.format("tryLevel"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				checkAndTryLevel();
			}
		});
		if(HexaMaze.settings.isExtensionActive(Extension.LEVEL_CHECKER)) {
			leftBar.addButton(HexaMaze.bundle.format("showSolutions"), new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					showSolutions();
				}
			});
		}
		leftBar.addButton(HexaMaze.bundle.format("saveQuit"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				saveAnqQuit();
			}
		});
		leftBar.addButton(HexaMaze.bundle.format("quit"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				askAndQuit();
			}
		});


	}

	/**
	 * Checks if the input data are valid and allows the user to try their level
	 */
	private void checkAndTryLevel() {
		int timer = checksData(time.getText(), name.getText());
		if (timer > -1) {
			game.tryGame(name.getText(),difficultySelectBox.getSelected(),timer,gameLibGDX,this);
		}
	}

	/**
	 * Checks if the input data are valid
	 * @param _time					Level limit time
	 * @param _name					Level Name
	 * @return						True if the input data are correct
	 */
	private int checksData(String _time, String _name) {
		try {
			if (!((EditorMap)game.getMap()).checkLevel(game.getContainer())) {
				throw new InvalidObjectException("Invalid Level");
			}
			int min = Integer.valueOf(_time.split(":")[0]);
			int sec = Integer.valueOf(_time.split(":")[1]);
			if (min + sec <= 0) {
				throw new NumberFormatException();
			}

			if (_name == null || _name.length() == 0) {
				throw new InvalidNameException();
			}
			return min * 60 + sec;

		} catch (NumberFormatException | ArrayIndexOutOfBoundsException d) {
			HexaMaze.widgetFactory.createDialog(HexaMaze.bundle.format("validTime")).show(stage);
		} catch (InvalidNameException e) {
			HexaMaze.widgetFactory.createDialog(HexaMaze.bundle.format("validName")).show(stage);
		} catch (InvalidObjectException e) {
			HexaMaze.widgetFactory.createDialog(HexaMaze.bundle.format("validLevel")).show(stage);
		}
		return -1;

	}

	/**
	 * Copies the selected existing level
	 */
	private void copyLevel() {
		GameLevel selected = gameLevelSelectBox.getSelected();
		game.loadLevel(selected, stage.getCamera());
		width.setText(String.valueOf(game.getMap().getWidth()));
		height.setText(String.valueOf(game.getMap().getHeight()));
		gameTable.getCells().get(0).clearActor();
		gameTable.getCells().get(0).setActor(game.getRenderer());

	}

	/**
	 * Checks the size introduced by the user and updates the map
	 */
	private void checkAndUpdateSize() {
		try {
			int newWidth = Integer.valueOf(width.getText());
			int newHeight = Integer.valueOf(height.getText());
			if (newHeight <= 0 || newWidth <= 0)
				throw new NumberFormatException();
			game.changeSize(newHeight, newWidth, stage.getCamera());
			gameTable.getCells().get(0).clearActor();
			gameTable.getCells().get(0).setActor(game.getRenderer());
			game.getRenderer().addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.getController().processInput(x, y, game.getRenderer(), false);
				}
			});
		} catch (NumberFormatException e) {
			HexaMaze.widgetFactory.createDialog(HexaMaze.bundle.format("validSize")).show(stage);
		}
	}

	/**
	 * Saves the level and returns to the main menu
	 */
	private void saveAnqQuit() {
		int timer = checksData(time.getText(), name.getText());
		if (timer > -1) {
			game.saveFiles(name.getText(), difficultySelectBox.getSelected(), timer);
			gameLibGDX.setScreen(new MainMenuScreen(gameLibGDX));
		}
	}

	/**
	 * Returns to the main menu after asking for a confirmation
	 */
	private void askAndQuit() {
		new Dialog("", HexaMaze.skin, "dialog") {
			protected void result(Object object) {
				if ((Boolean) object)
					gameLibGDX.setScreen(new MainMenuScreen(gameLibGDX));
			}
		}.text(HexaMaze.bundle.format("leaveMsg")).button(HexaMaze.bundle.format("yes"), true).button(HexaMaze.bundle.format("no"), false).key(Input.Keys.ENTER, true)
				.key(Input.Keys.ESCAPE, false).show(stage);
	}

	/**
	 * Shows to user the solutions for his level
	 */
	private void showSolutions() {
		int timer = checksData(time.getText(), name.getText());
		if (timer > -1) {
			game.saveFiles(name.getText(), difficultySelectBox.getSelected(), timer, true);
			GameLevel gameLevel = new GameLevel("tempLevel.tmx", "tempLevel.xml", HexaMaze.bundle.format("editorTest"), Difficulty.EASY);
			gameLibGDX.setScreen(new AutomaticGameScreen<>(gameLibGDX, new AutomaticGame(gameLevel), AutomaticController.Mode.MANUAL, this, stage));
		}
	}


}
