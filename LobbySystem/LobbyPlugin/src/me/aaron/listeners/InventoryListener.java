package me.aaron.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import me.aaron.commands.GmCmds;
import me.aaron.commands.HidePlayer;
import me.aaron.main.LobbyPlugin;

public class InventoryListener implements Listener {

	private LobbyPlugin pl;

	public InventoryListener(LobbyPlugin pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		Inventory settingsInv = Bukkit.createInventory(null, 27, LobbyPlugin.prefix + ChatColor.RED + "Settings");

		if (e.getInventory().equals(GmCmds.inv)) {
			if (e.getCurrentItem().getType() == Material.DIAMOND_BLOCK) {
				e.setCancelled(true);
				p.setGameMode(GameMode.CREATIVE);
				p.closeInventory();
				return;
			}
			if (e.getCurrentItem().getType() == Material.GRASS) {
				e.setCancelled(true);
				p.setGameMode(GameMode.ADVENTURE);
				p.closeInventory();
				return;
			}

			if (e.getCurrentItem().getType() == Material.BARRIER) {
				e.setCancelled(true);
				p.setGameMode(GameMode.SPECTATOR);
				p.closeInventory();
				return;
			}

			if (e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
				e.setCancelled(true);
				return;
			}
		} else if (e.getInventory().equals(HidePlayer.inv)) {
			if (e.getSlot() == 9) {
				e.setCancelled(true);
				p.sendMessage(pl.prefix + ChatColor.GREEN + "Alle Spieler sind nun versteckt!");
				p.closeInventory();
				pl.usingHide.add(p.getName());
				for (Player allP : Bukkit.getServer().getOnlinePlayers()) {
					if (!allP.getName().equals(p.getName())) {
						p.hidePlayer(allP);
					}
				}
				return;
			}

			if (e.getSlot() == 13) {
				e.setCancelled(true);

				p.closeInventory();
			}

				e.setCancelled(true);
				p.sendMessage(pl.prefix + ChatColor.GREEN + "Alle Spieler sind nun sichtbar!");
				p.closeInventory();
				pl.usingHide.remove(p.getName());
				for (Player allP : Bukkit.getServer().getOnlinePlayers()) {
					if (!allP.getName().equals(p.getName())) {
						p.showPlayer(allP);
					}
				}
				return;
			}

			if (e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
				e.setCancelled(true);
				return;
			}
			if (e.getCurrentItem().getType() == Material.VINE) {
				e.setCancelled(true);
				return;
			}
		}
	}