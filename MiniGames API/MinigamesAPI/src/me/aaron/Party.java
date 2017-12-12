package me.aaron;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Party {
	
	private String owner;
	
	private ArrayList<String> players = new ArrayList<>();
	
	public Party(final String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	public ArrayList<String> getPlayers() {
		return players;
	}
	
	public void addPlayer(final String p) {
		if (!this.players.contains(p)) {
			this.players.add(p);
		}
		
		Bukkit.getPlayer(p).sendMessage(MinigamesAPI.getAPI().partyMessages.you_joined_party.replaceAll("<player>", this.getOwner()));
		this.tellAll(MinigamesAPI.getAPI().partyMessages.player_joined_party.replaceAll("<player>", p));
	}
	
	public boolean removePlayer(final String p) {
		if (this.players.contains(p)) {
			this.players.remove(p);
			final Player ppp = Bukkit.getPlayer(p);
			if (ppp != null) {
				ppp.sendMessage(MinigamesAPI.getAPI().partyMessages.you_left_party.replaceAll("<player>", getOwner()));
			}
			tellAll(MinigamesAPI.getAPI().partyMessages.player_left_party.replaceAll("<player>", p));
			return true;
		}
		return false;
	}
	
	public boolean containsPlayer(final String p) {
		return players.contains(p);
	}
	
	public void disband() {
		tellAll(MinigamesAPI.getAPI().partyMessages.party_disbanded);
		if (MinigamesAPI.getAPI().globalParty.containsKey(owner)) {
			players.clear();
			MinigamesAPI.getAPI().globalParty.remove(owner);
		}
	}
	
	private void tellAll(final String msg) {
		for (final String p : this.getPlayers()) {
			final Player pp  = Bukkit.getPlayer(p);
			if (pp != null) {
				pp.sendMessage(msg);
			}
		}
		final Player ppp = Bukkit.getPlayer(this.getOwner());
		if (ppp != null) {
			ppp.sendMessage(msg);
		}
	}
	
	
}
