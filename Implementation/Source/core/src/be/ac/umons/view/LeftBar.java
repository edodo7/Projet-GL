package be.ac.umons.view;

import be.ac.umons.HexaMaze;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;


public class LeftBar extends Table {

	private final float elheight = HexaMaze.HEIGHT / 21f;
	private HashMap<String, Container> containers;
	private Label.LabelStyle titStyle;
	private Label.LabelStyle contStyle;
	private Table infoTable;
	private Table buttonsTable;

	public LeftBar() {
		super();
		this.top().left();
		infoTable = new Table();
		buttonsTable = new Table();
		Label gap = new Label("", HexaMaze.skin);
		containers = new HashMap<>();
		titStyle = new Label.LabelStyle(gap.getStyle().font, Color.BLACK);
		contStyle = new Label.LabelStyle(gap.getStyle().font, Color.BLACK);
		titStyle.background = new Image(new Texture("colors/gray.png")).getDrawable();
		contStyle.background = new Image(new Texture("colors/white.png")).getDrawable();
		gap.setStyle(titStyle);
		this.add(infoTable).expandX().fill();
		this.row();
		this.add(gap).expand().fill();
		this.row();
		this.add(buttonsTable).expandX().fill();
		this.row();
	}

	/**
	 * Adds a label with a title
	 * @param title				Title of the label
	 * @param data				Data to be shown in the label
	 */
	public void addLabelContainer(String title, String data) {
		LabelContainer cont = new LabelContainer();
		addContainer(cont, title, HexaMaze.widgetFactory.createLabel(data));
	}

	/**
	 * Adds an actor with a title
	 * @param title				Title of the Actor
	 * @param actor				Actor to be shown
	 */
	public void addActorContainer(String title, Actor actor) {
		Container<Actor> cont = new Container<>();
		addContainer(cont, title, actor);
	}

	/**
	 * Adds a container
	 * @param cont				Container to be added
	 * @param title				Title
	 * @param data				Data to be shown
	 * @param <T>				Type of the data
	 */
	private <T extends Actor> void addContainer(Container cont, String title, T data) {
		containers.put(title, cont);
		cont.setTitle(title);
		cont.setData(data);
		infoTable.add(cont.getTitle()).height(elheight).expandX().fill();
		infoTable.row();
		infoTable.add(cont.getData()).height(elheight).expandX().fill();
		infoTable.row();
	}

	/**
	 * Adds a container containing multiple elements
	 * @param cont				Container to be added
	 * @param title				Title
	 * @param data				Multiple data to be shown
	 * @param <T>				Type of the data
	 */
	private <T extends Actor> void addContainer(Container<Actor> cont, String title, T[] data) {
		Table interTable = new Table();
		for (Actor actor : data) {
			interTable.add(actor).height(elheight).expandX().fill();
		}
		interTable.row();
		addContainer(cont, title, interTable);
	}

	/**
	 * Updates the text of an label
	 * @param title				Title of the label
	 * @param data				New text
	 */
	public void updateLabel(String title, String data) {
		((LabelContainer) containers.get(title)).setText(data);
	}

	/**
	 * Adds a TextButton on the bottom
	 * @param text				Text of the TextButton
	 * @param listener			Listener to be attached to the TextButton
	 * @return					The added TextButton
	 */
	public TextButton addButton(String text, ChangeListener listener) {
		TextButton button = HexaMaze.widgetFactory.createTextButton(text);
		if (listener != null) {
			button.addListener(listener);
		}
		buttonsTable.add(button).height(elheight).expandX().fill();
		buttonsTable.row();
		return button;
	}

	/**
	 * Adds an actor withou a title
	 * @param actor				Actor to be added
	 * @param height            height of the actor
	 */
	public void addActorWithoutLabel(Actor actor,float height) {
		infoTable.add(actor).height(height).expandX().fill().padTop(2).row();
	}

	public void addActorWithoutLabel(Actor actor) {
		infoTable.add(actor).height(elheight).expandX().fill().padTop(2).row();
	}


	/**
	 * Adds an actor below the buttons
	 * @param actor				Actor to be added
	 */
	public void addActorAtBottom(Actor actor) {
		this.add(actor).height(elheight).expandX().fill();
		this.row();
	}

	/**
	 * Class that contains an actor and a title
	 * @param <T>				Type of actor
	 */
	private class Container<T extends Actor> {

		protected T data;
		private Label title;

		private Container() {
			title = HexaMaze.widgetFactory.createLabel("");
			title.setStyle(titStyle);
			title.setAlignment(Align.center);
		}

		public Label getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title.setText(title);
		}

		public T getData() {
			return data;
		}

		public void setData(T newData) {
			data = newData;
		}


	}

	/**
	 * Class that contains a label and a title
	 */
	private class LabelContainer extends Container<Label> {

		private LabelContainer() {
			super();
		}

		public void setText(String text) {
			data.setText(text);
		}

		@Override
		public void setData(Label label) {
			super.setData(label);
			data.setStyle(contStyle);
		}


	}
}

