package main.me.aaron;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Party {
	
	private UUID owner;
	
	private ArrayList<UUID> players = new ArrayList<>();
	
	public Party(final UUID owner) {
		this.owner = owner;
	}
	
	public UUID getOwner() {
		return this.owner;
	}
	
	public ArrayList<UUID> getPlayers() {
		return players;
	}
	
	public void addPlayer(final UUID p) {
		if (!this.players.contains(p)) {
			this.players.add(p);
		}
		Bukkit.getPlayer(p).sendMessage(MinigamesAPI.getAPI().partyMessages.you_joined_party.replaceAll("<player>", Bukkit.getPlayer(this.getOwner()).getName()));
		this.tellAll(MinigamesAPI.getAPI().partyMessages.player_joined_party.replaceAll("<player>", Bukkit.getPlayer(this.owner).getName()));
	}
	
	public boolean removePlayer(final UUID uuid) {
		if (players.contains(uuid)) {
			players.remove(uuid);
			final Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.sendMessage(MinigamesAPI.getAPI().partyMessages.you_left_party.replaceAll("<player>", Bukkit.getPlayer(owner).getName()));
				tellAll(MinigamesAPI.getAPI().partyMessages.player_left_party.replaceAll("<player>", p.getName()));
			}
			return true;
		}
		return false;
	}
	
	public boolean containsPlayer(final UUID uuid) {
		return players.contains(uuid);
	}
	
	public void disband() {
		tellAll(MinigamesAPI.getAPI().partyMessages.party_disbanded);
		if (MinigamesAPI.getAPI().globalParty.containsKey(owner)) {
			players.clear();
			MinigamesAPI.getAPI().globalParty.remove(owner);
		}
	}
	
	private void tellAll(final String msg) {
		for (final UUID p : this.getPlayers()) {
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
