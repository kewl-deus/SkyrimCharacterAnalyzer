package de.dengot.skyrim.test;

import static org.testng.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.testng.annotations.Test;

import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.model.steam.AchievementProvider;
import de.dengot.skyrim.model.steam.SteamProfile;
import de.dengot.skyrim.model.steam.SteamProfileDataFetcher;
import de.dengot.skyrim.reporting.SteamProfileReportGenerator;
import de.dengot.steamcommunityclient.SteamCommunityClient;

public class TestSteamReportGenerator {

    @Test
    public void testGeneration() {
        Collection<SteamProfile> profiles = new LinkedList<SteamProfile>();
        profiles.add(new SteamProfile(76561197963574585L, "kewl-deus"));
        profiles.add(new SteamProfile(76561197977709598L, "gabba_the_hutt"));

        SteamCommunityClient communityClient = new SteamCommunityClient("192.168.69.6", 8080);
        SteamProfileDataFetcher dataFetcher = new SteamProfileDataFetcher(communityClient);
        dataFetcher.fetchAchievements(profiles);

        SteamProfileReportGenerator generator =
                new SteamProfileReportGenerator(new PngChartWriter());
        generator.createReport(profiles, "c:/temp/skyrimreport");
    }

    @Test(enabled = false)
    public void testAchievements() {
        // Locale.setDefault(Locale.ENGLISH);
        AchievementProvider provider = new AchievementProvider();
        assertEquals(provider.getAchievements().size(), 50);
        assertEquals(provider.getAchievement("new_achievement_16_0").getName(), "Darkness Returns");
    }

    public void testTimestampParsing() {
        // Nov 11, 2011 7:27pm
        long ts = 1321036057;

        Calendar cal;
        // cal = Calendar.getInstance();
        cal = new GregorianCalendar(2011, Calendar.NOVEMBER, 11, 19, 27);
        System.out.println("ts = " + ts);
        System.out.println("date = " + cal.getTimeInMillis());
        System.out.println("diff = " + (cal.getTimeInMillis() - ts));

        cal.setTimeInMillis(ts * 1000);

        DateFormat df = new SimpleDateFormat();
        System.out.println(df.format(cal.getTime()));
    }
}
