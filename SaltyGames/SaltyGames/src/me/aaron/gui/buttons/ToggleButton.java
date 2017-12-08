package me.aaron.gui.buttons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ToggleButton extends AButton {
	
	private boolean toggled = false;
	private MaterialData toggleData;
	private String toggleDisplayName = "missing name";
	private List<String> toggleLore = new ArrayList<>(Arrays.asList("missing lore"));
	
	public ToggleButton(MaterialData mat, int count, MaterialData mat2) {
		super(mat, count);
		this.toggleData = mat2;
	}
	
	
	public ToggleButton toggle() {
		toggled = !toggled;
		MaterialData mat = toggleData;
		ItemMeta meta = getItemMeta();
		String displayName = toggleDisplayName;
		ArrayList<String> lore = new ArrayList<>(toggleLore);
		
		toggleData = getData();
		toggleDisplayName = meta.getDisplayName();
		toggleLore = new ArrayList<>(meta.getLore());
		
		setData(mat);
		setType(mat.getItemType());
		setDurability(mat.getData());
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.values());
		setItemMeta(meta);
		return this;
	}


	public void setToggleDisplayName(String toggleDisplayName) {
		this.toggleDisplayName = toggleDisplayName;
	}

	public void setToggleLore(List<String> toggleLore) {
		this.toggleLore = toggleLore;
	}
}