package de.dengot.skyrim.reporting.chart;

import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;

public class LevelDeltaBarChartProducer extends DeltaBarChartProducer {

    public LevelDeltaBarChartProducer() {
    	super(new LocalizedLabel("Level"));
    }

    @Override
    protected int getSnapshotValue(SkyrimCharacterSnapshot snapshot) {
        return snapshot.getLevel();
    }
}
