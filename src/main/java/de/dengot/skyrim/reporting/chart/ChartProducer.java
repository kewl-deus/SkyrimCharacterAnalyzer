package de.dengot.skyrim.reporting.chart;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;

import de.dengot.skyrim.model.SkyrimCharacterList;

public abstract class ChartProducer {

	private static final Paint CHART_BACKGROUND = new Color(0xFBEFD5);

	private static final Paint PLOT_BACKGROUND = new Color(0xFCFFF0);

	protected abstract JFreeChart createChart(SkyrimCharacterList characters);

	public JFreeChart produceChart(SkyrimCharacterList characters) {
		JFreeChart chart = createChart(characters);
		chart.setBackgroundPaint(CHART_BACKGROUND);
		chart.getPlot().setBackgroundPaint(PLOT_BACKGROUND);
		return chart;
	}

}
