package de.dengot.skyrim.model.queryoptimized;

import java.util.ArrayList;
import java.util.List;

import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterList;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;

public class QueryOptimizedModelFactory {

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
		return qoSnap;

	}
}
