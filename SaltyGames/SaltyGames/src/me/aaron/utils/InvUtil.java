package me.aaron.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import me.aaron.main.SaltyGamesSettings;

public class InvUtil {
	
	public static Inventory createInv(InventoryHolder owner, int size, String title) {
		if (SaltyGamesSettings.checkInventoryLenght && title.length() > 32) {
			title = StringUtil.shorten(title, 32);
		}
		return Bukkit.createInventory(owner, size, title);
	}
	
	public static Inventory createInv(InventoryHolder owner, InventoryType invType, String title) {
		if (SaltyGamesSettings.checkInventoryLenght && title.length() > 32) {
			title = StringUtil.shorten(title, 32);
		}
		return Bukkit.createInventory(owner, invType, title);
	}
}
