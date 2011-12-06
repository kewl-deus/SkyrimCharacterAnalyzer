package de.dengot.skyrim.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jfree.chart.JFreeChart;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.io.SkyrimCharacterSerializer;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.StatisticCategoryProvider;
import de.dengot.skyrim.model.queryoptimized.QueryOptimizedModelFactory;
import de.dengot.skyrim.reporting.chart.CategoryBarChartProducer;
import de.dengot.skyrim.reporting.chart.ChartProducer;
import de.dengot.skyrim.reporting.chart.CumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.DeltaBarChartProducer;
import de.dengot.skyrim.reporting.chart.LevelBarChartProducer;
import de.dengot.skyrim.reporting.chart.LevelCumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.LevelDeltaBarChartProducer;

public class TestChartGeneration {

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
    public void testChartProduction() throws IOException {
        String statName = "Chests Looted";
        writeChart(new CumulativeAreaChartProducer(statName, false), "CumulativeAreaChart");
        writeChart(new CumulativeAreaChartProducer(statName, true), "NormalizedCumulativeAreaChart");
        writeChart(new DeltaBarChartProducer(statName), "DeltaBarChart");
        writeChart(new CategoryBarChartProducer(StatisticCategoryProvider.getProvider()
                .getCategory("General")), "CategoryBarChart");
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