package de.dengot.skyrim.reporting.chart.character;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;

public class LevelBarChartProducer extends CharacterChartProducer {

    @Override
    protected JFreeChart createChart(SkyrimCharacterList characters) {

        CategoryDataset dataset = createDataset(characters);

        JFreeChart chart =
                ChartFactory.createBarChart3D("Character Levels", "Characters", "Level", dataset,
                        PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();

        BarRenderer3D renderer = (BarRenderer3D) categoryplot.getRenderer();
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setItemLabelAnchorOffset(10D);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
                TextAnchor.BASELINE_LEFT));

        // NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        // numberaxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());

        return chart;
    }

    protected CategoryDataset createDataset(SkyrimCharacterList characters) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SkyrimCharacter skyrimCharacter : characters) {
            int level = skyrimCharacter.getCurrentLevel();
            dataset.addValue(new Integer(level), "Levels", skyrimCharacter.getName());
        }
        return dataset;
    }

}
