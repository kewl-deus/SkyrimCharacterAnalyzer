package de.dengot.skyrim.reporting.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.TextAnchor;

import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;

public class LevelBarChartProducer extends ChartProducer {

    @Override
    public JFreeChart createChart(SkyrimCharacterList characters) {
        
        CategoryDataset dataset = createDataset(characters);
        double avgLevel = getAverageLevel(characters);
        
        JFreeChart chart =
                ChartFactory.createBarChart3D("Character Levels", "Characters", "Level", dataset,
                        PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
        
        AverageRelatedBarRenderer3D renderer = new AverageRelatedBarRenderer3D(avgLevel);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setItemLabelAnchorOffset(10D);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        categoryplot.setRenderer(renderer);
        
        ValueMarker valuemarker =
                new ValueMarker(avgLevel, new Color(200, 200, 255), new BasicStroke(
                        1.0F), new Color(200, 200, 255), new BasicStroke(1.0F), 1.0F);
        categoryplot.addRangeMarker(valuemarker, Layer.BACKGROUND);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setMaximumBarWidth(0.050000000000000003D);
        
        
        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
        
        return chart;
    }
    
    protected double getAverageLevel(SkyrimCharacterList characters){
        int sum = 0;
        for (SkyrimCharacter skyrimCharacter : characters) {
            sum += skyrimCharacter.getCurrentLevel();
        }
        return sum / characters.getCharacters().size();
    }

    protected CategoryDataset createDataset(SkyrimCharacterList characters) {
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SkyrimCharacter skyrimCharacter : characters) {
            int level = skyrimCharacter.getCurrentLevel();
            dataset.addValue(new Integer(level), "Levels", skyrimCharacter.getName());
        }
        return dataset;
    }

    protected static class AverageRelatedBarRenderer3D extends BarRenderer3D {

        private static final long serialVersionUID = 2777201536833956564L;
        
        private double avgValue;
        
        public AverageRelatedBarRenderer3D(double avgValue){
            super();
            this.avgValue = avgValue;
        }
        
        public Paint getItemPaint(int i, int j) {
            CategoryDataset categorydataset = getPlot().getDataset();
            double d = categorydataset.getValue(i, j).doubleValue();
            if (d >= avgValue) {
                return Color.green;
            } else {
                return Color.red;
            }
        }

    }
}
