package de.dengot.skyrim.reporting.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;
import de.dengot.skyrim.model.SkyrimConstants;

public class TimeSeriesChartProducer extends ChartProducer {

    private LocalizedLabel statLabel;

    public TimeSeriesChartProducer(LocalizedLabel statLabel) {
        this.statLabel = statLabel;
    }

    @Override
    protected JFreeChart createChart(SkyrimCharacterList characters) {

        XYDataset dataset = createTimeSeriesDataset(characters);

        JFreeChart chart =
                ChartFactory.createTimeSeriesChart("Total " + this.statLabel.getLocalizedText(),
                        "Time", "Amount", dataset, true, true, false);

        TextTitle texttitle =
                new TextTitle("Cumalitive Timeline (in gamedays) for "
                        + this.statLabel.getLocalizedText());
        texttitle.setPosition(RectangleEdge.TOP);
        texttitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.050000000000000003D,
                0.050000000000000003D, 0.050000000000000003D, 0.050000000000000003D));
        texttitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(texttitle);

        return chart;
    }

    protected XYDataset createTimeSeriesDataset(SkyrimCharacterList characters) {
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
