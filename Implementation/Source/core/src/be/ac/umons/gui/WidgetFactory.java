package be.ac.umons.gui;


import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Class allowing to easily create widgets with an uniform style
 */
public interface WidgetFactory {

	/**
	 * Creates a Label
	 * @param name				Text to be used in the label
	 * @return					A Label
	 */
	Label createLabel(String name);

	/**
	 * Creates a Label to be used as title
	 * @param name				Text to be used in the label
	 * @return					A Label
	 */
	Label createTitleLabel(String name);

	/**
	 * Creates a Label to be used as sub-title
	 * @param name				Text to be used in the label
	 * @return					A Label
	 */
	Label createSubTitleLabel(String name);

	/**
	 * Creates a CheckBox
	 * @param name				Text to be used in the CheckBox
	 * @return					A CheckBox
	 */
	CheckBox createCheckBox(String name);

	/**
	 * Creates a TextButton
	 * @param name				Text to be used in the TextButton
	 * @return					A TextButton
	 */
	TextButton createTextButton(String name);

	/**
	 * Creates a larger TextButton
	 * @param name				Text to be used in the TextButton
	 * @return					A TextButton
	 */
	TextButton createLargeTextButton(String name);

	/**
	 * Creates a SelectBox
	 *
	 * @param <T>   the SelectBox type
	 * @return      the SelectBox
	 */
	<T> SelectBox<T> createSelectBox();

	/**
	 * Creates a larger SelectBox
	 *
	 * @param <T>   the SelectBox type
	 * @return		the SelectBox
	 */
	<T> SelectBox<T> createLargeSelectBox();

	/**
	 * Creates a Dialog with a OK button that hides it
	 * @param text  the text of the dialog
	 * @return      the Dialog instance
	 */
	Dialog createDialog(String text);
}
