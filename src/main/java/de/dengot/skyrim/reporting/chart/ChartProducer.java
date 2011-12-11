package de.dengot.skyrim.reporting.chart;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;

import de.dengot.skyrim.model.SkyrimCharacterList;

public abstract class ChartProducer {

	protected static final Paint CHART_BACKGROUND = new Color(0xFBEFD5);

	protected static final Paint PLOT_BACKGROUND = new Color(0xFCFFF0);

	public abstract JFreeChart createChart(SkyrimCharacterList characters);

}
