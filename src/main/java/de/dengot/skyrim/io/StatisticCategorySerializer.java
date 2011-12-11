package de.dengot.skyrim.io;

import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;

import de.dengot.skyrim.io.converter.LocalizedLabelConverter;
import de.dengot.skyrim.io.converter.StatisticCategoryTypeConverter;
import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategorySet;

public class StatisticCategorySerializer {

	public StatisticCategorySet read(Reader reader) {
		XStream xs = createXStream();
		Object rawResult = xs.fromXML(reader);
		return (StatisticCategorySet) rawResult;
	}

	public void write(StatisticCategorySet categoryList, Writer writer) {
		XStream xs = createXStream();
		xs.toXML(categoryList, writer);
	}

	private XStream createXStream() {
		XStream xs = new XStream();

		xs.registerConverter(new StatisticCategoryTypeConverter());
		xs.registerConverter(new LocalizedLabelConverter());

		xs
				.processAnnotations(new Class[] { StatisticCategorySet.class, StatisticCategory.class,
						LocalizedLabel.class });

		return xs;
	}
}
