package me.aaron.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import me.aaron.main.LobbyPlugin;

public class PlayerChangeWorld implements Listener {
	
	private LobbyPlugin pl;
	
	public PlayerChangeWorld(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onPlayerChangeWorld(final PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		
		World from = e.getFrom();
		World to = p.getWorld();
		
		if (from.getName() == pl.getConfig().getString("hub.world")) {
			return;
		} else {
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			JoinListener.giveSpawnItems(p);
			pl.clearChat(p);
		}
	}
}
