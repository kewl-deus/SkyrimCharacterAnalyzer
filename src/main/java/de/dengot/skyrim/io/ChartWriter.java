package de.dengot.skyrim.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.JFreeChart;

public abstract class ChartWriter {

    public abstract void writeChart(JFreeChart chart, int width, int height, OutputStream out)
            throws IOException;

    public void writeChart(JFreeChart chart, int width, int height, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        this.writeChart(chart, width, height, out);
        out.flush();
        out.close();
    }

}
