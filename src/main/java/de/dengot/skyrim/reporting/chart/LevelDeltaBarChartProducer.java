package de.dengot.skyrim.reporting.chart;

import de.dengot.skyrim.model.SkyrimCharacterSnapshot;

public class LevelDeltaBarChartProducer extends DeltaBarChartProducer {

    public LevelDeltaBarChartProducer() {
        super("Level");
    }

    @Override
    protected int getSnapshotValue(SkyrimCharacterSnapshot snapshot) {
        return snapshot.getLevel();
    }
}
