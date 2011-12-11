package de.dengot.skyrim.test;

import org.testng.annotations.Test;

import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.reporting.MultiThreadedCharacterReportGenerator;

public class TestCharacterReportGenerator extends AbstractSkyrimCharacterBasedTestCase {

	@Test
	public void testReportGeneration() {
		long start = System.currentTimeMillis();

		MultiThreadedCharacterReportGenerator mtRepoGen = new MultiThreadedCharacterReportGenerator(
				new PngChartWriter());
		mtRepoGen.createReport(sampleCharacters, "c:/temp/skyrimreport");
		// duration: 58s dualcore / 24s quadcore

		long finish = System.currentTimeMillis();
		long duration = finish - start;
		float floatDuration = duration / 1000;

		System.out.println("Report Generation took: " + floatDuration + " secs");
	}

}
