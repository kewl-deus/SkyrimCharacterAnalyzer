package de.dengot.skyrim.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.jfree.chart.JFreeChart;

public abstract class ChartWriter {

    public abstract String getFilenameSuffix();
    
    public abstract void writeChart(JFreeChart chart, int width, int height, OutputStream out)
            throws IOException;

    public void writeChart(JFreeChart chart, int width, int height, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        this.writeChart(chart, width, height, out);
        out.flush();
        out.close();
    }

    public String suffixFilename(String filename){
        return MessageFormat.format("{0}.{1}", filename, getFilenameSuffix());
    }
}
