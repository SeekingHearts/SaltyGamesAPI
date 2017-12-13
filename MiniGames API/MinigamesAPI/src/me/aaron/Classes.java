package me.aaron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.shampaggon.crackshot.CSUtility;

import me.aaron.config.ClassesConfig;
import me.aaron.utils.AClass;
import me.aaron.utils.IconMenu;
import me.aaron.utils.Util;
import me.aaron.utils.Validator;
import net.milkbowl.vault.economy.EconomyResponse;

public class Classes {

	JavaPlugin pl;
	PluginInstance pli;
	public HashMap<String, IconMenu> lasticonm = new HashMap<>();

	public Classes(final JavaPlugin pl) {
		this.pl = pl;
		pli = MinigamesAPI.getAPI().getPluginInstance(pl);
	}

	public Classes(final JavaPlugin pl, final PluginInstance pli) {
		this.pl = pl;
		this.pli = pli;
	}

	public void openGUI(final String p) {
		final Classes cl = this;
		IconMenu iconm;
		final int mincount = pli.getAClasses().keySet().size();
		if (!Validator.isPlayerOnline(p)) {
			return;
		}
		final Player player = Bukkit.getPlayerExact(p);
		if (lasticonm.containsKey(p)) {
			iconm = lasticonm.get(p);
		} else {
			iconm = new IconMenu(pli.getMessagesConfig().classes_item,
					(9 * pl.getConfig().getInt(ArenaConfigStrings.CONFIG_CLASSES_GUI_ROWS) > mincount - 1)
							? 9 * pl.getConfig().getInt(ArenaConfigStrings.CONFIG_CLASSES_GUI_ROWS)
							: Math.round(mincount / 9) * 9 + 9,
					event -> {
						if (event.getPlayer().getName().equalsIgnoreCase(p)) {
							if (pli.global_players.containsKey(p)) {
								if (pli.getArenas().contains(pli.global_players.get(p))) {
									final String d = event.getName();
									final Player p1 = event.getPlayer();
									if (pli.getAClasses().containsKey(d)) {
										cl.setClass(pli.getClassesHandler().getInternalNameByName(d), p1.getName(),
												true);
									}
								}
							}
						}
						event.setWillClose(true);
					}, pl);

			int c = 0;
			for (final String ac : pli.getAClasses().keySet()) {
				final AClass ac_ = pli.getAClasses().get(ac);
				if (ac_.isEnabled()) {
					if (!pli.show_classes_without_usage_permission) {
						if (!kitPlayerHasPermissions(ac_.getInternalname(), player)) {
							continue;
						}
					}
					int slot = c;
					if (pli.getClassesConfig().getConfig().isSet("config.kits." + ac_.getInternalname() + ".slot")) {
						slot = pli.getClassesConfig().getConfig()
								.getInt("config.kits." + ac_.getInternalname() + ".slot");
						if (slot < 0 || slot > iconm.getSize() - 1) {
							slot = c;
						}
					}
					iconm.setOption(slot, ac_.getIcon().clone(), ac_.getName(), pli.getClassesConfig().getConfig()
							.getString("config.kits." + ac_.getInternalname() + ".lore").split(";"));
					c++;
				}
			}
		}

		iconm.open(player);
		lasticonm.put(p, iconm);
	}

	public void getClass(final String player) {
		if (!this.pli.getPClasses().containsKey(player)) {
			ArenaLogger.debug(player
					+ " didn't select any kit and the auto_add_default_kit option might be turned off, thus he won't get any starting items.");
			return;
		}
		final AClass c = this.pli.getPClasses().get(player);
		final Player p = Bukkit.getServer().getPlayer(player);
		Util.clearInv(p);
		final ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(c.getItems()));
		final ArrayList<ItemStack> temp = new ArrayList<>(Arrays.asList(c.getItems()));
		final ArrayList<String> tempguns = new ArrayList<>();
		final ArrayList<PotionEffectType> temppotions = new ArrayList<>();
		final ArrayList<Integer> temppotions_lv = new ArrayList<>();
		final ArrayList<Integer> temppotions_duration = new ArrayList<>();

