package me.aaron.data;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesSettings;

public class SGPlayer {
	
	private UUID uuid;
	private boolean playSounds = true;
	private SaltyGames pl;
	private DataBase stats;
	
	private int tokens;
	
	public SGPlayer(SaltyGames pl, UUID uuid) {
		this.uuid = uuid;
		this.pl = pl;
		this.stats = pl.getDataBase();
		
		if (!SaltyGamesSettings.useMySQL) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				stats.set(uuid.toString(), "name", p.getName());
			}
		}
		loadData();
	}

	private void loadData() {
		stats.loadPlayer(this, true);
	}

	public boolean isPlaySounds() {
		return playSounds;
	}

	public void setPlaySounds(boolean playSounds) {
		this.playSounds = playSounds;
	}
	
	public void toggleSound() {
		this.playSounds = !playSounds;
	}

	public int getTokens() {
		return this.tokens;
	}

	public void setTokens(int newTokens) {
		this.tokens = newTokens;
		pl.getPluginManager().getGuiManager().updateTokens(this);
	}

	public UUID getUuid() {
		return uuid;
	}
	
	
	public void remove() {
		pl.getPluginManager().getGuiManager().removePlayer(this.uuid);
	}
	
	public void save() {
		save(false);
	}
	
	public void save(boolean async) {
		stats.savePlayer(this, async);
	}
}
