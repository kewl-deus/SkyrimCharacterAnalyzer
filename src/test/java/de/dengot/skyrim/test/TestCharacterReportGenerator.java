package de.dengot.skyrim.test;

import generated.Playerstats;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.testng.annotations.Test;

import de.dengot.skyrim.io.PngChartWriter;
import de.dengot.skyrim.model.SkyrimCharacter;
import de.dengot.skyrim.model.SkyrimConstants;
import de.dengot.skyrim.model.steam.SteamSkyrimCharacter;
import de.dengot.skyrim.reporting.MultiThreadedCharacterReportGenerator;
import de.dengot.steamcommunityclient.SteamCommunityClient;

public class TestCharacterReportGenerator extends AbstractSkyrimCharacterBasedTestCase {

	@Test(enabled=false)
	public void testReportGeneration() {
		long start = System.currentTimeMillis();

		MultiThreadedCharacterReportGenerator mtRepoGen = new MultiThreadedCharacterReportGenerator(
				new PngChartWriter());
		mtRepoGen.createReport(sampleCharacters, "c:/temp/skyrimreport");
		// duration: 58s dualcore / 24s quadcore

		long finish = System.currentTimeMillis();
		long duration = finish - start;
		float floatDuration = duration / 1000;

		System.out.println("Report Generation took: " + floatDuration + " secs");
	}

	@Test
	public void testAchievements() throws IOException{
		Properties characterOwners = new Properties();
		characterOwners.setProperty("Ragnar Valkyrson", "kewl-deus");
		characterOwners.setProperty("Surevane", "gabba_the_hutt");
		
		SteamCommunityClient steamClient = new SteamCommunityClient();
		for (SkyrimCharacter skyrimCharacter : sampleCharacters) {
			String steamProfileName = characterOwners.getProperty(skyrimCharacter.getName());
			
			if (steamProfileName == null){
				continue;
			}
			
			SteamSkyrimCharacter steamPlayer = (SteamSkyrimCharacter) skyrimCharacter;
			Playerstats playerstats = steamClient.getPlayerstats(steamProfileName, SkyrimConstants.STEAM_GAME_NAME);
			steamPlayer.addAchievements(playerstats.getAchievements().getAchievement());

			System.out.println(MessageFormat.format("{0} has {1} achievements", steamPlayer.getName(), steamPlayer
					.getAchievements().size()));

		}
	}
}
