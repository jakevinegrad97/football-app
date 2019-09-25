package com.vinegrad.model;

public class Fixture {

	private int round;
	private Team homeTeam;
	private Team awayTeam;
	private int homeScore;
	private int awayScore;
	
	public int getRound() {
		return round;
	}
	public Team getHomeTeam() {
		return homeTeam;
	}
	public Team getAwayTeam() {
		return awayTeam;
	}
	public int getHomeScore() {
		return homeScore;
	}
	public int getAwayScore() {
		return awayScore;
	}
	public Fixture(int round, Team homeTeam, Team awayTeam) {
		this.round = round;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
	}
	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}
	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public void addResult(int homeScore, int awayScore) {
		this.homeScore = homeScore;
		this.awayScore = awayScore;
	}
	@Override
	public String toString() {
		return homeTeam.getName() + " " + homeScore + " : "
				+ awayScore + " " + awayTeam.getName();
	}
	public String beforeMatch() {
		return homeTeam.getName() + " vs. " + awayTeam.getName();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awayTeam == null) ? 0 : awayTeam.hashCode());
		result = prime * result + ((homeTeam == null) ? 0 : homeTeam.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fixture other = (Fixture) obj;
		if (awayTeam == null) {
			if (other.awayTeam != null)
				return false;
		} else if (!awayTeam.equals(other.awayTeam))
			return false;
		if (homeTeam == null) {
			if (other.homeTeam != null)
				return false;
		} else if (!homeTeam.equals(other.homeTeam))
			return false;
		return true;
	}
	
	
}

