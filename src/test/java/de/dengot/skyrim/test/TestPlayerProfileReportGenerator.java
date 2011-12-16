package de.dengot.skyrim.test;

import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.Test;

import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.model.SkyrimConstants;
import de.dengot.skyrim.reporting.PlayerProfileReportGenerator;
import de.dengot.steamcommunityclient.SteamCommunityClient;
import de.dengot.steamcommunityclient.model.PlayerProfile;

public class TestPlayerProfileReportGenerator {

    @Test
    public void testGeneration() {

        long kewldeusSteamId = 76561197963574585L;
        long gabbaTheHuttSteamId = 76561197977709598L;

        long[] steamIds = { kewldeusSteamId, gabbaTheHuttSteamId };
        List<PlayerProfile> profiles = new LinkedList<PlayerProfile>();

        SteamCommunityClient communityClient = new SteamCommunityClient();
        for (int i = 0; i < steamIds.length; i++) {
            profiles.add(communityClient.getPlayerProfile(steamIds[i],
                    SkyrimConstants.STEAM_GAME_NAME));
        }

        PlayerProfileReportGenerator generator =
                new PlayerProfileReportGenerator(new PngChartWriter());
        generator.createReport(profiles, "c:/temp/skyrimreport");
    }

}
