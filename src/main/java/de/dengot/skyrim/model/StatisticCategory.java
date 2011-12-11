package de.dengot.skyrim.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Category")
public class StatisticCategory {

	@XStreamAlias("TypeId")
	@XStreamAsAttribute
	private StatisticCategoryType categoryType;

	@XStreamAlias("Name")
	private LocalizedLabel name;

	@XStreamAlias("StatisticSet")
	private List<LocalizedLabel> statLabels;

	public StatisticCategory(StatisticCategoryType categoryType, String name, List<String> statNames) {
		super();
		this.categoryType = categoryType;

		this.name = new LocalizedLabel(name);

		this.statLabels = new ArrayList<LocalizedLabel>();
		for (String statName : statNames) {
			LocalizedLabel locLabel = new LocalizedLabel(statName);
			this.statLabels.add(locLabel);
		}
	}

	public StatisticCategoryType getCategoryType() {
		return categoryType;
	}

	public String getName() {
		return this.name.getDefaultLabel();
	}

	public List<String> getLocalizedStatNames() {
		List<String> translatedNames = new ArrayList<String>();
		for (LocalizedLabel locLabel : statLabels) {
			translatedNames.add(locLabel.getDefaultLabel());
		}
		return translatedNames;
	}
	
	public List<LocalizedLabel> getStatLabels(){
		return this.statLabels;
	}

	@Override
	public String toString() {
		return getName();
	}
}
