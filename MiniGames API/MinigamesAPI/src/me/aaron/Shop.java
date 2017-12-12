package me.aaron;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.config.ShopConfig;
import me.aaron.utils.IconMenu;
import me.aaron.utils.ShopItem;
import me.aaron.utils.Util;
import net.milkbowl.vault.economy.EconomyResponse;

public class Shop {

	JavaPlugin pl;
	PluginInstance pli;

	public HashMap<String, IconMenu> lastIconM = new HashMap<>();
	public LinkedHashMap<String, ShopItem> shopItems = new LinkedHashMap<>();

	public Shop(final JavaPlugin pl, final PluginInstance pli) {
		this.pl = pl;
		this.pli = pli;
	}

	public void openGUI(final String p) {
		IconMenu iconMenu;
		final int minCount = pli.getAClasses().keySet().size();
		if (lastIconM.containsKey(p)) {
			iconMenu = lastIconM.get(p);
		} else {
			iconMenu = new IconMenu(pli.getMessagesConfig().shop_item,
					(9 * pl.getConfig().getInt(ArenaConfigStrings.CONFIG_SHOP_GUI_ROWS) > minCount - 1)
							? 9 * pl.getConfig().getInt(ArenaConfigStrings.CONFIG_SHOP_GUI_ROWS)
							: Math.round(minCount / 9) * +9,
					event -> {
						if (event.getPlayer().getName().equalsIgnoreCase(p)) {
							if (Shop.this.pli.global_players.containsKey(p)) {
								if (Shop.this.pli.getArenas().contains(Shop.this.pli.global_players.get(p))) {
									final String d = event.getName();
									final Player p1 = event.getPlayer();
									Shop.this.buy(p1, d);
								}
							}
						}
						event.setWillClose(true);
					}, pl);

			final ShopConfig shopConfig = pli.getShopConfig();
			int c = 0;
			for (final String ac : shopItems.keySet()) {
				final ShopItem ac_ = shopItems.get(ac);
				if (ac_.isEnabled()) {
					int slot = c;
					if (pli.getShopConfig().getConfig().isSet("config.shop_items." + ac_.getInternalname() + ".slot")) {
						slot = pli.getShopConfig().getConfig()
								.getInt("config.shop_items." + ac_.getInternalname() + ".slot");
						if (slot < 0 || slot > iconMenu.getSize() - 1)
							slot = c;
					}
					String color = ChatColor.GREEN + "";
					if (shopConfig.getConfig().isSet("players.bought." + p + "." + ac_.getInternalname()))
						color = ChatColor.RED + "";
					iconMenu.setOption(slot, ac_.getIcon().clone(), color + ac_.getName(), pli.getShopConfig()
							.getConfig().getString("config.shop_items." + ac_.getInternalname() + ".lore").split(";"));
					c++;
				}
			}
		}
		iconMenu.open(Bukkit.getPlayerExact(p));
		lastIconM.put(p, iconMenu);
	}

	public void loadShopItems() {
		Bukkit.getScheduler().runTaskLater(pl, () -> {
			
			final FileConfiguration config = pli.getShopConfig().getConfig();
			if (config.isSet("config.shop_items")) {
				for (final String aclss : config.getConfigurationSection("config.shop_items.").getKeys(false)) {
					final ShopItem itm = new ShopItem(pl, config.getString("config.shop_items." + aclss + ".name"), aclss,
                            config.isSet("config.shop_items." + aclss + ".enabled") ? config.getBoolean("config.shop_items." + aclss + ".enabled") : true,
                            Util.parseItems(config.getString("config.shop_items." + aclss + ".items")), Util.parseItems(config.getString("config.shop_items." + aclss + ".icon")).get(0));
					shopItems.put(aclss, itm);
				}
			}
			
		}, 20L);
	}
	
	public boolean buy(final Player p, final String item_displayname) {
		for (final String ac : shopItems.keySet()) {
			final ShopItem ac_ = shopItems.get(ac);
			if (ac_.getName().equalsIgnoreCase(ChatColor.stripColor(item_displayname))) {
				takeMoney(p, ac_.getInternalname());
				return true;
			}
		}
		return false;
	}
	
	public boolean takeMoney(final Player p, final String item)
    {
        MinigamesAPI.getAPI();
        if (!MinigamesAPI.getAPI().economyAvailable())
        {
            pl.getLogger().warning("Economy is turned OFF. Turn it ON in the config.");
            return false;
        }
        if (!requiresMoney(item))
        {
            return false;
        }
        if (MinigamesAPI.getAPI().economyAvailable())
        {
            final ShopConfig shopConfig = pli.getShopConfig();
            if (!shopConfig.getConfig().isSet("players.bought." + p.getName() + "." + item))
            {
                final int money = shopConfig.getConfig().getInt("config.shop_items." + item + ".money_amount");
                MinigamesAPI.getAPI();
                if (MinigamesAPI.econ.getBalance(p.getName()) >= money)
                {
                    MinigamesAPI.getAPI();
                    final EconomyResponse r = MinigamesAPI.econ.withdrawPlayer(p.getName(), money);
                    if (!r.transactionSuccess())
                    {
                        p.sendMessage(String.format("An error occured: %s", r.errorMessage));
                        return false;
                    }
                    shopConfig.getConfig().set("players.bought." + p.getName() + "." + item, true);
                    shopConfig.saveConfig();
                    p.sendMessage(
                            pli.getMessagesConfig().successfully_bought_shopitem.replaceAll("<shopitem>", shopItems.get(item).getName()).replaceAll("<money>", Integer.toString(money)));
                }
                else
                {
                    p.sendMessage(pli.getMessagesConfig().not_enough_money);
                    return false;
                }
            }
            else
            {
                p.sendMessage(pli.getMessagesConfig().already_bought_shopitem.replaceAll("<shopitem>", shopItems.get(item).getName()));
                return true;
            }
            return true;
        }
        else
        {
            return false;
        }
    }
	
	public boolean hasItemBought(final String p, final String itm) {
		return pli.getShopConfig().getConfig().isSet("players.bought." + p + "." + itm);
	}
	
	public boolean requiresMoney(final String itm) {
		return pli.getShopConfig().getConfig().getBoolean("config.shop_items." + itm + ".requires_money");
	}
	
	public boolean buyByInternalName(final Player p, final String itemName) {
		for (final String ac : shopItems.keySet()) {
			final ShopItem ac_ = shopItems.get(ac);
			if (ac_.getInternalname().equalsIgnoreCase(ChatColor.stripColor(itemName))) {
				takeMoney(p, ac_.getInternalname());
				return true;
			}
		}
		return false;
	}
	
	public void giveShopItems(final Player p) {
		for (final ShopItem ac : shopItems.values()) {
			if (ac.usesItems(pli)) {
				for (final ItemStack itm : ac.getItems()) {
					p.getInventory().addItem(itm);
				}
				p.updateInventory();
			}
		}
	}
	
}
