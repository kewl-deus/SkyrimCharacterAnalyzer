package de.dengot.skyrim.test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import de.dengot.skyrim.io.StatisticCategorySerializer;
import de.dengot.skyrim.model.PropertiesStatisticCategoryProvider;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategoryProvider;
import de.dengot.skyrim.model.StatisticCategorySet;

public class TestStatisticCategorySerialization {

	@Test
	public void testWriteToXml() throws Exception {
		StatisticCategoryProvider catProvider = new PropertiesStatisticCategoryProvider();

		StatisticCategorySet categorySet = new StatisticCategorySet();
		for (StatisticCategory cat : catProvider) {
			categorySet.add(cat);
		}

		StringWriter writer = new StringWriter();
		StatisticCategorySerializer serializer = new StatisticCategorySerializer();
		serializer.write(categorySet, writer);

		//System.out.println(writer.toString());

		StatisticCategorySet unmarshalledCatSet = serializer.read(new StringReader(writer.toString()));
		assertEquals(unmarshalledCatSet.getCategories().size(), catProvider.getCategories().size());
	}
}
