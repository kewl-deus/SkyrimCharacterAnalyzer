package de.dengot.skyrim.model;

import java.util.List;

public class StatisticCategory {

	private StatisticCategoryType categoryType;
	
	private String name;

	private List<String> statNames;
	
	public StatisticCategory(StatisticCategoryType categoryType, String name, List<String> statNames) {
		super();
		this.categoryType = categoryType;
		this.name = name;
		this.statNames = statNames;
	}

	public StatisticCategoryType getCategoryType() {
		return categoryType;
	}

	public String getName() {
		return name;
	}
	
	public List<String> getStatNames() {
		return statNames;
	}
	
	@Override
	public String toString() {
	    return getName();
	}
}
