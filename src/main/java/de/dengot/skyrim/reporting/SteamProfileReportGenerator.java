package de.dengot.skyrim.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.model.SkyrimConstants;
import de.dengot.skyrim.model.steam.AchievementProvider;
import de.dengot.skyrim.model.steam.AchievementUnlock;
import de.dengot.skyrim.model.steam.SteamAchievement;
import de.dengot.skyrim.model.steam.SteamProfile;
import de.dengot.skyrim.reporting.table.Table;
import de.dengot.skyrim.reporting.table.TableRow;

public class SteamProfileReportGenerator {
    private static final XLogger LOGGER =
            XLoggerFactory.getXLogger(SteamProfileReportGenerator.class);

    private static final int CHART_WIDTH = 1400;
    private static final int CHART_HEIGHT = 800;

    private VelocityEngine velocity;
    private File outputFolder;
    private ChartWriter chartWriter;
    private AchievementProvider achievementProvider;

    private Map<String, Template> templates;

    public SteamProfileReportGenerator(ChartWriter chartWriter) {
        this.chartWriter = chartWriter;

        this.velocity = new VelocityEngine();
        this.velocity.setProperty("resource.loader", "class");
        this.velocity.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        this.velocity.init();

        this.templates = new HashMap<String, Template>();

        this.achievementProvider = new AchievementProvider();

    }

    public void createReport(Collection<SteamProfile> profiles, String outputPath) {
        try {
            this.outputFolder = new File(outputPath);

            if (!this.outputFolder.exists()) {
                this.outputFolder.mkdirs();
            }
            copyToOutputFolder("skyrim_logo.jpg");
            copyToOutputFolder("steam-playerstats.css");
            copyToOutputFolder("steam-global.css");

            SortedSet<SteamAchievement> achievements = achievementProvider.getAchievements();
            for (SteamAchievement achievement : achievements) {
                IOUtils.copy(getClass().getResourceAsStream(
                        "/de/dengot/skyrim/template/steam/achievement/logos/"
                                + achievement.getLogo()), new FileOutputStream(new File(
                        outputFolder, achievement.getLogo())));
            }

            writePlayerstatsPage(profiles);
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    private void writePlayerstatsPage(Collection<SteamProfile> profiles) throws IOException {

        SortedSet<SteamAchievement> achievements = achievementProvider.getAchievements();
        Map<String, Table> achieveRankings = new HashMap<String, Table>();

        for (SteamAchievement achievement : achievements) {
            Table rankingTab = createRankingTable(profiles, achievement);
            achieveRankings.put(achievement.getApiName(), rankingTab);
        }

        VelocityContext context = new VelocityContext();
        context.put("achievements", achievements);
        context.put("achieveRankings", achieveRankings);

        FileWriter writer = new FileWriter(new File(this.outputFolder, "steam-playerstats.html"));
        Template template = loadTemplate("steam-playerstats.vm");
        template.merge(context, writer);
        writer.flush();
        writer.close();
    }

    private Table<String> createRankingTable(Collection<SteamProfile> profiles,
            SteamAchievement achievement) {

        SimpleDateFormat dateFormat = new SimpleDateFormat();

        Table<String> table = new Table<String>();
        table.addColumn("rankingPosition");
        table.addColumn("playerName");
        table.addColumn("unlockTime");

        List<SteamProfile> list = new LinkedList<SteamProfile>(profiles);
        Collections.sort(list, new SteamProfileByAchievementUnlockComparator(
                SkyrimConstants.STEAM_GAME_NAME, achievement.getApiName()));
        Iterator<SteamProfile> it = list.iterator();
        int i = 0;
        while (i < 3 && it.hasNext()) {
            SteamProfile profile = it.next();
            i++;

            AchievementUnlock unlock =
                    profile.getAchievementUnlock(SkyrimConstants.STEAM_GAME_NAME, achievement
                            .getApiName());
            if (unlock == null) {
                break;
            }

            TableRow<String> newRow = table.newRow();
            newRow.setCell("rankingPosition", String.valueOf(i));
            newRow.setCell("playerName", profile.getAccountName());
            newRow.setCell("unlockTime", dateFormat.format(unlock.getUnlockTime().getTime()));
        }

        return table;
    }

    private synchronized Template loadTemplate(String filename) {
        Template template = templates.get(filename);
        if (template == null) {
            template = velocity.getTemplate("/de/dengot/skyrim/template/steam/" + filename);
            templates.put(filename, template);
        }
        return template;
    }

    private void copyToOutputFolder(String filename) throws IOException {
        IOUtils.copy(
                getClass().getResourceAsStream("/de/dengot/skyrim/template/steam/" + filename),
                new FileOutputStream(new File(outputFolder, filename)));
    }

    private class SteamProfileByAchievementUnlockComparator implements Comparator<SteamProfile> {

        private String game;
        private String achievementApiName;

        private Calendar lockedTimestamp;

        public SteamProfileByAchievementUnlockComparator(String game, String achievementApiName) {
            super();
            this.game = game;
            this.achievementApiName = achievementApiName;
            this.lockedTimestamp = Calendar.getInstance();
            this.lockedTimestamp.setTimeInMillis(Long.MAX_VALUE);
        }

        public int compare(SteamProfile p1, SteamProfile p2) {
            AchievementUnlock au1 = p1.getAchievementUnlock(game, achievementApiName);
            AchievementUnlock au2 = p2.getAchievementUnlock(game, achievementApiName);

            Calendar cal1 = au1 == null ? lockedTimestamp : au1.getUnlockTime();
            Calendar cal2 = au2 == null ? lockedTimestamp : au2.getUnlockTime();

            return cal1.compareTo(cal2);
        }
    };
}