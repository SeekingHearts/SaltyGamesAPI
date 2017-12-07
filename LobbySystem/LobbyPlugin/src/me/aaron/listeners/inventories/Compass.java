package me.aaron.listeners.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.aaron.methods.Invs;

public class Compass implements Listener {
	
	@EventHandler
	public void onCompass(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (p.getItemInHand().getType() == Material.COMPASS) {
				if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§5Teleportierer")) {
					Invs.openCompass(p);
				}
			}
		}
	}

}
