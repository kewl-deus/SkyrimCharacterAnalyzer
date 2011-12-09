package de.dengot.skyrim.io;

import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class PngChartWriter extends ChartWriter {

    @Override
    public String getFilenameSuffix() {
        return "png";
    }
    
    @Override
    public void writeChart(JFreeChart chart, int width, int height, OutputStream out)
            throws IOException {
        ChartUtilities.writeChartAsPNG(out, chart, width, height);
    }

}
