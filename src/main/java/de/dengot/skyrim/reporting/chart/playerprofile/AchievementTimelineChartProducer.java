package de.dengot.skyrim.reporting.chart.playerprofile;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import de.dengot.steamcommunityclient.model.PlayerProfile;

public class AchievementTimelineChartProducer extends PlayerProfileChartProducer {

    private boolean fillArea;

    /**
     * @param fillArea
     *            when <code>true</code> render fill areas under timelines
     */
    public AchievementTimelineChartProducer(boolean fillArea) {
        this.fillArea = fillArea;
    }

    @Override
    protected JFreeChart createChart(Collection<PlayerProfile> profiles) {
        TimeSeriesCollection dataset = createTimeSeriesDataset(profiles);
        JFreeChart chart = fillArea ? createTimeAreaChart(dataset) : createTimeLineChart(dataset);
        return chart;
    }

    protected JFreeChart createTimeLineChart(TimeSeriesCollection dataset) {
        JFreeChart chart =
                ChartFactory.createXYAreaChart("Unlocked Achievements", "Time", "Amount", dataset,
                        PlotOrientation.VERTICAL, true, true, false);

        return chart;
    }

    protected JFreeChart createTimeAreaChart(TimeSeriesCollection dataset) {
        ValueAxis timeAxis = new DateAxis("Time");
        timeAxis.setLowerMargin(0.02); // reduce the default margins
        timeAxis.setUpperMargin(0.02);

        NumberAxis valueAxis = new NumberAxis("Amount");
        valueAxis.setAutoRangeIncludesZero(false); // override default

        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);

        XYAreaRenderer renderer = new XYAreaRenderer();
        plot.setRenderer(renderer);
        plot.setForegroundAlpha(0.65F);

        JFreeChart chart =
                new JFreeChart("Unlocked Achievements", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        return chart;
    }

    protected TimeSeriesCollection createTimeSeriesDataset(Collection<PlayerProfile> profiles) {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();

        for (PlayerProfile profile : profiles) {

            TimeSeries timeseries = new TimeSeries(profile.getSteamName());

            List<Calendar> unlocks = new LinkedList<Calendar>();
            for (String achievementApiName : profile.getUnlockedAchievements()) {
                Calendar unlockTime = profile.getUnlockTime(achievementApiName);
                unlocks.add(unlockTime);
            }
            Collections.sort(unlocks);

            int unlockCounter = 0;
            for (Calendar unlockTime : unlocks) {

                Day day = new Day(unlockTime.getTime());
                int value = ++unlockCounter;

                Number existingVal = timeseries.getValue(day);
                if (existingVal != null) {
                    value = Math.max(value, existingVal.intValue());
                }
                timeseries.addOrUpdate(day, value);
            }

            timeseriescollection.addSeries(timeseries);
        }

        return timeseriescollection;
    }
}
