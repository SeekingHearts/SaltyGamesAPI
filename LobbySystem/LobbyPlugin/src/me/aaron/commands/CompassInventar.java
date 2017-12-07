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

public class CompassInventar implements CommandExecutor {

	private LobbyPlugin pl;
	
	public CompassInventar(LobbyPlugin pl) {
		this.pl = pl;
	}
	
	public static Inventory inv = Bukkit.createInventory(null, 27, LobbyPlugin.prefix + ChatColor.AQUA + "Games");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		ItemStack hub = new ItemStack(Material.NETHER_STAR); {
			ItemMeta meta = hub.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + "Hub");
			hub.setItemMeta(meta);
		}
		ItemStack fb = new ItemStack(Material.WORKBENCH); {
			ItemMeta meta = fb.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "Freebuild");
			fb.setItemMeta(meta);
		}
		ItemStack ovo = new ItemStack(Material.IRON_SWORD); {
			ItemMeta meta = ovo.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "1vs1");
			ovo.setItemMeta(meta);
		}
		ItemStack bw = new ItemStack(Material.BED); {
			ItemMeta meta = bw.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_GRAY + "BedWars");
			bw.setItemMeta(meta);
		}
		ItemStack barrier = new ItemStack(Material.BARRIER); {
			ItemMeta meta = bw.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Coming Soon");
			barrier.setItemMeta(meta);
		}
		ItemStack vines = new ItemStack(Material.VINE); {
			ItemMeta meta = bw.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "Schöner Platzhalter");
			vines.setItemMeta(meta);
		}
		ItemStack gp = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData()); {
			ItemMeta meta = bw.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_PURPLE + "Platzhalter");
			gp.setItemMeta(meta);
		}
		
		inv.setItem(0, vines);
		inv.setItem(1, gp);
		inv.setItem(2, gp);
		inv.setItem(3, barrier);
		inv.setItem(4, gp);
		inv.setItem(5, barrier);
		inv.setItem(6, gp);
		inv.setItem(7, gp);
		inv.setItem(8, vines);
		inv.setItem(9, gp);
		inv.setItem(10, gp);
		inv.setItem(11, fb);
		inv.setItem(12, gp);
		inv.setItem(13, hub);
		inv.setItem(14, gp);
		inv.setItem(15, bw);
		inv.setItem(16, gp);
		inv.setItem(17, gp);
		inv.setItem(18, vines);
		inv.setItem(19, gp);
		inv.setItem(20, gp);
		inv.setItem(21, barrier);
		inv.setItem(22, gp);
		inv.setItem(23, ovo);
		inv.setItem(24, gp);
		inv.setItem(25, gp);
		inv.setItem(26, vines);
		
		p.openInventory(inv);
		
		return true;
	}
	
}
