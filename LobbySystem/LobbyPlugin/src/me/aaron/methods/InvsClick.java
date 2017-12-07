package me.aaron.methods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.aaron.main.LobbyPlugin;
import me.aaron.utils.UserData;

public class InvsClick implements Listener {

	private LobbyPlugin pl;

	public InvsClick(LobbyPlugin pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onInvsClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (pl.inBuildMode.contains(p)) {
			e.setCancelled(false);
		} else {
			e.setCancelled(true);
		}

		if (e.getInventory().getName().equalsIgnoreCase(pl.prefix + "§6Profil")) {

			UserData.load(p);

			if (!UserData.get().contains("Settings")) {
				UserData.get().createSection("Settings");
				UserData.get().set("Settings.JumpPads", true);
				UserData.get().set("Settings.Chat", true);
				UserData.get().set("Settings.DoubleJump", true);
				UserData.save();
			}

			if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aFreunde")) {
				p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
				Bukkit.dispatchCommand(p, "friendsgui");
				p.closeInventory();
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cEinstellungen")) {
				p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
				p.closeInventory();
				Invs.openSettings(p);
			} else {
				e.setCancelled(true);
			}
		} else if (e.getInventory().getName().equalsIgnoreCase(pl.prefix + "§cEinstellungen")) {

			UserData.create(p);

			if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cJumpPads Aus")) {
				p.playSound(p.getLocation(), Sound.PISTON_RETRACT, 1, 1);
				UserData.load(p);
				UserData.get().set("Settings.JumpPads", false);
				UserData.save();
				p.closeInventory();
				// Invs.openSettings(p);
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aJumpPads An")) {
				p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 1, 1);
				UserData.load(p);
				UserData.get().set("Settings.JumpPads", true);
				UserData.save();
				p.closeInventory();
				// Invs.openSettings(p);
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cChat Aus")) {
				p.playSound(p.getLocation(), Sound.PISTON_RETRACT, 1, 1);
				UserData.load(p);
				UserData.get().set("Settings.Chat", false);
				UserData.save();
				p.closeInventory();
				// Invs.openSettings(p);
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aChat An")) {
				p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 1, 1);
				UserData.load(p);
				UserData.get().set("Settings.Chat", true);
				UserData.save();
				p.closeInventory();
				// Invs.openSettings(p);
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cDoubleJump Aus")) {
				p.playSound(p.getLocation(), Sound.PISTON_RETRACT, 1, 1);
				UserData.load(p);
				UserData.get().set("Settings.DoubleJump", false);
				UserData.save();
				p.closeInventory();
				// Invs.openSettings(p);
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aDoubleJump An")) {
				p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 1, 1);
				UserData.load(p);
				UserData.get().set("Settings.DoubleJump", true);
				UserData.save();
				p.closeInventory();
				// Invs.openSettings(p);
			} else {
				e.setCancelled(true);
			}
		} else if (e.getInventory().getName().equalsIgnoreCase(pl.prefix + ChatColor.AQUA + "Games")) {

			if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Freebuild")) {

				double x = pl.getConfig().getDouble("freebuild.x");
				double y = pl.getConfig().getDouble("freebuild.y");
				double z = pl.getConfig().getDouble("freebuild.z");
				double pitch = pl.getConfig().getDouble("freebuild.pitch");
				double yaw = pl.getConfig().getDouble("freebuild.yaw");

				p.teleport(new Location(p.getWorld(), x, y, z, (float) yaw, (float) pitch));
				p.closeInventory();
			} else if (e.getCurrentItem().getItemMeta().getDisplayName()
					.equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "BedWars")) {

				double x = pl.getConfig().getDouble("bedwars.x");
				double y = pl.getConfig().getDouble("bedwars.y");
				double z = pl.getConfig().getDouble("bedwars.z");
				double pitch = pl.getConfig().getDouble("bedwars.pitch");
				double yaw = pl.getConfig().getDouble("bedwars.yaw");

				e.setCancelled(true);
				p.teleport(new Location(p.getWorld(), x, y, z, (float) yaw, (float) pitch));
				p.closeInventory();
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "1vs1")) {

				double x = pl.getConfig().getDouble("1vs1.x");
				double y = pl.getConfig().getDouble("1vs1.y");
				double z = pl.getConfig().getDouble("1vs1.z");
				double pitch = pl.getConfig().getDouble("1vs1.pitch");
				double yaw = pl.getConfig().getDouble("1vs1.yaw");

				e.setCancelled(true);
				p.teleport(new Location(p.getWorld(), x, y, z, (float) yaw, (float) pitch));
				p.closeInventory();
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Hub")) {

				double x = pl.getConfig().getDouble("hub.x");
				double y = pl.getConfig().getDouble("hub.y");
				double z = pl.getConfig().getDouble("hub.z");
				double pitch = pl.getConfig().getDouble("hub.pitch");
				double yaw = pl.getConfig().getDouble("hub.yaw");

				e.setCancelled(true);
				p.teleport(new Location(p.getWorld(), x, y, z, (float) yaw, (float) pitch));
				p.closeInventory();
			} else {
				e.setCancelled(true);
			}
		} else if (e.getInventory().getName().equalsIgnoreCase(pl.prefix + "§bHide §6Players")) {

			if (!(e.getCurrentItem() == null)) {

				if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8Hide EveryOne")) {
					pl.usingHide.add(p.getName());
					for (Player allP : Bukkit.getServer().getOnlinePlayers()) {
						if (!allP.getName().equals(p.getName())) {
							p.hidePlayer(allP);
						}
					}
					p.sendMessage(pl.prefix + "§cAlle Spieler sind nun versteckt!");
					e.setCancelled(true);
					p.closeInventory();
				}
				/*
				 * else if (e.getCurrentItem().getItemMeta().getDisplayName().
				 * equalsIgnoreCase("§dShow Only Staff")) { for (Player allP :
				 * Bukkit.getServer().getOnlinePlayers()) { if
				 * (!allP.hasPermission("lobby.vip")) { p.hidePlayer(allP); } }
				 * if (pl.usingHide.contains(p.getName())) {
				 * pl.usingHide.remove(p.getName()); } p.sendMessage(pl.prefix +
				 * "§dEs werden nur noch Team Mitglieder angezeigt!");
				 * e.setCancelled(true); p.closeInventory(); } else
				 */ if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aShow EveryOne")) {
					pl.usingHide.remove(p.getName());
					for (Player allP : Bukkit.getServer().getOnlinePlayers()) {
						if (!allP.getName().equals(p.getName())) {
							p.showPlayer(allP);
						}
					}
					p.sendMessage(pl.prefix + "§aAlle Spieler werden nun angezeigt!");
					e.setCancelled(true);
					p.closeInventory();
				} else {
					e.setCancelled(true);
				}
			}
		}
	}
}