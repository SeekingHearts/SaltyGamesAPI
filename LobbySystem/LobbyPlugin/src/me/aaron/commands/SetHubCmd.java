package me.aaron.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.events.SpawnJoinEvent;
import me.aaron.main.LobbyPlugin;

public class SetHubCmd implements CommandExecutor {

	private LobbyPlugin pl;

	public SetHubCmd(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender;

		if (p.hasPermission("lobby.sethub")) {

			pl.getConfig().set("hub.world", p.getLocation().getWorld().getName());
			pl.getConfig().set("hub.x", p.getLocation().getX());
			pl.getConfig().set("hub.y", p.getLocation().getY());
			pl.getConfig().set("hub.z", p.getLocation().getZ());
			pl.getConfig().set("hub.pitch", p.getLocation().getPitch());
			pl.getConfig().set("hub.yaw", p.getLocation().getYaw());
			pl.saveConfig();

			p.sendMessage(pl.prefix + ChatColor.GREEN + "Der Spawn wurde erfolgreich gesetzt!");
			pl.getServer().getPluginManager().callEvent(new SpawnJoinEvent(p));
			
		} else {
			p.sendMessage(pl.prefix + ChatColor.RED + "Du hast keine Rechte!");
			return true;
		}
		return true;
	}
}
