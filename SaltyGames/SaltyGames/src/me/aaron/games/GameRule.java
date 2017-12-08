package me.aaron.games;

import java.util.HashSet;

import me.aaron.data.SaveType;

public class GameRule {

	protected HashSet<SaveType> saveTps;
	
	protected boolean saveStats;
	
	protected String key;
	
	public GameRule(boolean saveStats, HashSet<SaveType> saveTps, String key) {
		this.saveStats = saveStats;
		this.saveTps = saveTps;
		this.key = key;
	}

	public HashSet<SaveType> getSaveTps() {
		return saveTps;
	}

	public boolean isSaveStats() {
		return saveStats;
	}

	public String getKey() {
		return key;
	}
}
