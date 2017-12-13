package main.me.aaron.listeners;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import main.me.aaron.ArenaConfigStrings;
import main.me.aaron.MinigamesAPI;

public class SignBreak implements Listener {
	
	FileConfiguration config = MinigamesAPI.getAPI().getConfig();
	
	@EventHandler
	public void onSignBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.SIGN_POST || e.getBlock().getType() == Material.WALL_SIGN) {
			String server = MinigamesAPI.getAPI().getServerBySignLocation(e.getBlock().getLocation());
			if (server != null) {
				MinigamesAPI.getAPI().signManager.detachSign(e.getBlock().getLocation());
				final String[] split = MinigamesAPI.getAPI().getInfoBySignLocation(e.getBlock().getLocation()).split(":");
				config.set(ArenaConfigStrings.ARENAS_PREFIX + split[0] + "." + split[1], null);
			}
		}
	}

}
