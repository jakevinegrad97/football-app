package com.vinegrad.fixtures;

import static java.lang.Math.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.vinegrad.model.Fixture;
import com.vinegrad.model.Team;
import com.vinegrad.table.TableReader;

public class FixtureGenerator {

	private static final Logger LOGGER = Logger.getLogger(FixtureGenerator.class);

	private static final List<Fixture> fixtures = generateFixtures();

	public static List<Fixture> getFixtures() {
		return fixtures;
	}
	
	private static final List<Team> nlTeams = TableReader.read("src/main/resources/NL.txt");
	
	private static final List<Fixture> nationalLeagueFixtures = getFixturesPerLeague(nlTeams);
	
	public static List<Fixture> getNationalLeagueFixtures() {
		return nationalLeagueFixtures;
	}

	private static List<Fixture> generateFixtures() {

		List<Team> plTeams = TableReader.read("src/main/resources/PL.txt");
		List<Team> chTeams = TableReader.read("src/main/resources/CH.txt");
		List<Team> l1Teams = TableReader.read("src/main/resources/L1.txt");
		List<Team> l2Teams = TableReader.read("src/main/resources/L2.txt");

		List<Fixture> result = getFixturesPerLeague(plTeams);
		result.addAll(getFixturesPerLeague(chTeams));
		result.addAll(getFixturesPerLeague(l1Teams));
		result.addAll(getFixturesPerLeague(l2Teams));

		return result;

	}

	private static List<Fixture> getFixturesPerLeague(List<Team> teams) {
		final String league = teams.get(0).getLeague().getInitials();

		LOGGER.info(teams.get(0).getLeague().getDisplayName() + "\n");
		
		final int NUMBER_OF_TEAMS = teams.size();

		List<Fixture> fixtures = new ArrayList<>();

		for (Team homeTeam : teams) {
			for (Team awayTeam : teams) {
				if (!homeTeam.equals(awayTeam)) {
					Fixture fixture = new Fixture(0, homeTeam, awayTeam);
					fixtures.add(fixture);
				}
			}
		}
		
		if(league.equals("NL")) {
			System.out.println("");
		}

		final int NUMBER_OF_FIXTURES = fixtures.size();

		List<Fixture> result = new ArrayList<>();

		List<Team> teamsInThisRound = new ArrayList<>();
		List<Team> teamsLeft = teams.stream().collect(Collectors.toList());
		List<String> thisRound = new ArrayList<>();

		int currentRound = 1;

		for (int i = 0; i < NUMBER_OF_FIXTURES; i++) {
			final int NEW_FIXTURES_SIZE = fixtures.size();
			Fixture newFixture = fixtures.get((int) floor(NEW_FIXTURES_SIZE * random()));
			Team homeTeam = newFixture.getHomeTeam();
			Team awayTeam = newFixture.getAwayTeam();
			int attempt = 0;
			while (teamsInThisRound.contains(homeTeam) || teamsInThisRound.contains((awayTeam))) {
				newFixture = fixtures.get((int) floor(NEW_FIXTURES_SIZE * random()));
				homeTeam = newFixture.getHomeTeam();
				awayTeam = newFixture.getAwayTeam();
				if (attempt > 10) {
					for (Team home : teamsLeft) {
						boolean isGood = false;
						for (Team away : teamsLeft) {
							if (!home.equals(away)) {
								newFixture = new Fixture(0, home, away);
								homeTeam = newFixture.getHomeTeam();
								awayTeam = newFixture.getAwayTeam();
								isGood = false;
								LOGGER.debug("Attempt: " + attempt + ", try: " + newFixture.beforeMatch());
								if (result.stream().filter(f -> f.getHomeTeam().equals(home) && f.getAwayTeam().equals(away)).count() == 0) {
									isGood = true;
									break;
								}
							}
						}
						if (isGood)
							break;
					}
				}
				attempt++;
			}
			thisRound.add(newFixture.beforeMatch());
			newFixture.setRound(currentRound);
			LOGGER.info("Accepted Fixture: " + newFixture.beforeMatch());
			result.add(newFixture);
			fixtures.remove(newFixture);
			teamsInThisRound.add(homeTeam);
			teamsInThisRound.add(awayTeam);
			teamsLeft.remove(homeTeam);
			teamsLeft.remove(awayTeam);
			if (teamsInThisRound.size() == NUMBER_OF_TEAMS) {
				teamsInThisRound = new ArrayList<Team>();
				teamsLeft = teams.stream().collect(Collectors.toList());
				thisRound = new ArrayList<String>();
				currentRound++;
				LOGGER.info("Round: " + currentRound + "\n");
			}
		}

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter("src/main/resources/fixtures" + league + ".txt");
			bw = new BufferedWriter(fw);
			int round = 0;
			for (int i = 0; i < fixtures.size(); i++) {
				if (fixtures.get(i).getRound() != round) {
					round++;
					bw.write("#" + round + "\n");
				}
				bw.write(fixtures.get(i).beforeMatch() + "\n");

			}
			bw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return result;
	}
}
