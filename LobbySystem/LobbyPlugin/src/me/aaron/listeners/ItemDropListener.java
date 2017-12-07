package me.aaron.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import me.aaron.main.LobbyPlugin;

public class ItemDropListener implements Listener {
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Material droppedItm = e.getItemDrop().getItemStack().getType();
		
		if (droppedItm == Material.COMPASS || droppedItm == Material.BLAZE_ROD || droppedItm == Material.TNT || droppedItm == Material.SKULL_ITEM) {
			e.setCancelled(true);
			updateInventory(e.getPlayer());
		}
	}
	
	public static void updateInventory(final Player p) {
		p.getServer().getScheduler().runTaskLater(LobbyPlugin.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				if (p != null && p.isOnline())
					p.updateInventory();
			}
		}, 2L);
	}
}