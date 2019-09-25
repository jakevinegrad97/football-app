package com.vinegrad.table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.vinegrad.model.League;
import com.vinegrad.model.Team;

public class TableReader {

	public static List<Team> read(String path) {
		
		League league = League.of(path.replace("src/main/resources/", "").replace(".txt", "").trim());
		
		try {
			List<Team> teams =
			 Files.lines(Paths.get(path))
				.map(line -> {
					String[] split = line.split(":");
					return new Team(split[0].trim(), Integer.valueOf(split[1].trim()), Integer.valueOf(split[2].trim()), League.of(split[3].trim()));
				})
					.collect(Collectors.toList());
			for(Team team : teams) {
				team.setLeague(league);
			}
			return teams;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	} 
}
