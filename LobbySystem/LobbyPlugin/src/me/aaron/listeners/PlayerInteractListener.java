package me.aaron.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.aaron.main.LobbyPlugin;

public class PlayerInteractListener implements Listener {
	
	private LobbyPlugin pl;
	
	public PlayerInteractListener(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFastBow(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if (p.hasPermission("lobby.fastbow")) {
			e.setCancelled(true);
			
			if (!pl.fastBow.contains(p))
				return;
			
			if (p.getItemInHand().getType() != Material.BOW)
				return;
			
			Arrow arrow = (Arrow) new ItemStack(Material.ARROW); {
				arrow.setBounce(true);
				arrow.setVelocity(arrow.getVelocity().setZ(5));
				arrow.setCritical(true);
			}
			
			p.launchProjectile(arrow.getClass());
		}
		
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (!p.getWorld().getName().equals(pl.getConfig().getString("hub.world"))) 
				return;
			
			if (p.getItemInHand().getType() == Material.TNT) {
				if (p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Silent Lobby")) {
					e.setCancelled(true);
					p.performCommand("silentlobby");
					return;
				}
			} else if (p.getItemInHand().getType() == Material.WOOD_PICKAXE) {
				if (p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Toggle PvP")) {
					e.setCancelled(true);
					p.performCommand("pvpoff");
					return;
				}
			} else {
				return;
			}
		}
	}
	
	@EventHandler
	public void onArrowLaunch(ProjectileLaunchEvent e) {
		
		if (e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			if (p.hasPermission("lobby.fastbow")) {
				if (pl.fastBow.contains(p)) {
					if (e.getEntity() instanceof Arrow) {
						
						if (p.getInventory().contains(new ItemStack(Material.LEATHER_LEGGINGS, 1))) {
							e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(20));
							return;
						}
						e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(2));
					}
				}
			}
		}
		
	}

}
