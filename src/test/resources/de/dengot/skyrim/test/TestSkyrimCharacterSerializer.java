package de.dengot.skyrim.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.testng.annotations.Test;

import de.dengot.skyrim.io.SkyrimCharacterSerializer;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;

public class TestSkyrimCharacterSerializer {

    @Test
    public void testReadFromXml() {
        InputStream input = getClass().getResourceAsStream("/skyrimcharacters.xml");
        InputStreamReader reader = new InputStreamReader(input);

        SkyrimCharacterSerializer serializer = new SkyrimCharacterSerializer();
        SkyrimCharacterList charList = serializer.read(reader);

        for (SkyrimCharacter ch : charList) {
            System.out.println(ch.getName());
        }
    }

    @Test(enabled = false)
    public void testWriteToXml() throws IOException {

        SkyrimCharacterList charList = new SkyrimCharacterList();
        charList.getCharacters().add(new SkyrimCharacter("testname", "testrace"));

        SkyrimCharacterSerializer serializer = new SkyrimCharacterSerializer();
        serializer.write(charList, new FileWriter("c:/temp/skyrimtestchar.xml"));
    }

}
