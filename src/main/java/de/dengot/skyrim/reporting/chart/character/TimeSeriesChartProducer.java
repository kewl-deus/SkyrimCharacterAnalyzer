package de.dengot.skyrim.reporting.chart.character;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;

public class TimeSeriesChartProducer extends CharacterChartProducer {

    private LocalizedLabel statLabel;
    private boolean fillArea;

    /**
     * @param statLabel
     * @param fillArea
     *            when <code>true</code> render fill areas under timelines
     */
    public TimeSeriesChartProducer(LocalizedLabel statLabel, boolean fillArea) {
        this.statLabel = statLabel;
        this.fillArea = fillArea;
    }

    @Override
    protected JFreeChart createChart(SkyrimCharacterList characters) {
        TimeSeriesCollection dataset = createTimeSeriesDataset(characters);

        JFreeChart chart = fillArea ? createTimeAreaChart(dataset) : createTimeLineChart(dataset);

        TextTitle texttitle = new TextTitle("Realtime stats for " + this.statLabel.getLocalizedText());
        texttitle.setPosition(RectangleEdge.TOP);
        texttitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.050000000000000003D,
                0.050000000000000003D, 0.050000000000000003D, 0.050000000000000003D));
        texttitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(texttitle);

        return chart;
    }

    protected JFreeChart createTimeLineChart(TimeSeriesCollection dataset) {
        JFreeChart chart =
                ChartFactory.createXYAreaChart("Total " + this.statLabel.getLocalizedText(),
                        "Time", "Amount", dataset, PlotOrientation.VERTICAL, true, true, false);

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
                new JFreeChart("Total " + this.statLabel.getLocalizedText(),
                        JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        return chart;
    }

    protected TimeSeriesCollection createTimeSeriesDataset(SkyrimCharacterList characters) {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();

        for (SkyrimCharacter skyrimCharacter : characters) {

            TimeSeries timeseries = new TimeSeries(skyrimCharacter.getName());

            for (SkyrimCharacterSnapshot snapshot : skyrimCharacter.getHistory()) {

                Day day = new Day(snapshot.getSaveTime().getTime());
                int value = getSnapshotValue(snapshot);

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

    protected int getSnapshotValue(SkyrimCharacterSnapshot snapshot) {
        return snapshot.getStatisticValue(this.statLabel.getKey());
    }

}
