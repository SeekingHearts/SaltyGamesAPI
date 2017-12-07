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
		}/* else if (e.getInventory().equals(CompassInventar.inv)) {
			e.setCancelled(true);
			if (e.getCurrentItem().getType() == Material.WORKBENCH) {
				double x = pl.getConfig().getDouble("freebuild.x");
				double y = pl.getConfig().getDouble("freebuild.y");
				double z = pl.getConfig().getDouble("freebuild.z");
				double pitch = pl.getConfig().getDouble("freebuild.pitch");
				double yaw = pl.getConfig().getDouble("freebuild.yaw");

				
				p.teleport(new Location(p.getWorld(), x, y, z, (float) yaw, (float) pitch));
				p.closeInventory();
				return;
			}
			if (e.getCurrentItem().getType() == Material.BED) {
				double x = pl.getConfig().getDouble("bedwars.x");
				double y = pl.getConfig().getDouble("bedwars.y");
				double z = pl.getConfig().getDouble("bedwars.z");
				double pitch = pl.getConfig().getDouble("bedwars.pitch");
				double yaw = pl.getConfig().getDouble("bedwars.yaw");

				e.setCancelled(true);
				p.teleport(new Location(p.getWorld(), x, y, z, (float) yaw, (float) pitch));
				p.closeInventory();
				return;
			}
			if (e.getCurrentItem().getType() == Material.IRON_SWORD) {
				double x = pl.getConfig().getDouble("1vs1.x");
				double y = pl.getConfig().getDouble("1vs1.y");
				double z = pl.getConfig().getDouble("1vs1.z");
				double pitch = pl.getConfig().getDouble("1vs1.pitch");
				double yaw = pl.getConfig().getDouble("1vs1.yaw");

				e.setCancelled(true);
				p.teleport(new Location(p.getWorld(), x, y, z, (float) yaw, (float) pitch));
				p.closeInventory();
				return;
			}
			if (e.getCurrentItem().getType() == Material.NETHER_STAR) {
				double x = pl.getConfig().getDouble("hub.x");
				double y = pl.getConfig().getDouble("hub.y");
				double z = pl.getConfig().getDouble("hub.z");
				double pitch = pl.getConfig().getDouble("hub.pitch");
				double yaw = pl.getConfig().getDouble("hub.yaw");

				e.setCancelled(true);
				p.teleport(new Location(p.getWorld(), x, y, z, (float) yaw, (float) pitch));
				p.closeInventory();
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
			if (e.getCurrentItem().getType() == Material.BARRIER) {
				e.setCancelled(true);
				return;
			}
		}*/ else if (e.getInventory().equals(HidePlayer.inv)) {
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
		}/* else if (e.getInventory().equals(ProfileInv.inv)) {
			if (e.getSlot() == 11) {
				e.setCancelled(true);
				p.closeInventory();
				
				ItemStack toggleJump = new ItemStack(Material.GOLD_PLATE);
				{
					ItemMeta m = toggleJump.getItemMeta();
					m.setDisplayName(ChatColor.GREEN + "Toggle JumpPads");
					m.setLore(Arrays.asList(ChatColor.GRAY + "Schalte die JumpPads " + ChatColor.DARK_RED + "aus/an!"));
					toggleJump.setItemMeta(m);
				}

				ItemStack toggleChat = new ItemStack(Material.PAPER);
				{
					ItemMeta m = toggleChat.getItemMeta();
					m.setDisplayName(ChatColor.GREEN + "Toggle Chat");
					m.setLore(Arrays.asList(ChatColor.GRAY + "Schaltet den Chat " + ChatColor.DARK_RED + "aus/an!"));
					toggleChat.setItemMeta(m);
				}

				ItemStack togglePvP = new ItemStack(Material.DIAMOND_SWORD);
				{
					ItemMeta m = togglePvP.getItemMeta();
					m.setDisplayName(ChatColor.DARK_RED + "Toggle PvP");
					m.setLore(Arrays.asList(ChatColor.GRAY + "Schaltet das PvP " + ChatColor.DARK_RED + "aus/an!"));
					togglePvP.setItemMeta(m);
				}

				ItemStack toggleDoubleJump = new ItemStack(Material.FEATHER);
				{
					ItemMeta m = toggleDoubleJump.getItemMeta();
					m.setDisplayName(ChatColor.GREEN + "Toggle DoubleJump");
					m.setLore(
							Arrays.asList(ChatColor.GRAY + "Schalte den DoubleJump " + ChatColor.DARK_RED + "aus/an!"));
					toggleDoubleJump.setItemMeta(m);
				}
				
				settingsInv.setItem(10, toggleJump);
				settingsInv.setItem(12, toggleChat);
				settingsInv.setItem(14, togglePvP);
				settingsInv.setItem(16, toggleDoubleJump);
				
				p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
				
				p.openInventory(settingsInv);
				return;
			}
			if (e.getSlot() == 15) {
				e.setCancelled(true);
				p.closeInventory();
				p.performCommand("friendlist");
				return;
			}
		} else if (e.getInventory().equals(settingsInv)) {

			
			
		}*/
	}