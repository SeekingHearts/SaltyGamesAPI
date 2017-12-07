package me.aaron.commands;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class CheckArrays implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public CheckArrays(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (p.getName().equals("Sistogy")) {
			
			if (args.length == 1) {
				
				if (args[0].equalsIgnoreCase("usingHide")) {
					for (String player : pl.usingHide) {
						String pla = Bukkit.getServer().getPlayer(player).getName();
						p.sendMessage(pl.prefix + pla);
					}
				}
				if (args[0].equalsIgnoreCase("fastbow")) {
					for (String player : pl.fastBow) {
						String pla = Bukkit.getServer().getPlayer(player).getName();
						p.sendMessage(pl.prefix + pla);
					}
				}
				if (args[0].equalsIgnoreCase("inSilentLobby")) {
					for (String player : pl.inSilentLobby) {
						String pla = Bukkit.getServer().getPlayer(player).getName();
						p.sendMessage(pl.prefix + pla);
					}
				}
				if (args[0].equalsIgnoreCase("doubleJumpOn")) {
					for (Entry<String, Boolean> entry : pl.doubleJump.entrySet()) {
						String pla = Bukkit.getServer().getPlayer(entry.getKey()).getName();
						p.sendMessage(pl.prefix + pla);
					}
				}
				if (args[0].equalsIgnoreCase("jumpPadsOn")) {
					for (Entry<String, Boolean> entry : pl.jumpPads.entrySet()) {
						String pla = Bukkit.getServer().getPlayer(entry.getKey()).getName();
						p.sendMessage(pl.prefix + pla);
					}
				}
				
			} else {
				p.sendMessage(pl.prefix + ChatColor.RED + "Usage: " + ChatColor.AQUA + "/checkarrays <usingHide/fastBow/usingSpy/inSilentLobby/doubleJumpOn/jumpPadsOn>");
				return true;
			}
			
		} else {
			p.sendMessage(pl.prefix + ChatColor.RED + "Du hast keine Rechte.");
			return true;
		}
		
		return true;
	}
	
	

}
