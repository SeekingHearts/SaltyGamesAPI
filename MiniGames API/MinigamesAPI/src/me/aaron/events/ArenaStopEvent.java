package me.aaron.events;

import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.Arena;

public class ArenaStopEvent extends ArenaEvent {

	public ArenaStopEvent(final JavaPlugin pl, final Arena ar) {
		super(pl, ar);
	}
}
