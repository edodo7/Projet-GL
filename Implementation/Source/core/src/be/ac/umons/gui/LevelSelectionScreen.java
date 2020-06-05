package be.ac.umons.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.GameLevel;
import be.ac.umons.game.Mode;

/**
 * Screen used for the level selection
 */
public class LevelSelectionScreen extends MenuScreen {

	private int page = 0;
	private int pages;
	private Table levelContainer;
	private TextButton prevPage;
	private TextButton nextPage;
	private ArrayList<GameLevel> levels;
	private int startingIndex;
	private TextButton practice;
	private TextButton arcade;
	private TextButton continuous;
	private TextButton oneTime;
	private Mode gameMode=Mode.PRACTICE;
	private Mode laserMode=Mode.CONTINUOUS;

	/**
	 * Constructor
	 * @param game			Game (LibGDX) used to switch between screens
	 */
	public LevelSelectionScreen(Game game) {

		//CREATES STRUCTURE
		super(game, HexaMaze.bundle.format("selectLevel"));
		//ADDS LISTENER TO THE BACK BUTTON
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				LevelSelectionScreen.this.game.setScreen(new MainMenuScreen(LevelSelectionScreen.this.game));
			}
		});

		//LEVEL SELECTION
		this.levels = GameLevel.createLevelArray();
		pages = Math.max(((levels.size()-1)/8)+1, 1);
		makeLevelContainer();
		table.add(levelContainer).fill().expand().colspan(2);
		table.row();
		Table pagesTable = new Table();
		prevPage = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("previousPage"));
		prevPage.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				page--;
				nextPage.setDisabled(false);
				if (page == 0)
					prevPage.setDisabled(true);
				makeLevelContainer();
			}
		});
		nextPage = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("nextPage"));
		nextPage.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				page++;
				prevPage.setDisabled(false);
				if (page == pages - 1)
					nextPage.setDisabled(true);
				makeLevelContainer();
			}
		});
		if (page == 0)
			prevPage.setDisabled(true);
		if (page == pages - 1)
			nextPage.setDisabled(true);

		pagesTable.add(prevPage).height(20).width(HexaMaze.WIDTH / 2f).fill().spaceRight(10);
		pagesTable.add(nextPage).height(20).width(HexaMaze.WIDTH / 2f).fill();
		table.add(pagesTable).fill().expand().colspan(2);
		table.row();

		//OPTIONS CHOOSER
		final int botBarHeight = 100;

		Table gameModeOptions = new Table();
		Label gameModeLabel = HexaMaze.widgetFactory.createSubTitleLabel(HexaMaze.bundle.format("gameMode"));
		gameModeLabel.setAlignment(Align.center);
		gameModeOptions.add(gameModeLabel).expand().width(HexaMaze.WIDTH / 2).colspan(2).row();

		practice = HexaMaze.widgetFactory.createLargeTextButton(HexaMaze.bundle.format("practice"));
		practice.setDisabled(true);
		practice.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				practice.setDisabled(true);
				arcade.setDisabled(false);
				gameMode = Mode.PRACTICE;
			}
		});
		arcade = HexaMaze.widgetFactory.createLargeTextButton(HexaMaze.bundle.format("arcade"));
		arcade.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				arcade.setDisabled(true);
				practice.setDisabled(false);
				gameMode = Mode.ARCADE;
			}
		});
		gameModeOptions.add(practice).height(botBarHeight).grow().width(HexaMaze.WIDTH / 4);
		gameModeOptions.add(arcade).height(botBarHeight).grow().width(HexaMaze.WIDTH / 4);

		Table laserModeOptions = new Table();
		Label laserModeLabel = HexaMaze.widgetFactory.createSubTitleLabel(HexaMaze.bundle.format("laserMode"));
		laserModeLabel.setAlignment(Align.center);
		laserModeOptions.add(laserModeLabel).width(HexaMaze.WIDTH / 2).colspan(2).row();

		oneTime = HexaMaze.widgetFactory.createLargeTextButton(HexaMaze.bundle.format("oneTimeOnly"));
		oneTime.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				oneTime.setDisabled(true);
				continuous.setDisabled(false);
				laserMode = Mode.ONE_TIME;
			}
		});
		continuous = HexaMaze.widgetFactory.createLargeTextButton(HexaMaze.bundle.format("continuous"));
		continuous.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				oneTime.setDisabled(false);
				continuous.setDisabled(true);
				laserMode = Mode.CONTINUOUS;
			}
		});
		continuous.setDisabled(true);
		laserModeOptions.add(continuous).height(botBarHeight).grow().expand().width(HexaMaze.WIDTH / 4);
		laserModeOptions.add(oneTime).height(botBarHeight).grow().width(HexaMaze.WIDTH / 4);

		Table options = new Table();
		options.add(gameModeOptions).expand().spaceRight(10);
		options.add(laserModeOptions).expand();

		table.add(options).expand();
		table.pack();
	}

	/**
	 * Updates the tables of Levels with the levels corresponding to the current page
	 */
	private void makeLevelContainer() {
		if (levelContainer == null)
			levelContainer = new Table();
		levelContainer.clearChildren();
		for (int i = page * 8; i < (page * 8) + 8; i++) {
			if (i == (page * 8) + 4) {
				levelContainer.row();
			}
			levelContainer.add(new LevelPreview(i < levels.size() ? levels.get(i) : null, game, this)).grow().pad(10);
		}
	}

	/**
	 * returns the selected Game Mode
	 * @return				Selected Game Mode
	 */
	public Mode getGameMode() {
		return gameMode;
	}

	/**
	 * returns the selected Laser Mode
	 * @return				Selected Laser Mode
	 */
	public Mode getLaserMode() {
		return laserMode;
	}


}
