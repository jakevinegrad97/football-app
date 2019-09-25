package com.vinegrad.model;

import java.util.ArrayList;
import java.util.List;

public class Team {

	private String name;
	private int gamesPlayed;
	private int won;
	private int drawn;
	private int lost;
	private int goalsScored;
	private int goalsConceded;
	private int goalDifference;
	private int place;
	private List<Integer> last5 = new ArrayList<>();
	private int attack;
	private int defence;
	private League league;
	private League startingLeague;
	
	public League getStartingLeague() {
		return startingLeague;
	}
	
	public void setGoalsScored(int goalsScored) {
		this.goalsScored = goalsScored;
	}

	public void setGoalsConceded(int goalsConceded) {
		this.goalsConceded = goalsConceded;
	}

	public void resetGoals() {
		goalsScored = 0;
		goalsConceded = 0;
	}
	
	public void setLeague(League league) {
		this.league = league;
	}
	
	public void promote() {
		switch(league) {
		case CHAMPIONSHIP :
			league = League.PREMIER_LEAGUE;
			break;
		case LEAGUE_1 :
			league = League.CHAMPIONSHIP;
			break;
		case LEAGUE_2 :
			league = League.LEAGUE_1;
			break;
		case NATIONAL_LEAGUE :
			league = League.LEAGUE_2;
			break;
		default :
			league = League.PREMIER_LEAGUE;
			break;
		}
	}
	
	public void relegate() {
		switch(league) {
		case PREMIER_LEAGUE :
			league = League.CHAMPIONSHIP;
			break;
		case CHAMPIONSHIP :
			league = League.LEAGUE_1;
			break;
		case LEAGUE_1 :
			league = League.LEAGUE_2;
			break;
		case LEAGUE_2 :
			league = League.NATIONAL_LEAGUE;
			break;
		default :
			league = League.LEAGUE_2;
			break;
		}
	}
	
