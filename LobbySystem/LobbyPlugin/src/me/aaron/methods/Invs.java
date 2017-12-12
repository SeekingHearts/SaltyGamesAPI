package me.aaron.methods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.aaron.main.LobbyPlugin;
import me.aaron.utils.ItemUtils;
import me.aaron.utils.UserData;

public class Invs {

	public static void openProfile(Player player) {
		Player p = player;

		Inventory profile = Bukkit.createInventory(null, 9, LobbyPlugin.prefix + "§6Profil");

		ItemStack placeholder = ItemUtils.getItem(Material.STAINED_GLASS_PANE, "§cPlatzhalter", "", 15, 1);

		Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {

			@Override
			public void run() {
				profile.setItem(6,
						ItemUtils.getItem(Material.STAINED_CLAY, "§aFreunde", "§7Öffne deine Freundesliste!", 13, 1));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

				Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {

					@Override
					public void run() {
						profile.setItem(2, ItemUtils.getItem(Material.PAPER, "§cEinstellungen",
								"§7Öffne deine Einstellungen!", 0, 1));
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
					}
				}, 2);

			}
		}, 2);

		profile.setItem(0, placeholder);
		profile.setItem(1, placeholder);
		profile.setItem(2, placeholder);
		profile.setItem(3, placeholder);
		profile.setItem(4, placeholder);
		profile.setItem(5, placeholder);
		profile.setItem(6, placeholder);
		profile.setItem(7, placeholder);
		profile.setItem(8, placeholder);

		p.openInventory(profile);
	}

	public static void openSettings(Player player) {
		Player p = player;

		if (!LobbyPlugin.settingsActivated == false) {

			UserData.create(p);

			Inventory settings = Bukkit.createInventory(p, 3 * 9, LobbyPlugin.prefix + "§cEinstellungen");

			ItemStack placeholder = ItemUtils.getItem(Material.STAINED_GLASS_PANE, "Platzhalter", "", 15, 1);

			ItemStack JPadsOn = ItemUtils.getItem(Material.GOLD_PLATE, "§aJumpPads An",
					"§7Klicke um die JumpPads §4auszuschalten", 0, 1);
			ItemStack JPadsOff = ItemUtils.getItem(Material.STONE_PLATE, "§cJumpPads Aus",
					"§7Klicke um die JumpPads §canzuschalten", 0, 1);

			ItemStack ChatOn = ItemUtils.getItem(Material.SIGN, "§aChat An", "§7Klicke um den Chat §causzuschalten", 0,
					1);
			ItemStack ChatOff = ItemUtils.getItem(Material.GOLD_PLATE, "§cChat Aus",
					"§7Klicke um den Chat §canzuschalten", 0, 1);

			ItemStack DJumpOn = ItemUtils.getItem(Material.GOLD_PLATE, "§aDoubleJump An",
					"§7Klicke um den DoubleJump §causzuschalten", 0, 1);
			ItemStack DJumpOff = ItemUtils.getItem(Material.GOLD_PLATE, "§cDoubleJump Aus",
					"§7Klicke um den DoubleJump §canzuschalten", 0, 1);

			settings.setItem(0, placeholder);
			settings.setItem(1, placeholder);
			settings.setItem(2, placeholder);
			settings.setItem(3, placeholder);
			settings.setItem(4, placeholder);
			settings.setItem(5, placeholder);
			settings.setItem(6, placeholder);
			settings.setItem(7, placeholder);
			settings.setItem(8, placeholder);
			settings.setItem(9, placeholder);
			settings.setItem(10, placeholder);
			settings.setItem(11, placeholder);
			settings.setItem(12, placeholder);
			settings.setItem(13, placeholder);
			settings.setItem(14, placeholder);
			settings.setItem(15, placeholder);
			settings.setItem(16, placeholder);
			settings.setItem(17, placeholder);
			settings.setItem(18, placeholder);
			settings.setItem(19, placeholder);
			settings.setItem(20, placeholder);
			settings.setItem(21, placeholder);
			settings.setItem(22, placeholder);
			settings.setItem(23, placeholder);
			settings.setItem(24, placeholder);
			settings.setItem(25, placeholder);
			settings.setItem(26, placeholder);

			if (UserData.get().getBoolean("Settings.JumpPads") == true) {
				settings.setItem(11, JPadsOn);
			} else {
				settings.setItem(11, JPadsOff);
			}
			if (UserData.get().getBoolean("Settings.Chat") == true) {
				settings.setItem(13, ChatOn);
			} else {
				settings.setItem(13, ChatOff);
			}
			if (UserData.get().getBoolean("Settings.DoubleJump") == true) {
				settings.setItem(15, DJumpOn);
			} else {
				settings.setItem(15, DJumpOff);
			}

			p.openInventory(settings);

		} else {
			p.sendMessage(LobbyPlugin.prefix + "§4Coming Soon");
		}
	}
	
	public static void openCompass(Player p) {
		
		Inventory compass = Bukkit.createInventory(null, 3 * 9, LobbyPlugin.prefix + ChatColor.AQUA + "Games");
		
		ItemStack placeholder = ItemUtils.getItem(Material.STAINED_GLASS_PANE, "Platzhalter", "", 15, 1);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				compass.setItem(3, ItemUtils.getItem(Material.BARRIER, "§4Coming Soon", "", 0, 1));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						compass.setItem(5, ItemUtils.getItem(Material.BARRIER, "§4Coming Soon", "", 0, 1));
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
							
							@Override
							public void run() {
								compass.setItem(21, ItemUtils.getItem(Material.BARRIER, "§4Coming Soon", "", 0, 1));
								p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
								
										Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
											
											@Override
											public void run() {
												compass.setItem(11, ItemUtils.getItem(Material.WORKBENCH, "§aFreeBuild", "§7Teleportiert dich zum FreeBuild!", 0, 1));
												p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
												
												Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
													
													@Override
													public void run() {
														compass.setItem(13, ItemUtils.getItem(Material.NETHER_STAR, "§6Hub", "§7Teleportiert dich zum Hub", 0, 1));
														p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
														
														Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
															
															@Override
															public void run() {
																compass.setItem(15, ItemUtils.getItem(Material.BED, "§dBedWars", "§7Teleportiert dich zum BedWars", 0, 1));
																p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
																
																Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
																	
																	@Override
																	public void run() {
																		compass.setItem(23, ItemUtils.getItem(Material.IRON_SWORD, "§c1vs1", "§7Teleportiert dich zum 1 gegen 1", 0, 1));
																		p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
																	}
																}, 2);
															}
														}, 2);
													}
												}, 2);
											}
										}, 2);
							}
						}, 2);
					}
				}, 2);
			}
		}, 2);
		
		compass.setItem(0, placeholder);
		compass.setItem(1, placeholder);
		compass.setItem(2, placeholder);
		compass.setItem(3, placeholder);
		compass.setItem(4, placeholder);
		compass.setItem(5, placeholder);
		compass.setItem(6, placeholder);
		compass.setItem(7, placeholder);
		compass.setItem(8, placeholder);
		compass.setItem(9, placeholder);
		compass.setItem(10, placeholder);
		compass.setItem(11, placeholder);
		compass.setItem(12, placeholder);
		compass.setItem(13, placeholder);
		compass.setItem(14, placeholder);
		compass.setItem(15, placeholder);
		compass.setItem(16, placeholder);
		compass.setItem(17, placeholder);
		compass.setItem(18, placeholder);
		compass.setItem(19, placeholder);
		compass.setItem(20, placeholder);
		compass.setItem(21, placeholder);
		compass.setItem(22, placeholder);
		compass.setItem(23, placeholder);
		compass.setItem(24, placeholder);
		compass.setItem(25, placeholder);
		compass.setItem(26, placeholder);
		
		p.openInventory(compass);
		
	}
	
	public static void openHidePlayer(Player p) {
		
		Inventory hideplayer = Bukkit.createInventory(null, 9, LobbyPlugin.prefix + "§bHide §6Players");
		
		ItemStack placeholder = ItemUtils.getItem(Material.STAINED_GLASS_PANE, "Platzhalter", "", 15, 1);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				hideplayer.setItem(7, ItemUtils.getItem(Material.INK_SACK, "§8Hide EveryOne", "§7Verstecke alle Spieler", 8, 1));
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
				
				
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyPlugin.getInstance(), new Runnable() {
							
							@Override
							public void run() {
								hideplayer.setItem(1, ItemUtils.getItem(Material.INK_SACK, "§aShow EveryOne", "§7Zeige alle Spieler an", 10, 1));
								p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
							}
						}, 2);
					}
				}, 2);
		
		hideplayer.setItem(0, placeholder);
		hideplayer.setItem(1, placeholder);
		hideplayer.setItem(2, placeholder);
		hideplayer.setItem(3, placeholder);
		hideplayer.setItem(4, placeholder);
		hideplayer.setItem(5, placeholder);
		hideplayer.setItem(6, placeholder);
		hideplayer.setItem(7, placeholder);
		hideplayer.setItem(8, placeholder);
		
		p.openInventory(hideplayer);
		
	}
}