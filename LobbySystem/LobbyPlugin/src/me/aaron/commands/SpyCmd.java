package me.aaron.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class SpyCmd implements CommandExecutor {
	
	public boolean toggled = false;
	
	private LobbyPlugin pl;
	
	public SpyCmd(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (p.hasPermission("lobby.spy")) {
			
			if (!(args.length == 0)) {
				p.sendMessage(pl.prefix + "Usage: " + ChatColor.GOLD + "/spy");
				return true;
			}
			
			pl.usingSpy.add(p.getName());
			p.sendMessage(pl.prefix + ChatColor.GREEN + "Du belauscht nun alle Befehle von normalen Spielern!");
			
			
		} else {
			p.sendMessage(pl.prefix + ChatColor.RED + "Du hast keine Rechte!");
			return true;
		}
		
		return true;
	}
	
	
	public void toggle() {
		if (toggled) {
			toggled = false;
		} else {
			toggled = true;
		}
	}

}
