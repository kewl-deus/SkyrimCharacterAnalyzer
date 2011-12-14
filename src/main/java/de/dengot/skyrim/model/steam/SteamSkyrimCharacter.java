package de.dengot.skyrim.model.steam;

import generated.Playerstats.Achievements.Achievement;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimCharacterSnapshot;

public class SteamSkyrimCharacter extends SkyrimCharacter {

	private Set<Achievement> achievements;

	public SteamSkyrimCharacter(String name, String race) {
		super(name, race);
		achievements = new HashSet<Achievement>();
	}

	public SteamSkyrimCharacter(String name, String race, List<SkyrimCharacterSnapshot> history) {
		super(name, race, history);
		achievements = new HashSet<Achievement>();
	}

	public Set<Achievement> getAchievements() {
		return achievements;
	}

	public boolean addAchievement(Achievement achievement) {
		return achievements.add(achievement);
	}

	public boolean addAchievements(Collection<? extends Achievement> c) {
		return achievements.addAll(c);
	}

}
