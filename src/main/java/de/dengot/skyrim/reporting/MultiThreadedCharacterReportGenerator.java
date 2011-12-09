package de.dengot.skyrim.reporting;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jfree.chart.JFreeChart;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategoryProvider;
import de.dengot.skyrim.reporting.chart.CategoryBarChartProducer;
import de.dengot.skyrim.reporting.chart.CumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.DeltaBarChartProducer;
import de.dengot.skyrim.reporting.chart.LevelBarChartProducer;
import de.dengot.skyrim.reporting.chart.LevelCumulativeAreaChartProducer;
import de.dengot.skyrim.reporting.chart.LevelDeltaBarChartProducer;
import de.dengot.skyrim.reporting.table.Table;
import de.dengot.skyrim.reporting.table.TableRow;
import de.dengot.skyrim.reporting.worker.ChartProductionWorker;
import de.dengot.skyrim.reporting.worker.ChartProductionWorkload;
import de.dengot.skyrim.reporting.worker.TemplateMergeWorker;
import de.dengot.skyrim.reporting.worker.TemplateMergeWorkload;

public class MultiThreadedCharacterReportGenerator extends CharacterReportGenerator {

    private static final XLogger LOGGER =
            XLoggerFactory.getXLogger(MultiThreadedCharacterReportGenerator.class);

    private static final int CHART_WIDTH = 1400;
    private static final int CHART_HEIGHT = 800;

    private VelocityEngine velocity;
    private StatisticCategoryProvider statCatProvider;
    private File outputFolder;
    private ChartWriter chartWriter;

    private Map<String, Template> templates;

    public MultiThreadedCharacterReportGenerator(ChartWriter chartWriter) {
        this.chartWriter = chartWriter;

        this.velocity = new VelocityEngine();
        this.velocity.setProperty("resource.loader", "class");
        this.velocity.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        this.velocity.init();

        this.statCatProvider = StatisticCategoryProvider.getProvider();

        this.templates = new HashMap<String, Template>();

    }

    private synchronized Template loadTemplate(String filename) {
        Template template = templates.get(filename);
        if (template == null) {
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

            // collect workload
            List<ChartProductionWorkload> chartWorkloads =
                    new LinkedList<ChartProductionWorkload>();
            List<TemplateMergeWorkload> pageWorkloads = new LinkedList<TemplateMergeWorkload>();

            Set<StatisticCategory> categories = statCatProvider.getCategories();
            for (StatisticCategory category : categories) {

                writeCategorySummaryChart(category, characters);

                chartWorkloads.addAll(createCategoryChartsWorkload(category, characters));
                pageWorkloads.add(createStatNamesFrameWorkload(category));
                pageWorkloads.add(createCategorySummaryFrameWorkload(category, characters));

                for (String statName : category.getStatNames()) {
                    TemplateMergeWorkload statsPageWorload =
                            createStatsPageWorkload(statName, characters);
                    pageWorkloads.add(statsPageWorload);
                }
            }

            // process workload
            processChartWorkload(8, chartWorkloads);
            processPageWorkload(8, pageWorkloads);

        } catch (IOException e) {
            LOGGER.catching(e);
        } catch (InterruptedException ie) {
            LOGGER.catching(ie);
        }
    }

    private void processChartWorkload(int threadCount, List<ChartProductionWorkload> chartWorkloads)
            throws InterruptedException {

        ChartProductionWorker[] workers = new ChartProductionWorker[threadCount];
        for (int i = 0; i < chartWorkloads.size(); i++) {
            int workerNo = i % threadCount;
            ChartProductionWorker worker = workers[workerNo];
            if (worker == null) {
                worker = new ChartProductionWorker();
                workers[workerNo] = worker;
            }
            worker.enqueue(chartWorkloads.get(i));
        }

        processParallel(workers);
    }

