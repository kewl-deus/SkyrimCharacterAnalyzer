package de.dengot.skyrim.reporting.chart;

import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;

public class LevelCumulativeAreaChartProducer extends CumulativeAreaChartProducer {

    public LevelCumulativeAreaChartProducer() {
        super(new LocalizedLabel("Level"));
    }

    @Override
    protected int getSnapshotValue(SkyrimCharacterSnapshot snapshot) {
        return snapshot.getLevel();
    }
    
    @Override
    protected int getCharacterValueAtGameDay(int gameDay, SkyrimCharacter skyrimCharacter) {
        return skyrimCharacter.getLevelAtGameDay(gameDay);
    }
}
