package be.ac.umons.game;

import be.ac.umons.model.xml.EditorFileHandler;

/**
 * Level used by the editor
 */
public class EditorLevel extends Level {


	private int width;
	private int height;

	/**
	 * Constructor
	 */
	public EditorLevel() {
		super("editor.tmx");
		width = 5;
		height = 5;
		init();
	}

	/**
	 * Initializes the editor
	 */
	private void init() {
		EditorFileHandler.createDefault(width, height, tmx);
	}

	/**
	 * Updates the sizes of the level
	 * @param _width			New width
	 * @param _height			New height
	 */
	public void update(int _width, int _height) {
		width = _width;
		height = _height;
		EditorFileHandler.createDefault(width, height, tmx);
	}

	/**
	 * Returns the width of the level
	 * @return					Width of the level
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Updates the width of the level
	 * @param width				New width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns the height of the level
	 * @return					Height of the level
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Updates the height of the level
	 * @param height				New height
	 */
	public void setHeight(int height) {
		this.height = height;
	}


}
