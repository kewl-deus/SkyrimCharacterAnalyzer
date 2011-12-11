package de.dengot.skyrim.model.queryoptimized;

import java.util.ArrayList;
import java.util.List;

import de.dengot.skyrim.model.LocalizedLabel;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;
import de.dengot.skyrim.model.StatisticCategory;
import de.dengot.skyrim.model.StatisticCategoryProvider;

public class QueryOptimizedModelFactory {

	private StatisticCategoryProvider statisticCategoryProvider;

	public QueryOptimizedModelFactory(StatisticCategoryProvider statisticCategoryProvider) {
		super();
		this.statisticCategoryProvider = statisticCategoryProvider;
	}

	public SkyrimCharacterList createQueryOptimized(SkyrimCharacterList characters) {
		List<SkyrimCharacter> optimizedChars = new ArrayList<SkyrimCharacter>();
		for (SkyrimCharacter skyrimCharacter : characters) {
			optimizedChars.add(createQueryOptimized(skyrimCharacter));
		}
		SkyrimCharacterList resultList = new SkyrimCharacterList();
		resultList.setCharacters(optimizedChars);
		return resultList;
	}

	public QueryOptimizedCharacter createQueryOptimized(SkyrimCharacter character) {

		List<SkyrimCharacterSnapshot> optHistory = new ArrayList<SkyrimCharacterSnapshot>();
		for (SkyrimCharacterSnapshot snap : character.getHistory()) {
			optHistory.add(createQueryOptimized(snap));
		}

		QueryOptimizedCharacter qoChar = new QueryOptimizedCharacter(character.getName(), character.getRace(),
				optHistory);
		return qoChar;
	}

	public QueryOptimizedSnapshot createQueryOptimized(SkyrimCharacterSnapshot snapshot) {
		QueryOptimizedSnapshot qoSnap = new QueryOptimizedSnapshot(snapshot.getId(), snapshot.getLabel(), snapshot
				.getSaveTime(), snapshot.getLevel(), snapshot.getLocation(), snapshot.getStats());

		for (StatisticCategory category : statisticCategoryProvider) {
			for (LocalizedLabel statLabel : category.getStatLabels()) {
				int statisticValue = qoSnap.getStatisticValue(statLabel.getKey());
				for (String lang : statLabel.getLanguages()) {
					qoSnap.setStatisticValue(statLabel.getLabel(lang), statisticValue);
				}
			}
		}

		return qoSnap;

	}
}
