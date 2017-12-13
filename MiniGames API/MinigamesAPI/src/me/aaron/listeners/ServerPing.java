package me.aaron.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import me.aaron.MinigamesAPI;

public class ServerPing implements Listener {
	
	@EventHandler
	public void onServerPing(ServerListPingEvent e) {
		if (MinigamesAPI.getAPI().motd != null) {
			e.setMotd(MinigamesAPI.getAPI().motd);
		}
	}

}
