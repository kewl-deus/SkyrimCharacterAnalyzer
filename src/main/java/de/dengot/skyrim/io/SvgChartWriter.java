package de.dengot.skyrim.io;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class SvgChartWriter extends ChartWriter{

    @Override
    public String getFilenameSuffix() {
        return "svg";
    }
    
    @Override
    public void writeChart(JFreeChart chart, int width, int height, OutputStream out) throws IOException {

        // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // draw the chart in the SVG generator
        chart.draw(svgGenerator, new Rectangle(width, height));

        // Write svg file
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        svgGenerator.stream(writer, true /* use css */);                       
        writer.flush();
    }


}
