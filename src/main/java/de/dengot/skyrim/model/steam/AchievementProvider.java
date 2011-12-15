package de.dengot.skyrim.model.steam;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

public class AchievementProvider {

    private static final String basePath = "de.dengot.skyrim.model.achievement";

    private static final ResourceBundle bundleNames =
            ResourceBundle.getBundle(basePath + ".AchievementNames");

    private static final ResourceBundle bundleDescriptions =
            ResourceBundle.getBundle(basePath + ".AchievementDescriptions");

    private static final ResourceBundle bundleLogos =
            ResourceBundle.getBundle(basePath + ".AchievementLogos");

    private Map<String, SteamAchievement> achievements;

    public AchievementProvider() {
        loadAchievements();
    }

    public SortedSet<SteamAchievement> getAchievements() {
        SortedSet<SteamAchievement> sortedAchievements =
                new TreeSet<SteamAchievement>(new Comparator<SteamAchievement>() {
                    public int compare(SteamAchievement a1, SteamAchievement a2) {
                        return a1.getName().compareTo(a2.getName());
                    }
                });
        sortedAchievements.addAll(this.achievements.values());
        return Collections.unmodifiableSortedSet(sortedAchievements);
    }

    public SteamAchievement getAchievement(String apiName) {
        return this.achievements.get(apiName);
    }

    public void loadAchievements() {
        this.achievements = new HashMap<String, SteamAchievement>();
        for (String apiName : getApiNames()) {
            SteamAchievement achievement =
                    new SteamAchievement(apiName, getName(apiName), getDescription(apiName),
                            getLogo(apiName));
            achievements.put(apiName, achievement);
        }
    }

    private SortedSet<String> getApiNames() {
        SortedSet<String> apiNames = new TreeSet<String>();
        Enumeration<String> keys = bundleNames.getKeys();
        while (keys.hasMoreElements()) {
            apiNames.add(keys.nextElement());
        }
        return apiNames;
    }

    private String getDescription(String achivementApiName) {
        return bundleDescriptions.getString(achivementApiName);
    }

    private String getName(String achivementApiName) {
        return bundleNames.getString(achivementApiName);
    }

    private String getLogo(String achivementApiName) {
        return bundleLogos.getString(achivementApiName);
    }
}
