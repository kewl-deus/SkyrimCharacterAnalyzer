package de.dengot.skyrim.io;

import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;

import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;
import de.dengot.skyrim.model.StatisticValue;

public class XmlSkyrimCharacterSerializer implements SkyrimCharacterSerializer{
	
    public SkyrimCharacterList read(Reader reader) {
    	XStream xs = createXStream();
        Object rawResult = xs.fromXML(reader);
        SkyrimCharacterList characterList = (SkyrimCharacterList) rawResult;
        return characterList;
    }

    public void write(SkyrimCharacterList characterList, Writer writer) {
        XStream xs = createXStream();
        xs.toXML(characterList, writer);
    }
    
    private XStream createXStream(){
        XStream xs = new XStream();
        
        xs.processAnnotations(new Class[] { SkyrimCharacterList.class, SkyrimCharacter.class,
                SkyrimCharacterSnapshot.class, StatisticValue.class });
        
        return xs;
    }
}
