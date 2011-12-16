package de.dengot.skyrim.reporting.chart.character;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategoryProvider;

public class CategoryBarChartProducer extends CharacterChartProducer {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(CategoryBarChartProducer.class);

    private StatisticCategory statsCategory;

    public CategoryBarChartProducer(StatisticCategory statsCategory) {
        super();
        this.statsCategory = statsCategory;
    }

    @Override
    protected JFreeChart createChart(SkyrimCharacterList characters) {

        CategoryDataset dataset = createDataset(characters);

        JFreeChart chart =
                ChartFactory.createBarChart3D(statsCategory.getLocalizedName(), "Category", "Amount",
                        dataset, PlotOrientation.VERTICAL, true, true, false);

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

        List<LocalizedLabel> sortedLabels = new ArrayList<LocalizedLabel>(this.statsCategory.getStatLabels());
        Collections.sort(sortedLabels, StatisticCategoryProvider.LabelByLocalizedTextComparator);
        for (LocalizedLabel statLabel : sortedLabels) {
            for (SkyrimCharacter skyrimCharacter : characters) {
                int value = getCharacterValue(skyrimCharacter, statLabel.getKey());
                dataset.addValue(value, skyrimCharacter.getName(), statLabel.getLocalizedText());
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
