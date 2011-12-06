package de.dengot.skyrim.reporting.worker;

import java.io.File;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class TemplateMergePayload {

    private Template template;
    private VelocityContext context;
    private File outputFile;

    public TemplateMergePayload(Template template, VelocityContext context, File outputFile) {
        super();
        this.template = template;
        this.context = context;
        this.outputFile = outputFile;
    }

    public Template getTemplate() {
        return template;
    }

    public VelocityContext getContext() {
        return context;
    }

    public File getOutputFile() {
        return outputFile;
    }

    
}
