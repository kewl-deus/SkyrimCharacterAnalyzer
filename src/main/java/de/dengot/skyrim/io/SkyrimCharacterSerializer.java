package de.dengot.skyrim.io;

import java.io.Reader;
import java.io.Writer;

import de.dengot.skyrim.model.SkyrimCharacterList;

public interface SkyrimCharacterSerializer {

	public SkyrimCharacterList read(Reader reader);

	public void write(SkyrimCharacterList characterList, Writer writer);
}
