package me.aaron.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.aaron.main.LobbyPlugin;

public class RespawnListener implements Listener {
	
	private LobbyPlugin pl;
	
	public RespawnListener(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		
		Player p = e.getPlayer();
		
			World w = Bukkit.getServer().getWorld(pl.getConfig().getString("hub.world"));
			double x = pl.getConfig().getDouble("hub.x");
			double y = pl.getConfig().getDouble("hub.y");
			double z = pl.getConfig().getDouble("hub.z");
			double pitch = pl.getConfig().getDouble("hub.pitch");
			double yaw = pl.getConfig().getDouble("hub.yaw");
			
			e.setRespawnLocation(new Location(w, x, y, z, (float) yaw, (float) pitch));
			
			JoinListener.giveSpawnItems(p);
			
			return;
	}

}
