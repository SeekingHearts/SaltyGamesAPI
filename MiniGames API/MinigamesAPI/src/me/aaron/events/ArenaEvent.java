package me.aaron.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.Arena;

public class ArenaEvent extends Event {

	private final JavaPlugin pl;
	private final Arena ar;
	
	private static final HandlerList handlers = new HandlerList();
	
	public ArenaEvent(final JavaPlugin pl, final Arena ar) {
		this.pl = pl;
		this.ar = ar;
	}
	
	public JavaPlugin getPlugin() {
		return pl;
	}

	public Arena getArena() {
		return ar;
	}
	
	@Override
	public HandlerList getHandlers() {
		return ArenaEvent.handlers;
	}
	
	public static HandlerList getHandlerList() {
		return ArenaEvent.handlers;
	}
}
