package de.dengot.skyrim.model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public abstract class StatisticCategoryProvider implements Iterable<StatisticCategory>{

	protected final Comparator<StatisticCategory> categoryComparator = new Comparator<StatisticCategory>() {
		public int compare(StatisticCategory o1, StatisticCategory o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	
	public abstract SortedSet<StatisticCategory> getCategories();

	public StatisticCategory getCategory(String name) {
		for (StatisticCategory cat : getCategories()) {
			if (cat.getName().equals(name)) {
				return cat;
			}
		}
		return null;
	}
	
	public Iterator<StatisticCategory> iterator() {
		return getCategories().iterator();
	}
}