    private void processPageWorkload(int threadCount, List<TemplateMergeWorkload> pageWorkload)
            throws InterruptedException {

        TemplateMergeWorker[] workers = new TemplateMergeWorker[threadCount];
        for (int i = 0; i < pageWorkload.size(); i++) {
            int workerNo = i % threadCount;
            TemplateMergeWorker worker = workers[workerNo];
            if (worker == null) {
                worker = new TemplateMergeWorker();
                workers[workerNo] = worker;
            }
            worker.enqueue(pageWorkload.get(i));
        }
        processParallel(workers);

    }

    private void processParallel(Runnable... workers) throws InterruptedException {
        List<Thread> threads = new LinkedList<Thread>();
        for (Runnable worker : workers) {
            Thread thread = new Thread(worker);
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private List<ChartProductionWorkload> createCategoryChartsWorkload(
            StatisticCategory statsCategory, SkyrimCharacterList characters) {

        List<ChartProductionWorkload> workloadList = new ArrayList<ChartProductionWorkload>();

        for (String statName : statsCategory.getStatNames()) {

            String filenameCumulAreaChart =
                    this.chartWriter.suffixFilename(format("{0} {1}", statName,
                            "cumulative-areachart"));
            ChartProductionWorkload workloadCumulAreaChart =
                    new ChartProductionWorkload(new CumulativeAreaChartProducer(statName),
                            this.chartWriter, new File(this.outputFolder, filenameCumulAreaChart),
                            characters, CHART_WIDTH, CHART_HEIGHT);
            workloadList.add(workloadCumulAreaChart);

            String filenameDeltaBarChart =
                    this.chartWriter.suffixFilename(format("{0} {1}", statName, "delta-barchart"));
            ChartProductionWorkload workloadDeltaBarChart =
                    new ChartProductionWorkload(new DeltaBarChartProducer(statName), chartWriter,
                            new File(this.outputFolder, filenameDeltaBarChart), characters,
                            CHART_WIDTH, CHART_HEIGHT);
            workloadList.add(workloadDeltaBarChart);
        }

        return workloadList;
    }

    private void writeCategorySummaryChart(StatisticCategory category,
            SkyrimCharacterList characters) throws IOException {
        CategoryBarChartProducer catBarProducer = new CategoryBarChartProducer(category);
        JFreeChart catSummaryBarChart = catBarProducer.createChart(characters);
        writeChart(catSummaryBarChart, format("{0} category-summarybar-chart", category.getName()));
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

    private TemplateMergeWorkload createStatNamesFrameWorkload(StatisticCategory category)
            throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("statNames", category.getStatNames());
        File outputFile = new File(this.outputFolder, "frame-" + category.getName() + ".html");
        return new TemplateMergeWorkload(loadTemplate("frame-category-content.vm"), context,
                outputFile);
    }

    private TemplateMergeWorkload createStatsPageWorkload(String statName,
            SkyrimCharacterList characters) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("statName", statName);
        context.put("playerMaxValueTable", createStatsTable(statName, characters));
        File outputFile = new File(this.outputFolder, statName + ".html");
        return new TemplateMergeWorkload(loadTemplate("statspage.vm"), context, outputFile);
    }

    private TemplateMergeWorkload createCategorySummaryFrameWorkload(StatisticCategory category,
            SkyrimCharacterList characters) {
        VelocityContext context = new VelocityContext();

        context.put("cat", category);
        context.put("playerMaxValueTable", createSummaryTable(characters, Collections
                .singleton(category)));

        File outputFile =
                new File(this.outputFolder, "frame-summary-" + category.getName() + ".html");
        return new TemplateMergeWorkload(loadTemplate("category-summarypage.vm"), context,
                outputFile);
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
        String filename = chartWriter.suffixFilename(chartName);
        File file = new File(this.outputFolder, filename);

        LOGGER.trace("Writing " + file.getPath());

        chartWriter.writeChart(chart, CHART_WIDTH, CHART_HEIGHT, file);
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
