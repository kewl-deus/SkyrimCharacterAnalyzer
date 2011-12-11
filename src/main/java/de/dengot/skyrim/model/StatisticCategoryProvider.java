package de.dengot.skyrim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

public abstract class StatisticCategoryProvider implements Iterable<StatisticCategory> {

	protected static final Comparator<StatisticCategory> categoryComparator = new Comparator<StatisticCategory>() {
		public int compare(StatisticCategory o1, StatisticCategory o2) {
			return o1.getLocalizedName().compareTo(o2.getLocalizedName());
		}
	};

	protected static final Comparator<LocalizedLabel> localizedLabelComparator = new Comparator<LocalizedLabel>() {
		public int compare(LocalizedLabel l1, LocalizedLabel l2) {
			return l1.getLocalizedText().compareTo(l2.getLocalizedText());
		};
	};

	public abstract SortedSet<StatisticCategory> getCategories();

	public List<LocalizedLabel> getAllStats() {
		List<LocalizedLabel> allStats = new ArrayList<LocalizedLabel>();
		for (StatisticCategory category : this) {
			allStats.addAll(category.getStatLabels());
		}

		Collections.sort(allStats, localizedLabelComparator);
		return allStats;
	}

	public StatisticCategory getCategory(String key) {
		for (StatisticCategory cat : this) {
			if (cat.getName().getKey().equals(key)) {
				return cat;
			}
		}
		return null;
	}

	public Iterator<StatisticCategory> iterator() {
		return getCategories().iterator();
	}
}
