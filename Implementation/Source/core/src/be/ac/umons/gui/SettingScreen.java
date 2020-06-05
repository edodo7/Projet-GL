package be.ac.umons.gui;


import be.ac.umons.HexaMaze;
import be.ac.umons.game.Extension;
import be.ac.umons.util.UserManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * Screen used for the settings modification
 */
public class SettingScreen extends MenuScreen {

	private Label extensionLabel;
	private CheckBox editor;
	private CheckBox levelChecker;
	private CheckBox laserDiffColor;
	private CheckBox laserDiffInt;
	private CheckBox hallOfFame;
	private SelectBox<Language> languageSelectBox;
    private TextField passwordField;
    private TextField avatarPath;
    private TextButton logout;

	/**
	 * Constructor
	 * @param game			Game (LibGDX) used to switch between screens
	 */
	public SettingScreen(Game game) {
		super(game, HexaMaze.bundle.format("settings"));

		extensionLabel = HexaMaze.widgetFactory.createSubTitleLabel(" " + HexaMaze.bundle.format("extensions"));
		editor = HexaMaze.widgetFactory.createCheckBox(HexaMaze.bundle.format("editorExtension"));
		levelChecker = HexaMaze.widgetFactory.createCheckBox(HexaMaze.bundle.format("levelCheckerExtension"));
		laserDiffColor = HexaMaze.widgetFactory.createCheckBox(HexaMaze.bundle.format("colorExtension"));
		laserDiffInt = HexaMaze.widgetFactory.createCheckBox(HexaMaze.bundle.format("intensityExtension"));
		Label languageLabel = HexaMaze.widgetFactory.createSubTitleLabel(" " + HexaMaze.bundle.format("language"));
		languageSelectBox = HexaMaze.widgetFactory.createLargeSelectBox();
		hallOfFame = HexaMaze.widgetFactory.createCheckBox(HexaMaze.bundle.format("savingExtensionTitle"));
		languageSelectBox.setAlignment(Align.center);
		languageSelectBox.setItems(Language.values());
		languageSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Language selected = languageSelectBox.getSelected();
				HexaMaze.settings.setLanguage(selected);
				HexaMaze.loadBundle(selected);
				// Gdx.app.postRunnable(() -> game.setScreen(new SettingScreen(game))); TODO
			}
		});

		editor.setChecked(HexaMaze.settings.isExtensionActive(Extension.EDITOR));
		levelChecker.setChecked(HexaMaze.settings.isExtensionActive(Extension.LEVEL_CHECKER));
		laserDiffColor.setChecked(HexaMaze.settings.isExtensionActive(Extension.COLOR));
		laserDiffInt.setChecked(HexaMaze.settings.isExtensionActive(Extension.INTENSITY));
		hallOfFame.setChecked(HexaMaze.settings.isExtensionActive(Extension.USERS));
		languageSelectBox.setSelected(HexaMaze.settings.getLanguage());

		Table container = new Table();
		container.defaults().expandX().left().spaceBottom(5);
		container.add(extensionLabel).width(HexaMaze.WIDTH).row();
		container.add(editor).padLeft(30).row();
		container.add(levelChecker).padLeft(30).row();
		container.add(laserDiffColor).padLeft(30).row();
		container.add(laserDiffInt).padLeft(30).row();
		container.add(hallOfFame).padLeft(30).row();
		container.add(languageLabel).width(HexaMaze.WIDTH).row();
		container.add(languageSelectBox).padLeft(30).row();

        if (HexaMaze.settings.isExtensionActive(Extension.USERS) && !UserManager.getUserManager().getCurrentUser().isAnonymous()){
			Label passwordLabel = HexaMaze.widgetFactory.createLabel(HexaMaze.bundle.format("newPassword"));
			Label avatarLabel = HexaMaze.widgetFactory.createLabel(HexaMaze.bundle.format("avatarPath"));
			passwordField = new TextField("",HexaMaze.skin);
			avatarPath = new TextField("",HexaMaze.skin);
			passwordField.setPasswordMode(true);
			passwordField.setPasswordCharacter('*');
			Label userLabel = HexaMaze.widgetFactory.createSubTitleLabel(" " + UserManager.getUserManager().getCurrentUser().getName());
			logout = HexaMaze.widgetFactory.createTextButton(HexaMaze.bundle.format("logout"));
			logout.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					game.setScreen(new Login(game));
				}
			});
			container.add(userLabel).width(HexaMaze.WIDTH).row();
			container.add(passwordLabel).padLeft(30).row();
			container.add(passwordField).padLeft(30).row();
			container.add(avatarLabel).padLeft(30).row();
			container.add(avatarPath).padLeft(30).row();
			container.add(logout).padLeft(30).row();
		}

		table.add(container);
		table.pack();
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(!HexaMaze.settings.isExtensionActive(Extension.USERS) && hallOfFame.isChecked())
					game.setScreen(new Login(SettingScreen.this.game));
				else
					game.setScreen(new MainMenuScreen(SettingScreen.this.game));
			}
		});
	}

	// Allow settings to be saved even when we quit application by closing window.
	@Override
	public void hide() {
        if (HexaMaze.settings.isExtensionActive(Extension.USERS) && !UserManager.getUserManager().getCurrentUser().isAnonymous()){
			UserManager userManager = UserManager.getUserManager();
			if (passwordField.getText().length() != 0){
				userManager.getCurrentUser().setPassword(passwordField.getText());
			}
			if (avatarPath.getText().length() != 0) {
				userManager.getCurrentUser().setPathToAvatarImage(avatarPath.getText());
			}
			userManager.saveUsers();
		}
		HexaMaze.settings.setExtension(Extension.EDITOR, editor.isChecked());
		HexaMaze.settings.setExtension(Extension.LEVEL_CHECKER, levelChecker.isChecked());
		HexaMaze.settings.setExtension(Extension.COLOR, laserDiffColor.isChecked());
		HexaMaze.settings.setExtension(Extension.INTENSITY, laserDiffInt.isChecked());
		HexaMaze.settings.setExtension(Extension.USERS,hallOfFame.isChecked());
		HexaMaze.settings.saveSettings();
	}
}
