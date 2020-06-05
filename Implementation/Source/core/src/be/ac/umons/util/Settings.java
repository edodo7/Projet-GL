package be.ac.umons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EnumMap;

import be.ac.umons.game.Extension;
import be.ac.umons.gui.Language;

/**
 * Class containing the settings of this game
 */
public class Settings {
	private static final String settingsPath = "settings.ser";
	private SettingsData data = new SettingsData();

	/**
	 * Actives/deactivates an extension
	 * @param extension				The extension
	 * @param enable				New state of the extension
	 */
	public void setExtension(Extension extension, boolean enable) {
		data.extensions.put(extension, enable);
	}

	/**
	 * Returns true if the extension is active
	 * @param extension				The extension
	 * @return						True if the extension is active
	 */
	public boolean isExtensionActive(Extension extension) {
		return data.extensions.get(extension);
	}

	/**
	 * Returns the active language
	 * @return						Active language
	 */
	public Language getLanguage() {
		return data.language;
	}

	/**
	 * Changes the active language
	 * @param language				New active language
	 */
	public void setLanguage(Language language) {
		data.language = language;
	}

	/**
	 * Loads settings file
	 */
	public void loadSettings() {
		File file = new File(settingsPath);
		if (file.exists()) {
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
				data = (SettingsData) in.readObject();
				if (data == null)
					throw new IllegalStateException("Error while loading settings");
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves settings file
	 */
	public void saveSettings() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(settingsPath));
			out.writeObject(data);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Class representing the settings data
	 */
	private static class SettingsData implements Serializable {
		EnumMap<Extension, Boolean> extensions = new EnumMap<Extension, Boolean>(Extension.class);
		Language language = Language.English;

		public SettingsData() {
			for (Extension extension : Extension.values())
				extensions.put(extension, false);
		}
	}
}
