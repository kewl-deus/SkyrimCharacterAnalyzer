package de.dengot.skyrim.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("CategorySet")
public class StatisticCategorySet implements Iterable<StatisticCategory> {

	@XStreamImplicit
	private Set<StatisticCategory> categories;

	public StatisticCategorySet() {
		this.categories = new HashSet<StatisticCategory>();
	}

	public Set<StatisticCategory> getCategories() {
		return categories;
	}

	public void add(StatisticCategory cat) {
		categories.add(cat);
	}

	public Iterator<StatisticCategory> iterator() {
		return categories.iterator();
	}
}
