package main.me.aaron.events;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import main.me.aaron.Arena;

public class PlayerJoinLobbyEvent extends PlayerEvent {

	public PlayerJoinLobbyEvent(final Player p, final JavaPlugin pl, final Arena ar) {
		super(p, pl, ar);
	}
}
