package me.aaron.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.main.LobbyPlugin;

public class GmCmds implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public GmCmds(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	public static Inventory inv = Bukkit.createInventory(null, 9, "§5GameMode §6Changer");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (!p.getWorld().getName().equals(pl.getConfig().getString("hub.world")))
			return true;
		
		if (!p.hasPermission("lobby.gamemode")) {
			p.sendMessage(pl.prefix + ChatColor.RED + "Du hast keine Rechte dazu!");
			return true;
		}
		
		ItemStack cr = new ItemStack(Material.DIAMOND_BLOCK); {
			ItemMeta meta = cr.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + "Creative");
			cr.setItemMeta(meta);
		}
		
		ItemStack sv = new ItemStack(Material.GRASS); {
			ItemMeta meta = sv.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "Adventure");
			sv.setItemMeta(meta);
		}
		
		ItemStack spec = new ItemStack(Material.BARRIER); {
			ItemMeta meta = spec.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Spectator");
			spec.setItemMeta(meta);
		}
		
		ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.PURPLE.getData()); {
			ItemMeta meta = filler.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GRAY + "Nur ein Platzhalter!");
			filler.setItemMeta(meta);
		}
		
		inv.setItem(0, cr);
		inv.setItem(1, filler);
		inv.setItem(2, filler);
		inv.setItem(3, filler);
		inv.setItem(4, sv);
		inv.setItem(5, filler);
		inv.setItem(6, filler);
		inv.setItem(7, filler);
		inv.setItem(8, spec);
		
		p.openInventory(inv);
		
		return true;
	}

}
