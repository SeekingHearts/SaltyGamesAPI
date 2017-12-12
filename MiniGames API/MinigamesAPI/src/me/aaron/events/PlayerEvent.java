package me.aaron.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.Arena;

public class PlayerEvent extends Event {

	private final JavaPlugin pl;
	private final Arena ar;
	private final Player p;
	
	private static final HandlerList handlers = new HandlerList();
	
	
	public PlayerEvent(final Player p, final JavaPlugin pl, final Arena ar) {
		this.p = p;
		this.pl = pl;
		this.ar = ar;
	}
	
	public JavaPlugin getPlugin() {
		return pl;
	}

	public Arena getArena() {
		return ar;
	}

	public Player getPlayer() {
		return p;
	}

	@Override
	public HandlerList getHandlers() {
		return PlayerEvent.handlers;
	}
	
	public static HandlerList getHandlerList() {
		return PlayerEvent.handlers;
	}
}
