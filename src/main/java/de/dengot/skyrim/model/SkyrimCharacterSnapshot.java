package de.dengot.skyrim.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Snapshot")
public class SkyrimCharacterSnapshot {

    @XStreamAlias("Id")
    @XStreamAsAttribute
    private int id;

    @XStreamAlias("Label")
    @XStreamAsAttribute
    private String label;

    @XStreamAlias("SaveTime")
    @XStreamAsAttribute
    private Calendar saveTime;

    @XStreamAlias("Level")
    @XStreamAsAttribute
    private int level;

    @XStreamAlias("Location")
    @XStreamAsAttribute
    private String location;

    @XStreamImplicit
    private List<StatisticValue> stats;

    public SkyrimCharacterSnapshot(int id, String label, Calendar saveTime, int level,
            String location, List<StatisticValue> stats) {
        super();
        this.id = id;
        this.label = label;
        this.saveTime = saveTime;
        this.level = level;
        this.location = location;
        this.stats = stats;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Calendar getSaveTime() {
        return saveTime;
    }

    public int getLevel() {
        return level;
    }

    public String getLocation() {
        return location;
    }

    public List<StatisticValue> getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public int getStatisticValue(String statName) {
        for (StatisticValue statsVal : getStats()) {
            if (statsVal.getName().equals(statName)) {
                return statsVal.getValue();
            }
        }
        return -1;
    }
}
