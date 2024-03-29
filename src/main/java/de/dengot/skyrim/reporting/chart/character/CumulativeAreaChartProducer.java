package de.dengot.skyrim.reporting.chart.character;

import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;
import de.dengot.skyrim.model.SkyrimConstants;

public class CumulativeAreaChartProducer extends CharacterChartProducer {

    private LocalizedLabel statLabel;
    private boolean normalized;

    public CumulativeAreaChartProducer(LocalizedLabel statLabel) {
        this(statLabel, false);
    }

    public CumulativeAreaChartProducer(LocalizedLabel statLabel, boolean normalized) {
        super();
        this.statLabel = statLabel;
        this.normalized = normalized;
    }

    @Override
    protected JFreeChart createChart(SkyrimCharacterList characters) {

        XYDataset dataset =
                normalized ? createNormalizedDataset(characters) : createDataset(characters);

        JFreeChart chart =
                ChartFactory.createXYAreaChart("Total " + this.statLabel.getLocalizedText(),
                        SkyrimConstants.DAYS_PASSED, "Amount", dataset, PlotOrientation.VERTICAL,
                        true, true, false);
        
        TextTitle texttitle =
                new TextTitle("Cumalitive Timeline (in gamedays) for " + this.statLabel.getLocalizedText());
        texttitle.setPosition(RectangleEdge.TOP);
        texttitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.050000000000000003D,
                0.050000000000000003D, 0.050000000000000003D, 0.050000000000000003D));
        texttitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(texttitle);

        XYPlot xyplot = (XYPlot) chart.getPlot();
        xyplot.setForegroundAlpha(0.65F);

//        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
//        numberaxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());

        return chart;
    }

    protected XYDataset createDataset(SkyrimCharacterList characters) {

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        for (SkyrimCharacter skyrimCharacter : characters) {

            XYSeries xyseries = new XYSeries(skyrimCharacter.getName());

            for (SkyrimCharacterSnapshot snapshot : skyrimCharacter.getHistory()) {

                int gameDay = snapshot.getStatisticValue(SkyrimConstants.DAYS_PASSED);
                int value = getSnapshotValue(snapshot);

                xyseries.add(new Integer(gameDay), new Integer(value));
            }

            xyseriescollection.addSeries(xyseries);
        }

        xyseriescollection.setIntervalWidth(0.0D);
        return xyseriescollection;
    }

    private XYDataset createNormalizedDataset(SkyrimCharacterList characters) {

        final int totalDaysPassed = characters.getMaxValue(SkyrimConstants.DAYS_PASSED);

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        for (SkyrimCharacter skyrimCharacter : characters) {

            XYSeries xyseries = new XYSeries(skyrimCharacter.getName());

            for (int gameDay = 0; gameDay <= totalDaysPassed; gameDay++) {

                int value = getCharacterValueAtGameDay(gameDay, skyrimCharacter);
                xyseries.add(new Integer(gameDay), new Integer(value));
            }
            xyseriescollection.addSeries(xyseries);
        }

        xyseriescollection.setIntervalWidth(0.0D);
        return xyseriescollection;
    }

    protected int getSnapshotValue(SkyrimCharacterSnapshot snapshot){
        return snapshot.getStatisticValue(this.statLabel.getKey());
    }
    
    protected int getCharacterValueAtGameDay(int gameDay, SkyrimCharacter skyrimCharacter){
        return skyrimCharacter.getValueAtGameDay(gameDay, this.statLabel.getKey());
    }
}
