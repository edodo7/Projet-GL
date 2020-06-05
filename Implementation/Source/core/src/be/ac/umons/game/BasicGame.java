package be.ac.umons.game;

import be.ac.umons.HexaMaze;
import be.ac.umons.controller.BasicGameController;
import be.ac.umons.util.State;
import be.ac.umons.util.UserManager;

import java.io.*;

/**
 * Game allowing the user to play a Level
 */
public class BasicGame extends Game<BasicGameController> {

	private int moves;
	private long limitSeconds;
	private long remainingSeconds;
	private Mode gameMode;
	private Mode laserMode;

	/**
	 * Constructor
	 * @param level				GameLevel to be played
	 * @param _gameMode			Game mode
	 * @param _laserMode		Laser mode
	 */
	public BasicGame(GameLevel level, Mode _gameMode, Mode _laserMode) {
		super(level);
		controller = new BasicGameController(this);
		gameMode = _gameMode;
		laserMode = _laserMode;
		if (laserMode == Mode.CONTINUOUS)
			map.sendLaser();
		moves = 0;
		limitSeconds = level.getTimer();
	}

	/**
	 * Returns the score made by the user
	 * @return	Score made by the user
	 */
	public int getScore() {
		return 1000000 / ((int) Math.sqrt((limitSeconds-remainingSeconds) * moves) + 1);
	}

	/**
	 * Returns the Game Mode
	 * @return					"Arcade" or "Practice"
	 */
	public Mode getGameMode() {
		return gameMode;
	}

	/**
	 * Returns the Laser Mode
	 * @return					"Continuous" or "One Time"
	 */
	public Mode getLaserMode() {
		return laserMode;
	}

	/**
	 * Increments the moves counter
	 */
	public void addMove() {
		moves++;
	}

	/**
	 * Returns the moves counter
	 * @return					Number of moves made by the user
	 */
	public int getMoves() {
		return moves;
	}

	/**
	 * Returns the time counter (in seconds)
	 * @return					Value of the time counter (in seconds)
	 */
	public long getRemainingSeconds() {
		return remainingSeconds;
	}

	public long getLimitSeconds() {
		return limitSeconds;
	}

	/**
	 * Updates the time counter with a new value
	 * @param _seconds			New value of the time counter
	 */
	public void setRemainingSeconds(long _seconds) {
		remainingSeconds = limitSeconds - _seconds;
		if(remainingSeconds <= 0){
			if(gameMode == Mode.ARCADE)
				map.setLost(true);
			else
				remainingSeconds = 0;
		}
	}

	/**
	 * Stops the game by stopping the controller and updating the level's score file if needed
	 */
	public void stopGame() {
		controller.cleanController();
		if (gameMode==Mode.ARCADE && map.hasWon()) {
			if (getScore() > level.getMaxScore()){
				level.saveMaxScore(getScore());
			}
			if (HexaMaze.settings.isExtensionActive(Extension.USERS) && !UserManager.getUserManager().getCurrentUser().isAnonymous()
                && getScore() > level.loadUserScore(UserManager.getUserManager().getCurrentUser())){
				level.saveMaxScore(getScore(),UserManager.getUserManager().getCurrentUser());
			}
			if (getLevel().getNextLevel() != null) {getLevel().getNextLevel().setPlayable(true);}
		}


	}

	public void loadState(){
		String settingsPath = "usersSave" + File.separator + UserManager.getUserManager().getCurrentUser().getName() +
							  File.separator + getLevel().getXml().replace(".xml",".save");
		File file = new File(settingsPath);
		if (file.exists()) {
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
				State state = (State) in.readObject();
				if (state == null)
					throw new IllegalStateException("Error while loading settings");
				getMap().setHexagons(state.hexagons);
				getContainer().setHexagons(state.container);
				setRemainingSeconds(state.seconds);
				moves = state.moves;
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void saveState(){
		State state = new State(getMap().getHexagons(),getContainer().getHexagons(),getRemainingSeconds(),getMoves());
		String settingsPath = "usersSave" + File.separator + UserManager.getUserManager().getCurrentUser().getName() +
							  File.separator + getLevel().getXml().replace(".xml",".save");
		try {
			File settingsFile = new File(settingsPath);
			if(!settingsFile.exists())
				settingsFile.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(settingsPath));
			out.writeObject(state);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}