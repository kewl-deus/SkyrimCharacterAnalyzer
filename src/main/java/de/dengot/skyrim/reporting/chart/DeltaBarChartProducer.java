package de.dengot.skyrim.reporting.chart;

import java.text.MessageFormat;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.IntervalXYDataset;
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

public class DeltaBarChartProducer extends ChartProducer {

    private LocalizedLabel statLabel;

    public DeltaBarChartProducer(LocalizedLabel statLabel) {
        super();
        this.statLabel = statLabel;
    }
    
    @Override
    public JFreeChart createChart(SkyrimCharacterList characters) {

        IntervalXYDataset dataset = createDataset(characters);

        JFreeChart chart =
                ChartFactory.createXYBarChart(this.statLabel.getLocalizedText(), SkyrimConstants.DAYS_PASSED, false,
                        "Amount", dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(CHART_BACKGROUND);

        TextTitle texttitle =
                new TextTitle(MessageFormat.format("Change of {0} per gameday", this.statLabel.getLocalizedText()));
        texttitle.setPosition(RectangleEdge.TOP);
        texttitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.050000000000000003D,
                0.050000000000000003D, 0.050000000000000003D, 0.050000000000000003D));
        texttitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(texttitle);

        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(PLOT_BACKGROUND);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
        //rangeAxis.setTickUnit(new NumberTickUnit(10));
        
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        
        return chart;
    }

    protected IntervalXYDataset createDataset(SkyrimCharacterList characters) {

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        for (SkyrimCharacter skyrimCharacter : characters) {

            XYSeries xyseries = new XYSeries(skyrimCharacter.getName());

            int lastDaysValue = 0;
            for (SkyrimCharacterSnapshot snapshot : skyrimCharacter.getHistory()) {

                int gameDay = snapshot.getStatisticValue(SkyrimConstants.DAYS_PASSED);
                int value = getSnapshotValue(snapshot);
                int deltaValue = value - lastDaysValue;
                xyseries.add(new Integer(gameDay), new Integer(deltaValue));
                lastDaysValue = value;
            }

            xyseriescollection.addSeries(xyseries);
        }

        xyseriescollection.setIntervalWidth(0.0D);
        return xyseriescollection;
    }
    
    protected int getSnapshotValue(SkyrimCharacterSnapshot snapshot){
        return snapshot.getStatisticValue(this.statLabel.getKey());
    }
}
