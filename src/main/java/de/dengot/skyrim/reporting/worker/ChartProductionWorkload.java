package de.dengot.skyrim.reporting.worker;

import java.io.File;

import de.dengot.skyrim.io.ChartWriter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.reporting.chart.character.CharacterChartProducer;

public class ChartProductionWorkload {

    private CharacterChartProducer chartProducer;
    private ChartWriter chartWriter;
    private File outputFile;
    private SkyrimCharacterList skyrimCharacters;
    private int width;
    private int height;

    
    
    public ChartProductionWorkload(CharacterChartProducer chartProducer, ChartWriter chartWriter,
            File outputFile, SkyrimCharacterList skyrimCharacters, int width, int height) {
        super();
        this.chartProducer = chartProducer;
        this.chartWriter = chartWriter;
        this.outputFile = outputFile;
        this.skyrimCharacters = skyrimCharacters;
        this.width = width;
        this.height = height;
    }

    public CharacterChartProducer getChartProducer() {
        return chartProducer;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public ChartWriter getChartWriter() {
        return chartWriter;
    }

    public SkyrimCharacterList getSkyrimCharacters() {
        return skyrimCharacters;
    }

    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }
}
