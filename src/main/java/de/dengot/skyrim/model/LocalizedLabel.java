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

	public String getLabel(Locale locale) {
		return getLabel(locale.getLanguage());
	}
	
	/**
	 * @param twoLetterLangIsoCode ISO 639
	 * @return
	 */
	public String getLabel(String twoLetterLangIsoCode) {
		String label = translations.getProperty(twoLetterLangIsoCode);
		return label == null ? key : label;
	}

	public String getDefaultLabel() {
		return getLabel(Locale.getDefault());
	}

	public void setLabel(Locale locale, String translation) {
		setLabel(locale.getLanguage(), translation);
	}
	
	/**
	 * @param twoLetterLangCode ISO 639
	 * @param translation
	 */
	public void setLabel(String twoLetterLangCode, String translation){
		this.translations.setProperty(twoLetterLangCode, translation);
	}
	
	public Set<String> getLanguages() {
		return translations.stringPropertyNames();
	}

	@Override
	public String toString() {
		return getDefaultLabel();
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
