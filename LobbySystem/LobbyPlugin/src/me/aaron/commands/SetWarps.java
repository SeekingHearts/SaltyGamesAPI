package me.aaron.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class SetWarps implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public SetWarps(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			if (!p.getWorld().getName().equals(pl.getConfig().getString("hub.world")))
				return true;
			
			if (p.hasPermission("lobby.setwarps")) {
				
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("freebuild")) {
						
						pl.getConfig().set("freebuild.x", p.getLocation().getX());
						pl.getConfig().set("freebuild.y", p.getLocation().getY());
						pl.getConfig().set("freebuild.z", p.getLocation().getZ());
						pl.getConfig().set("freebuild.pitch", p.getLocation().getPitch());
						pl.getConfig().set("freebuild.yaw", p.getLocation().getYaw());
						pl.saveConfig();
						
						p.sendMessage(pl.prefix + ChatColor.GREEN + "Der Spawn für" + ChatColor.DARK_GREEN + " FreeBuild " + ChatColor.GREEN + "festgelegt!");
						return true;
					} else if (args[0].equalsIgnoreCase("bedwars")) {
						
						pl.getConfig().set("bedwars.x", p.getLocation().getX());
						pl.getConfig().set("bedwars.y", p.getLocation().getY());
						pl.getConfig().set("bedwars.z", p.getLocation().getZ());
						pl.getConfig().set("bedwars.pitch", p.getLocation().getPitch());
						pl.getConfig().set("bedwars.yaw", p.getLocation().getYaw());
						pl.saveConfig();
						
						p.sendMessage(pl.prefix + ChatColor.GREEN + "Der Spawn für" + " §bBedWars " + ChatColor.GREEN + "festgelegt!");
						return true;
					} else if (args[0].equalsIgnoreCase("1vs1")) {
						
						pl.getConfig().set("1vs1.x", p.getLocation().getX());
						pl.getConfig().set("1vs1.y", p.getLocation().getY());
						pl.getConfig().set("1vs1.z", p.getLocation().getZ());
						pl.getConfig().set("1vs1.pitch", p.getLocation().getPitch());
						pl.getConfig().set("1vs1.yaw", p.getLocation().getYaw());
						pl.saveConfig();
						
						p.sendMessage(pl.prefix + ChatColor.GREEN + "Der Spawn für" + ChatColor.DARK_GREEN + " 1vs1 " + ChatColor.GREEN + "festgelegt!");
						return true;
					} else {
						p.sendMessage(pl.prefix + ChatColor.RED + "Usage: " + ChatColor.AQUA + "/setwarps <freebuild/bedwars/1vs1>");
						return true;
					}
				} else {
					p.sendMessage(pl.prefix + ChatColor.RED + "Usage: " + ChatColor.AQUA + "/setwarps <freebuild/bedwars/1vs1>");
					return true;
				}
				
			} else {
				sender.sendMessage(pl.prefix + ChatColor.RED + "Du hast keine Rechte.");
				return true;
			}
		} else {
			sender.sendMessage(pl.prefix + ChatColor.RED + "Dieser Befehl kann nur von Spielern benutzt werden.");
			return true;
		}
	}
}
