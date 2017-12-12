package me.aaron.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.aaron.main.LobbyPlugin;

public class PlayerDamageListener implements Listener {
	
	private LobbyPlugin pl;
	
	public PlayerDamageListener(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player target = (Player) e.getEntity();
			Player p = (Player) e.getDamager();
			
			e.setCancelled(true);
			
			
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerFallDamage(final EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		if (e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		}
	}
}
