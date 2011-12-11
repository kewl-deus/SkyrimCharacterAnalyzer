package de.dengot.skyrim.test;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.JFreeChart;
import org.testng.annotations.Test;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategoryProvider;
import de.dengot.skyrim.model.XmlStatisticCategoryProvider;
import de.dengot.skyrim.reporting.chart.CategoryBarChartProducer;
import de.dengot.skyrim.reporting.chart.CategorySplittedBarChartProducer;
import de.dengot.skyrim.reporting.chart.ChartProducer;
import de.dengot.skyrim.reporting.chart.CumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.DeltaBarChartProducer;
import de.dengot.skyrim.reporting.chart.LevelBarChartProducer;
import de.dengot.skyrim.reporting.chart.LevelCumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.LevelDeltaBarChartProducer;

public class TestChartGeneration extends AbstractSkyrimCharacterBasedTestCase {

	@Test
	public void testChartProduction() throws IOException {
		StatisticCategoryProvider catProvider = new XmlStatisticCategoryProvider();

		for (StatisticCategory cat : catProvider) {
			writeChart(new CategorySplittedBarChartProducer(cat), "CategorySplittedBarChart-" + cat.getName());
			writeChart(new CategoryBarChartProducer(cat), "CategoryBarChart-" + cat.getName());
		}

		String statName = "Chests Looted";
		writeChart(new CumulativeAreaChartProducer(statName, false), "CumulativeAreaChart");
		writeChart(new CumulativeAreaChartProducer(statName, true), "NormalizedCumulativeAreaChart");
		writeChart(new DeltaBarChartProducer(statName), "DeltaBarChart");
		writeChart(new LevelBarChartProducer(), "LevelBarChart");
		writeChart(new LevelCumulativeAreaChartProducer(), "LevelCumulativeAreaChart");
		writeChart(new LevelDeltaBarChartProducer(), "LevelDeltaBarChart");

	}

	public void writeChart(ChartProducer chartProducer, String filename) throws IOException {
		System.out.println("Writing " + filename);
		JFreeChart chart = chartProducer.createChart(sampleCharacters);
		ChartWriter cw = new PngChartWriter();
		cw.writeChart(chart, 1600, 800, new File("c:/temp/" + filename + ".png"));
		// ChartUtilities.writeImageMap(writer, name, info,
		// useOverLibForToolTips)
	}

}
