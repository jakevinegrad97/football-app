package com.vinegrad.year;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class YearHandler {

	public static void handleYear() {
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			int oldYear = Files.lines(Paths.get("src/main/resources/year.txt"))
				.mapToInt(year -> Integer.valueOf(year.trim()))
				.sum();
			int newYear = oldYear + 1;
			fw = new FileWriter("src/main/resources/year.txt");
			bw = new BufferedWriter(fw);
			bw.write(String.valueOf(newYear));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
