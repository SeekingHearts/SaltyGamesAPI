package main.java.me.aaron.saltygamesapi.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import main.java.me.aaron.saltygamesapi.MinigamesAPI;

public class ServerPing implements Listener {
	
	@EventHandler
	public void onServerPing(ServerListPingEvent e) {
		if (MinigamesAPI.getAPI().motd != null) {
			e.setMotd(MinigamesAPI.getAPI().motd);
		}
	}

}
