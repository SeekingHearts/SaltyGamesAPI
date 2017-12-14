package main.java.me.aaron.saltygamesapi.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import main.java.me.aaron.saltygamesapi.ArenaConfigStrings;
import main.java.me.aaron.saltygamesapi.MinigamesAPI;

public class BlockBreak implements Listener {
	
	FileConfiguration config = MinigamesAPI.getAPI().getConfig();
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.SIGN_POST || e.getBlock().getType() == Material.WALL_SIGN) {
			if (config.isSet(ArenaConfigStrings.ARENAS_PREFIX)) {
				for (String mg_key : config.getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX).getKeys(false)) {
					for (String ar_key : config.getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX + mg_key + ".").getKeys(false)) {
						final ConfigurationSection sec = config.getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key);
						
						if (sec.contains("world")) {
							Location loc = new Location(Bukkit.getWorld(config.getString(
									ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".world"))
									, config.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".loc.x")
									, config.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".loc.y")
									, config.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".loc.z"));
							if (loc.distance(e.getBlock().getLocation()) < 1) {
								config.set(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".server", null);
								config.set(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".world", null);
								config.set(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".loc", null);
								MinigamesAPI.getAPI().saveConfig();
								return;
							}
						}
						if (sec.contains("specworld")) {
							Location loc = new Location(Bukkit.getWorld(config.getString(
									ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specworld"))
									, config.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specloc.x")
									, config.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specloc.y")
									, config.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specloc.z"));
							if (loc.distance(e.getBlock().getLocation()) < 1) {
								config.set(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specserver", null);
								config.set(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specworld", null);
								config.set(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specloc", null);
								MinigamesAPI.getAPI().saveConfig();
								return;
							}
						}
					}
				}
			}
		}
	}
}
