package de.dengot.skyrim.model.queryoptimized;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dengot.skyrim.model.SkyrimCharacterSnapshot;
import de.dengot.skyrim.model.StatisticValue;

public class QueryOptimizedSnapshot extends SkyrimCharacterSnapshot {

    private Map<String, Integer> statsCache;

    public QueryOptimizedSnapshot(int id, String label, Calendar saveTime, int level,
            String location, List<StatisticValue> stats) {
        super(id, label, saveTime, level, location, stats);
        createStatsCache();
    }

    private void createStatsCache() {
        statsCache = new HashMap<String, Integer>();
        for (StatisticValue statsVal : getStats()) {
            statsCache.put(statsVal.getName(), statsVal.getValue());
        }
    }

    @Override
    public int getStatisticValue(String statName) {
        return statsCache.get(statName);
    }
}
