package de.dengot.skyrim.reporting.worker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.jfree.chart.JFreeChart;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.reporting.chart.ChartProducer;

public class ChartProductionWorker implements Runnable {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ChartProductionWorker.class);

    private Queue<ChartProductionWorkload> workloadQueue;

    public ChartProductionWorker() {
        super();
        workloadQueue = new LinkedList<ChartProductionWorkload>();
    }

    public void enqueue(ChartProductionWorkload workload) {
        workloadQueue.add(workload);
    }

    public void run() {

        try {
            ChartProductionWorkload workload = null;
            while ((workload = workloadQueue.poll()) != null) {

                LOGGER.trace("Writing " + workload.getOutputFile().getPath());

                ChartProducer producer = workload.getChartProducer();
                JFreeChart chart = producer.createChart(workload.getSkyrimCharacters());
                ChartWriter writer = workload.getChartWriter();
                writer.writeChart(chart, workload.getWidth(), workload.getHeight(), workload
                        .getOutputFile());

            }

        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }
}
