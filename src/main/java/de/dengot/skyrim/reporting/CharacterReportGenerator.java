package de.dengot.skyrim.reporting;

import de.dengot.skyrim.model.SkyrimCharacterList;

public abstract class CharacterReportGenerator {

    public abstract void createReport(SkyrimCharacterList characters, String outputPath);
}