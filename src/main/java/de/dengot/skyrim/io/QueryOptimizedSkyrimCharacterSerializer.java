package de.dengot.skyrim.io;

import java.io.Reader;
import java.io.Writer;

import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.XmlStatisticCategoryProvider;
import de.dengot.skyrim.model.queryoptimized.QueryOptimizedModelFactory;

public class QueryOptimizedSkyrimCharacterSerializer implements SkyrimCharacterSerializer {

	private QueryOptimizedModelFactory modelFactory;
	private SkyrimCharacterSerializer wrappedSerializer;

	public QueryOptimizedSkyrimCharacterSerializer(SkyrimCharacterSerializer wrappedSerializer) {
		super();
		this.modelFactory = new QueryOptimizedModelFactory(new XmlStatisticCategoryProvider());
		this.wrappedSerializer = wrappedSerializer;
	}

	public SkyrimCharacterList read(Reader reader) {
		SkyrimCharacterList characterList = wrappedSerializer.read(reader);
		return modelFactory.createQueryOptimized(characterList);
	}

	public void write(SkyrimCharacterList characterList, Writer writer) {
		throw new UnsupportedOperationException("supports read-only yet");
	}

}
