package de.dengot.skyrim.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Character")
public class SkyrimCharacter {

    @XStreamAlias("Name")
    @XStreamAsAttribute
    private String name;

    @XStreamAlias("Race")
    @XStreamAsAttribute
    private String race;

    @XStreamImplicit
    private List<SkyrimCharacterSnapshot> history;

    public SkyrimCharacter(String name, String race) {
        this(name, race, new ArrayList<SkyrimCharacterSnapshot>());
    }

    public SkyrimCharacter(String name, String race, List<SkyrimCharacterSnapshot> history) {
        super();
        this.name = name;
        this.race = race;
        this.history = history;
    }

    public String getName() {
        return name;
    }

    public String getRace() {
        return race;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}[{1}]", getName(), getRace());
    }

    public List<SkyrimCharacterSnapshot> getHistory() {
        return history;
    }

    public int getCurrentLevel() {
        int level = 0;
        for (SkyrimCharacterSnapshot snapshot : getHistory()) {
            if (snapshot.getLevel() > level) {
                level = snapshot.getLevel();
            }
        }
        return level;
    }

    public int getMaxValue(String statName) {
        int result = 0;
        for (SkyrimCharacterSnapshot snapshot : getHistory()) {
            int val = snapshot.getStatisticValue(statName);
            if (val > result) {
                result = val;
            }
        }
        return result;
    }

    public int getMinValue(String statName) {
        int result = Integer.MAX_VALUE;
        for (SkyrimCharacterSnapshot snapshot : getHistory()) {
            int val = snapshot.getStatisticValue(statName);
            if (val < result) {
                result = val;
            }
        }
        return result;
    }

    public int getValueAtGameDay(int gameDay, String statName) {
        int result = 0;
        for (SkyrimCharacterSnapshot snapshot : getHistory()) {
            int daysPassed = snapshot.getStatisticValue(SkyrimConstants.DAYS_PASSED);
            result = snapshot.getStatisticValue(statName);
            if (daysPassed > gameDay) {
                break;
            }
        }
        return result;
    }

    public int getLevelAtGameDay(int gameDay) {
        int result = 0;
        for (SkyrimCharacterSnapshot snapshot : getHistory()) {
            int daysPassed = snapshot.getStatisticValue(SkyrimConstants.DAYS_PASSED);
            result = snapshot.getLevel();
            if (daysPassed > gameDay) {
                break;
            }
        }
        return result;
    }

    /**
     * Delta = value[gameday] - value[previous(gameday)]
     * 
     * @param gameDay
     * @param statName
     * @return
     */
    public int getDeltaValue(int gameDay, String statName) {
        int prevValue = getValueAtGameDay(gameDay - 1, statName);
        int actualValue = getValueAtGameDay(gameDay, statName);
        return actualValue - prevValue;
    }
}
