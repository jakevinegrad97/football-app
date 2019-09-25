package com.vinegrad.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.vinegrad.cup.FACup;
import com.vinegrad.fixtures.FixtureGenerator;
import com.vinegrad.fixtures.FixtureWriter;
import com.vinegrad.model.League;
import com.vinegrad.model.Team;
import com.vinegrad.table.TableReader;
import com.vinegrad.table.TableWriter;
import com.vinegrad.year.YearHandler;

public class Matchday {

	public static void main(String[] args) throws InterruptedException {
		int round = 1;
		Scanner scanner = new Scanner(System.in);
		
		List<Team> teams = new ArrayList<>();
		
		while(round <= FixtureWriter.NUMBER_OF_ROUNDS) {
			Thread.sleep(500);
			System.out.println("Press enter to play next round!");
			String a = scanner.nextLine();
			Thread.sleep(500);
			teams = FixtureWriter.writeFixtures(round++, teams);
			TableWriter.write(teams, League.PREMIER_LEAGUE);
			TableWriter.write(teams, League.CHAMPIONSHIP);
			TableWriter.write(teams, League.LEAGUE_1);
			TableWriter.write(teams, League.LEAGUE_2);
			System.out.println();
			Thread.sleep(500);
			readTable(League.PREMIER_LEAGUE);
			System.out.println();
			Thread.sleep(500);
			readTable(League.CHAMPIONSHIP);
			System.out.println();
			Thread.sleep(500);
			Thread.sleep(500);
			readTable(League.LEAGUE_1);
			System.out.println();
			Thread.sleep(500);
			readTable(League.LEAGUE_2);
			System.out.println();
			if(round > FixtureWriter.NUMBER_OF_ROUNDS) {
				Thread.sleep(1000);
				System.out.println("FA Cup time!\n");
				Thread.sleep(1000);
				List<Team> nl = TableReader.read("src/main/resources/NL.txt");
				List<Team> forCup = teams.stream().map(team -> team).collect(Collectors.toList());
				forCup.addAll(nl);
				FACup.simulateCup(forCup);
				Thread.sleep(1000);
				TableWriter.updateTables(teams);
			}
		}
		scanner.close();
		YearHandler.handleYear();
	}
	public static void readTable(League league) {
		System.out.println("-----------------------------------------------------------------------------------");
		System.out.println(league.getDisplayName() + " Table\n");
		try {
			Files.lines(Paths.get("src/main/resources/" + league.getInitials() + "Table.txt"))
				.skip(1)
				.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
