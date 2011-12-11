package de.dengot.skyrim.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.testng.annotations.BeforeTest;

import de.dengot.skyrim.io.QueryOptimizedSkyrimCharacterSerializer;
import de.dengot.skyrim.io.SkyrimCharacterSerializer;
import de.dengot.skyrim.io.XmlSkyrimCharacterSerializer;
import de.dengot.skyrim.model.SkyrimCharacterList;

public class AbstractSkyrimCharacterBasedTestCase {

	protected SkyrimCharacterList sampleCharacters;

	@SuppressWarnings("unused")
	@BeforeTest
	protected void loadSampleCharacters() {
		InputStream input = getClass().getResourceAsStream("/skyrimcharacters.xml");
		InputStreamReader reader = new InputStreamReader(input);

		SkyrimCharacterSerializer serializer = new QueryOptimizedSkyrimCharacterSerializer(
				new XmlSkyrimCharacterSerializer());
		this.sampleCharacters = serializer.read(reader);
	}

	@SuppressWarnings("unused")
	@BeforeTest
	protected void setDefaultLanguage() {
		Locale.setDefault(Locale.GERMAN);
	}
}
