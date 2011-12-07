package de.dengot.skyrim.reporting.chart;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.StatisticCategory;

public class CategoryBarChartProducer extends ChartProducer {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(CategoryBarChartProducer.class);

    private StatisticCategory statsCategory;

    public CategoryBarChartProducer(StatisticCategory statsCategory) {
        super();
        this.statsCategory = statsCategory;
    }

    @Override
    public JFreeChart createChart(SkyrimCharacterList characters) {

        CategoryDataset dataset = createDataset(characters);

        JFreeChart chart =
                ChartFactory.createBarChart3D(statsCategory.getName(), "Category", "Amount",
                        dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();

        CategoryAxis categoryaxis = categoryplot.getDomainAxis();
        categoryaxis.setCategoryLabelPositions(CategoryLabelPositions
                .createUpRotationLabelPositions(0.39269908169872414D));

        CategoryItemRenderer categoryitemrenderer = categoryplot.getRenderer();
        categoryitemrenderer.setBaseItemLabelsVisible(true);

        BarRenderer barrenderer = (BarRenderer) categoryitemrenderer;
        barrenderer.setItemMargin(0.20000000000000001D);
        barrenderer.setBaseItemLabelsVisible(true);
        barrenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

        // NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        // numberaxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());

        LogarithmicAxis logAxis = new LogarithmicAxis("Amount");
        // logAxis.setLog10TickLabelsFlag(true);
        logAxis.setStrictValuesFlag(false);
        
        categoryplot.setRangeAxis(logAxis);

        return chart;
    }

    protected CategoryDataset createDataset(SkyrimCharacterList characters) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (String statName : this.statsCategory.getStatNames()) {
            for (SkyrimCharacter skyrimCharacter : characters) {
                int value = getCharacterValue(skyrimCharacter, statName);
                dataset.addValue(value, skyrimCharacter.getName(), statName);
            }
        }

        return dataset;
    }

    protected int getCharacterValue(SkyrimCharacter skyrimCharacter, String statName) {
        int val = skyrimCharacter.getMaxValue(statName);
        LOGGER
                .trace("CharacterValue({}, {}) = {}",
                        new Object[] { skyrimCharacter, statName, val });
        return val;
    }

}
