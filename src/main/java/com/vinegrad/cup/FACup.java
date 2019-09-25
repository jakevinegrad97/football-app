package com.vinegrad.cup;

import static java.lang.Math.floor;
import static java.lang.Math.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.vinegrad.model.Fixture;
import com.vinegrad.model.League;
import com.vinegrad.model.Team;

public class FACup {

	// Semifinal replay?!?!
	public static void simulateCup(List<Team> teams) throws InterruptedException {

		Scanner scanner = new Scanner(System.in);
		int round = 1;

		List<Team> notInDraw = teams.stream().filter(
				team -> team.getLeague().equals(League.PREMIER_LEAGUE) || team.getLeague().equals(League.CHAMPIONSHIP))
				.collect(Collectors.toList());

		teams.removeAll(notInDraw);

		Thread.sleep(1000);
		List<Fixture> fixtures = getFixtures(teams);

		System.out.println("Press enter to simulate round " + round);

		String a = scanner.nextLine();

		List<Team> teamsLeft = simulateFixtures(fixtures);

		List<Team> forNextDraw = notInDraw.stream()
				.filter(team -> team.getLeague().equals(League.CHAMPIONSHIP) || team.getPlace() > 16)
				.collect(Collectors.toList());

		teamsLeft.addAll(forNextDraw);
		notInDraw.removeAll(forNextDraw);

		round = 2;

		Thread.sleep(1000);
		fixtures = getFixtures(teamsLeft);

		System.out.println("Press enter to simulate round " + round);

		a = scanner.nextLine();

		teamsLeft = simulateFixtures(fixtures);

		round = 3;

		Thread.sleep(1000);
		fixtures = getFixtures(teamsLeft);

		System.out.println("Press enter to simulate round " + round);

		a = scanner.nextLine();

		teamsLeft = simulateFixtures(fixtures);

		teamsLeft.addAll(notInDraw);

		round = 4;

		while (teamsLeft.size() > 1) {
			Thread.sleep(1000);
			fixtures = getFixtures(teamsLeft);
			if (fixtures.size() == 1)
				System.out.println("Press enter to simulate the Final: " + fixtures.get(0).beforeMatch());
			else if (fixtures.size() == 2)
				System.out.println("Press enter to simulate the Semi Finals: " + fixtures.get(0).beforeMatch() + " and "
						+ fixtures.get(1).beforeMatch());
			else if (fixtures.size() == 4)
				System.out.println("Press enter to simulate the Quarter Finals");
			else
				System.out.println("Press enter to simulate round " + round);
			a = scanner.nextLine();
			teamsLeft = simulateFixtures(fixtures);
			round++;
		}

		Team winner = teamsLeft.get(0);

		Thread.sleep(1000);
		System.out.println(winner.getName() + " win!!");

	}

	private static List<Team> simulateFixtures(List<Fixture> fixtures) throws InterruptedException {
		List<Team> winners = new ArrayList<>();
		for (Fixture fixture : fixtures) {
			Team homeTeam = fixture.getHomeTeam();
			Team awayTeam = fixture.getAwayTeam();

			double awayDisadvantage = fixtures.size() > 2 ? 0.9 : 1;

			double homeChance = 
					((homeTeam.getAttack() + (100 - awayTeam.getDefence())) / 2
							+ (20 - homeTeam.getPlace()) + awayTeam.getPlace()
							+ floor(100 * random())
							+ ((6 - homeTeam.getLeague().getTier()) * 12
							+ awayTeam.getLeague().getTier() * 12) / 2) / 3 
							+ homeTeam.getLast5().stream().mapToInt(a -> a).sum() + 10;
			double awayChance = awayDisadvantage
					* ((awayTeam.getAttack() + (100 - homeTeam.getDefence())) / 2
							+ (20 - awayTeam.getPlace()) + homeTeam.getPlace()
							+ floor(100 * random())
							+ ((6 - awayTeam.getLeague().getTier()) * 12 
							+ homeTeam.getLeague().getTier() * 12) / 2) / 3
							+ awayTeam.getLast5().stream().mapToInt(a -> a).sum() + 10;

			int homeScore = getScore(homeChance);
			int awayScore = getScore(awayChance);
			Thread.sleep(1000);
			System.out.println(
					"\n" + homeTeam.getName() + " " + homeScore + " : " + awayScore + " " + awayTeam.getName());

			homeTeam.addResult(homeScore, awayScore);
			awayTeam.addResult(awayScore, homeScore);
			
			if (homeScore == awayScore) {
				if (fixtures.size() > 2) {
					homeChance *= 0.9;
					awayChance /= 0.9;
					homeScore = getScore(homeChance);
					awayScore = getScore(awayChance);
					Thread.sleep(1000);
					System.out.println("\nREPLAY: " + awayTeam.getName() + " " + awayScore + " : " + homeScore + " "
							+ homeTeam.getName());
					homeTeam.addResult(homeScore, awayScore);
					awayTeam.addResult(awayScore, homeScore);
					if (homeScore == awayScore) {
						List<Team> pens = List.of(homeTeam, awayTeam);
						Team winner = pens.get((int) floor(2 * random()));
						Thread.sleep(1000);
						System.out.println(winner.getName() + " win on penalties");
						winners.add(winner);
					} else {
						winners.add(homeScore > awayScore ? homeTeam : awayTeam);
					}
				} else {
					List<Team> pens = List.of(homeTeam, awayTeam);
					Team winner = pens.get((int) floor(2 * random()));
					Thread.sleep(1000);
					System.out.println(winner.getName() + " win on penalties");
					winners.add(winner);
					Thread.sleep(1000);
				}
			} else {
				winners.add(homeScore > awayScore ? homeTeam : awayTeam);
			}
		}
		System.out.println();
		return winners;
	}

	private static List<Fixture> getFixtures(List<Team> teams) {
		System.out.println("Next round's fixtures: ");
		List<Fixture> result = new ArrayList<>();
		while (teams.size() > 0) {
			Team homeTeam = teams.get((int) Math.floor(teams.size() * Math.random()));
			teams.remove(homeTeam);
			Team awayTeam = homeTeam;
			while (homeTeam.equals(awayTeam)) {
				awayTeam = teams.get((int) Math.floor(teams.size() * Math.random()));
			}
			teams.remove(awayTeam);
			Fixture fixture = new Fixture(1, homeTeam, awayTeam);
			System.out.println(fixture.beforeMatch());
			result.add(fixture);
		}
		System.out.println();
		return result;
	}

	private static int getScore(double chance) {
		int result = 0;
		if (chance < 55)
			result = (int) floor(2 * random());
		else if (chance > 55 && chance < 75)
			result = (int) floor(3 * random());
		else if (chance > 75 && chance < 85)
			result = (int) floor(1 + 4 * random());
		else if (chance > 85 && chance < 90)
			result = (int) floor(2 + 4 * random());
		else {
			result = (int) floor(3 + 4 * random());
		}
		return result;
	}
}
