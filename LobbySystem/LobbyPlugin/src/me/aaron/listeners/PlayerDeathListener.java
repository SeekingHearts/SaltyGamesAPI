package me.aaron.listeners;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.aaron.main.LobbyPlugin;

public class PlayerDeathListener implements Listener {
	
	private LobbyPlugin pl;
	
	public PlayerDeathListener(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		try {
			
			e.getDrops().clear();
			
			Player killed = e.getEntity();
			
			Player killer = killed.getKiller();
			
			double health = killer.getHealth() / 2;
			DecimalFormat df = new DecimalFormat("0.0");
			String healthNew = df.format(health);
			
			
			killer.sendMessage(pl.prefix + "Du hast " + ChatColor.GOLD + killed.getDisplayName() + ChatColor.AQUA + " getötet! "
			+ ChatColor.GOLD +"[" + ChatColor.BOLD+ChatColor.GRAY + healthNew+ChatColor.RESET + ChatColor.RED + " \u2764" + ChatColor.GOLD + "]");
			
			killed.sendMessage(pl.prefix + "Du wurdest von " + ChatColor.GOLD + killer.getDisplayName() + ChatColor.AQUA + " getötet! " + ChatColor.GOLD
					+ "[" + ChatColor.BOLD + ChatColor.GRAY + healthNew + ChatColor.RESET + ChatColor.RED + " \u2764"+ChatColor.GOLD + "]");
			
			
			e.setDeathMessage("");
			
			killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1, 1);
			
		} catch (Exception e2) {}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
