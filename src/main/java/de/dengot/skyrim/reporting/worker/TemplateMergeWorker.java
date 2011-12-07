package de.dengot.skyrim.reporting.worker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class TemplateMergeWorker implements Runnable {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TemplateMergeWorker.class);

    private Queue<TemplateMergeWorkload> workloadQueue;

    public TemplateMergeWorker() {
        super();
        workloadQueue = new LinkedList<TemplateMergeWorkload>();
    }

    public void enqueue(TemplateMergeWorkload workload) {
        workloadQueue.add(workload);
    }

    public void run() {

        try {
            TemplateMergeWorkload workload = null;
            while ((workload = workloadQueue.poll()) != null) {

                LOGGER.trace("Writing " + workload.getOutputFile().getPath());

                FileWriter writer = new FileWriter(workload.getOutputFile());
                workload.getTemplate().merge(workload.getContext(), writer);
                writer.flush();
                writer.close();
            }

        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

}
