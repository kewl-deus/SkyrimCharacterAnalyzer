package de.dengot.skyrim.test;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.dengot.skyrim.io.SkyrimCharacterSerializer;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.queryoptimized.QueryOptimizedModelFactory;
import de.dengot.skyrim.reporting.MultiThreadedCharacterReportGenerator;

public class TestCharacterReportGenerator {

	private SkyrimCharacterList sampleCharacters;

	@BeforeTest
	private void loadSampleCharacters() {
		InputStream input = getClass().getResourceAsStream("/skyrimcharacters.xml");
		InputStreamReader reader = new InputStreamReader(input);

		SkyrimCharacterSerializer serializer = new SkyrimCharacterSerializer();
		SkyrimCharacterList charList = serializer.read(reader);
		this.sampleCharacters = new QueryOptimizedModelFactory().createQueryOptimized(charList);
	}

	@Test
	public void testReportGeneration() {
		long start = System.currentTimeMillis();

		MultiThreadedCharacterReportGenerator mtRepoGen = new MultiThreadedCharacterReportGenerator();
		mtRepoGen.createReport(sampleCharacters, "c:/temp/skyrimreport");
		// duration: 58s dualcore / 24s quadcore

		long finish = System.currentTimeMillis();
		long duration = finish - start;
		float floatDuration = duration / 1000;

		System.out.println("Report Generation took: " + floatDuration + " secs");
	}

}
