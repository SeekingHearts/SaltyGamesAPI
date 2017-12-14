package main.me.aaron.events;

import org.bukkit.plugin.java.JavaPlugin;

import main.me.aaron.Arena;

public class ArenaStartEvent extends ArenaEvent {

	public ArenaStartEvent(final JavaPlugin pl, final Arena ar) {
		super(pl, ar);
	}
}
