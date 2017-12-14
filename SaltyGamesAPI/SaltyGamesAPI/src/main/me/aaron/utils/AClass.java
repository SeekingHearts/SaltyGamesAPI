package main.me.aaron.utils;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AClass {

	private final JavaPlugin pl;
	private final String name;
	private final String internalname;
	private ArrayList<ItemStack> items = new ArrayList<>();
	private final ItemStack icon;
	private boolean enabled = true;
	
	public AClass(final JavaPlugin pl, final String name, final ArrayList<ItemStack> items) {
		this(pl, name, name, true, items, items.get(0));
	}
	
	public AClass(final JavaPlugin pl, final String name, final String internalname, final ArrayList<ItemStack> items) {
		this(pl, name, internalname, true, items, items.get(0));
	}
	
	public AClass(final JavaPlugin pl, final String name, final String internalname, final boolean enabled, final ArrayList<ItemStack> items) {
		this(pl, name, internalname, enabled, items, items.get(0));
	}
	
	public AClass(final JavaPlugin pl, final String name, final String internalname, final boolean enabled
			, final ArrayList<ItemStack> items, final ItemStack icon) {
		this.pl = pl;
		this.name = name;
		this.internalname = internalname;
		this.enabled = enabled;
		this.items = items;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public String getInternalname() {
		return internalname;
	}

	public ItemStack[] getItems() {
		final ItemStack[] ret = new ItemStack[items.size()];
		int c = 0;
		for (final ItemStack f : items) {
			ret[c] = f;
			c++;
		}
		return ret;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	
	
}
