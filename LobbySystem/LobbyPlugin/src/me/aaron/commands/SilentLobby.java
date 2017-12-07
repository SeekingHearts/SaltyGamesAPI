package me.aaron.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class SilentLobby implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public SilentLobby(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (!p.getWorld().getName().equals(pl.getConfig().getString("hub.world")))
				return true;
			
			if (p.hasPermission("lobby.silent")) {
				if (pl.inSilentLobby.contains(p.getName())) {
					pl.inSilentLobby.remove(p.getName());
					p.sendMessage(pl.prefix + ChatColor.RED + "Du befindest dich nun nicht mehr in der Silent Lobby");
					for (Player players : Bukkit.getServer().getOnlinePlayers()) {
						players.showPlayer(p);
						p.showPlayer(players);
					}
				} else {
					pl.inSilentLobby.add(p.getName());
					pl.clearChat(p);
					for (Player players : Bukkit.getServer().getOnlinePlayers()) {
						players.hidePlayer(p);
						p.hidePlayer(players);
					}
				}
			}
		}
		return true;
	}
}