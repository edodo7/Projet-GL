package be.ac.umons.game;

/**
 * Class representing a level
 */
public class Level {

	protected String tmx;
	protected String name;
	protected Difficulty difficulty;

	/**
	 * Constructor
	 * @param tmx			Name of the tmx file
	 */
	public Level(String tmx) {
		this.tmx = tmx;
	}

	/**
	 * Returns the name of the tmx file used by this level
	 * @return				The name of the tmx file used by this level
	 */
	public String getTmx() {
		return tmx;
	}

	/**
	 * Updates tmx file used by this level
	 * @param tmx		    The name of the tmx file to be used by this level
	 */
	public void setTmx(String tmx) {
		this.tmx = tmx;
	}

	/**
	 * Returns the name of this level
	 * @return				Name of this level
	 */
	public String getName() {
		return name;
	}

	/**
	 * Updates the name of this level with a new one
	 * @param name			New name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the difficulty of this level
	 * @return				Difficulty of this level
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * Updates the difficulty of this level with a new one
	 * @param difficulty			New Difficulty
	 */
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}
}

