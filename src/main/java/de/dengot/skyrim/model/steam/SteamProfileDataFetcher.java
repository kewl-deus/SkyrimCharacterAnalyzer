package de.dengot.skyrim.model.steam;

import generated.Playerstats;
import generated.Playerstats.Achievements.Achievement;

import java.util.Calendar;
import java.util.Collection;

import de.dengot.skyrim.model.SkyrimConstants;
import de.dengot.steamcommunityclient.SteamCommunityClient;

public class SteamProfileDataFetcher {

    private SteamCommunityClient communityClient;
    private AchievementProvider achievementProvider;

    public SteamProfileDataFetcher(SteamCommunityClient communityClient) {
        super();
        this.communityClient = communityClient;
        this.achievementProvider = new AchievementProvider();
    }

    public void fetchAchievements(Collection<SteamProfile> profiles) {
        for (SteamProfile profile : profiles) {
            Playerstats playerstats =
                    communityClient.getPlayerstats(profile.getAccountId(),
                            SkyrimConstants.STEAM_GAME_NAME);

            for (Achievement achieve : playerstats.getAchievements().getAchievement()) {
                if (achieve.getClosed() == 1) {

                    SteamAchievement steamAchieve =
                            this.achievementProvider.getAchievement(achieve.getApiname());

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(achieve.getUnlockTimestamp() * 1000);
                    AchievementUnlock unlock =
                            new AchievementUnlock(SkyrimConstants.STEAM_GAME_NAME, steamAchieve,
                                    cal);

                    profile.addAchievementUnlock(unlock);
                }
            }
        }
    }
}
