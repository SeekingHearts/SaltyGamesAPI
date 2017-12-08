package me.aaron.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeftSaltyGamesEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private Player p;
	
	public LeftSaltyGamesEvent(Player p) {
		this.p = p;
		
		Bukkit.getPluginManager().callEvent(this);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
