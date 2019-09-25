package com.vinegrad.model;

public enum League {

	PREMIER_LEAGUE(1, "PL", "Premier League"),
	CHAMPIONSHIP(2, "CH", "Championship"),
	LEAGUE_1(3, "L1", "League 1"),
	LEAGUE_2(4, "L2", "League 2"),
	NATIONAL_LEAGUE(5, "NL", "National League");

	private int tier;
	private String initials;
	private String displayName;
	
	League(int tier, String initials, String displayName) {
		this.tier = tier;
		this.initials = initials;
		this.displayName = displayName;
	}

	public static League of(String initials) {
		switch(initials) {
		case "PL" :
			return PREMIER_LEAGUE;
		case "CH" :
			return CHAMPIONSHIP;
		case "L1" :
			return LEAGUE_1;
		case "L2" :
			return LEAGUE_2;
		case "NL" :
			return NATIONAL_LEAGUE;
		default :
			return null;
		}
	}
	
	public int getTier() {
		return tier;
	}

	public String getInitials() {
		return initials;
	}

	public String getDisplayName() {
		return displayName;
	}

	
}
