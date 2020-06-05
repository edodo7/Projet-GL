package be.ac.umons.gui;


import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import be.ac.umons.HexaMaze;

/**
 * Class that implements WidgetFactory and give widget with an uniform style
 */
public class ScaledTextWidgetFactory implements WidgetFactory {

	@Override
	public Label createLabel(String name) {
		Label label = new Label(name, HexaMaze.skin);
		label.setFontScale(0.4f);
		return label;
	}

	@Override
	public Label createTitleLabel(String name) {
		Label label = new Label(name, HexaMaze.skin, "highQuality");
		label.setFontScale(0.5f);
		return label;
	}

	@Override
	public Label createSubTitleLabel(String name) {
		Label label = new Label(name, HexaMaze.skin, "highQuality");
		label.setFontScale(0.35f);
		Pixmap background = new Pixmap(100, 100, Pixmap.Format.RGB888);
		background.setColor(Color.DARK_GRAY);
		background.fill();
		Label.LabelStyle style = new Label.LabelStyle(label.getStyle());
		style.background = new Image(new Texture(background)).getDrawable();
		label.setStyle(style);
		return label;
	}

	@Override
	public CheckBox createCheckBox(String name) {
		CheckBox checkBox = new CheckBox(name, HexaMaze.skin);
		checkBox.getLabel().setFontScale(0.5f);
		return checkBox;
	}

	@Override
	public TextButton createTextButton(String name) {
		TextButton textButton = new TextButton(name, HexaMaze.skin);
		textButton.getLabel().setFontScale(0.4f);
		return textButton;
	}


	@Override
	public TextButton createLargeTextButton(String name) {
		TextButton textButton = new TextButton(name, HexaMaze.skin, "highQuality");
		textButton.getLabel().setFontScale(0.3f);
		return textButton;
	}

	@Override
	public<T> SelectBox<T> createSelectBox() {
		SelectBox<T> selectBox = new SelectBox<>(HexaMaze.skin);
		SelectBox.SelectBoxStyle style = HexaMaze.skin.get(SelectBox.SelectBoxStyle.class);
		style.font.getData().setScale(0.4f);
		selectBox.setStyle(style);
		return selectBox;
	}

	@Override
	public<T> SelectBox<T> createLargeSelectBox() {
		SelectBox<T> selectBox = new SelectBox<>(HexaMaze.skin);
		SelectBox.SelectBoxStyle style = HexaMaze.skin.get(SelectBox.SelectBoxStyle.class);
		style.font.getData().setScale(0.6f);
		selectBox.setStyle(style);
		return selectBox;
	}

	@Override
	public Dialog createDialog(String text) {
		HexaMaze.skin.get(SelectBox.SelectBoxStyle.class).font.getData().setScale(0.4f);
		return new Dialog("", HexaMaze.skin, "dialog") {
			protected void result(Object object) {
				this.hide();
			}
		}.text(text).button("OK").key(Input.Keys.ENTER, true)
				.key(Input.Keys.ESCAPE, false);
	}
}