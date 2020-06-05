package be.ac.umons.game;

/**
 * Enumeration representing the different levels of difficulty
 */
public enum Difficulty {
	EASY,
	MEDIUM,
	HARD;

	@Override
	public String toString() {
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
	}
}
