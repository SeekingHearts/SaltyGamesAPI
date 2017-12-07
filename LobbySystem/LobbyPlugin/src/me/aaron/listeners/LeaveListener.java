package me.aaron.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.aaron.main.LobbyPlugin;

public class LeaveListener implements Listener {
	
	private LobbyPlugin pl;
	
	public LeaveListener(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage("");
	}

}
