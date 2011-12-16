package de.dengot.skyrim.reporting.chart.playerprofile;

import java.util.Collection;

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

import de.dengot.steamcommunityclient.model.PlayerProfile;

public class PlaytimeBarChartProducer extends PlayerProfileChartProducer {

    @Override
    protected JFreeChart createChart(Collection<PlayerProfile> profiles) {

        CategoryDataset dataset = createDataset(profiles);

        JFreeChart chart =
                ChartFactory.createBarChart3D("Playtime", "Players", "Hours played", dataset,
                        PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();

        BarRenderer3D renderer = (BarRenderer3D) categoryplot.getRenderer();
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setItemLabelAnchorOffset(10D);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
                TextAnchor.BASELINE_LEFT));

        return chart;
    }

    protected CategoryDataset createDataset(Collection<PlayerProfile> profiles) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (PlayerProfile profile : profiles) {
            dataset.addValue(profile.getPlaytime(), "Playtime", profile.getSteamName());
        }
        return dataset;
    }
}
