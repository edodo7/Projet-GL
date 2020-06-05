package be.ac.umons;


import be.ac.umons.game.Extension;
import be.ac.umons.gui.Language;
import be.ac.umons.gui.Login;
import be.ac.umons.gui.MainMenuScreen;
import be.ac.umons.gui.ScaledTextWidgetFactory;
import be.ac.umons.util.Settings;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;


public class HexaMaze extends Game {
	public static final int WIDTH = 720;
	public static final int HEIGHT = 480;
	public static final String TITLE = "Hexagonal Laser Maze";
	public static I18NBundle bundle;
	public static Skin skin;
	public static SpriteBatch spriteBatch;
	public static ShapeRenderer shapeRenderer;
	public static Settings settings = new Settings();
	public static ScaledTextWidgetFactory widgetFactory = new ScaledTextWidgetFactory();

	/**
	 * Set the current language of the application
	 * @param language  the language of the application
	 */
	public static void loadBundle(Language language) {
		FileHandle localHandle = Gdx.files.internal("locals/menu");
		bundle = I18NBundle.createBundle(localHandle, new Locale(language.getLanguageCode(), language.getCountryCode()));
	}

	@Override
	public void create() {
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		settings.loadSettings();

		loadBundle(settings.getLanguage());

		FreeTypeFontGenerator arialGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
		FreeTypeFontParameter typeParameter = new FreeTypeFontParameter();
		typeParameter.size = 80;
		BitmapFont highQuality = arialGenerator.generateFont(typeParameter);
		highQuality.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		typeParameter.size = 40;
		BitmapFont mediumQuality = arialGenerator.generateFont(typeParameter);
		mediumQuality.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		FreeTypeFontGenerator captainGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/captain.ttf"));
		typeParameter.size = 100;
		BitmapFont captainFont = captainGenerator.generateFont(typeParameter);

		skin = new Skin();
		skin.add("highQuality", highQuality);
		skin.add("mediumQuality", mediumQuality);
		skin.add("captainFont", captainFont);
		skin.addRegions(new TextureAtlas(Gdx.files.internal("skins/flat/skin.atlas")));
		skin.load(Gdx.files.internal("skins/flat/skin.json"));
		if (settings.isExtensionActive(Extension.USERS))
			setScreen(new Login(this));
		else
			setScreen(new MainMenuScreen(this));
	}
}
