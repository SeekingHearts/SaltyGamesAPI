package me.aaron.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.main.LobbyPlugin;

public class GetBoosterCmd implements CommandExecutor {
	
	private LobbyPlugin pl;
	
	public GetBoosterCmd(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args) {
		
		Player p = (Player) sender;
		
		if (!p.getWorld().getName().equals(pl.getConfig().getString("hub.world")))
			return true;
		
		if (p.hasPermission("lobby.booster")) {
			ItemStack boostitm = new ItemStack(Material.SLIME_BALL);
			{
				ItemMeta meta = boostitm.getItemMeta();

				meta.setDisplayName(ChatColor.DARK_PURPLE + "Booster");
				meta.setLore(Arrays.asList(ChatColor.GREEN + "Erlaubt dir dich zu Boosten!",
						ChatColor.DARK_AQUA + "Viel Spaß ^^"));

				boostitm.setItemMeta(meta);
			}

			p.getInventory().addItem(boostitm);
			p.sendMessage(pl.prefix + "Du hast deinen persöhnlichen " + ChatColor.DARK_PURPLE + "Booster"
					+ ChatColor.AQUA + " erhalten!");
			
		}
		
		
		return true;
	}
	
	

}
