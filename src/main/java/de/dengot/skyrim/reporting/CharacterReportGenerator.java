package de.dengot.skyrim.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jfree.chart.JFreeChart;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategoryProvider;
import de.dengot.skyrim.reporting.chart.CumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.DeltaBarChartProducer;

public class CharacterReportGenerator {

    private VelocityEngine velocity;
    private StatisticCategoryProvider statCatProvider;
    private File outputFolder;
    
    private Template statsPageTemplate;

    public CharacterReportGenerator() {
        velocity = new VelocityEngine();
        velocity.setProperty("resource.loader", "class");
        velocity.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocity.init();

        statCatProvider = StatisticCategoryProvider.getProvider();
        
        statsPageTemplate = loadTemplate("statspage.vm");
    }

    private Template loadTemplate(String filename) {
        return velocity.getTemplate("/de/dengot/skyrim/template/" + filename);
    }

    private void copyFileToOutput(String filename) throws IOException {
        IOUtils.copy(getClass().getResourceAsStream("/de/dengot/skyrim/template/" + filename),
                new FileOutputStream(new File(outputFolder, filename)));
    }

    public void createReport(SkyrimCharacterList characters, String outputPath) {
        try {
            this.outputFolder = new File(outputPath);

            if (!this.outputFolder.exists()) {
                this.outputFolder.mkdirs();
            }

            // copy index file
            copyFileToOutput("index.html");
            copyFileToOutput("frame-summary.html");
            copyFileToOutput("sca-styles.css");

            writeCategoriesFrame();
            writeAllStatNamesFrame();

            Set<StatisticCategory> categories =
                    StatisticCategoryProvider.getProvider().getCategories();
            for (StatisticCategory category : categories) {

                writeStatNamesFrame(category);

                for (String statName : category.getStatNames()) {
                    writeCharts(statName, characters);
                    writeStatsPage(statName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCategoriesFrame() throws IOException {
        Template template = loadTemplate("frame-categories.vm");

        VelocityContext context = new VelocityContext();

        context.put("categories", statCatProvider.getCategories());

        FileWriter writer = new FileWriter(new File(this.outputFolder, "frame-categories.html"));
        template.merge(context, writer);
        writer.flush();
        writer.close();
    }

    private void writeAllStatNamesFrame() throws IOException {
        Template template = loadTemplate("frame-category-content.vm");

        VelocityContext context = new VelocityContext();

        Set<StatisticCategory> categories = statCatProvider.getCategories();
        List<String> allStatNames = new ArrayList<String>();
        for (StatisticCategory category : categories) {
            allStatNames.addAll(category.getStatNames());
        }

        context.put("statNames", allStatNames);

        FileWriter writer = new FileWriter(new File(this.outputFolder, "frame-statnames.html"));
        template.merge(context, writer);
        writer.flush();
        writer.close();
    }

    private void writeStatNamesFrame(StatisticCategory category) throws IOException {
        Template template = loadTemplate("frame-category-content.vm");

        VelocityContext context = new VelocityContext();

        context.put("statNames", category.getStatNames());

        FileWriter writer =
                new FileWriter(new File(this.outputFolder, "frame-" + category.getName() + ".html"));
        template.merge(context, writer);
        writer.flush();
        writer.close();
    }

    private void writeStatsPage(String statName) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("statName", statName);
        FileWriter writer = new FileWriter(new File(this.outputFolder, statName + ".html"));
        statsPageTemplate.merge(context, writer);
        writer.flush();
        writer.close();
    }

    private void writeCharts(String statName, SkyrimCharacterList characters) throws IOException {
        JFreeChart cumulAreaChart =
                new CumulativeAreaChartProducer(statName).createChart(characters);
        writeChart(cumulAreaChart, "cumulative-areachart", statName);

        JFreeChart deltaBarChart = new DeltaBarChartProducer(statName).createChart(characters);
        writeChart(deltaBarChart, "delta-barchart", statName);
    }

    private void writeChart(JFreeChart chart, String chartName, String statName) throws IOException {
        ChartWriter chartWriter = new PngChartWriter();
        String filename = MessageFormat.format("{0} {1}.png", statName, chartName);
        File file = new File(this.outputFolder, filename);
        chartWriter.writeChart(chart, 1400, 800, file);
    }
}
