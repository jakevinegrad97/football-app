package com.vinegrad.table;

import static java.lang.Math.floor;
import static java.lang.Math.random;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.vinegrad.fixtures.FixtureWriter;
import com.vinegrad.model.Fixture;
import com.vinegrad.model.League;
import com.vinegrad.model.Team;

public class TableWriter {

	public static void write(List<Team> teams, League league) {
		List<Team> table =
				teams.stream()
				.filter(team -> team.getLeague().equals(league))
				.sorted((Comparator.comparing(Team::getPoints).reversed())
						.thenComparing(Comparator.comparing(Team::getGoalDifference).reversed())
						.thenComparing(Comparator.comparing(Team::getGoalsScored).reversed())
						.thenComparing(Comparator.comparing(Team::getWon).reversed())
						.thenComparing(Comparator.comparing(Team::getName)))
					.collect(Collectors.toList());
		try(var fw = new FileWriter("src/main/resources/" + league.getInitials() + "Table.txt"); var bw = new BufferedWriter(fw)) {
			bw.write("#									    P   W   D   L   F   A   D   P\n");
			int place = 1;
			for(Team team : table) {
				team.setPlace(place);
				if(place < 10) {
					bw.write(" " + place++ + " : " + team.toString());
				} else {
					bw.write(place++ + " : " + team.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try(var fw = new FileWriter("src/main/resources/" + league.getInitials() + ".txt"); var bw = new BufferedWriter(fw);) {
			teams.stream()
				.filter(team -> team.getLeague().equals(league))
				.sorted(Comparator.comparing(Team::getName))
				.forEach(team -> {
					try {
						bw.write(team.getName() + " : " + team.getAttack() + " : " + team.getDefence() + " : " + team.getStartingLeague().getInitials() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateTables(List<Team> teams) throws InterruptedException {
		Scanner scanner = new Scanner(System.in);
		List<Team> pl = teams.stream().filter(team -> team.getLeague().equals(League.PREMIER_LEAGUE)).collect(Collectors.toList());
		List<Team> ch = teams.stream().filter(team -> team.getLeague().equals(League.CHAMPIONSHIP)).collect(Collectors.toList());
		List<Team> l1 = teams.stream().filter(team -> team.getLeague().equals(League.LEAGUE_1)).collect(Collectors.toList());
		List<Team> l2 = teams.stream().filter(team -> team.getLeague().equals(League.LEAGUE_2)).collect(Collectors.toList());
		
		List<Team> championshipPlayOffs = new ArrayList<>();
		List<Team> league1PlayOffs = new ArrayList<>();
		List<Team> league2PlayOffs = new ArrayList<>();
		
		for(Team team : pl) {
			if(team.getPlace() >= 18)
				team.relegate();
		}
		for(Team team : ch) {
			if (team.getPlace() <= 2)
				team.promote();
			else if(team.getPlace() > 2 && team.getPlace() < 7) {
				championshipPlayOffs.add(team);
			}
			else if(team.getPlace() >= 22)
				team.relegate();
		}
		for(Team team : l1) {
			if (team.getPlace() <= 2)
				team.promote();
			else if(team.getPlace() > 2 && team.getPlace() < 7) {
				league1PlayOffs.add(team);
			}
			else if(team.getPlace() >= 21)
				team.relegate();
		}
		for(Team team : l2) {
			if(team.getPlace() <= 3)
				team.promote();
			else if(team.getPlace() > 3 && team.getPlace() < 8) {
				league2PlayOffs.add(team);
			}
			else if(team.getPlace() > 22) {
				team.relegate();
			}
		}
		
		List<Team> nationalLeague = FixtureWriter.writeNationalLeagueFixtures();
		write(nationalLeague, League.NATIONAL_LEAGUE);
		Team winner = nationalLeague.get(0);
		winner.promote();
		Team second = nationalLeague.get(1);
		second.promote();
		
		
		Thread.sleep(1000);
		System.out.println("\nPress enter for League 2 Play-Offs!");
		String a = scanner.nextLine();
		Thread.sleep(1000);
		playOffs(league2PlayOffs.stream().sorted(Comparator.comparing(Team::getPlace)).collect(Collectors.toList()));
		
		Thread.sleep(1000);
		System.out.println("\nPress enter for League 1 Play-Offs!");
		a = scanner.nextLine();
		Thread.sleep(1000);
		playOffs(league1PlayOffs.stream().sorted(Comparator.comparing(Team::getPlace)).collect(Collectors.toList()));
		
		Thread.sleep(1000);
		System.out.println("\nPress enter for Championship Play-Offs!");
		a = scanner.nextLine();
		Thread.sleep(1000);
		playOffs(championshipPlayOffs.stream().sorted(Comparator.comparing(Team::getPlace)).collect(Collectors.toList()));
		
		Thread.sleep(2000);
		
		System.out.println("\n" + winner.getName() + " and " + second.getName() + " are promoted from the National League!");
		
		List<Team> allTeams = pl;
		allTeams.addAll(ch);
		allTeams.addAll(l1);
		allTeams.addAll(l2);
		allTeams.addAll(nationalLeague);
		
		List<Team> plNew = allTeams.stream().filter(team -> team.getLeague().equals(League.PREMIER_LEAGUE)).collect(Collectors.toList());
		List<Team> chNew = allTeams.stream().filter(team -> team.getLeague().equals(League.CHAMPIONSHIP)).collect(Collectors.toList());
		List<Team> l1New = allTeams.stream().filter(team -> team.getLeague().equals(League.LEAGUE_1)).collect(Collectors.toList());
		List<Team> l2New = allTeams.stream().filter(team -> team.getLeague().equals(League.LEAGUE_2)).collect(Collectors.toList());
		List<Team> nlNew = allTeams.stream().filter(team -> team.getLeague().equals(League.NATIONAL_LEAGUE)).collect(Collectors.toList());
		
		write(plNew);
		write(chNew);
		write(l1New);
		write(l2New);
		write(nlNew);
		scanner.close();
	}
	
	private static void playOffs(List<Team> teams) throws InterruptedException {
		for(Team team : teams)
			team.resetGoals();
		Fixture fixture1 = new Fixture(0, teams.get(3), teams.get(0));
		Fixture fixture2 = new Fixture(0, teams.get(2), teams.get(1));
		Fixture fixture3 = new Fixture(0, teams.get(0), teams.get(3));
		Fixture fixture4 = new Fixture(0, teams.get(1), teams.get(2));
		List<Fixture> fixtures1 = new ArrayList<>(List.of(fixture1, fixture3));
		List<Fixture> fixtures2 = new ArrayList<>(List.of(fixture2, fixture4));
		Team finalist1 = determineVictor(fixtures1);
		Team finalist2 = determineVictor(fixtures2);
		Thread.sleep(3000);
		Team winner = determineVictor(finalist1, finalist2);
		Thread.sleep(1000);
		System.out.println("\n" + winner.getName() + " are promoted!");
		winner.promote();
	}

	private static Team determineVictor(Team finalist1, Team finalist2) throws InterruptedException {
		final double homeForm = finalist1.getLast5().stream().mapToInt(i -> i).sum();
		final double awayForm = finalist2.getLast5().stream().mapToInt(i -> i).sum();
		
		final double homeChance = ((finalist1.getAttack() + (100 - finalist2.getDefence())) / 2 + (homeForm + (15 - awayForm)) + 1.5 * (20 - finalist1.getPlace()) + finalist2.getPlace() + floor(100 * random())) / 3 + 20;
		final double awayChance = ((finalist2.getAttack() + (100 - finalist1.getDefence())) / 2 + (awayForm + (15 - homeForm)) + 1.5 * (20 - finalist2.getPlace()) + finalist1.getPlace() + floor(100 * random())) / 3 + 20;			
		
		int score1 = getScore(homeChance);
		int score2 = getScore(awayChance);
		
		System.out.println("\nFinal: " + finalist1.getName() + " " + score1 + " : " + score2 + " " + finalist2.getName());
		
		if(score1 != score2)
			return score1 > score2 ? finalist1 : finalist2;
		else {
			Thread.sleep(2000);
			List<Team> pens = List.of(finalist1, finalist2);
			Team winner = pens.get((int) floor(2 * random()));
			System.out.println(winner.getName() + " win on penalties!\n");
			Thread.sleep(1000);
			return winner;
			}
	}

	private static Team determineVictor(List<Fixture> fixtures) throws InterruptedException {
		for(Fixture fixture : fixtures) {
			Team homeTeam = fixture.getHomeTeam();
			Team awayTeam = fixture.getAwayTeam();
			
			final double homeForm = homeTeam.getLast5().stream().mapToInt(i -> i).sum();
			final double awayForm = awayTeam.getLast5().stream().mapToInt(i -> i).sum();
			
			final double homeChance = ((homeTeam.getAttack() + (100 - awayTeam.getDefence())) / 2 + (homeForm + (15 - awayForm)) + 1.5 * (20 - homeTeam.getPlace()) + awayTeam.getPlace() + floor(100 * random())) / 3;
			final double awayChance = 0.9 * ((awayTeam.getAttack() + (100 - homeTeam.getDefence())) / 2 + (awayForm + (15 - homeForm)) + 1.5 * (20 - awayTeam.getPlace()) + homeTeam.getPlace() + floor(100 * random())) / 3;			
			
			int home = getScore(homeChance);
			int away = getScore(awayChance);
			
			System.out.println(homeTeam.getName() + " " + home + " : " + away + " " + awayTeam.getName());
			Thread.sleep(1000);
			
			homeTeam.setGoalsScored(homeTeam.getGoalsScored() + home);
			awayTeam.setGoalsScored(awayTeam.getGoalsScored() + away);
			Thread.sleep(1000);
		}
		
		Team team1 = fixtures.get(0).getHomeTeam();
		Team team2 = fixtures.get(0).getAwayTeam();
		
		System.out.println("\nAggregate: " + team1.getName() + " " + team1.getGoalsScored() + " : " + team2.getGoalsScored() + " " + team2.getName() + "\n");
	
		Thread.sleep(1000);
		
		if(team1.getGoalsScored() != team2.getGoalsScored()) {
			Team winner = team1.getGoalsScored() > team2.getGoalsScored() ? team1 : team2;
			return winner;
		} else {
			List<Team> pens = List.of(team1, team2, team2);
			Team winner = pens.get((int) floor(3 * random()));
			Thread.sleep(2000);
			System.out.println("\n" + winner.getName() + " win on penalties!\n");
			Thread.sleep(1000);
			return winner;
		}
		
	}

	private static void write(List<Team> teams) {
		String league = teams.get(0).getLeague().getInitials();
		try(var fw = new FileWriter("src/main/resources/" + league + ".txt"); var bw = new BufferedWriter(fw)){
			teams.stream()
				.sorted(Comparator.comparing(Team::getName))
				.forEach(team -> {
					try {
						bw.write(team.getName() + " : " + team.getAttack() + " : " + team.getDefence() + " : " + team.getStartingLeague().getInitials() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
		} catch (IOException e) {
			e.printStackTrace();
		}
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
