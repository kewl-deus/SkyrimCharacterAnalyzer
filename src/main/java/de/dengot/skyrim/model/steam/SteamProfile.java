package de.dengot.skyrim.model.steam;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

public class SteamProfile {

    private long accountId;

    private String accountName;

    private Set<AchievementUnlock> achievementUnlocks;

    public SteamProfile(long accountId, String accountName) {
        super();
        this.accountId = accountId;
        this.accountName = accountName;
        this.achievementUnlocks = new HashSet<AchievementUnlock>();
    }

    public long getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public AchievementUnlock getAchievementUnlock(String game, String achievementApiName) {
        for (AchievementUnlock unlock : getAchievementUnlocks()) {
            if (unlock.getGame().equals(game)
                    && unlock.getAchievement().getApiName().equals(achievementApiName)) {
                return unlock;
            }
        }
        return null;
    }

    public Set<AchievementUnlock> getAchievementUnlocks() {
        return achievementUnlocks;
    }

    public boolean addAchievementUnlock(AchievementUnlock achievementUnlock) {
        return achievementUnlocks.add(achievementUnlock);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}[{1,number,0}]", getAccountName(), getAccountId());
    }
}
