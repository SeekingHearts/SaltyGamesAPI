package main.java.me.aaron.saltygamesapi.events;

import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.aaron.saltygamesapi.Arena;

public class ArenaStopEvent extends ArenaEvent {

	public ArenaStopEvent(final JavaPlugin pl, final Arena ar) {
		super(pl, ar);
	}
}
