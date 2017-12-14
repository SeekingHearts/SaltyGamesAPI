package main.java.me.aaron.saltygamesapi.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.aaron.saltygamesapi.Arena;
import main.java.me.aaron.saltygamesapi.ArenaConfigStrings;
import main.java.me.aaron.saltygamesapi.ArenaLogger;
import main.java.me.aaron.saltygamesapi.MinigamesAPI;
import main.java.me.aaron.saltygamesapi.PluginInstance;

public class Validator {
	
	public static boolean isPlayerOnline(final String p_) {
		final Player p = Bukkit.getPlayer(p_);
		if (p != null) {
			return true;
		}
		return false;
	}

	public static boolean isPlayerOnline(final UUID p_) {
		final Player p = Bukkit.getPlayer(p_);
		if (p != null) {
			return true;
		}
		return false;
	}
	
	public static boolean isPlayerValid(final JavaPlugin pl, final String player, final Arena ar) {
		return Validator.isPlayerValid(pl, player, ar.getInternalName());
	}
	
	public static boolean isPlayerValid(final JavaPlugin pl, final String player, final String ar) {
		if (!Validator.isPlayerOnline(player)) {
			return false;
		}
		final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(pl);
		if (!pli.global_players.containsKey(player)) {
			return false;
		}
		if (!pli.global_players.get(player).getInternalName().equalsIgnoreCase(ar)) {
			return false;
		}
		return true;
	}
	
	public static boolean isArenaValid(final JavaPlugin pl, final Arena ar) {
		return Validator.isArenaValid(pl, ar.getInternalName());
	}
	
	public static boolean isArenaValid(final JavaPlugin pl, final String ar) {
		final FileConfiguration config = MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().getConfig();
		return isArenaValid(pl, ar, config);
	}
	
	public static boolean isArenaValid(JavaPlugin pl, final String ar, final FileConfiguration cfg) {
		final FileConfiguration config = cfg;
		final boolean hasLobby = config.isSet(ArenaConfigStrings.ARENAS_PREFIX + ar + ".lobby");
		final boolean hasSpawn = config.isSet(ArenaConfigStrings.ARENAS_PREFIX + ar + ".spawns.spawn0");
		
		if (!hasLobby || !hasSpawn) {
			ArenaLogger.debug(ChatColor.AQUA + ar + " ist ungültig! Lobby:" + hasLobby + " spawns.spawn0:" + hasSpawn);
			return false;
		}
		return true;
	}
}
