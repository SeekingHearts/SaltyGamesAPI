package me.aaron.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.aaron.listeners.JoinListener;
import me.aaron.main.LobbyPlugin;

public class PvPOff implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public PvPOff(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	public static int x = 5;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (!p.getWorld().getName().equals(pl.getConfig().getString("hub.world")))
			return true;
		
		pl.willStopPvP.add(p.getName());
		Bukkit.getScheduler().runTaskTimer(pl, new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if (!pl.willStopPvP.contains(p.getName())) {
					this.cancel();
					return;
				}
				
				if (x == 0) {
						JoinListener.giveSpawnItems(p);
						pl.toggledPvP.remove(p.getName());
						pl.willStopPvP.remove(p.getName());
						this.cancel();
						return;
					} else {
						p.sendMessage(pl.prefix + ChatColor.GOLD + "Du darfst für 5 Sekunden keinen Schaden bekommen!");
						p.sendMessage("");
						p.sendMessage(pl.prefix + ChatColor.AQUA + "PvP ist aus in: " + x);
						x--;
					}
				}
		}, 0, 20);
		
		return true;
	}
}
