package de.dengot.skyrim.io;

import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;

import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;
import de.dengot.skyrim.model.StatisticValue;

public class SkyrimCharacterSerializer {

    public SkyrimCharacterList read(Reader reader) {
        XStream xs = new XStream();

        xs.processAnnotations(new Class[] { SkyrimCharacterList.class, SkyrimCharacter.class,
                SkyrimCharacterSnapshot.class, StatisticValue.class });

        Object rawResult = xs.fromXML(reader);
        return (SkyrimCharacterList) rawResult;
    }

    public void write(SkyrimCharacterList characterList, Writer writer) {
        XStream xs = new XStream();
        xs.toXML(characterList, writer);
    }
}
