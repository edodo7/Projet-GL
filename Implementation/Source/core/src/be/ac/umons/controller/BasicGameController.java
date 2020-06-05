package be.ac.umons.controller;

import be.ac.umons.game.BasicGame;
import be.ac.umons.game.Mode;
import be.ac.umons.gui.BasicGameScreen;
import be.ac.umons.model.Map;
import be.ac.umons.model.levelChecker.Hint;
import be.ac.umons.model.levelChecker.LevelChecker;
import be.ac.umons.util.Pos;

/**
 * Controller used by the LevelChecker extension
 */
public class BasicGameController extends Controller<BasicGame> {

	/**
	 * Constructor
	 * @param game				A BasicGame that will contain the controller
	 */
	public BasicGameController(BasicGame game) {
		super(game);
		addDragAndDrop();
	}

	/**
	 * Provides a hint
	 */
	public void applyHint() throws IllegalArgumentException {
		new Hint(game.getMap(), game.getContainer(), true).apply();
		updateLaser();
	}

	/**
	 * Provides a solution
	 */
	public void resolve() {
		new Hint(game.getMap(), game.getContainer(), false).applyAll();
		game.getMap().sendLaser();
		updateLaser();
	}

	/**
	 * Sends the laser
	 * @param screen			Screen where the game is beign shown
	 */
	public void sendLaser(BasicGameScreen screen) {
		game.getMap().sendLaser();
		game.stopGame();
		screen.showEndLeftBar(game.getMap().hasWon());
		screen.setFinished(true);
	}

	/**
	 * Updates the laser by adding a new move
	 */
	public void updateLaser() {
		game.addMove();
		if (game.getLaserMode() == Mode.CONTINUOUS)
			game.getMap().sendLaser();
	}

	@Override
	public void switchHexagon(Pos from, Pos to, boolean fromBot, boolean toBot) {
		super.switchHexagon(from, to, fromBot, toBot);
		updateLaser();
	}

	@Override
	public void rotateHexagon(Pos position, boolean antiClockwise, boolean botBar) {
		super.rotateHexagon(position, antiClockwise, botBar);
		updateLaser();
	}


}