	public League getLeague() {
		return league;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefence() {
		return defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public List<Integer> getLast5() {
		return last5;
	}
	
	public int getGoalDifference() {
		return goalDifference;
	}

	private int points;
	public String getName() {
		return name;
	}
	public int getGamesPlayed() {
		return gamesPlayed;
	}
	public int getWon() {
		return won;
	}
	public int getDrawn() {
		return drawn;
	}
	public int getLost() {
		return lost;
	}
	public int getGoalsScored() {
		return goalsScored;
	}
	public int getGoalsConceded() {
		return goalsConceded;
	}
	public int getPoints() {
		return points;
	}
	public Team(String name, int gamesPlayed, int won, int drawn, int lost, int goalsScored, int goalsConceded,
			int points, int attack, int defence, League league, League startingLeague) {
		this.name = name;
		this.gamesPlayed = gamesPlayed;
		this.won = won;
		this.drawn = drawn;
		this.lost = lost;
		this.goalsScored = goalsScored;
		this.goalsConceded = goalsConceded;
		this.points = points;
		this.goalDifference = goalsScored - goalsConceded;
		this.place = 10;
		this.attack = attack;
		this.defence = defence;
		this.league = league;
		this.startingLeague = startingLeague;
	}
	public Team(String name, int attack, int defence, League startingLeague) {
		this.name = name;
		this.gamesPlayed = 0;
		this.won = 0;
		this.drawn = 0;
		this.lost = 0;
		this.goalsScored = 0;
		this.goalsConceded = 0;
		this.points = 0;
		this.place = 10;
		this.attack = attack;
		this.defence = defence;
		this.startingLeague = startingLeague;
	}
	
	public void addResult(int goalsScored, int goalsConceded) {
		gamesPlayed++;
		this.goalsScored += goalsScored;
		this.goalsConceded += goalsConceded;
		this.goalDifference += (goalsScored - goalsConceded);
		attack += (int) Math.floor((goalsScored / 2) * Math.random());
		defence -= (int) Math.floor((goalsConceded / 2) * Math.random());
		if(goalsConceded == 0)
			defence += 1;
		switch((int) Math.signum(goalsScored - goalsConceded)) {
		case -1 :
			lost++;
			attack -= (int) Math.floor(2 * Math.random());
			defence -= (int) Math.floor(2 * Math.random());
			addForm(0);
			break;
		case 0 :
			drawn++;
			addForm(1);
			points++;
			break;
		case 1 :
			won++;
			attack += (int) Math.floor(2 * Math.random());
			defence += (int) Math.floor(2 * Math.random());
			addForm(3);
			points += 3;
			break;
		}
		
		checkAttack();
		checkDefence();
	}
	
	private void checkAttack() {
		int leagueBoost = 5 * (league.getTier() - startingLeague.getTier());
		if(attack > 100 + leagueBoost)
			attack = 100 + leagueBoost;
		switch(league) {
		case PREMIER_LEAGUE :
			if(attack < 70 + leagueBoost)
				attack = 70 + leagueBoost;
			break;
		case CHAMPIONSHIP :
			if(attack > 75 + leagueBoost)
				attack = 75 + leagueBoost;
			if(attack < 60 + leagueBoost)
				attack = 60 + leagueBoost;
			break;
		case LEAGUE_1 :
			if(attack > 65 + leagueBoost)
				attack = 65 + leagueBoost;
			if(attack < 50 + leagueBoost)
				attack = 50 + leagueBoost;
			break;
		case LEAGUE_2 :
			if(attack > 60 + leagueBoost)
				attack = 60 + leagueBoost;
			if(attack < 40 + leagueBoost)
				attack = 40 + leagueBoost;
			break;
		case NATIONAL_LEAGUE:
			if(attack > 50 + leagueBoost)
				attack = 50 + leagueBoost;
			if(attack < 30 + leagueBoost)
				attack = 30 + leagueBoost;
			break;
		default:
			break;
				
		}
	}

	private void checkDefence() {
		int leagueBoost = 5 * (league.getTier() - startingLeague.getTier());
		if(defence > 100 + leagueBoost)
			defence = 100 + leagueBoost;
		switch(league) {
		case PREMIER_LEAGUE :
			if(defence < 70 + leagueBoost)
				defence = 70 + leagueBoost;
			break;
		case CHAMPIONSHIP :
			if(defence > 75 + leagueBoost)
				defence = 75 + leagueBoost;
			if(defence < 60 + leagueBoost)
				defence = 60 + leagueBoost;
			break;
		case LEAGUE_1 :
			if(defence > 65 + leagueBoost)
				defence = 65 + leagueBoost;
			if(defence < 50 + leagueBoost)
				defence = 50 + leagueBoost;
			break;
		case LEAGUE_2 :
			if(defence > 60 + leagueBoost)
				defence = 60 + leagueBoost;
			if(defence < 40 + leagueBoost)
				defence = 40 + leagueBoost;
			break;
		case NATIONAL_LEAGUE:
			if(defence > 50 + leagueBoost)
				defence = 50 + leagueBoost;
			if(defence < 30 + leagueBoost)
				defence = 30 + leagueBoost;
			break;
		default:
			break;
				
		}
	}

	private void addForm(int points) {
		if(last5.size() < 5)
			last5.add(points);
		else {
			last5.remove(0);
			last5.add(points);
		}
	}
	
	@Override
	public String toString() {
		String tab = "";
		for(int i = 0; i < 32 - name.length(); i++) {
			tab += " ";
		}
		return name + " : " + tab + gamesPlayed + "   " + won + "   "
				+ drawn + "   " + lost + "   " + goalsScored + "   " + goalsConceded
				+ "   " + goalDifference + "   " + points + "\n";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Team other = (Team) obj;
		if (drawn != other.drawn)
			return false;
		if (gamesPlayed != other.gamesPlayed)
			return false;
		if (goalsConceded != other.goalsConceded)
			return false;
		if (goalsScored != other.goalsScored)
			return false;
		if (lost != other.lost)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (points != other.points)
			return false;
		if (won != other.won)
			return false;
		return true;
	}
	
	
	
	
}
