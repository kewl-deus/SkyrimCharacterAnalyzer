package de.dengot.skyrim.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jfree.chart.JFreeChart;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategoryProvider;
import de.dengot.skyrim.reporting.chart.CategoryBarChartProducer;
import de.dengot.skyrim.reporting.chart.LevelBarChartProducer;
import de.dengot.skyrim.reporting.chart.LevelCumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.LevelDeltaBarChartProducer;
import de.dengot.skyrim.reporting.worker.ChartProductionWorker;
import de.dengot.skyrim.reporting.worker.TemplateMergePayload;
import de.dengot.skyrim.reporting.worker.TemplateMergeWorker;

public class MultiThreadedCharacterReportGenerator {

    private VelocityEngine velocity;
    private StatisticCategoryProvider statCatProvider;
    private File outputFolder;

    private Template statsPageTemplate;

    public MultiThreadedCharacterReportGenerator() {
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

            // copy files untouched
            copyFileToOutput("index.html");
            copyFileToOutput("sca-styles.css");
            
            writeMainSummaryFrame(characters);
            writeMainSummaryCharts(characters);

            writeCategoriesFrame();
            writeAllStatNamesFrame();

            Queue<Thread> threads = new LinkedList<Thread>();

            Set<StatisticCategory> categories =
                    StatisticCategoryProvider.getProvider().getCategories();

            for (StatisticCategory category : categories) {

                writeCategorySummaryChart(category, characters);

                ChartProductionWorker chartWorker =
                        new ChartProductionWorker(characters, category, outputFolder);
                threads.add(new Thread(chartWorker));

                TemplateMergeWorker templateWorker = new TemplateMergeWorker();
                threads.add(new Thread(templateWorker));

                templateWorker.enqueue(createStatNamesFramePayload(category));
                templateWorker.enqueue(createCategorySummaryFramePayload(category));

                for (String statName : category.getStatNames()) {
                    TemplateMergePayload statsPagePaylod =
                            createStatsPagePayload(statName, characters);
                    templateWorker.enqueue(statsPagePaylod);
                }
            }

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }

    private void writeCategorySummaryChart(StatisticCategory category,
            SkyrimCharacterList characters) throws IOException {
        CategoryBarChartProducer catBarProducer = new CategoryBarChartProducer(category);
        JFreeChart catSummaryBarChart = catBarProducer.createChart(characters);
        writeChart(catSummaryBarChart, MessageFormat.format("{0} category-summarybar-chart",
                category.getName()));
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

    private TemplateMergePayload createStatNamesFramePayload(StatisticCategory category)
            throws IOException {
        Template template = loadTemplate("frame-category-content.vm");
        VelocityContext context = new VelocityContext();
        context.put("statNames", category.getStatNames());
        File outputFile = new File(this.outputFolder, "frame-" + category.getName() + ".html");
        return new TemplateMergePayload(template, context, outputFile);
    }

    private TemplateMergePayload createStatsPagePayload(String statName,
            SkyrimCharacterList characters) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("statName", statName);
        context.put("playerMaxValueTable", createStatsTable(statName, characters));
        File outputFile = new File(this.outputFolder, statName + ".html");
        return new TemplateMergePayload(statsPageTemplate, context, outputFile);
    }

    private TemplateMergePayload createCategorySummaryFramePayload(StatisticCategory category) {
        Template template = loadTemplate("category-summarypage.vm");
        VelocityContext context = new VelocityContext();
        context.put("cat", category);
        // TODO add table
        File outputFile =
                new File(this.outputFolder, "frame-summary-" + category.getName() + ".html");
        return new TemplateMergePayload(template, context, outputFile);
    }

    private Table<Integer> createStatsTable(String statName, SkyrimCharacterList characters) {
        Table<Integer> table = new Table<Integer>();

        for (SkyrimCharacter skyrimCharacter : characters) {
            table.addColumn(skyrimCharacter.getName());
        }

        Map<String, Integer> newRow = table.newRow();

        for (SkyrimCharacter skyrimCharacter : characters) {
            int maxVal = skyrimCharacter.getMaxValue(statName);
            newRow.put(skyrimCharacter.getName(), maxVal);
        }
        return table;
    }

    private void writeChart(JFreeChart chart, String chartName) throws IOException {
        ChartWriter chartWriter = new PngChartWriter();
        String filename = MessageFormat.format("{0}.png", chartName);
        File file = new File(this.outputFolder, filename);

        System.out.println("Writing " + file.getPath());

        chartWriter.writeChart(chart, 1400, 800, file);
    }

    private Table<String> createMainSummaryTable(SkyrimCharacterList characters) {
        Table<String> table = new Table<String>();

        table.addColumn("Statistic");
        for (SkyrimCharacter skyrimCharacter : characters) {
            table.addColumn(skyrimCharacter.getName());
        }

        Set<StatisticCategory> categories = StatisticCategoryProvider.getProvider().getCategories();

        for (StatisticCategory category : categories) {
            for (String statName : category.getStatNames()) {

                Map<String, String> newRow = table.newRow();
                newRow.put("Statistic", statName);

                for (SkyrimCharacter skyrimCharacter : characters) {
                    int maxVal = skyrimCharacter.getMaxValue(statName);
                    newRow.put(skyrimCharacter.getName(), String.valueOf(maxVal));
                }
            }
        }
        return table;
    }

    private void writeMainSummaryFrame(SkyrimCharacterList characters) throws IOException {
        Template template = loadTemplate("frame-summary.vm");

        VelocityContext context = new VelocityContext();

        context.put("playerMaxValueTable", createMainSummaryTable(characters));

        FileWriter writer = new FileWriter(new File(this.outputFolder, "frame-summary.html"));
        template.merge(context, writer);
        writer.flush();
        writer.close();
    }

    private void writeMainSummaryCharts(SkyrimCharacterList characters) throws IOException {
        JFreeChart levelBarChart = new LevelBarChartProducer().createChart(characters);
        JFreeChart levelCumulAreaChart =
                new LevelCumulativeAreaChartProducer().createChart(characters);
        JFreeChart levelDeltaBarChart = new LevelDeltaBarChartProducer().createChart(characters);

        writeChart(levelBarChart, "level-barchart");
        writeChart(levelCumulAreaChart, "level-cumulative-areachart");
        writeChart(levelDeltaBarChart, "level-delta-barchart");
    }
}
