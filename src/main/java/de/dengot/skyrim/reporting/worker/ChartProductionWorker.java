package de.dengot.skyrim.reporting.worker;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.jfree.chart.JFreeChart;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.reporting.chart.CumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.DeltaBarChartProducer;

public class ChartProductionWorker implements Runnable {

    private SkyrimCharacterList characters;
    private StatisticCategory statsCategory;
    private File outputFolder;

    public ChartProductionWorker(SkyrimCharacterList characters, StatisticCategory statsCategory,
            File outputFolder) {
        super();
        this.characters = characters;
        this.statsCategory = statsCategory;
        this.outputFolder = outputFolder;
    }

    public void run() {
        try {
            for (String statName : statsCategory.getStatNames()) {
                writeCharts(statName, characters);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCharts(String statName, SkyrimCharacterList characters) throws IOException {
        JFreeChart cumulAreaChart =
                new CumulativeAreaChartProducer(statName).createChart(characters);
        writeChart(cumulAreaChart, "cumulative-areachart", statName);

        JFreeChart deltaBarChart = new DeltaBarChartProducer(statName).createChart(characters);
        writeChart(deltaBarChart, "delta-barchart", statName);
    }

    private void writeChart(JFreeChart chart, String chartName, String statName) throws IOException {
        ChartWriter chartWriter = new PngChartWriter();
        String filename = MessageFormat.format("{0} {1}.png", statName, chartName);
        File file = new File(this.outputFolder, filename);
        
        System.out.println("Writing " + file.getPath());
        
        chartWriter.writeChart(chart, 1400, 800, file);
    }
}
