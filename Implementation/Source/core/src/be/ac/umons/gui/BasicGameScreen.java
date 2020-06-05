package be.ac.umons.gui;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.BasicGame;
import be.ac.umons.game.Extension;
import be.ac.umons.game.Mode;
import be.ac.umons.util.BackgroundDrawable;
import be.ac.umons.util.UserManager;
import be.ac.umons.view.LeftBar;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.File;



/**
 * Screen used to play a game
 * @param <T>			Game to be played
 */
public class BasicGameScreen<T extends BasicGame> extends GameScreen<T> {

	protected boolean finished = false;
	protected LeftBar endLevel;
	protected long startTimer;
	protected long seconds;

	/**
	 * Constructor
	 * @param libGDXGame		Game (LibGDX) used to switch between screens
	 * @param game				BasicGame to be shown
	 */
	public BasicGameScreen(Game libGDXGame, T game) {
		super(libGDXGame, game);
	}

	@Override
	public void initGameTable() {
		gameTable = new Table();
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
		gameTable.add(game.getRenderer()).fill().height(HexaMaze.HEIGHT * 4 / 5f);
		gameTable.row();
		gameTable.add(game.getBotRenderer()).fill().expand();
		Texture text = new Texture("colors/dark_gray.png");
		gameTable.setBackground(new BackgroundDrawable(text));
	}

	@Override
	public void initLeft() {
		startTimer = System.currentTimeMillis();
		leftBar = new LeftBar();
		if(HexaMaze.settings.isExtensionActive(Extension.USERS)) {
			Image avatar;
			try {
				avatar = new Image(new Texture(new FileHandle(new File(UserManager.getUserManager().getCurrentUser().getPathToAvatarImage()))));
			} catch (GdxRuntimeException e) {
				avatar = new Image(new Texture(Gdx.files.internal("defaultUser.png")));
			}
			leftBar.addActorWithoutLabel(avatar, 130);
		}
		String timeToString = String.format("%02d", game.getLimitSeconds() / 60) + ":" + String.format("%02d", (game.getLimitSeconds() % 60));
		leftBar.addLabelContainer(HexaMaze.bundle.format("moves"), "0");
		leftBar.addLabelContainer(HexaMaze.bundle.format("time"), timeToString);

		if (game.getLaserMode() == Mode.ONE_TIME) {
			leftBar.addButton(HexaMaze.bundle.format("sendLaser"), new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					game.getController().sendLaser(BasicGameScreen.this);
				}
			});
		}
		leftBar.addButton(HexaMaze.bundle.format("restart"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameLibGDX.setScreen(new BasicGameScreen<>(gameLibGDX, new BasicGame(game.getLevel(), game.getGameMode(), game.getLaserMode())));
			}
		});
		if (HexaMaze.settings.isExtensionActive(Extension.LEVEL_CHECKER)) {
			leftBar.addButton(HexaMaze.bundle.format("clue"), new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					try {
						game.getController().applyHint();
						checkEnd();
					} catch(IllegalArgumentException e) {
						HexaMaze.widgetFactory.createDialog(HexaMaze.bundle.format("alreadySolution")).show(stage);
					}
				}
			});
			leftBar.addButton(HexaMaze.bundle.format("resolve"), new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					game.getController().resolve();
					checkEnd();
				}
			});
		}
		leftBar.addButton(HexaMaze.bundle.format("quit"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(HexaMaze.settings.isExtensionActive(Extension.USERS) && !UserManager.getUserManager().getCurrentUser().isAnonymous()){
					game.saveState();
				}
				gameLibGDX.setScreen(new LevelSelectionScreen(gameLibGDX));
			}
		});
	}

	/**
	 * Modifies the elements in the left bar to show the result of the game
	 * @param win				True if the user has won
	 */
	public void showEndLeftBar(boolean win) {
		endLevel = new LeftBar();
		endLevel.addLabelContainer(HexaMaze.bundle.format("result"), HexaMaze.bundle.format(win ? "win" : "lose"));
		if (win)
			endLevel.addLabelContainer(HexaMaze.bundle.format("score"), String.valueOf(game.getScore()));
		endLevel.addLabelContainer(HexaMaze.bundle.format("bestScore"), String.valueOf(game.getLevel().getMaxScore()));
		endLevel.addLabelContainer(HexaMaze.bundle.format("level"), game.getLevel().getName());
		String timeToString = String.format("%02d", (game.getLimitSeconds() - game.getRemainingSeconds()) / 60) + ":" + String.format("%02d", ((game.getLimitSeconds() - game.getRemainingSeconds())) % 60);
		endLevel.addLabelContainer(HexaMaze.bundle.format("time"), timeToString);
		endLevel.addLabelContainer(HexaMaze.bundle.format("moves"), String.valueOf(game.getMoves()));
		endLevel.addButton(HexaMaze.bundle.format("restart"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameLibGDX.setScreen(new BasicGameScreen<>(gameLibGDX, new BasicGame(game.getLevel(), game.getGameMode(), game.getLaserMode())));
			}
		});
		if (win) {
			TextButton nextLevelButton = endLevel.addButton(HexaMaze.bundle.format("nextLevel"), new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					gameLibGDX.setScreen(new BasicGameScreen<>(gameLibGDX, new BasicGame(game.getLevel().getNextLevel(), game.getGameMode(), game.getLaserMode())));
				}
			});
			if (game.getLevel().getNextLevel() == null)
				nextLevelButton.setDisabled(true);
		}
		endLevel.addButton(HexaMaze.bundle.format("quit"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameLibGDX.setScreen(new LevelSelectionScreen(gameLibGDX));
			}
		});
		table.getCell(leftBar).setActor(endLevel);
	}

	/**
	 * Checks if the user has won or lost, and if true calls showEndLeftBar()
	 */
	public void checkEnd() {
		boolean win = game.getMap().hasWon();
		boolean lose = game.getMap().hasLost();
		if (!finished) {
			if (game.getGameMode()==Mode.PRACTICE) {
				if (win && !lose) {
					game.stopGame();
					showEndLeftBar(true);
					finished = true;
				}
			} else {
				if (win || lose) {
					game.stopGame();
					showEndLeftBar(!lose);
					finished = true;
				}
			}
		}
	}

	@Override
	public void render(float delta) {
		seconds = ((System.currentTimeMillis() - startTimer) / 1000);
		game.setRemainingSeconds(seconds);
		long minutes = game.getRemainingSeconds() / 60;
		String timeToString = String.format("%02d", minutes) + ":" + String.format("%02d", (game.getRemainingSeconds() % 60));
		leftBar.updateLabel(HexaMaze.bundle.format("time"), timeToString);
		leftBar.updateLabel(HexaMaze.bundle.format("moves"), String.valueOf(game.getMoves()));
		leftBar.act(delta);
		super.render(delta);
		checkEnd();
	}

	/**
	 * Updates th finished attribute which represents if the game has ended
	 * @param finished			True if the game has ended
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

}
