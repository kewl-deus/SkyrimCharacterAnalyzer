package de.dengot.skyrim.io.converter;

import com.thoughtworks.xstream.converters.SingleValueConverter;

import de.dengot.skyrim.model.StatisticCategoryType;

public class StatisticCategoryTypeConverter implements SingleValueConverter {

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type == StatisticCategoryType.class;
	}

	public Object fromString(String str) {
		int catId = Integer.parseInt(str);
		return StatisticCategoryType.getForId(catId);
	}

	public String toString(Object obj) {
		StatisticCategoryType catType = (StatisticCategoryType) obj;
		return String.valueOf(catType.Id());
	}
}