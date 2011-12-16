package de.dengot.skyrim.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jfree.chart.JFreeChart;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.model.SkyrimAchievementProvider;
import de.dengot.skyrim.reporting.chart.playerprofile.AchievementTimelineChartProducer;
import de.dengot.skyrim.reporting.chart.playerprofile.PlayerProfileChartProducer;
import de.dengot.skyrim.reporting.chart.playerprofile.PlaytimeBarChartProducer;
import de.dengot.skyrim.reporting.table.Table;
import de.dengot.skyrim.reporting.table.TableRow;
import de.dengot.steamcommunityclient.model.Achievement;
import de.dengot.steamcommunityclient.model.PlayerProfile;

public class PlayerProfileReportGenerator {
    private static final XLogger LOGGER =
            XLoggerFactory.getXLogger(PlayerProfileReportGenerator.class);

    private static final int CHART_WIDTH = 1400;
    private static final int CHART_HEIGHT = 800;

    private VelocityEngine velocity;
    private File outputFolder;
    private ChartWriter chartWriter;
    private SkyrimAchievementProvider achievementProvider;

    private Map<String, Template> templates;

    public PlayerProfileReportGenerator(ChartWriter chartWriter) {
        this.chartWriter = chartWriter;

        this.velocity = new VelocityEngine();
        this.velocity.setProperty("resource.loader", "class");
        this.velocity.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        this.velocity.init();

        this.templates = new HashMap<String, Template>();

        this.achievementProvider = new SkyrimAchievementProvider();

    }

    public void createReport(Collection<PlayerProfile> profiles, String outputPath) {
        try {
            this.outputFolder = new File(outputPath);

            if (!this.outputFolder.exists()) {
                this.outputFolder.mkdirs();
            }
            copyToOutputFolder("skyrim_logo.jpg");
            copyToOutputFolder("steam-playerstats.css");
            copyToOutputFolder("steam-global.css");

            SortedSet<Achievement> achievements = achievementProvider.getAchievements();
            for (Achievement achievement : achievements) {
                IOUtils.copy(getClass().getResourceAsStream(
                        "/de/dengot/skyrim/template/steam/achievement/logos/"
                                + achievement.getLogo()), new FileOutputStream(new File(
                        outputFolder, achievement.getLogo())));
            }

            writePlayerstatsPage(profiles);
            writeChart(new AchievementTimelineChartProducer(true), "achievements-timeline-chart",
                    profiles);
            writeChart(new PlaytimeBarChartProducer(), "playtime-barchart", profiles);

        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    private void writeChart(PlayerProfileChartProducer chartProducer, String filename,
            Collection<PlayerProfile> profiles) throws IOException {
        JFreeChart chart = chartProducer.produceChart(profiles);
        this.chartWriter.writeChart(chart, CHART_WIDTH, CHART_HEIGHT, new File(this.outputFolder,
                this.chartWriter.suffixFilename(filename)));
    }

    private void writePlayerstatsPage(Collection<PlayerProfile> profiles) throws IOException {

        SortedSet<Achievement> achievements = achievementProvider.getAchievements();
        Map<String, Table> achieveRankings = new HashMap<String, Table>();

        for (Achievement achievement : achievements) {
            Table rankingTab = createRankingTable(profiles, achievement);
            achieveRankings.put(achievement.getApiName(), rankingTab);
        }

        VelocityContext context = new VelocityContext();
        context.put("achievements", achievements);
        context.put("achieveRankings", achieveRankings);

        FileWriter writer = new FileWriter(new File(this.outputFolder, "steam-playerstats.html"));
        Template template = loadTemplate("steam-playerstats");
        template.merge(context, writer);
        writer.flush();
        writer.close();
    }

    private Table<String> createRankingTable(Collection<PlayerProfile> profiles,
            Achievement achievement) {

        SimpleDateFormat dateFormat = new SimpleDateFormat();

        Table<String> table = new Table<String>();
        table.addColumn("rankingPosition");
        table.addColumn("playerName");
        table.addColumn("unlockTime");

        List<PlayerProfile> list = new LinkedList<PlayerProfile>(profiles);
        Collections.sort(list, new PlayerProfileByAchievementUnlockComparator(achievement
                .getApiName()));
        Iterator<PlayerProfile> it = list.iterator();
        int i = 0;
        while (i < 3 && it.hasNext()) {
            PlayerProfile profile = it.next();
            i++;

            if (!profile.hasAchievement(achievement.getApiName())) {
                break;
            }

            TableRow<String> newRow = table.newRow();
            newRow.setCell("rankingPosition", i + ".");
            newRow.setCell("playerName", profile.getSteamName());
            newRow.setCell("unlockTime", MessageFormat.format("({0,date,medium})", profile
                    .getUnlockTime(achievement.getApiName()).getTime()));
        }

        return table;
    }

    private synchronized Template loadTemplate(String filename) {
        Template template = templates.get(filename);
        if (template == null) {
            template = velocity.getTemplate("/de/dengot/skyrim/template/steam/" + filename + ".html");
            templates.put(filename, template);
        }
        return template;
    }

    private void copyToOutputFolder(String filename) throws IOException {
        IOUtils.copy(
                getClass().getResourceAsStream("/de/dengot/skyrim/template/steam/" + filename),
                new FileOutputStream(new File(outputFolder, filename)));
    }

    private class PlayerProfileByAchievementUnlockComparator implements Comparator<PlayerProfile> {

        private String achievementApiName;

        private Calendar lockedTimestamp;

        public PlayerProfileByAchievementUnlockComparator(String achievementApiName) {
            super();
            this.achievementApiName = achievementApiName;
            this.lockedTimestamp = Calendar.getInstance();
            this.lockedTimestamp.setTimeInMillis(Long.MAX_VALUE);
        }

        public int compare(PlayerProfile p1, PlayerProfile p2) {
            Calendar cal1 =
                    p1.hasAchievement(achievementApiName) ? p1.getUnlockTime(achievementApiName)
                            : lockedTimestamp;
            Calendar cal2 =
                    p2.hasAchievement(achievementApiName) ? p2.getUnlockTime(achievementApiName)
                            : lockedTimestamp;

            return cal1.compareTo(cal2);
        }
    };
}