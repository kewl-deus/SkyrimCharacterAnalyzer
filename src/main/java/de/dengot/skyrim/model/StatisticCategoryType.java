package de.dengot.skyrim.model;

public enum StatisticCategoryType {
	General(0), Quest(1), Combat(2), Magic(3), Crafting(4), Crime(5);

	private final int id;

	private StatisticCategoryType(int id) {
		this.id = id;
	}

	public int Id() {
		return this.id;
	}

	public static StatisticCategoryType getForId(int catId) {
		for (StatisticCategoryType catType : StatisticCategoryType.values()) {
			if (catType.Id() == catId) {
				return catType;
			}
		}
		return null;
	}
}