		// crackshot support
		for (final ItemStack item : temp) {
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasDisplayName()) {
						if (item.getItemMeta().getDisplayName().startsWith("crackshot:")) {
							items.remove(item);
							tempguns.add(item.getItemMeta().getDisplayName().split(":")[1]);
						} else if (item.getItemMeta().getDisplayName().startsWith("potioneffect:")) {
							items.remove(item);
							final String potioneffect = item.getItemMeta().getDisplayName().split(":")[1];
							final String data = item.getItemMeta().getDisplayName().split(":")[2];
							final Integer time = Integer.parseInt(data.substring(0, data.indexOf("#")));
							final Integer lv = Integer.parseInt(data.split("#")[1]);
							if (PotionEffectType.getByName(potioneffect) != null) {
								temppotions.add(PotionEffectType.getByName(potioneffect));
								temppotions_lv.add(lv);
								temppotions_duration.add(time);
							}
						}
					}
				}
			}
		}

		for (final ItemStack item : items) {
			if (item != null) {
				Color c_ = null;
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasDisplayName()) {
						if (item.getItemMeta().getDisplayName().startsWith("#")
								&& item.getItemMeta().getDisplayName().length() == 7) {
							c_ = Util.hexToRgb(item.getItemMeta().getDisplayName());
						}
					}
				}
				if (item.getTypeId() == 298 || item.getTypeId() == 302 || item.getTypeId() == 306
						|| item.getTypeId() == 310 || item.getTypeId() == 314) {
					if (item.getTypeId() == 298) {
						final LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
						if (c_ != null) {
							lam.setColor(c_);
						}
						item.setItemMeta(lam);
					}
					p.getInventory().setHelmet(item);
					continue;
				}
				if (item.getTypeId() == 299 || item.getTypeId() == 303 || item.getTypeId() == 307
						|| item.getTypeId() == 311 || item.getTypeId() == 315) {
					if (item.getTypeId() == 299) {
						final LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
						if (c_ != null) {
							lam.setColor(c_);
						}
						item.setItemMeta(lam);
					}
					p.getInventory().setChestplate(item);
					continue;
				}
				if (item.getTypeId() == 300 || item.getTypeId() == 304 || item.getTypeId() == 308
						|| item.getTypeId() == 312 || item.getTypeId() == 316) {
					if (item.getTypeId() == 300) {
						final LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
						if (c_ != null) {
							lam.setColor(c_);
						}
						item.setItemMeta(lam);
					}
					p.getInventory().setLeggings(item);
					continue;
				}
				if (item.getTypeId() == 301 || item.getTypeId() == 305 || item.getTypeId() == 309
						|| item.getTypeId() == 313 || item.getTypeId() == 317) {
					if (item.getTypeId() == 301) {
						final LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
						if (c_ != null) {
							lam.setColor(c_);
						}
						item.setItemMeta(lam);
					}
					p.getInventory().setBoots(item);
					continue;
				}
				if (item.getType() != Material.AIR) {
					p.getInventory().addItem(item);
				}
			}
		}
		// p.getInventory().setContents((ItemStack[]) items.toArray(new
		// ItemStack[items.size()]));
		p.updateInventory();

		if (MinigamesAPI.getAPI().crackshotAvailable()) {
			for (final String t : tempguns) {
				final CSUtility cs = new CSUtility();
				cs.giveWeapon(p, t, 1);
			}
		}

		Bukkit.getScheduler().runTaskLater(pl, () -> {
			if (p != null) {
				int index = 0;
				for (final PotionEffectType t : temppotions) {
					p.addPotionEffect(new PotionEffect(t, temppotions_duration.get(index), temppotions_lv.get(index)));
					index++;
				}
			}
		}, 10L);
	}

	public void setClass(String iname, final String player, final boolean money) {
		String internalname = iname;
		for (final String c : this.pli.getAClasses().keySet()) {
			if (c.toLowerCase().equalsIgnoreCase(internalname.toLowerCase())) {
				internalname = c;
			}
		}
		if (!this.kitPlayerHasPermissions(internalname, Bukkit.getPlayer(player))) {
			Bukkit.getPlayer(player).sendMessage(this.pli.getMessagesConfig().no_perm);
			return;
		}
		boolean continue_ = true;
		if (money) {
			if (this.kitRequiresMoney(internalname)) {
				continue_ = this.kitTakeMoney(Bukkit.getPlayer(player), internalname);
			}
		}
		if (continue_) {
			final AClass classByInternalname = this.getClassByInternalName(internalname);
			this.pli.setPClass(player, classByInternalname);
			if (classByInternalname != null) {
				final String set_kit_msg = this.pli.getMessagesConfig().set_kit;
				Bukkit.getPlayer(player).sendMessage(set_kit_msg.replaceAll("<kit>",
						ChatColor.translateAlternateColorCodes('&', classByInternalname.getName())));
			}
		}
	}

	public String getInternalNameByName(final String name) {
		for (final AClass ac : pli.getAClasses().values()) {
			if (ac.getName().equalsIgnoreCase(name))
				return ac.getInternalname();
		}
		return "default";
	}

	public AClass getClassByInternalName(final String internalname) {
		for (final AClass ac : pli.getAClasses().values()) {
			if (ac.getInternalname().equalsIgnoreCase(internalname))
				return ac;
		}
		return null;
	}

	public boolean hasClass(final String p) {
		return pli.getPClasses().containsKey(p);
	}

	public String getSelectedClass(final String p) {
		if (hasClass(p))
			return pli.getPClasses().get(p).getInternalname();
		return "default";
	}

	public void loadClasses() {
		Bukkit.getScheduler().runTaskLater(this.pl, () -> {
			final FileConfiguration config = pli.getClassesConfig().getConfig();
			if (config.isSet("config.kits")) {
				for (final String aclass : config.getConfigurationSection("config.kits.").getKeys(false)) {
					AClass n;
					if (config.isSet("config.kits." + aclass + ".icon")) {
						n = new AClass(pl, config.getString("config.kits." + aclass + ".name"), aclass,
								config.isSet("config.kits." + aclass + ".enabled")
										? config.getBoolean("config.kits." + aclass + ".enabled") : true,
								Util.parseItems(config.getString("config.kits." + aclass + ".items")),
								Util.parseItems(config.getString("config.kits." + aclass + ".icon")).get(0));
					} else {
						n = new AClass(pl, config.getString("config.kits." + aclass + ".name"), aclass,
								config.isSet("config.kits." + aclass + ".enabled")
										? config.getBoolean("config.kits." + aclass + ".enabled") : true,
								Util.parseItems(config.getString("config.kits." + aclass + ".items")));
					}
					Classes.this.pli.addAClass(config.getString("config.kits." + aclass + ".name"), n);
					if (!config.isSet("config.kits." + aclass + ".items")
							|| !config.isSet("config.kits." + aclass + ".lore")) {
						Classes.this.pl.getLogger().warning("One of the classes found in the config file is invalid: "
								+ aclass + ". Missing itemid or lore!");
					}
				}
			}
		}, 20L);
	}

	public static void loadClasses(final JavaPlugin pl) {
		Bukkit.getScheduler().runTaskLater(pl, () -> {
			final FileConfiguration config = MinigamesAPI.getAPI().getPluginInstance(pl).getClassesConfig().getConfig();
			if (config.isSet("config.kits")) {
				for (final String aclass : config.getConfigurationSection("config.kits.").getKeys(false)) {
					AClass n;
					if (config.isSet("config.kits." + aclass + ".icon")) {
						n = new AClass(pl, config.getString("config.kits." + aclass + ".name"), aclass,
								config.isSet("config.kits." + aclass + ".enabled")
										? config.getBoolean("config.kits." + aclass + ".enabled") : true,
								Util.parseItems(config.getString("config.kits." + aclass + ".items")),
								Util.parseItems(config.getString("config.kits." + aclass + ".icon")).get(0));
					} else {
						n = new AClass(pl, config.getString("config.kits." + aclass + ".name"), aclass,
								config.isSet("config.kits." + aclass + ".enabled")
										? config.getBoolean("config.kits." + aclass + ".enabled") : true,
								Util.parseItems(config.getString("config.kits." + aclass + ".items")));
					}
					// pli.addAClass(aclass, n);
					MinigamesAPI.getAPI().getPluginInstance(pl)
							.addAClass(config.getString("config.kits." + aclass + ".name"), n);
					if (!config.isSet("config.kits." + aclass + ".items")
							|| !config.isSet("config.kits." + aclass + ".lore")) {
						pl.getLogger().warning("One of the classes found in the config file is invalid: " + aclass
								+ ". Missing itemid or lore!");
					}
				}
			}
		}, 20L);
	}

	public boolean kitRequiresMoney(final String kit) {
		return pli.getClassesConfig().getConfig().getBoolean("config.kits." + kit + ".requires_money");
	}

	public boolean kitTakeMoney(final Player p, final String kit) {
		// Credits
		if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_USE_CREDITS_INSTEAD_MONEY_FOR_KITS)) {
			final String uuid = p.getUniqueId().toString();
			int points = 0;
			if (!MinigamesAPI.getAPI().statsglobal.getConfig().isSet("players." + uuid + ".points")) {
				points = this.pli.getStatsInstance().getPoints(p.getName());
				MinigamesAPI.getAPI().statsglobal.getConfig().set("players." + uuid + ".points", points);
				MinigamesAPI.getAPI().statsglobal.saveConfig();
			} else {
				points = MinigamesAPI.getAPI().statsglobal.getConfig().getInt("players." + uuid + ".points");
			}
			if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_BUY_CLASSES_FOREVER)) {
				final ClassesConfig cl = this.pli.getClassesConfig();
				if (!cl.getConfig().isSet("players.bought_kits." + p.getName() + "." + kit)) {
					final int money = this.pli.getClassesConfig().getConfig()
							.getInt("config.kits." + kit + ".money_amount");
					if (points >= money) {
						MinigamesAPI.getAPI().statsglobal.getConfig().set("players." + uuid + ".points",
								points - money);
						MinigamesAPI.getAPI().statsglobal.saveConfig();
						cl.getConfig().set("players.bought_kits." + p.getName() + "." + kit, true);
						cl.saveConfig();
						p.sendMessage(this.pli.getMessagesConfig().successfully_bought_kit
								.replaceAll("<kit>",
										ChatColor.translateAlternateColorCodes('&',
												this.getClassByInternalName(kit).getName()))
								.replaceAll("<money>", Integer.toString(money)));
					} else {
						p.sendMessage(this.pli.getMessagesConfig().not_enough_money);
						return false;
					}
				} else {
					return true;
				}
			} else {
				if (this.hasClass(p.getName())) {
					if (this.getSelectedClass(p.getName()).equalsIgnoreCase(kit)) {
						return false;
					}
				}
				final ClassesConfig config = this.pli.getClassesConfig();
				final int money = config.getConfig().getInt("config.kits." + kit + ".money_amount");
				if (points >= money) {
					MinigamesAPI.getAPI().statsglobal.getConfig().set("players." + uuid + ".points", points - money);
					MinigamesAPI.getAPI().statsglobal.saveConfig();
					p.sendMessage(this.pli.getMessagesConfig().successfully_bought_kit
							.replaceAll("<kit>",
									ChatColor.translateAlternateColorCodes('&',
											this.getClassByInternalName(kit).getName()))
							.replaceAll("<money>", Integer.toString(money)));
				} else {
					p.sendMessage(this.pli.getMessagesConfig().not_enough_money);
					return false;
				}
			}
			return true;
		}

		MinigamesAPI.getAPI();
		// Money (economy)
		if (!MinigamesAPI.getAPI().economyAvailable()) {
			this.pl.getLogger().warning("Economy is turned OFF. You can turn it on in the config.");
			return false;
		}
		if (MinigamesAPI.getAPI().economyAvailable()) {
			if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_BUY_CLASSES_FOREVER)) {
				final ClassesConfig cl = this.pli.getClassesConfig();
				if (!cl.getConfig().isSet("players.bought_kits." + p.getName() + "." + kit)) {
					final int money = this.pli.getClassesConfig().getConfig()
							.getInt("config.kits." + kit + ".money_amount");
					MinigamesAPI.getAPI();
					if (MinigamesAPI.econ.getBalance(p.getName()) >= money) {
						MinigamesAPI.getAPI();
						final EconomyResponse r = MinigamesAPI.econ.withdrawPlayer(p.getName(), money);
						if (!r.transactionSuccess()) {
							p.sendMessage(String.format("An error occured: %s", r.errorMessage));
							return false;
						}
						cl.getConfig().set("players.bought_kits." + p.getName() + "." + kit, true);
						cl.saveConfig();
						p.sendMessage(this.pli.getMessagesConfig().successfully_bought_kit
								.replaceAll("<kit>",
										ChatColor.translateAlternateColorCodes('&',
												this.getClassByInternalName(kit).getName()))
								.replaceAll("<money>", Integer.toString(money)));
					} else {
						p.sendMessage(this.pli.getMessagesConfig().not_enough_money);
						return false;
					}
				} else {
					return true;
				}
			} else {
				if (this.hasClass(p.getName())) {
					if (this.getSelectedClass(p.getName()).equalsIgnoreCase(kit)) {
						return false;
					}
					if (this.kitRequiresMoney(kit)) {
						Util.sendMessage(this.pl, p, this.pli.getMessagesConfig().kit_warning);
					}
				}
				final ClassesConfig config = this.pli.getClassesConfig();
				final int money = config.getConfig().getInt("config.kits." + kit + ".money_amount");
				MinigamesAPI.getAPI();
				if (MinigamesAPI.econ.getBalance(p.getName()) >= money) {
					MinigamesAPI.getAPI();
					final EconomyResponse r = MinigamesAPI.econ.withdrawPlayer(p.getName(), money);
					if (!r.transactionSuccess()) {
						p.sendMessage(String.format("An error occured: %s", r.errorMessage));
						return false;
					}
					p.sendMessage(this.pli.getMessagesConfig().successfully_bought_kit
							.replaceAll("<kit>",
									ChatColor.translateAlternateColorCodes('&',
											this.getClassByInternalName(kit).getName()))
							.replaceAll("<money>", Integer.toString(money)));
				} else {
					p.sendMessage(this.pli.getMessagesConfig().not_enough_money);
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean kitPlayerHasPermissions(final String kit, final Player p) {
		if (pli.getClassesConfig().getConfig().getBoolean("config.kits." + kit + ".requires_permission")) {
			return true;
		} else {
			if (p.hasPermission(pli.getClassesConfig().getConfig().getString("config.kits." + kit + ".permission_node"))) {
				return true;
			} else {
				return false;
			}
		}
	}
}
