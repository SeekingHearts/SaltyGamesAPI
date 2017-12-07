package me.aaron.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.aaron.main.LobbyPlugin;

public class HungerListener implements Listener {

	private LobbyPlugin pl;

	public HungerListener(LobbyPlugin pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onPlayerHunger(FoodLevelChangeEvent e) {

		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
				e.setCancelled(true);
				if (p.getFoodLevel() < 19.0D) {
					p.setFoodLevel(20);
				}
		}
	}
}
