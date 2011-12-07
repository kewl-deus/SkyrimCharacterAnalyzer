package de.dengot.skyrim.reporting.chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jfree.data.statistics.HistogramBin;

public class Histogram implements Iterable<HistogramBin> {

    private LinkedList<HistogramBin> histogramBins;

    public Histogram(double[] values, int bins) {
        this(values, bins, getMinimum(values), getMaximum(values));
    }

    public Histogram(double[] values, int bins, double minimum, double maximum) {
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        } else if (bins < 1) {
            throw new IllegalArgumentException("The 'bins' value must be at least 1.");
        }
        histogramBins =
                new LinkedList<HistogramBin>(createHistogramBins("histgram", values, bins, minimum,
                        maximum));
    }

    public HistogramBin getMostFrequencyBin() {
        HistogramBin freqBin = getLowerBoundBin();

        for (HistogramBin hbin : this) {
            if (hbin.getCount() > freqBin.getCount()) {
                freqBin = hbin;
            }
        }
        return freqBin;
    }

    public Iterator<HistogramBin> iterator() {
        return histogramBins.iterator();
    }

    public int getBinCount() {
        return histogramBins.size();
    }

    public HistogramBin getLowerBoundBin() {
        return this.histogramBins.getFirst();
    }

    public HistogramBin getUpperBoundBin() {
        return this.histogramBins.getLast();
    }

    private List<HistogramBin> createHistogramBins(Comparable key, double[] values, int bins,
            double minimum, double maximum) {

        double binWidth = (maximum - minimum) / bins;

        double lower = minimum;
        double upper;
        List<HistogramBin> binList = new ArrayList<HistogramBin>(bins);
        for (int i = 0; i < bins; i++) {
            HistogramBin bin;
            // make sure bins[bins.length]'s upper boundary ends at maximum
            // to avoid the rounding issue. the bins[0] lower boundary is
            // guaranteed start from min
            if (i == bins - 1) {
                bin = new HistogramBin(lower, maximum);
            } else {
                upper = minimum + (i + 1) * binWidth;
                bin = new HistogramBin(lower, upper);
                lower = upper;
            }
            binList.add(bin);
        }
        // fill the bins
        for (int i = 0; i < values.length; i++) {
            int binIndex = bins - 1;
            if (values[i] < maximum) {
                double fraction = (values[i] - minimum) / (maximum - minimum);
                if (fraction < 0.0) {
                    fraction = 0.0;
                }
                binIndex = (int) (fraction * bins);
                // rounding could result in binIndex being equal to bins
                // which will cause an IndexOutOfBoundsException - see bug
                // report 1553088
                if (binIndex >= bins) {
                    binIndex = bins - 1;
                }
            }
            HistogramBin bin = (HistogramBin) binList.get(binIndex);
            bin.incrementCount();
        }
        return binList;
    }

    /**
     * Returns the minimum value in an array of values.
     * 
     * @param values
     *            the values (<code>null</code> not permitted and zero-length
     *            array not permitted).
     * 
     * @return The minimum value.
     */
    private static double getMinimum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("Null or zero length 'values' argument.");
        }
        double min = Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Returns the maximum value in an array of values.
     * 
     * @param values
     *            the values (<code>null</code> not permitted and zero-length
     *            array not permitted).
     * 
     * @return The maximum value.
     */
    private static double getMaximum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("Null or zero length 'values' argument.");
        }
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }
}
