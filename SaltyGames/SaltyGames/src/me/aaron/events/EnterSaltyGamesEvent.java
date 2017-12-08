package me.aaron.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnterSaltyGamesEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private String cancelMessage = "none";
	private String[] args;
	
	private Player p;

	public EnterSaltyGamesEvent(Player p, String... args) {
		this.p = p;
		this.args = args;
		
		Bukkit.getPluginManager().callEvent(this);
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public String getCancelMessage() {
		return cancelMessage;
	}

	public void setCancelMessage(String cancelMessage) {
		this.cancelMessage = cancelMessage;
	}

	public String[] getArgs() {
		return args;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
