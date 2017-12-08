package me.aaron.gui.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.main.SaltyGames;
import me.aaron.utils.ClickAction;
import me.aaron.utils.ItemStackUtil;
import me.aaron.utils.StringUtil;

class MainShop extends Shop {

	
	MainShop(SaltyGames plugin, GUIManager guiManager, int slots, ShopManager shopManager, String[] args) {
        super(plugin, guiManager, slots, shopManager, args, plugin.lang.SHOP_TITLE_MAIN_SHOP);
		
		loadCategories();
	}
	
	private void loadCategories() {
		List<String> lore;
		ItemStack buttonItem;
		
		for (String cat : shop.getConfigurationSection("shop.categories").getKeys(false)) {
			ConfigurationSection category = shop.getConfigurationSection("shop.categories." + cat);
			buttonItem = ItemStackUtil.getItemStack(category.getString("materialData"));
			
			if (buttonItem == null) {
				Bukkit.getLogger().log(Level.WARNING, " fehler beim laden:   shop.categories." + cat);
				Bukkit.getLogger().log(Level.WARNING, "      fehlerhafte material data");
				continue;
			}
			if (category.getBoolean("glow")) {
				buttonItem = plugin.getNMS().addGlow(buttonItem);
			}
			AButton button = new AButton(buttonItem);
			ItemMeta meta = button.getItemMeta();
			
			if (category.isString("displayName")) {
				meta.setDisplayName(StringUtil.color(category.getString("displayName")));
			}
			if (category.isList("lore")) {
				lore = new ArrayList<>(category.getStringList("lore"));
				
				for (int i = 0; i < lore.size(); i++) {
					lore.set(i, StringUtil.color(lore.get(i)));
				}
				meta.setLore(lore);
			}
			button.setItemMeta(meta);
			button.setAction(ClickAction.OPEN_SHOP_PAGE);
			button.setArgs(cat, "0");
			
			setButton(button);
			
			shopManager.loadCategory(cat);
		}
	}

}
