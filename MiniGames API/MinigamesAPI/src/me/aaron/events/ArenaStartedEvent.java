package me.aaron.events;

import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.Arena;

public class ArenaStartedEvent extends ArenaEvent {

	public ArenaStartedEvent(final JavaPlugin pl, final Arena ar) {
		super(pl, ar);
	}
}
