package be.ac.umons.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.BasicGame;
import be.ac.umons.game.Extension;
import be.ac.umons.view.LeftBar;

/**
 * Screen allowing the user to test levels being created in the editor
 */
public class TryScreen extends BasicGameScreen<BasicGame> {

	private EditorScreen editorScreen;

	/**
	 * Constructor
	 * @param libGDXGame				Game (LibGDX) used to switch between screens
	 * @param game						BasicGame to be shown
	 * @param _editorScreen				EditorScreen to go back
	 */
	public TryScreen(Game libGDXGame, BasicGame game, EditorScreen _editorScreen) {
		super(libGDXGame, game);
		editorScreen = _editorScreen;
	}


	@Override
	public void initLeft() {
		startTimer = System.currentTimeMillis();
		leftBar = new LeftBar();
		leftBar.addLabelContainer(HexaMaze.bundle.format("moves"), "0");
		String timeToString = String.format("%02d", game.getLimitSeconds() / 60) + ":" + String.format("%02d", (game.getLimitSeconds() % 60));
		leftBar.addLabelContainer(HexaMaze.bundle.format("time"), timeToString);

		leftBar.addButton(HexaMaze.bundle.format("sendLaser"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.getController().sendLaser(TryScreen.this);
			}
		});
		leftBar.addButton(HexaMaze.bundle.format("restart"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameLibGDX.setScreen(new TryScreen(gameLibGDX, new BasicGame(game.getLevel(), game.getGameMode(), game.getLaserMode()), editorScreen));
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
				game.getLevel().deleteFiles();
				Gdx.input.setInputProcessor(editorScreen.stage);
				gameLibGDX.setScreen(editorScreen);
			}
		});
	}

	@Override
	public void showEndLeftBar(boolean win) {
		endLevel = new LeftBar();
		endLevel.addLabelContainer(HexaMaze.bundle.format("result"), HexaMaze.bundle.format(win ? "win" : "lose"));
		if (win)
			endLevel.addLabelContainer(HexaMaze.bundle.format("score"), String.valueOf(game.getScore()));
		endLevel.addLabelContainer(HexaMaze.bundle.format("level"), game.getLevel().getName());
		String timeToString = String.format("%02d", (game.getLimitSeconds() - game.getRemainingSeconds()) / 60) + ":" + String.format("%02d", ((game.getLimitSeconds() - game.getRemainingSeconds()) % 60));
		endLevel.addLabelContainer(HexaMaze.bundle.format("time"), timeToString);
		endLevel.addLabelContainer(HexaMaze.bundle.format("moves"), String.valueOf(game.getMoves()));
		endLevel.addButton(HexaMaze.bundle.format("restart"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameLibGDX.setScreen(new TryScreen(gameLibGDX, new BasicGame(game.getLevel(), game.getGameMode(), game.getLaserMode()), editorScreen));
			}
		});
		endLevel.addButton(HexaMaze.bundle.format("quit"), new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.getLevel().deleteFiles();
				Gdx.input.setInputProcessor(editorScreen.stage);
				gameLibGDX.setScreen(editorScreen);
			}
		});
		table.getCell(leftBar).setActor(endLevel);
	}


}
