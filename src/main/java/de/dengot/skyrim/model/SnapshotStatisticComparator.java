package de.dengot.skyrim.model;

import java.util.Comparator;

public class SnapshotStatisticComparator implements Comparator<SkyrimCharacterSnapshot> {

    private String statName;

    public SnapshotStatisticComparator(String statName) {
        super();
        this.statName = statName;
    }

    public int compare(SkyrimCharacterSnapshot snap1, SkyrimCharacterSnapshot snap2) {
        int sv1 = snap1.getStatisticValue(statName);
        int sv2 = snap2.getStatisticValue(statName);
        return (sv1 < sv2 ? -1 : (sv1 == sv2 ? 0 : 1));
    }

}
