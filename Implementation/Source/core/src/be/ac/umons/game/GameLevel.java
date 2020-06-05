package be.ac.umons.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import be.ac.umons.HexaMaze;
import be.ac.umons.model.xml.XMLGameLoader;
import be.ac.umons.util.User;
import be.ac.umons.util.UserManager;

import java.io.*;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Level used by a Game
 */
public class GameLevel extends Level {


	private String xml;
	private int maxScore;
	private boolean playable = false;
	private GameLevel nextLevel = null;
	private GameLevel previousLevel = null;
	private int timer;
	private EnumMap<Extension, Boolean> extensions = new EnumMap<>(Extension.class);

	/**
	 * Cosntructor
	 * @param tmx				Name of the tmx file
	 * @param xml				Name of the xml file
	 * @param name				Name of the level
	 * @param difficulty		Difficulty of the level
	 */
	public GameLevel(String tmx, String xml, String name, Difficulty difficulty) {
		super(tmx);
		this.xml = xml;
		this.name = name;
		this.difficulty = difficulty;
		loadScore();
	}

	/**
	 * Reads the levels directory and creates an ArrayList of GameLevels using them
	 * @return				ArrayList of GameLevels containing all the levels of the levels folder
	 */
	public static ArrayList<GameLevel> createLevelArray() {
		ArrayList<GameLevel> levels = new ArrayList<>();
		FileHandle folder = Gdx.files.internal("levels");
		FileHandle[] files = folder.list();
		assert files != null;
		for (FileHandle file : files) {
			if (!file.isDirectory() && file.name().matches("^g3_[0-9]+.xml$")) {
				GameLevel level = new XMLGameLoader(new File("levels" + File.separator + file.name())).getGameData().getLevel();
				boolean isOk = true;
				for (Extension extension : Extension.values()) {
					if (level.isExtensionActive(extension) && !HexaMaze.settings.isExtensionActive(extension))
						isOk = false;
				}
				if (isOk)
					levels.add(level);
			}
		}
		GameLevel prev = null;
		for (GameLevel level : levels) {
			boolean test ;
			if (HexaMaze.settings.isExtensionActive(Extension.USERS) && !UserManager.getUserManager().getCurrentUser().isAnonymous())
				test = prev == null || prev.isFinished(UserManager.getUserManager().getCurrentUser());
			else
				test = prev == null || prev.isFinished();
			if (test)
				level.setPlayable(true);
			if (prev != null)
				prev.setNextLevel(level);
			level.setPreviousLevel(prev);
			prev = level;
		}
		return levels;
	}

	/**
	 * Reads the levels directory and returns the id of the last level
	 * @return				ID of the last level
	 */
	public static int getLastLevelID() {
		File folder = new File("." + File.separator + "levels");
		File[] files = folder.listFiles();
		int maxID = 0;
		for (File file : files) {
			if (file.isFile() && file.getName().matches("^g3_[0-9]+.xml$")) {
				int id = Integer.valueOf(file.getName().split("_")[1].split("\\.")[0]);
				if (id > maxID)
					maxID = id;
			}
		}
		return maxID;
	}

	/**
	 * Returns the name of the xml file used by this level
	 * @return				The name of the xml file used by this level
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * Updates xml file used by this level
	 * @param xml			The name of the xml file to be used by this level
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

	/**
	 * Loads the score file of this level and updates the maxScore attribute
	 */
	private void loadScore() {
		loadScore("levels" + File.separator + tmx.replace(".tmx", ".score"));
	}

	private void loadScore(String path){
		maxScore = 0;
		File file = new File(path);
		if (file.exists()) {
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
				maxScore = (int) in.readObject();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int loadUserScore(User user){
		String path = "usersSave" + File.separator + user.getName() + File.separator +
					  tmx.replace(".tmx", ".score");
		File file = new File(path);
		if (file.exists()) {
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
				int returnValue =  (int) in.readObject();
				in.close();
				return returnValue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}


	/**
	 * Saves an score to the score file of this level
	 * @param score			Score to be saved
	 */
	public void saveMaxScore(int score) {
		saveMaxScore(score,"levels" + File.separator + tmx.replace(".tmx", ".score"));
	}

	public void saveMaxScore(int score,String path){
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(score);
			out.close();
			maxScore = score;


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveMaxScore(int score, User user){
		saveMaxScore(score,"usersSave" + File.separator + user.getName() + File.separator + tmx.replace(".tmx", ".score"));
	}

	/**
	 * Returns the maximal score for this level
	 * @return				Maximal score for this level
	 */
	public int getMaxScore() {
		return maxScore;
	}

	/**
	 * Returns true if this level has already been finished
	 * @return				True if this level has already been finished
	 */
	public boolean isFinished() {
		return maxScore > 0;
	}

	public boolean isFinished(User user){
		return loadUserScore(user) > 0;
	}

	/**
	 * Returns true if this level can be played
	 * @return				True if this level can be played
	 */
	public boolean isPlayable() {
		return playable;
	}

	/**
	 * Allows/disallows this level to be played
	 * @param playable		True if this level can be played
	 */
	public void setPlayable(boolean playable) {
		this.playable = playable;
	}

	/**
	 * Returns the next level
	 * @return				Next Level
	 */
	public GameLevel getNextLevel() {
		return nextLevel;
	}

	/**
	 * Updates the next level reference
	 * @param nextLevel		New next level
	 */
	public void setNextLevel(GameLevel nextLevel) {
		this.nextLevel = nextLevel;
	}

	/**
	 * Returns the previous level
	 * @return				Previous level
	 */
	public GameLevel getPreviousLevel() {
		return previousLevel;
	}

	/**
	 * Updates the previous level reference
	 * @param previousLevel		New previous level
	 */
	public void setPreviousLevel(GameLevel previousLevel) {
		this.previousLevel = previousLevel;
	}

	/**
	 * Returns the limit time for this level
	 * @return					Limit time
	 */
	public int getTimer() {
		return timer;
	}

	/**
	 * Updates the limit time for this level
	 * @param timer				New limit time
	 */
	public void setTimer(int timer) {
		this.timer = timer;
	}

	/**
	 * Returns true if this level need the extension to be active
	 * @param extension				Extension to check
	 * @return						True if extension has to be active
	 */
	public boolean isExtensionActive(Extension extension) {
		if (extension == null)
			return true;
		return extensions.get(extension);
	}

	/**
	 * Adds/remvoes an extension to the required extensions
	 * @param extension				Extension
	 * @param enable				True if extension should be required
	 */
	public void setExtension(Extension extension, boolean enable) {
		if (extension == null)
			return;
		extensions.put(extension, enable);
	}

	/**
	 * Deletes the files belonging to this level
	 */
	public void deleteFiles() {
		File xml = new File("levels" + File.separator + getXml());
		File tmx = new File("levels" + File.separator + getTmx());
		xml.delete();
		tmx.delete();
	}

	@Override
	public String toString() {
		return name;
	}


}