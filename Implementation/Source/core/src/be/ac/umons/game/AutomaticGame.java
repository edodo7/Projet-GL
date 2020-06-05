package be.ac.umons.game;

import be.ac.umons.controller.AutomaticController;

/**
 * Game played automatically
 */
public class AutomaticGame extends Game<AutomaticController> {

	private int moves;
	private State state;

	/**
	 * Constructor
	 * @param level				GameLevel to be played
	 */
	public AutomaticGame(GameLevel level) {
		super(level);
		state = State.LOADING;
		controller = new AutomaticController(this);
	}

	/**
	 * Increments the moves counter
	 */
	public void addMove() {
		moves++;
	}

	/**
	 * Resets the moves counter
	 */
	public void resetMoves() {
		moves = 0;
	}

	/**
	 * Returns the moves counter
	 * @return					Number of moves made by the user
	 */
	public int getMoves() {
		return moves;
	}

	/**
	 * Returns the State
	 * @return					State
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * Updates the State with a new State
	 * @param state				New State
	 */
	public void setState(State state) {
		this.state = state;
	}

	public enum State {
		LOADING,
		LOADED,
		STOPPED
	}
}