package me.aaron.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.main.LobbyPlugin;

public class FastbowCmd implements CommandExecutor {

	private LobbyPlugin pl;

	public FastbowCmd(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender;

		if (!p.getWorld().getName().equals(pl.getConfig().getString("hub.world")))
			return true;
		
		if (p.hasPermission("lobby.fastbow")) {

			ItemStack bow = new ItemStack(Material.BOW); {
				ItemMeta m = bow.getItemMeta();
				m.setDisplayName("§dFast§6Bow");
				m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				m.spigot().setUnbreakable(true);
				bow.setItemMeta(m);
				bow.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
			}
			
			if (pl.fastBow.contains(p.getName())) {
				pl.fastBow.remove(p.getName());
				p.sendMessage(pl.prefix + ChatColor.RED + "Du hast nun keinen FastBow mehr!");
				p.getInventory().remove(bow);
				return true;
			}
			if (!pl.fastBow.contains(p.getName())) {
				pl.fastBow.add(p.getName());
				p.sendMessage(pl.prefix + ChatColor.GREEN + "Du hast nun einen FastBow Hack!");
				p.getInventory().addItem(bow);
				return true;
			}
		}
		return true;
	}

}
