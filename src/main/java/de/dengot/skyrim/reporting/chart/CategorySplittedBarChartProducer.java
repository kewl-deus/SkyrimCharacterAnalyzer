package de.dengot.skyrim.reporting.chart;

import java.text.NumberFormat;
import java.util.ListIterator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.HistogramBin;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.StatisticCategory;

public class CategorySplittedBarChartProducer extends ChartProducer {

    private static final XLogger LOGGER =
            XLoggerFactory.getXLogger(CategorySplittedBarChartProducer.class);

    private StatisticCategory statsCategory;

    public CategorySplittedBarChartProducer(StatisticCategory statsCategory) {
        super();
        this.statsCategory = statsCategory;
    }

    @Override
    public JFreeChart createChart(SkyrimCharacterList characters) {

        CategoryDataset dataset = createDataset(characters);

        JFreeChart baseChart = createPartialChart(dataset);
        JFreeChart topChart = createPartialChart(dataset);

        CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot();

        CategoryPlot topPlot = topChart.getCategoryPlot();
        CategoryPlot basePlot = baseChart.getCategoryPlot();
        combinedPlot.add(topPlot, 1);
        combinedPlot.add(basePlot, 5);

        Range rangeBounds = DatasetUtilities.findRangeBounds(dataset);
        LOGGER.trace("BarChart ranges from {} to {}", rangeBounds.getLowerBound(), rangeBounds
                .getUpperBound());

        Histogram histogram = createHistogram(characters, dataset.getColumnCount() * 10);
        LOGGER.trace("--------- Histogram --------------");
        for (HistogramBin hbin : histogram) {
            LOGGER.trace("HistogramBin[{} -> {}] contains {} values", new Object[] {
                    hbin.getStartBoundary(), hbin.getEndBoundary(), hbin.getCount() });
        }

        NumberAxis baseRangeAxis = (NumberAxis) basePlot.getRangeAxis();
        baseRangeAxis.setLowerBound(histogram.getMostFrequencyBin().getStartBoundary());
        baseRangeAxis.setUpperBound(histogram.getMostFrequencyBin().getEndBoundary());

        NumberAxis topRangeAxis = (NumberAxis) topPlot.getRangeAxis();
        topRangeAxis.setLowerBound(histogram.getUpperBoundBin().getStartBoundary());
        topRangeAxis.setUpperBound(rangeBounds.getUpperBound());

        JFreeChart combinedChart = new JFreeChart(statsCategory.getLocalizedName(), combinedPlot);

        CategoryAxis categoryaxis = combinedPlot.getDomainAxis();
        categoryaxis.setCategoryLabelPositions(CategoryLabelPositions
                .createUpRotationLabelPositions(0.39269908169872414D));

        return combinedChart;
    }

    protected JFreeChart createPartialChart(CategoryDataset dataset) {
        JFreeChart chart =
                ChartFactory.createBarChart3D(statsCategory.getLocalizedName(), "Category", "Amount",
                        dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(CHART_BACKGROUND);

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
        categoryplot.setBackgroundPaint(PLOT_BACKGROUND);

        CategoryAxis categoryaxis = categoryplot.getDomainAxis();
        categoryaxis.setCategoryLabelPositions(CategoryLabelPositions
                .createUpRotationLabelPositions(0.39269908169872414D));

        CategoryItemRenderer categoryitemrenderer = categoryplot.getRenderer();
        categoryitemrenderer.setBaseItemLabelsVisible(true);

        BarRenderer barrenderer = (BarRenderer) categoryitemrenderer;
        barrenderer.setItemMargin(0.20000000000000001D);
        barrenderer.setBaseItemLabelsVisible(true);
        barrenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());

        return chart;
    }

    protected Histogram createHistogram(SkyrimCharacterList characters, int binCount) {
        double[] values = new double[this.statsCategory.getStatLabels().size()];
        double minimum = Integer.MAX_VALUE;
        double maximum = Integer.MIN_VALUE;
        for (ListIterator<LocalizedLabel> it = this.statsCategory.getStatLabels().listIterator(); it
                .hasNext();) {
        	LocalizedLabel statLabel = it.next();
            double value = characters.getMaxValue(statLabel.getKey());
            values[it.previousIndex()] = value;

            minimum = value < minimum ? value : minimum;
            maximum = value > maximum ? value : maximum;
        }

        return new Histogram(values, binCount, minimum, maximum);
    }

    protected CategoryDataset createDataset(SkyrimCharacterList characters) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (LocalizedLabel statLabel : this.statsCategory.getStatLabels()) {
            for (SkyrimCharacter skyrimCharacter : characters) {
                int value = getCharacterValue(skyrimCharacter, statLabel);
                dataset.addValue(value, skyrimCharacter.getName(), statLabel.getLocalizedText());
            }
        }

        return dataset;
    }

    protected int getCharacterValue(SkyrimCharacter skyrimCharacter, LocalizedLabel statLabel) {
        int val = skyrimCharacter.getMaxValue(statLabel.getKey());
        LOGGER
                .trace("CharacterValue({}, {}) = {}",
                        new Object[] { skyrimCharacter, statLabel.getKey(), val });
        return val;
    }

}
