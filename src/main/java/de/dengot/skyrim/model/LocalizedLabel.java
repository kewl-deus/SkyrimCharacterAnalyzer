package de.dengot.skyrim.model;

import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("LocalizedLabel")
public class LocalizedLabel {

	private String key;

	private Properties translations;

	public LocalizedLabel(String key) {
		super();
		this.key = key;
		this.translations = new Properties();
	}

	public String getKey() {
		return key;
	}

	public String getText(Locale locale) {
		return getText(locale.getLanguage());
	}
	
	/**
	 * @param twoLetterLangIsoCode ISO 639
	 * @return
	 */
	public String getText(String twoLetterLangIsoCode) {
		String label = translations.getProperty(twoLetterLangIsoCode);
		return label == null ? key : label;
	}

	public String getLocalizedText() {
		return getText(Locale.getDefault());
	}

	public void setText(Locale locale, String translation) {
		setText(locale.getLanguage(), translation);
	}
	
	/**
	 * @param twoLetterLangCode ISO 639
	 * @param translation
	 */
	public void setText(String twoLetterLangCode, String translation){
		this.translations.setProperty(twoLetterLangCode, translation);
	}
	
	public Set<String> getLanguages() {
		return translations.stringPropertyNames();
	}

	@Override
	public String toString() {
		return getLocalizedText();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final LocalizedLabel other = (LocalizedLabel) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
