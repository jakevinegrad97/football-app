package com.vinegrad.fixtures;

import static java.lang.Math.floor;
import static java.lang.Math.random;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.vinegrad.model.Fixture;
import com.vinegrad.model.League;
import com.vinegrad.model.Team;
import com.vinegrad.table.TableReader;

public class FixtureWriter {

	private static List<Team> teams = TableReader.read("src/main/resources/CH.txt");
	public static final int NUMBER_OF_ROUNDS = 2 * (teams.size() - 1);

	public static List<Team> writeFixtures(int round, List<Team> teams) throws InterruptedException {
	
		List<Fixture> fixtures = 
			FixtureGenerator.getFixtures()
				.stream()
				.filter(f -> f.getRound() == round)
				.collect(Collectors.toList());
		List<Team> result = new ArrayList<>();
		System.out.println("Round " + round);
		Thread.sleep(500);
		League league = null;
		for(Fixture fixture : fixtures) {
			Team homeTeam = fixture.getHomeTeam();
			Team awayTeam = fixture.getAwayTeam();
			
			if(!homeTeam.getLeague().equals(league)) {
				league = homeTeam.getLeague();
				System.out.println("\n" + league.getDisplayName());
				Thread.sleep(500);
			}
			
			final double homeForm = homeTeam.getLast5().stream().mapToInt(i -> i).sum();
			final double awayForm = awayTeam.getLast5().stream().mapToInt(i -> i).sum();
			
			final double homeChance = ((homeTeam.getAttack() + (100 - awayTeam.getDefence())) / 2 + 2 * (homeForm + (15 - awayForm)) + (20 - homeTeam.getPlace()) + awayTeam.getPlace() + floor(100 * random())) / 3 + 20;
			final double awayChance = 0.9 * ((awayTeam.getAttack() + (100 - homeTeam.getDefence())) / 2 + 2 * (awayForm + (15 - homeForm)) + (20 - awayTeam.getPlace()) + homeTeam.getPlace() + floor(100 * random())) / 3 + 20;			
			
			int home = getScore(homeChance);
			int away = getScore(awayChance);
			
			fixture.addResult(home, away);
			if((round == 38 && league.equals(League.PREMIER_LEAGUE)) || round == 46)
				Thread.sleep(500);
			System.out.println(homeTeam.getName() + " " + home + " : " + away + " " + awayTeam.getName());
			homeTeam.addResult(home, away);
			awayTeam.addResult(away, home);
			
			result.add(homeTeam);
			result.add(awayTeam);
			
		}
		
		if(round > 38) {
			List<Team> pl = teams.
					stream().
					filter(team -> team.getLeague().equals(League.PREMIER_LEAGUE))
					.collect(Collectors.toList());
			result.addAll(pl);
		}
		return result;
		
	}
	
	public static List<Team> writeNationalLeagueFixtures() {
		List<Fixture> fixtures = FixtureGenerator.getNationalLeagueFixtures();
		
		List<Team> result = new ArrayList<>();
		
		for(Fixture fixture : fixtures) {
			Team homeTeam = fixture.getHomeTeam();
			Team awayTeam = fixture.getAwayTeam();
			
			final double homeForm = homeTeam.getLast5().stream().mapToInt(i -> i).sum();
			final double awayForm = awayTeam.getLast5().stream().mapToInt(i -> i).sum();
			
			final int homeStartingLeagueBoost = 5 * (homeTeam.getLeague().getTier() - homeTeam.getStartingLeague().getTier());
			final int awayStartingLeagueBoost = 5 * (awayTeam.getLeague().getTier() - awayTeam.getStartingLeague().getTier());
			
			final double homeChance = ((homeTeam.getAttack() + homeStartingLeagueBoost + (100 - awayTeam.getDefence() - awayStartingLeagueBoost)) / 2 + 2 * (homeForm + (15 - awayForm)) + (20 - homeTeam.getPlace()) + awayTeam.getPlace() + floor(100 * random())) / 3 + 20;
			final double awayChance = 0.9 * ((awayTeam.getAttack() + awayStartingLeagueBoost + (100 - homeTeam.getDefence() - homeStartingLeagueBoost)) / 2 + 2 * (awayForm + (15 - homeForm)) + (20 - awayTeam.getPlace()) + homeTeam.getPlace() + floor(100 * random())) / 3 + 20;			
			
			int home = getScore(homeChance);
			int away = getScore(awayChance);
			
			fixture.addResult(home, away);
			
			homeTeam.addResult(home, away);
			awayTeam.addResult(away, home);
			
			if(fixture.getRound() == 46) {
				result.add(homeTeam);
				result.add(awayTeam);
			}
		}
		return result
				.stream()
				.sorted((Comparator.comparing(Team::getPoints).reversed())
						.thenComparing(Comparator.comparing(Team::getGoalDifference).reversed())
						.thenComparing(Comparator.comparing(Team::getGoalsScored).reversed())
						.thenComparing(Comparator.comparing(Team::getWon).reversed())
						.thenComparing(Comparator.comparing(Team::getName)))
					.collect(Collectors.toList());
	}
	
	private static int getScore(double chance) {
		int result = 0;
		if(chance < 55)
			result = (int) floor(2 * random());
		else if(chance > 55 && chance < 75)
			result = (int) floor(3 * random());
		else if(chance > 75 && chance < 85)
			result = (int) floor(1 + 3 * random());
		else if(chance > 85 && chance < 90)
			result = (int) floor(2 + 3 * random());
		else {
			result = (int) floor(3 + 4 * random());
		}
		return result;
	}
}
