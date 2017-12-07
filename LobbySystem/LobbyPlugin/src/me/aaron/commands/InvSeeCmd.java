package me.aaron.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class InvSeeCmd implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public InvSeeCmd(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (p.hasPermission("lobby.invsee")) {
			
			if (args.length < 1) {
				p.sendMessage(pl.prefix + "Usage: " + ChatColor.GOLD + "/inv <Player>");
				return true;
			}
			
			Player target = pl.getServer().getPlayer(args[0]);
			
			if (target == null) {
				p.sendMessage(pl.prefix + ChatColor.RED + "Der Spieler " + ChatColor.GOLD + args[0].toString() + ChatColor.RED + " ist nicht online!");
				return true;
			}
			
			p.openInventory(target.getInventory());
			
		} else {
			p.sendMessage(pl.prefix + ChatColor.RED + "Du hast keine Rechte!");
			return true;
		}
		
		return true;
	}
	
	

}
