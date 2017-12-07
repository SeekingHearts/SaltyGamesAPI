package me.aaron.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	
	public static ItemStack getItem(Material mat, String name, String lore, int dmg, int amount) {
		
		ItemStack itm = new ItemStack(mat, amount, (short) dmg);
		ItemMeta meta = itm.getItemMeta();
		
		if (lore != null) {
			if (lore.contains("\n")) {
				ArrayList<String> lorelist = new ArrayList<>();
				String[] loresplit = lore.split("\n");
				
				for (String txt : loresplit) {
					lorelist.add(txt);
				}
				meta.setLore(lorelist);
			} else {
				meta.setLore(Arrays.asList(lore));
			}
		}
		meta.setDisplayName(name);
		itm.setItemMeta(meta);
		
		return itm;
	}

}
