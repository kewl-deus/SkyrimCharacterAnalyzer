package de.dengot.skyrim.model.queryoptimized;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dengot.skyrim.model.SkyrimCharacterSnapshot;
import de.dengot.skyrim.model.SkyrimConstants;
import de.dengot.skyrim.model.SnapshotStatisticComparator;
import de.dengot.skyrim.model.steam.SteamSkyrimCharacter;

public class QueryOptimizedCharacter extends SteamSkyrimCharacter {

    private static List<SkyrimCharacterSnapshot> sort(List<SkyrimCharacterSnapshot> snapshots) {
        List<SkyrimCharacterSnapshot> history = new ArrayList<SkyrimCharacterSnapshot>(snapshots);
        SnapshotStatisticComparator gamedayComp =
                new SnapshotStatisticComparator(SkyrimConstants.DAYS_PASSED);
        Collections.sort(history, gamedayComp);
        return history;
    }

    private int currentLevel;

    public QueryOptimizedCharacter(String name, String race, List<SkyrimCharacterSnapshot> history) {
        super(name, race, sort(history));
        this.currentLevel = super.getCurrentLevel();
    }

    @Override
    public int getCurrentLevel() {
        return this.currentLevel;
    }

}
