package de.dengot.skyrim.reporting.chart.playerprofile;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;

import org.jfree.chart.JFreeChart;

import de.dengot.steamcommunityclient.model.PlayerProfile;

public abstract class PlayerProfileChartProducer {

    private static final Paint CHART_BACKGROUND = new Color(0xFBEFD5);

    private static final Paint PLOT_BACKGROUND = new Color(0xFCFFF0);

    protected abstract JFreeChart createChart(Collection<PlayerProfile> profiles);

    public JFreeChart produceChart(Collection<PlayerProfile> profiles) {
        JFreeChart chart = createChart(profiles);
        chart.setBackgroundPaint(CHART_BACKGROUND);
        chart.getPlot().setBackgroundPaint(PLOT_BACKGROUND);
        return chart;
    }
}
