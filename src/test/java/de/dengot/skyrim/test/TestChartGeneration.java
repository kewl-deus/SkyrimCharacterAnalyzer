package de.dengot.skyrim.test;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.JFreeChart;
import org.testng.annotations.Test;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategoryProvider;
import de.dengot.skyrim.model.XmlStatisticCategoryProvider;
import de.dengot.skyrim.reporting.chart.character.CategoryBarChartProducer;
import de.dengot.skyrim.reporting.chart.character.CategorySplittedBarChartProducer;
import de.dengot.skyrim.reporting.chart.character.CharacterChartProducer;
import de.dengot.skyrim.reporting.chart.character.CumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.character.DeltaBarChartProducer;
import de.dengot.skyrim.reporting.chart.character.LevelBarChartProducer;
import de.dengot.skyrim.reporting.chart.character.LevelCumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.character.LevelDeltaBarChartProducer;
import de.dengot.skyrim.reporting.chart.character.TimeSeriesChartProducer;

public class TestChartGeneration extends AbstractSkyrimCharacterBasedTestCase {

	@Test
	public void testChartProduction() throws IOException {
		StatisticCategoryProvider catProvider = new XmlStatisticCategoryProvider();
		for (StatisticCategory cat : catProvider) {
			writeChart(new CategorySplittedBarChartProducer(cat), "CategorySplittedBarChart-" + cat.getName().getKey());
			writeChart(new CategoryBarChartProducer(cat), "CategoryBarChart-" + cat.getName().getKey());
		}

		LocalizedLabel statLabel = new LocalizedLabel("Chests Looted");
//		writeChart(new TimeSeriesChartProducer(statLabel, false), "RealTimeLineChart");
//		writeChart(new TimeSeriesChartProducer(statLabel, true), "RealTimeAreaChart");
//		writeChart(new CumulativeAreaChartProducer(statLabel, false), "CumulativeAreaChart");
//		writeChart(new CumulativeAreaChartProducer(statLabel, true), "CumulativeAreaChart-Normalized");
//		writeChart(new DeltaBarChartProducer(statLabel), "DeltaBarChart");
//		writeChart(new LevelBarChartProducer(), "LevelBarChart");
//		writeChart(new LevelCumulativeAreaChartProducer(), "LevelCumulativeAreaChart");
//		writeChart(new LevelDeltaBarChartProducer(), "LevelDeltaBarChart");

	}

	public void writeChart(CharacterChartProducer chartProducer, String filename) throws IOException {
		System.out.println("Writing " + filename);
		JFreeChart chart = chartProducer.produceChart(sampleCharacters);
		ChartWriter cw = new PngChartWriter();
		cw.writeChart(chart, 1600, 800, new File("c:/temp/" + filename + ".png"));
		// ChartUtilities.writeImageMap(writer, name, info, useOverLibForToolTips)
	}

}
