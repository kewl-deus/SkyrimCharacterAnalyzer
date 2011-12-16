package de.dengot.skyrim.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import de.dengot.steamcommunityclient.model.Achievement;

public class SkyrimAchievementProvider {

    private static final String basePath = "de.dengot.skyrim.model";

    private ResourceBundle bundleNames;

    private ResourceBundle bundleDescriptions;

    private ResourceBundle bundleLogos;

    private Map<String, Achievement> achievements;

    public SkyrimAchievementProvider() {
        bundleNames = ResourceBundle.getBundle(basePath + ".AchievementNames");
        bundleDescriptions = ResourceBundle.getBundle(basePath + ".AchievementDescriptions");
        bundleLogos = ResourceBundle.getBundle(basePath + ".AchievementLogos");
        loadAchievements();
    }

    public SortedSet<Achievement> getAchievements() {
        SortedSet<Achievement> sortedAchievements =
                new TreeSet<Achievement>(new Comparator<Achievement>() {
                    public int compare(Achievement a1, Achievement a2) {
                        return a1.getName().compareTo(a2.getName());
                    }
                });
        sortedAchievements.addAll(this.achievements.values());
        return Collections.unmodifiableSortedSet(sortedAchievements);
    }

    public Achievement getAchievement(String apiName) {
        return this.achievements.get(apiName);
    }

    public void loadAchievements() {
        this.achievements = new HashMap<String, Achievement>();
        for (String apiName : getApiNames()) {
            Achievement achievement =
                    new Achievement(apiName, getName(apiName), getDescription(apiName),
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
