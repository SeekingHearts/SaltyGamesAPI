package main.me.aaron.utils;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {
	
	private static HashMap<String, ItemStack[]> invContents = new HashMap<>();
	private static HashMap<String, ItemStack[]> armorContents = new HashMap<>();
	private static HashMap<String, Location> locs = new HashMap<>();
	private static HashMap<String, Integer> xpLvL = new HashMap<>();
	private static HashMap<String, GameMode> gm = new HashMap<>();
	
	public static void saveInventory(final Player p) {
		InventoryManager.invContents.put(p.getName(), p.getInventory().getContents());
		InventoryManager.armorContents.put(p.getName(), p.getInventory().getArmorContents());
		InventoryManager.locs.put(p.getName(), p.getLocation());
		InventoryManager.xpLvL.put(p.getName(), p.getLevel());
		InventoryManager.gm.put(p.getName(), p.getGameMode());
		p.getInventory().clear();
	}
	
	public static void restoreInventory(final Player p) {
		p.getInventory().clear();
		p.teleport(InventoryManager.locs.get(p.getName()));
		
		p.getInventory().setContents(InventoryManager.invContents.get(p.getName()));
		p.getInventory().setArmorContents(InventoryManager.armorContents.get(p.getName()));
		p.setLevel(InventoryManager.xpLvL.get(p.getName()));
		p.setGameMode(InventoryManager.gm.get(p.getName()));
		
		InventoryManager.invContents.remove(p.getName());
		InventoryManager.armorContents.remove(p.getName());
		InventoryManager.locs.remove(p.getName());
		InventoryManager.xpLvL.remove(p.getName());
	}

}
