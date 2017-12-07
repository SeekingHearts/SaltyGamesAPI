package me.aaron.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.aaron.main.LobbyPlugin;

public class BlockBreakPlaceListener implements Listener {
	
	private LobbyPlugin pl;
	
	public BlockBreakPlaceListener(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		
		if (pl.inBuildMode.contains(p)) {
			e.setCancelled(false);
			return;
		} else {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		if (pl.inBuildMode.contains(p)) {
			e.setCancelled(false);
			return;
		} else {
			e.setCancelled(true);
		}
	}

}
