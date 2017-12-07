package me.aaron.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class BuildModeCmd implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public BuildModeCmd(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (pl.inBuildMode.contains(p)) {
				pl.inBuildMode.remove(p);
				p.sendMessage(pl.prefix + "§cDu kannst nicht mehr bauen!");
			} else {
				if (p.hasPermission("lobby.buildmode")) {
					pl.inBuildMode.add(p);
					p.sendMessage(pl.prefix + "§2Du kannst nun bauen!");
				} else {
					p.sendMessage(pl.prefix + "§cKeine Rechte!");
				}
			}
			
		} else {
			sender.sendMessage(pl.prefix + "§cNur Spielern können diesen Command ausführen.");
		}
		
		return true;
	}
	
	

}
