package de.dengot.skyrim;

import java.io.FileReader;
import java.io.IOException;

import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.io.SkyrimCharacterSerializer;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.reporting.CharacterReportGenerator;
import de.dengot.skyrim.reporting.MultiThreadedCharacterReportGenerator;

public class CharacterAnalyzerApplication {

    public static void main(String[] args) throws IOException {
        String inputXmlFileLocation = args[0];
        String outputFolder = args[1];

        FileReader reader = new FileReader(inputXmlFileLocation);

        SkyrimCharacterSerializer serializer = new SkyrimCharacterSerializer();
        SkyrimCharacterList characters = serializer.read(reader);

        CharacterReportGenerator repoGen = new MultiThreadedCharacterReportGenerator(new PngChartWriter());
        repoGen.createReport(characters, outputFolder);
    }

}
