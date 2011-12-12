package de.dengot.skyrim.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

public class PropertiesStatisticCategoryProvider extends StatisticCategoryProvider {

	private static final String CATEGORIES_RESOURCE = "/de/dengot/skyrim/model/categories.properties";

	private static final String CATEGORIZEDSTATS_RESOURCE = "/de/dengot/skyrim/model/categorizedstats.properties";

	private SortedSet<StatisticCategory> categories;

	public PropertiesStatisticCategoryProvider() {
		categories = new TreeSet<StatisticCategory>(CategoryByLocalizedNameComparator);
		loadCategories();
	}

	private void loadCategories() {
		Properties catProperties = loadProperties(CATEGORIES_RESOURCE);
		Enumeration<?> keys = catProperties.keys();
		while (keys.hasMoreElements()) {
			String catKey = (String) keys.nextElement();
			int catId = Integer.parseInt(catKey);
			String catName = catProperties.getProperty(catKey);
			StatisticCategoryType catType = StatisticCategoryType.getForId(catId);

			List<String> statNames = loadStatNames(catType);
			StatisticCategory cat = new StatisticCategory(catType, catName, statNames);
			this.categories.add(cat);
		}
	}

	private List<String> loadStatNames(StatisticCategoryType catType) {
		Properties statProperties = loadProperties(CATEGORIZEDSTATS_RESOURCE);

		List<String> statNames = new ArrayList<String>();
		Enumeration<?> keys = statProperties.keys();
		while (keys.hasMoreElements()) {
			String statName = (String) keys.nextElement();
			String catKey = statProperties.getProperty(statName);
			int catId = Integer.parseInt(catKey);
			if (catId == catType.Id()) {
				statNames.add(statName);
			}
		}

		Collections.sort(statNames);
		return statNames;
	}

	@Override
	public SortedSet<StatisticCategory> getCategories() {
		return Collections.unmodifiableSortedSet(categories);
	}



	private Properties loadProperties(String resourcePath) {
		Properties props = new Properties();
		try {
			InputStream is = getClass().getResourceAsStream(resourcePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				String[] keyValuePair = line.split("=");
				props.setProperty(keyValuePair[0], keyValuePair[1]);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return props;
	}
}
