package me.aaron.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.main.LobbyPlugin;

public class GameModeListener implements Listener {
	
	private LobbyPlugin pl;
	
	public GameModeListener(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (p.getItemInHand().getType() == Material.DIAMOND) {
				
				if (!p.hasPermission("lobby.gamemode")) 
					return;
				
				if (!p.getWorld().getName().equals(pl.getConfig().getString("hub.world")))
					return;
				
				Inventory inv = Bukkit.createInventory(null, 9, pl.prefix + ChatColor.DARK_AQUA + "GameMode");
				
				ItemStack cr = new ItemStack(Material.DIAMOND_BLOCK); {
					ItemMeta meta = cr.getItemMeta();
					meta.setDisplayName(ChatColor.AQUA + "Creative");
					cr.setItemMeta(meta);
				}
				
				ItemStack sv = new ItemStack(Material.GRASS); {
					ItemMeta meta = sv.getItemMeta();
					meta.setDisplayName(ChatColor.GREEN + "Survival");
					sv.setItemMeta(meta);
				}
				
				ItemStack spec = new ItemStack(Material.BARRIER); {
					ItemMeta meta = spec.getItemMeta();
					meta.setDisplayName(ChatColor.DARK_RED + "Spectator");
					spec.setItemMeta(meta);
				}
				
				inv.setItem(1, cr);
				inv.setItem(2, sv);
				inv.setItem(5, spec);
				
				p.openInventory(inv);
			}
			
		}
		
	}

}
