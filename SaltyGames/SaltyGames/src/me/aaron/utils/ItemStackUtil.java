package me.aaron.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ItemStackUtil {

	public static final String MAT = "materialData";
	public static final String LORE = "lore";
	public static final String NAME = "displayName";
	public static final String GLOW = "glow";
	
	public static ItemStack getItemStack(String matDataString) {
		Material mat;
		short data;
		
		if (matDataString == null)
			return null;
		
		String[] obj = matDataString.split(":");
		
		if (obj.length == 2) {
			try {
				mat = Material.matchMaterial(obj[0]);
			} catch (Exception e) {
				return null;
			}
			
			try {
				data = Short.valueOf(obj[1]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			if (mat == null)
				return null;
			ItemStack stack = new ItemStack(mat, 1);
			stack.setDurability(data);
			return stack;
		} else {
			try {
				mat = Material.matchMaterial(obj[0]);
			} catch (Exception e) {
				return null;
			}
			
			return (mat == null ? null : new ItemStack(mat, 1));
		}
	}
	
	public static ItemStack createBookWithText(List<String> text) {
		return createItemWithText(text, new MaterialData(Material.BOOK_AND_QUILL));
	}
	
	public static ItemStack createItemWithText(List<String> text, MaterialData matData) {
		ItemStack helpItem = matData.toItemStack(1);
		
		ItemMeta meta = helpItem.getItemMeta();
		if (text != null) {
			if (text.size() > 0)
				meta.setDisplayName(text.get(0));
			if (text.size() > 1) {
				text.remove(0);
				meta.setLore(text);
			}
		}
		helpItem.setItemMeta(meta);
		return helpItem;
	}
	
	public static ItemStack loadItem(ConfigurationSection section){
        ItemStack toReturn = getItemStack(section.getString(MAT));
        if(toReturn == null) return null;
        ItemMeta meta = toReturn.getItemMeta();

        if(section.isString(NAME)){
            meta.setDisplayName(StringUtil.color(section.getString(NAME)));
        }
        if(section.isList(LORE)){
            meta.setLore(StringUtil.color(section.getStringList(LORE)));
        }
        toReturn.setItemMeta(meta);

        return toReturn;
    }
}
