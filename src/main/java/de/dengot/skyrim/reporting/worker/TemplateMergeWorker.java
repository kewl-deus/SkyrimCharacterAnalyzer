package de.dengot.skyrim.reporting.worker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class TemplateMergeWorker implements Runnable {

    private Queue<TemplateMergePayload> workload;
    
    
    public TemplateMergeWorker() {
        super();
        workload = new LinkedList<TemplateMergePayload>();
    }

    public void enqueue(TemplateMergePayload payload) {
        workload.add(payload);
    }

    public void run() {

        try {
            TemplateMergePayload payload = null;
            while((payload = workload.poll()) != null){
                
                System.out.println("Writing " + payload.getOutputFile().getPath());
                
                FileWriter writer = new FileWriter(payload.getOutputFile());
                payload.getTemplate().merge(payload.getContext(), writer);
                writer.flush();
                writer.close();    
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
