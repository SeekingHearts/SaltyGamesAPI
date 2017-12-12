package me.aaron;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;

public class ArenaBlock implements Serializable {

	private static final long serialVersionUID = -4488875519603999962L;

	private final int x, y, z;
	private final String world;
	private final Material mat;
	private byte data;

	private ArrayList<Material> item_mats;
	private ArrayList<Byte> item_data;
	private ArrayList<Integer> item_amounts;
	private ArrayList<String> item_displayNames;

	private ArrayList<Boolean> item_splash;

	private ItemStack[] inv;

	public ArenaBlock(final Block b, final boolean custom) {
		this.mat = b.getType();
		this.x = b.getX();
		this.y = b.getY();
		this.z = b.getZ();
		this.data = b.getData();
		this.world = b.getWorld().getName();

		if (custom) {
			this.inv = ((Chest) b.getState()).getInventory().getContents();
			this.item_mats = new ArrayList<>();
			this.item_data = new ArrayList<>();
			this.item_amounts = new ArrayList<>();
			this.item_displayNames = new ArrayList<>();
			this.item_splash = new ArrayList<>();

			for (final ItemStack itm : ((Chest) b.getState()).getInventory().getContents()) {
				if (itm != null) {
					this.item_mats.add(itm.getType());
					this.item_data.add(itm.getData().getData());
					this.item_amounts.add(itm.getAmount());
					this.item_displayNames.add(itm.getItemMeta().getDisplayName());

					if (itm.getType() == Material.POTION && itm.getDurability() > 0 && itm.getData().getData() > 0) {
						final Potion potion = Potion.fromDamage(itm.getDurability() & 0x3F);
						item_splash.add(potion.isSplash());
					} else {
						item_splash.add(false);
					}
				}
			}
		}
	}

	public ArenaBlock(final Location loc) {
		this.mat = Material.AIR;
		this.x = loc.getBlockX();
		this.y = loc.getBlockY();
		this.z = loc.getBlockZ();
		this.world = loc.getWorld().getName();
	}

	public Block getBlock() {
		final World w = Bukkit.getWorld(world);
		if (w == null)
			return null;
		final Block block = w.getBlockAt(x, y, z);
		return block;
	}

	public Material getMaterial() {
		return mat;
	}

	public byte getData() {
		return data;
	}

	public ItemStack[] getInventory() {
		return inv;
	}

	public ArrayList<ItemStack> getNewInventory() {
		final int c = 0;
		final ArrayList<ItemStack> ret = new ArrayList<>();
		for (int i = 0; i < item_mats.size(); i++) {
			ItemStack itm = new ItemStack(item_mats.get(i), item_amounts.get(i), item_data.get(i));
			final ItemMeta meta = itm.getItemMeta();

			meta.setDisplayName(item_displayNames.get(i));
			itm.setItemMeta(meta);
			if (itm.getType() == Material.POTION && itm.getDurability() > 0) {
				final Potion potion = Potion.fromDamage(itm.getDurability() & 0x3F);
				potion.setSplash(item_splash.get(i));
				itm = potion.toItemStack(item_amounts.get(i));
			}
			ret.add(itm);
		}
		return ret;
	}

	public static ItemStack getEnchmentBook(final Map<Enchantment, Integer> t) {
		final ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
		final ItemMeta meta = book.getItemMeta();
		final int i = 0;

		for (final Enchantment ench : t.keySet()) {
			meta.addEnchant(ench, t.get(ench), true);
		}
		book.setItemMeta(meta);
		return book;
	}
}
