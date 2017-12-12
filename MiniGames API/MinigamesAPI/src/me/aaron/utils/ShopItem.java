package me.aaron.utils;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.PluginInstance;

public class ShopItem extends AClass {

	public ShopItem(JavaPlugin pl, String name, String internalname, boolean enabled, ArrayList<ItemStack> items,
			ItemStack icon) {
		super(pl, name, internalname, enabled, items, icon);
	}
	
	public boolean usesItems(final PluginInstance pli) {
		return pli.getShopConfig().getConfig().getBoolean("config.shop_items." + getInternalname() + ".uses_items");
	}

}
