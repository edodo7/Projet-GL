package be.ac.umons.controller;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import be.ac.umons.game.AutomaticGame;
import be.ac.umons.model.Container;
import be.ac.umons.model.Map;
import be.ac.umons.model.levelChecker.Hint;
import be.ac.umons.model.levelChecker.LevelChecker;
import be.ac.umons.model.levelChecker.Solution;

/**
 * Controller used in the Demonstration Mode
 */
public class AutomaticController extends Controller<AutomaticGame> {

	private static final int delay = 500;
	private Map initialMap;
	private Container initialContainer;
	private final CopyOnWriteArrayList<EndLevelListener> listeners = new CopyOnWriteArrayList<>();
	private ArrayList<Solution> solutions;
	private int actualSolutionIndex;
	private Timer timer = new Timer();
	private LevelChecker levelChecker;
	private Thread loadingThread = null;
	private Mode mode = Mode.MANUAL;


	public AutomaticController(AutomaticGame game) {
		super(game);
		try {
			initialMap = game.getMap().clone();
			initialContainer = game.getContainer().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		actualSolutionIndex = 0;

		levelChecker = new LevelChecker(game.getMap(), game.getContainer());
		loadingThread = new Thread(() -> {
			solutions = levelChecker.getSolutions();
			if (game.getState() == AutomaticGame.State.STOPPED)
				return;
			assert solutions.size() > 0;
			this.game.setState(AutomaticGame.State.LOADED);
			Gdx.app.postRunnable(this::showSolution);
		});
		loadingThread.start();
	}

	/**
	 * Add listener on which the method onEndLevel will be called at the end of a level
	 * @param listener  a listener
	 */
	public void addEndLevelListener(EndLevelListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove an EndLevelListener
	 * @param listener  a listener
	 */
	public void removeEndLevelListener(EndLevelListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Call onEndLevel on each listener
	 */
	private void endLevelEvent() {
		for (EndLevelListener listener : listeners) {
			listener.onEndLevel();
		}
	}

	/**
	 * Get the current loading of the game
	 * If the Game is fully loaded, the loading stays to 1.0f
	 * @return  the loading, between 0.0f and 1.0f
	 */
	public double getLoading() {
		return levelChecker.getLoading();
	}

	/**
	 * Show the solution specified by actualSolutionIndex, stopping the previous showing solution if any
	 */
	public void showSolution() {
		if (game.getState() == AutomaticGame.State.LOADING)
			return;

		timer.cancel();
		timer.purge();
		timer = new Timer();
		try {
			game.setMap(initialMap.clone());
			game.getContainer().setHexagons(initialContainer.clone().getHexagons());
			// Remove clone to see the hint as the difference from the previous solution
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		game.resetMoves();

		Map solutionMap = new Map(solutions.get(actualSolutionIndex));
		Hint hint = new Hint(game.getMap(), game.getContainer(), solutionMap);
		game.getMap().sendLaser();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (game.getMap().equals(solutionMap)) {
					timer.cancel();
					timer.purge();
					if (mode == Mode.AUTOMATIC) {
						Gdx.app.postRunnable(AutomaticController.this::endLevelEvent);
					}
					return;
				}
				hint.apply();
				game.addMove();
				game.getMap().sendLaser();
			}
		}, delay, delay);
	}

	/**
	 * Get the mode of the controller
	 * @return  the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Set the mode of the controller
	 * @param mode  the mode
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * Call {@link #showSolution()} on the next solution
	 */
	public void nextSolution() {
		actualSolutionIndex = (actualSolutionIndex + 1) % solutions.size();
		showSolution();
	}

	/**
	 * Call {@link #showSolution()} on the previous solution
	 */
	public void previousSolution() {
		actualSolutionIndex = ((actualSolutionIndex - 1) + solutions.size()) % solutions.size();
		showSolution();
	}

	/**
	 * Get the currently found solutions of the game
	 * @return  the currently found solutions
	 */
	public ArrayList<Solution> getFoundSolutions() {
		return levelChecker.getFoundSolutions();
	}

	/**
	 * Get a number associated with the current solution
	 * @return  the solution number
	 */
	public int getSolutionNumber() {
		return actualSolutionIndex + 1;
	}

	public void stopLoading() {
		game.setState(AutomaticGame.State.STOPPED);
		levelChecker.stop();
	}

	/**
	 * Manual mode let the user browse solution
	 * Automatic mode browse the solutions automatically
	 */
	public enum Mode {
		MANUAL,
		AUTOMATIC
	}

	/**
	 * Must be implemented to be informed of the end of the level
	 */
	public interface EndLevelListener {
		void onEndLevel();
	}
}