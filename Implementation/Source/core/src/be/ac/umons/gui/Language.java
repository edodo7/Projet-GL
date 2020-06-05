package be.ac.umons.gui;

import java.io.Serializable;

/**
 * Enumeration representative of the different languages that can be used
 */
public enum Language implements Serializable {
	English("en", "EN"),
	French("fr", "FR");

	private String languageCode;
	private String countryCode;

	/**
	 * Constructor
	 * @param languageCode			Language code
	 * @param countryCode			Country code
	 */
	Language(String languageCode, String countryCode) {
		this.languageCode = languageCode;
		this.countryCode = countryCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getCountryCode() {
		return countryCode;
	}
}