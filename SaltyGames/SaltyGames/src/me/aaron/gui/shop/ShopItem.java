package me.aaron.gui.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ShopItem {
	
	private List<String> commands = new ArrayList<>();
	private List<String> permissions = new ArrayList<>();
	private List<String> noPermissions = new ArrayList<>();
	
	private boolean manipulatesInventory = false;
	
	private ItemStack itemStack;

	public List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public List<String> getNoPermissions() {
		return noPermissions;
	}

	public void setNoPermissions(List<String> noPermissions) {
		this.noPermissions = noPermissions;
	}

	public boolean ManipulatesInventory() {
		return manipulatesInventory;
	}

	public void setManipulatesInventory(boolean manipulatesInventory) {
		this.manipulatesInventory = manipulatesInventory;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	
	

}
