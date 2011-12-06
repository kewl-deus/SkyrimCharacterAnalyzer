package de.dengot.skyrim.reporting.chart;

import org.jfree.chart.JFreeChart;

import de.dengot.skyrim.model.SkyrimCharacterList;

public abstract class ChartProducer {

    public abstract JFreeChart createChart(SkyrimCharacterList characters);

}
