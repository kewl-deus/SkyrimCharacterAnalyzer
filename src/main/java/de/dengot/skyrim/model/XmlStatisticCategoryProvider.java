package de.dengot.skyrim.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import de.dengot.skyrim.io.StatisticCategorySerializer;

public class XmlStatisticCategoryProvider extends StatisticCategoryProvider {

	private static final String CATEGORIES_XML = "/de/dengot/skyrim/model/categories.xml";

	private SortedSet<StatisticCategory> categories;

	public XmlStatisticCategoryProvider() {
		StatisticCategorySerializer serializer = new StatisticCategorySerializer();

		InputStream is = getClass().getResourceAsStream(CATEGORIES_XML);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StatisticCategorySet categorySet = serializer.read(reader);
		this.categories = new TreeSet<StatisticCategory>(categoryComparator);
		this.categories.addAll(categorySet.getCategories());
	}

	@Override
	public SortedSet<StatisticCategory> getCategories() {
		return Collections.unmodifiableSortedSet(categories);
	}

}
