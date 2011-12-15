package de.dengot.skyrim.model.steam;

import java.util.Calendar;

public class AchievementUnlock {
    
    private String game;
    
    private SteamAchievement achievement;
    
    private Calendar unlockTime;

    public AchievementUnlock(String game, SteamAchievement achievement, Calendar unlockTime) {
        super();
        this.game = game;
        this.achievement = achievement;
        this.unlockTime = unlockTime;
    }

    public String getGame() {
        return game;
    }

    public SteamAchievement getAchievement() {
        return achievement;
    }

    public Calendar getUnlockTime() {
        return unlockTime;
    }
    
    
}
