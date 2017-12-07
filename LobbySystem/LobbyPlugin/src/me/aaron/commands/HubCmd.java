package me.aaron.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class HubCmd implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public HubCmd(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (!p.hasPermission("lobby.instanttp")) {
				p.sendMessage(pl.prefix + "Du wirst in 3 Sekunden zum Spawn teleportiert!");
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
					
					@Override
					public void run() {
						
						tpToHub(p);
						return;
					}
				},3*20L);
				
				return true;
		} else {
				tpToHub(p);
				return true;
			}
	}
	
	
	public void tpToHub(Player p) {
		try {
			double x = pl.getConfig().getDouble("hub.x");
			double y = pl.getConfig().getDouble("hub.y");
			double z = pl.getConfig().getDouble("hub.z");
			double pitch = pl.getConfig().getDouble("hub.pitch");
			double yaw = pl.getConfig().getDouble("hub.yaw");
			
			p.getVelocity().setY(0);
			
			p.teleport(new Location(p.getWorld(), x, y, z));
			p.getLocation().setPitch((float) pitch);
			p.getLocation().setYaw((float) yaw);
			
			
		} catch (Exception e) {
			p.sendMessage(pl.prefix + ChatColor.RED + "Kein Spawn festgelegt. Bitte wende dich umgehend an ein TeamMitglied!");
			e.printStackTrace();
		}
	}
}
