package me.aaron.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class SetBoosterBlocksCmd implements CommandExecutor {

	private LobbyPlugin pl;

	public SetBoosterBlocksCmd(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		

		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (p.hasPermission("lobby.setboosterblocks")) {

				if (args.length == 2) {

					if (args[1].isEmpty()) {
						p.sendMessage(pl.prefix + ChatColor.RED + "Usage: " + ChatColor.GOLD
								+ "/setboosterblocks <top/bottom> <id>");
						return true;
					}

					String material = args[1].toString();
					Material mat = Material.getMaterial(material);
					
					if (mat == null) {
						p.sendMessage(pl.prefix + ChatColor.RED + "Usage: " + ChatColor.GOLD
								+ "/setboosterblocks <top/bottom> <id>");
						return true;
					}
					
					if (args[0].equalsIgnoreCase("top")) {

						try {
							pl.getConfig().set("booster.top", mat.toString());
							p.sendMessage(pl.prefix + "Der" + ChatColor.GOLD + " obere " + ChatColor.AQUA
									+ "Booster Block wurde zu " + ChatColor.RED + ChatColor.BOLD.toString()
									+ mat.toString() + ChatColor.RESET + ChatColor.AQUA + " geändert!");
							pl.saveConfig();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (args[0].equalsIgnoreCase("bottom")) {

						try {

							pl.getConfig().set("booster.bottom", mat.toString());
							p.sendMessage(pl.prefix + "Der" + ChatColor.GOLD + " untere " + ChatColor.AQUA
									+ "Booster Block wurde zu " + ChatColor.RED + ChatColor.BOLD.toString()
									+ mat.toString() + ChatColor.RESET + ChatColor.AQUA + " geändert!");
							pl.saveConfig();

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						p.sendMessage(pl.prefix + ChatColor.RED + "Usage: " + ChatColor.GOLD
								+ "/setboosterblocks <top/bottom> <id>");
						return true;
					}
				} else {
					p.sendMessage(pl.prefix + ChatColor.RED + "Usage: " + ChatColor.GOLD
							+ "/setboosterblocks <top/bottom> <id>");
					return true;
				}

			} else {
				p.sendMessage(pl.prefix + ChatColor.RED + "Du hast keine Rechte!");
				return true;
			}
		}
		return true;

	}
}
