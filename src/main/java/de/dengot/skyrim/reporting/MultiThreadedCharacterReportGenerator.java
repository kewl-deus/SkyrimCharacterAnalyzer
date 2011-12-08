package de.dengot.skyrim.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

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
import de.dengot.skyrim.reporting.table.Table;
import de.dengot.skyrim.reporting.table.TableRow;
import de.dengot.skyrim.reporting.worker.ChartProductionWorker;
import de.dengot.skyrim.reporting.worker.TemplateMergeWorker;
import de.dengot.skyrim.reporting.worker.TemplateMergeWorkload;

public class MultiThreadedCharacterReportGenerator extends CharacterReportGenerator {

    private static final XLogger LOGGER =
            XLoggerFactory.getXLogger(MultiThreadedCharacterReportGenerator.class);

    private VelocityEngine velocity;
    private StatisticCategoryProvider statCatProvider;
    private File outputFolder;

    private Map<String, Template> templates;

    public MultiThreadedCharacterReportGenerator() {
        velocity = new VelocityEngine();
        velocity.setProperty("resource.loader", "class");
        velocity.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocity.init();

        statCatProvider = StatisticCategoryProvider.getProvider();
        
        templates = new HashMap<String, Template>();
    }

    private synchronized Template loadTemplate(String filename) {
        Template template = templates.get(filename);
        if (template == null){
            template = velocity.getTemplate("/de/dengot/skyrim/template/" + filename);
            templates.put(filename, template);
        }
        return template;
    }

    private void copyToOutputFolder(String filename) throws IOException {
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
            copyToOutputFolder("index.html");
            copyToOutputFolder("sca-styles.css");
            copyToOutputFolder("star_yellow.gif");

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
                templateWorker.enqueue(createCategorySummaryFramePayload(category, characters));

                for (String statName : category.getStatNames()) {
                    TemplateMergeWorkload statsPagePaylod =
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
            LOGGER.catching(e);
        } catch (InterruptedException ie) {
            LOGGER.catching(ie);
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
        VelocityContext context = new VelocityContext();

        context.put("categories", statCatProvider.getCategories());

        FileWriter writer = new FileWriter(new File(this.outputFolder, "frame-categories.html"));
        loadTemplate("frame-categories.vm").merge(context, writer);
        writer.flush();
        writer.close();
    }

    private void writeAllStatNamesFrame() throws IOException {
        VelocityContext context = new VelocityContext();

        Set<StatisticCategory> categories = statCatProvider.getCategories();
        List<String> allStatNames = new ArrayList<String>();
        for (StatisticCategory category : categories) {
            allStatNames.addAll(category.getStatNames());
        }

        Collections.sort(allStatNames);
        context.put("statNames", allStatNames);

        FileWriter writer = new FileWriter(new File(this.outputFolder, "frame-statnames.html"));
        loadTemplate("frame-category-content.vm").merge(context, writer);
        writer.flush();
        writer.close();
    }

    private TemplateMergeWorkload createStatNamesFramePayload(StatisticCategory category)
            throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("statNames", category.getStatNames());
        File outputFile = new File(this.outputFolder, "frame-" + category.getName() + ".html");
        return new TemplateMergeWorkload(loadTemplate("frame-category-content.vm"), context, outputFile);
    }

    private TemplateMergeWorkload createStatsPagePayload(String statName,
            SkyrimCharacterList characters) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("statName", statName);
        context.put("playerMaxValueTable", createStatsTable(statName, characters));
        File outputFile = new File(this.outputFolder, statName + ".html");
        return new TemplateMergeWorkload(loadTemplate("statspage.vm"), context, outputFile);
    }

    private TemplateMergeWorkload createCategorySummaryFramePayload(StatisticCategory category,
            SkyrimCharacterList characters) {
        VelocityContext context = new VelocityContext();

        context.put("cat", category);
        context.put("playerMaxValueTable", createSummaryTable(characters, Collections
                .singleton(category)));

        File outputFile =
                new File(this.outputFolder, "frame-summary-" + category.getName() + ".html");
        return new TemplateMergeWorkload(loadTemplate("category-summarypage.vm"), context, outputFile);
    }

    private Table<Integer> createStatsTable(String statName, SkyrimCharacterList characters) {
        Table<Integer> table = new Table<Integer>();

        for (SkyrimCharacter skyrimCharacter : characters) {
            table.addColumn(skyrimCharacter.getName());
        }

        TableRow<Integer> newRow = table.newRow();
        newRow.setDefaulCellValue(0);

        for (SkyrimCharacter skyrimCharacter : characters) {
            int maxVal = skyrimCharacter.getMaxValue(statName);
            newRow.setCell(skyrimCharacter.getName(), maxVal);
        }
        return table;
    }

    private void writeChart(JFreeChart chart, String chartName) throws IOException {
        ChartWriter chartWriter = new PngChartWriter();
        String filename = MessageFormat.format("{0}.png", chartName);
        File file = new File(this.outputFolder, filename);

        LOGGER.trace("Writing " + file.getPath());

        chartWriter.writeChart(chart, 1400, 800, file);
    }

    private Table<String> createSummaryTable(SkyrimCharacterList characters,
            Set<StatisticCategory> categories) {

        Table<String> table = new Table<String>();
        
        final NumberFormat integerFormat = NumberFormat.getIntegerInstance();

        Comparator<String> cellValueComparator = new Comparator<String>() {
            public int compare(String s1, String s2) {
                int thisVal = Integer.MIN_VALUE;
                int anotherVal = Integer.MIN_VALUE;
                
                try {
                    thisVal = integerFormat.parse(s1).intValue();
                } catch (ParseException e) {
                }

                try {
                    anotherVal = integerFormat.parse(s2).intValue();
                } catch (ParseException e) {
                }

                return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
            }
        };

        table.addColumn("Statistic");
        for (SkyrimCharacter skyrimCharacter : characters) {
            table.addColumn(skyrimCharacter.getName());
        }

        List<String> allStatNames = new ArrayList<String>();
        for (StatisticCategory category : categories) {
            for (String statName : category.getStatNames()) {
                allStatNames.add(statName);
            }
        }

        Collections.sort(allStatNames);
        for (String statName : allStatNames) {
            TableRow<String> newRow = new TableRow<String>(cellValueComparator);
            newRow.setDefaulCellValue("0");
            table.addRow(newRow);
            newRow.setCell("Statistic", statName);

            for (SkyrimCharacter skyrimCharacter : characters) {
                int maxVal = skyrimCharacter.getMaxValue(statName);
                newRow.setCell(skyrimCharacter.getName(), integerFormat.format(maxVal));
            }

        }

        // write sum values in footer
        TableRow<String> footer = table.createFooter();
        footer.setDefaulCellValue("0");
        footer.setCell("Statistic", "Leads");
        for (TableRow<String> row : table.getRows()) {
            for (String column : table.getColumns()) {
                if (row.isMaxValue(column)) {
                    String cell = footer.getCell(column);
                    int starCounter = 1;
                    try {
                        starCounter += Integer.parseInt(cell);
                    } catch (NumberFormatException e) {

                    }
                    footer.setCell(column, String.valueOf(starCounter));
                }
            }
        }

        return table;
    }

    private void writeMainSummaryFrame(SkyrimCharacterList characters) throws IOException {
        VelocityContext context = new VelocityContext();

        context.put("playerMaxValueTable", createSummaryTable(characters, statCatProvider
                .getCategories()));

        FileWriter writer = new FileWriter(new File(this.outputFolder, "frame-summary.html"));
        loadTemplate("frame-summary.vm").merge(context, writer);
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
