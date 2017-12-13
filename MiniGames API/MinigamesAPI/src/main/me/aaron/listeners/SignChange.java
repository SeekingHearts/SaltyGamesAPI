package main.me.aaron.listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import main.me.aaron.ArenaConfigStrings;
import main.me.aaron.MinigamesAPI;

public class SignChange implements Listener {
	
	FileConfiguration config = MinigamesAPI.getAPI().getConfig();
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		Player p = e.getPlayer();
		
		if (e.getLine(0).toLowerCase().equalsIgnoreCase("sglib")) {
			if (e.getPlayer().hasPermission(MinigamesAPI.getAPI().getPermissionPrefix() + ".sign") || e.getPlayer().isOp()) {
				if (!e.getLine(1).equalsIgnoreCase("") & !e.getLine(2).equalsIgnoreCase("") && !e.getLine(3).equalsIgnoreCase("")) {
					String mg = e.getLine(1);
					String ar = e.getLine(2);
					String server = e.getLine(3);
					
					config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".server", server);
					config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".world", p.getWorld().getName());
					config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".loc.x", e.getBlock().getLocation().getBlockX());
					config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".loc.y", e.getBlock().getLocation().getBlockY());
					config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".loc.z", e.getBlock().getLocation().getBlockZ());
					MinigamesAPI.getAPI().saveConfig();
					
					p.sendMessage(ChatColor.GREEN + "Schild wurde erfolgreich plaziert!");
					
					MinigamesAPI.getAPI().signManager.attachSign(e.getBlock().getLocation(), server, mg, ar, false, e);
					MinigamesAPI.getAPI().signManager.requestSignUpdate(e.getBlock().getLocation());
				}
			}
		} else if (e.getLine(0).toLowerCase().equalsIgnoreCase("sglibspec")) {
			if (e.getPlayer().hasPermission(MinigamesAPI.getAPI().getPermissionPrefix() + ".sign") || e.getPlayer().isOp()) {
				if (!e.getLine(1).equalsIgnoreCase("") && !e.getLine(2).equalsIgnoreCase("") && !e.getLine(3).equalsIgnoreCase("")) {
					String mg = e.getLine(1);
					String ar = e.getLine(2);
					String server = e.getLine(3);
					
					config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specserver", server); 
                    config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specworld", p.getWorld().getName()); 
                    config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specloc.x", e.getBlock().getLocation().getBlockX()); 
                    config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specloc.y", e.getBlock().getLocation().getBlockY()); 
                    config.set(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specloc.z", e.getBlock().getLocation().getBlockZ()); 
                    MinigamesAPI.getAPI().saveConfig();
                    
                    p.sendMessage(ChatColor.GREEN + "Schild wurde erfolgreich plaziert!");
                    
                    MinigamesAPI.getAPI().signManager.attachSign(e.getBlock().getLocation(), server, mg, ar, true, e);
                    MinigamesAPI.getAPI().signManager.requestSignUpdate(e.getBlock().getLocation());
				}
			}
		}
	}

}
