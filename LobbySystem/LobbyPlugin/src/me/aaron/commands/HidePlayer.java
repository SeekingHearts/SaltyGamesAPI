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

public class HidePlayer implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public HidePlayer(LobbyPlugin pl) {
		this.pl = pl;
	}

	public static Inventory inv = Bukkit.createInventory(null, 27, LobbyPlugin.prefix + ChatColor.AQUA + "Hide " + ChatColor.GOLD + "Players");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
				ItemStack gd = new ItemStack(Material.INK_SACK, 1, DyeColor.getByDyeData((byte) 10).getData()); {
					ItemMeta meta = gd.getItemMeta();
					meta.setDisplayName(ChatColor.GREEN + "Hide EveryOne!");
					gd.setItemMeta(meta);
				}
				ItemStack pd = new ItemStack(Material.INK_SACK, 1, DyeColor.getByDyeData((byte) 5).getData()); {
					ItemMeta meta = pd.getItemMeta();
					meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Show only " + ChatColor.DARK_PURPLE + "VIP!");
					pd.setItemMeta(meta);
				}
				ItemStack grd = new ItemStack(Material.INK_SACK, 1, DyeColor.getByDyeData((byte) 8).getData()); {
					ItemMeta meta = grd.getItemMeta();
					meta.setDisplayName(ChatColor.DARK_GRAY + "Show EveryOne!");
					grd.setItemMeta(meta);
				}
				ItemStack lg = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getData()); {
					ItemMeta meta = lg.getItemMeta();
					meta.setDisplayName(ChatColor.DARK_GREEN + "Platzhalter!");
					lg.setItemMeta(meta);
				}
				ItemStack vines = new ItemStack(Material.VINE); {
					ItemMeta meta = vines.getItemMeta();
					meta.setDisplayName(ChatColor.DARK_RED + "Schöner Platzhalter!");
					vines.setItemMeta(meta);
				}
				
				inv.setItem(0, lg);
				inv.setItem(1, lg);
				inv.setItem(2, vines);
				inv.setItem(3, lg);
				inv.setItem(4, lg);
				inv.setItem(5, lg);
				inv.setItem(6, vines);
				inv.setItem(7, lg);
				inv.setItem(8, lg);
				inv.setItem(9, gd);
				inv.setItem(10, lg);
				inv.setItem(11, lg);
				inv.setItem(12, lg);
				inv.setItem(13, pd);
				inv.setItem(14, lg);
				inv.setItem(15, lg);
				inv.setItem(16, lg);
				inv.setItem(17, grd);
				inv.setItem(18, lg);
				inv.setItem(19, lg);
				inv.setItem(20, vines);
				inv.setItem(21, lg);
				inv.setItem(22, lg);
				inv.setItem(23, lg);
				inv.setItem(24, vines);
				inv.setItem(25, lg);
				inv.setItem(26, lg);
				
				p.openInventory(inv);
				return true;
				
				
		} else {
			sender.sendMessage(pl.prefix + ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden!");
			return true;
		}
	}
}
