package be.ac.umons.gui;

import be.ac.umons.HexaMaze;
import be.ac.umons.game.BasicGame;
import be.ac.umons.game.Extension;
import be.ac.umons.game.GameLevel;
import be.ac.umons.util.UserManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.io.File;

/**
 * Table containing representing an existing level
 */
public class LevelPreview extends Table {
	private GameLevel level;
	private int maxScore;
	private Sprite done;
	private Sprite locked;
	private TextButton tit;
	private Game game;
    private Dialog continueMsg;

	/**
	 * Cosntructor
	 * @param _level				Level to be shown
	 * @param _game					Game (LibGDX) used to switch between screens
	 * @param selectionScreen		LevelSelectionScreen containing this instance
	 */
	public LevelPreview(GameLevel _level, Game _game, LevelSelectionScreen selectionScreen) {
		level = _level;
		game = _game;

		tit = HexaMaze.widgetFactory.createTextButton(level == null ? "" : level.getName());

		if (level == null) {
			this.setVisible(false);
		} else {
			boolean test;
			if (HexaMaze.settings.isExtensionActive(Extension.USERS) && !UserManager.getUserManager().getCurrentUser().isAnonymous())
				test = level.isFinished(UserManager.getUserManager().getCurrentUser());
			else
				test = level.isFinished();
			if (test)
				done = new Sprite(new Texture("img/done.png"));
			if (!level.isPlayable())
				locked = new Sprite(new Texture("img" + File.separator + "locked.png"));
			this.maxScore = level.getMaxScore();

			if (level.isPlayable()) {
				tit.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						BasicGame basicGame = new BasicGame(level, selectionScreen.getGameMode(), selectionScreen.getLaserMode());
						if(HexaMaze.settings.isExtensionActive(Extension.USERS)) {
						 	String settingFile = "usersSave" + File.separator + UserManager.getUserManager().getCurrentUser().getName() + File.separator +
								basicGame.getLevel().getXml().replace(".xml", ".save");
							File file = new File(settingFile);
							if (file.exists()) {
								continueMsg = new Dialog("", HexaMaze.skin) {
									@Override
									protected void result(Object object) {
										if ((Boolean) object) {
											basicGame.loadState();
										}
										game.setScreen(new BasicGameScreen(game, basicGame));
									}
								}.text(HexaMaze.bundle.format("continueOrRestart"))
										.button(HexaMaze.bundle.format("continue"), true)
										.button(HexaMaze.bundle.format("restart"), false);
								continueMsg.show(getStage());
							} else {
								game.setScreen(new BasicGameScreen(game, basicGame));
							}
						}
						else{
							game.setScreen(new BasicGameScreen(game, basicGame));
						}
					}
				});
			}
		}
		Label empty = new Label("", HexaMaze.skin);
		Label.LabelStyle titStyle = new Label.LabelStyle(empty.getStyle().font, empty.getStyle().fontColor);
		Label.LabelStyle contStyle = new Label.LabelStyle(empty.getStyle().font, empty.getStyle().fontColor);
		titStyle.background = new Image(new Texture("colors/dark_gray.png")).getDrawable();
		contStyle.background = new Image(new Texture("colors/light_gray.png")).getDrawable();
		this.add(tit).expand().fill().colspan(2);
		this.row();

		Label diffTitle = HexaMaze.widgetFactory.createLabel(HexaMaze.bundle.format("difficulty"));
		Label diffContent = HexaMaze.widgetFactory.createLabel(level == null ? "" : level.getDifficulty().toString());
		Label scoreTitle = HexaMaze.widgetFactory.createLabel(HexaMaze.bundle.format("maxScore"));
		Label scoreContent = HexaMaze.widgetFactory.createLabel(level == null ? "" : String.valueOf(this.maxScore));
		diffTitle.setStyle(titStyle);
		diffTitle.setAlignment(Align.center);
		scoreTitle.setStyle(titStyle);
		scoreTitle.setAlignment(Align.center);
		diffContent.setStyle(contStyle);
		diffContent.setAlignment(Align.center);
		scoreContent.setStyle(contStyle);
		scoreContent.setAlignment(Align.center);
		this.add(diffTitle).expand().fill();
		this.add(diffContent).expand().fill();
		this.row();
		this.add(scoreTitle).expand().fill();
		this.add(scoreContent).expand().fill();
		this.row();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
		batch.end();
		HexaMaze.spriteBatch.begin();
		Sprite sprite = null;
		if (level != null && level.isFinished())
			sprite = done;
		if (level != null && !level.isPlayable())
			sprite = locked;
		if (this.getWidth() != 0 && this.getHeight() != 0 && level != null && sprite != null) {
			HexaMaze.spriteBatch.setProjectionMatrix(this.getStage().getCamera().combined);
			sprite.setSize(this.getWidth() / 5, tit.getHeight());
			sprite.setPosition(this.getX() + (this.getWidth() * 4 / 5), this.getY() + (this.getHeight() - tit.getHeight()));
			sprite.draw(HexaMaze.spriteBatch);
		}
		HexaMaze.spriteBatch.end();
		batch.begin();
	}
}
