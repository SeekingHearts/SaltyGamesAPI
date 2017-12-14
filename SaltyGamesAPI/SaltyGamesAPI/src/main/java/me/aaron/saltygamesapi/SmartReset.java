package main.java.me.aaron.saltygamesapi;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import main.java.me.aaron.saltygamesapi.utils.ChangeCause;
import main.java.me.aaron.saltygamesapi.utils.SmartArenaBlock;
import main.java.me.aaron.saltygamesapi.utils.Util;

public class SmartReset implements Runnable {

	private final SmartBlockMap changed = new SmartBlockMap();

	private Arena ar;

	private final ArrayList<SmartArenaBlock> failedBlocks = new ArrayList<>();

	private long time = 0L;

	public SmartReset(final Arena ar) {
		this.ar = ar;
	}

	public SmartArenaBlock addChanged(final Block block) {
		if (changed.hasBlock(block.getLocation())) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("(1) Füge Blockänderung für " + block.getLocation() + " hinzu!");

			final SmartArenaBlock saBlock = new SmartArenaBlock(block, block.getType() == Material.CHEST,
					block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST);
			changed.putBlock(block.getLocation(), saBlock);
			return saBlock;
		}
		return null;
	}

	public void addChanged(Block[] loc) {
		if (loc != null) {
			for (final Block block : loc) {
				addChanged(block);
			}
		}
	}

	public SmartArenaBlock addChanged(Block b, BlockState blockReplacedState)
    {
        return addChanged(b.getLocation(), blockReplacedState.getType(), blockReplacedState.getData().getData());
    }

	public SmartArenaBlock addChanged(final Block block, final boolean isChest) {
		if (changed.hasBlock(block.getLocation())) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("(2) Füge Blockänderung für " + block.getLocation() + " hinzu!");

			final SmartArenaBlock saBlock = new SmartArenaBlock(block, isChest,
					block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST);
			changed.putBlock(block.getLocation(), saBlock);
			return saBlock;
		}
		return null;
	}

	public void addChanged(final Location loc) {
		if (changed.hasBlock(loc)) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("(4) Füge Blockänderung für " + loc + " hinzu");

			changed.putBlock(loc, new SmartArenaBlock(loc, Material.AIR, (byte) 0));
		}
	}

	public SmartArenaBlock addChanged(final Location loc, final Material mat, final byte data) {
		if (changed.hasBlock(loc)) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("(5) Füge Blockänderung für " + loc + " hinzu!");

			final SmartArenaBlock saBlock = new SmartArenaBlock(loc, mat, data);
			changed.putBlock(loc, saBlock);
			return saBlock;
		}
		return null;
	}
	
	public SmartArenaBlock addChanged(final Block block, final boolean isChest, final ChangeCause cause) {
		if (!changed.hasBlock(block.getLocation())) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("(3) Füge eine Blockänderung für " + block.getLocation());
			
			final SmartArenaBlock saBlock = new SmartArenaBlock(block, isChest, block.getType() == Material.WALL_SIGN
					|| block.getType() == Material.SIGN_POST);
			
			changed.putBlock(block.getLocation(), saBlock);
			return saBlock;
		}
		return null;
	}

	@Override
	public void run() {

		int rollBack = 0;

		final Iterator<SmartArenaBlock> it = changed.getBlocks().iterator();
		while (it.hasNext() && rollBack <= 70) {

			final SmartArenaBlock sab = it.next();

			try {
				MinigamesAPI.getAPI().getLogger().fine("Resetting Block " + sab.getBlock().getLocation());
				resetSmartResetBlock(sab);
				it.remove();
			} catch (final Exception e) {
				if (MinigamesAPI.debug)
					MinigamesAPI.getAPI().getLogger().log(Level.FINE,
							"Fehler bei Block " + sab.getBlock().getLocation(), e);
				failedBlocks.add(sab);
			}
			rollBack++;
		}

		if (it.hasNext()) {
			Bukkit.getScheduler().runTaskLater(ar.getPlugin(), this, 2L);
			return;
		}

		ar.setArenaState(ArenaState.JOIN);
		Util.updateSign(ar.getPlugin(), ar);

		ArenaLogger.debug(failedBlocks.size() + " müssen erneut resettet werden.");

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MinigamesAPI.getAPI(), () -> {

			SmartReset.this.changed.clear();

			for (final SmartArenaBlock sab : SmartReset.this.failedBlocks) {

				MinigamesAPI.getAPI().getLogger()
						.fine("Versuche die geänderten Blöcke erneut zu resetten " + sab.getBlock().getLocation());
				final Block b = sab.getBlock().getWorld().getBlockAt(sab.getBlock().getLocation());

				if (!b.getType().toString().equalsIgnoreCase(sab.getMaterial().toString())) {

					b.setType(sab.getMaterial());
					b.setData(sab.getData());
				}
				if (b.getType() == Material.CHEST) {
					b.setType(sab.getMaterial());
					b.setData(sab.getData());

					((Chest) b.getState()).getInventory().setContents(sab.getInventory());
					((Chest) b.getState()).update();
				}
			}
		}, 25L);
		ArenaLogger.debug("Reset Time: " + (System.currentTimeMillis() - time) + "ms");
	}

	public void reset() {
		time = System.currentTimeMillis();
		ar.getPlugin().getLogger()
				.info(changed.size() + " müssen in Arena " + ar.getInternalName() + " noch resettet werden.");
		Bukkit.getScheduler().runTask(ar.getPlugin(), this);
	}

	public void resetRaw() {
		for (final SmartArenaBlock sab : changed.getBlocks()) {
			try {
				resetSmartResetBlock(sab);
			} catch (final Exception e) {
				ar.setArenaState(ArenaState.JOIN);
				Util.updateSign(ar.getPlugin(), ar);
			}
		}
		changed.clear();
		ar.setArenaState(ArenaState.JOIN);
		Util.updateSign(ar.getPlugin(), ar);
	}

	public void resetSmartResetBlock(final SmartArenaBlock sab) {
		final Block block = sab.getBlock().getWorld().getBlockAt(sab.getBlock().getLocation());

		if (block.getType() == Material.FURNACE) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Ofen Inventar");
			((Furnace) block.getState()).getInventory().clear();
			((Furnace) block.getState()).update();
		}
		if (block.getType() == Material.CHEST) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Kisten Inventar");
			((Chest) block.getState()).getInventory().clear();
			((Chest) block.getState()).update();
		}
		if (block.getType() == Material.DROPPER) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Dropper Inventar");
			((Dropper) block.getState()).getInventory().clear();
			((Dropper) block.getState()).update();
		}
		if (block.getType() == Material.DISPENSER) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Dispenser Inventar");
			((Dispenser) block.getState()).getInventory().clear();
			((Dispenser) block.getState()).update();
		}
		if (block.getType() == Material.BREWING_STAND) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Braustand Inventar");
			((BrewingStand) block.getState()).getInventory().clear();
			((BrewingStand) block.getState()).update();
		}

		if (!block.getType().equals(sab.getMaterial()) || block.getData() != sab.getData()) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger()
						.fine("Resette Block Material und Data zu " + sab.getMaterial() + "/" + sab.getData());
			block.setType(sab.getMaterial());
			block.setData(sab.getData());
		} else if (MinigamesAPI.debug) {
			MinigamesAPI.getAPI().getLogger().fine("Überspringe Block von " + block.getType() + "/" + block.getData()
					+ " zu " + sab.getMaterial() + "/" + sab.getData());
		}

		if (block.getType() == Material.CHEST) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Kiste");

			if (sab.isDoubleChest()) {
				final DoubleChest dc = sab.getDoubleChest();
				final HashMap<Integer, ItemStack> chestinv = sab.getNewInventory();

				for (final Integer i : chestinv.keySet()) {
					final ItemStack itm = chestinv.get(i);
					if (itm != null) {
						dc.getInventory().setItem(i, itm);
					}
				}
				((Chest) block.getState()).update();
				return;
			}
			((Chest) block.getState()).getBlockInventory().clear();
			((Chest) block.getState()).update();

			final HashMap<Integer, ItemStack> chestinv = sab.getNewInventory();
			for (final Integer i : chestinv.keySet()) {
				final ItemStack itm = chestinv.get(i);
				if (itm != null) {
					if (i < 27) {
						((Chest) block.getState()).getBlockInventory().setItem(i, itm);
					}
				}
			}
			((Chest) block.getState()).update();
		}

		if (block.getType() == Material.DISPENSER) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Dispenser");
			final Dispenser d = (Dispenser) block.getState();
			d.getInventory().clear();
			final HashMap<Integer, ItemStack> chestinv = sab.getNewInventory();
			for (final Integer i : chestinv.keySet()) {
				final ItemStack item = chestinv.get(i);
				if (item != null) {
					if (i < 9) {
						d.getInventory().setItem(i, item);
					}
				}
			}
			d.getInventory().setContents(sab.getInventory());
			d.update();
		}
		if (block.getType() == Material.DROPPER) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Dropper");
			final Dropper d = (Dropper) block.getState();
			d.getInventory().clear();
			final HashMap<Integer, ItemStack> chestinv = sab.getNewInventory();
			for (final Integer i : chestinv.keySet()) {
				final ItemStack item = chestinv.get(i);
				if (item != null) {
					if (i < 9) {
						d.getInventory().setItem(i, item);
					}
				}
			}
			d.update();
		}
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Schild");
			final Sign sign = (Sign) block.getState();
			if (sign != null) {
				int i = 0;
				for (final String line : sab.getSignLines()) {
					sign.setLine(i, line);
					i++;
					if (i > 3) {
						break;
					}
				}
				sign.update();
			}
		}
		if (block.getType() == Material.SKULL) {
			if (MinigamesAPI.debug)
				MinigamesAPI.getAPI().getLogger().fine("Resette Kopf");
			block.setData((byte) 0x1);
			block.getState().setType(Material.SKULL);
			if (block.getState() instanceof Skull) {
				final Skull s = (Skull) block.getState();
				s.setSkullType(SkullType.PLAYER);
				s.setOwner(sab.getSkullOwner());
				s.setRotation(sab.getSkullORotation());
				s.update();
			}
		}
	}
	
	public void saveSmartBlockToFile() {
		final File f = new File(ar.getPlugin().getDataFolder() + "/" + ar.getInternalName() + "_smart");
		
		FileOutputStream fos;
		ObjectOutputStream oos = null;
		
		try {
			fos = new FileOutputStream(f);
			oos = new BukkitObjectOutputStream(fos);
		} catch (final IOException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
		
		for (final SmartArenaBlock block : changed.getBlocks()) {
			try {
				oos.writeObject(block);
			} catch (Exception e) {
				MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "IO Fehler", e);
			}
		}
		
		try {
			oos.close();
		} catch (Exception e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
		MinigamesAPI.getAPI().getLogger().info("Speicher SmartBlocks von " + ar.getInternalName());
	}
	
	public void loadSmartBlocksFromFile() {
		final File f = new File(ar.getPlugin().getDataFolder() + "/" + ar.getInternalName() + "_smart");
		if (!f.exists())
			return;
		
		FileInputStream fis = null;
		BukkitObjectInputStream ois = null;
		
		try {
			fis = new FileInputStream(f);
			ois = new BukkitObjectInputStream(fis);
		} catch (Exception e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
		
		try {
			while (true) {
				Object o = null;
				try {
					o = ois.readObject();
				} catch (final EOFException e) {
					MinigamesAPI.getAPI().getLogger().info("SmartReset für " + ar.getInternalName() + " beendet.");
				} catch (final ClosedChannelException e) {
					MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Irgendwas lief schief mit der SmartReset Datei und der Reset könnte nicht funktioniert haben.", e);
				}
				
				if (o != null) {
					final SmartArenaBlock sab = (SmartArenaBlock) o;
					resetSmartResetBlock(sab);
				} else {
					break;
				}
			}
		} catch (final IOException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		} catch (final ClassNotFoundException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
		
		try {
			ois.close();
		} catch (Exception e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
		
		if (f.exists()) {
			f.delete();
		}
	}
	
	private static final class SmartBlockMap extends TreeMap<Integer, Map<Location, SmartArenaBlock>> {

		private static final long serialVersionUID = 5732057083490348369L;

		public void putBlock(Location loc, SmartArenaBlock block) {
			this.computeIfAbsent(loc.getBlockY(), (key) -> new HashMap<>()).put(loc, block);
		}

		public boolean hasBlock(Location loc) {
			final Map<Location, SmartArenaBlock> map = this.get(loc.getBlockY());
			if (map != null) {
				return map.containsKey(loc);
			}
			return false;
		}

		public Iterable<SmartArenaBlock> getBlocks() {
			return new Iterable<SmartArenaBlock>() {

				@Override
				public Iterator<SmartArenaBlock> iterator() {
					return new NestedIterator<>(SmartBlockMap.this.values().iterator());
				}
			};
		}

	}

	public static final class NestedIterator<K, T> implements Iterator<T> {

		private Iterator<Map<K, T>> outer = null;

		private Iterator<T> inner = null;

		private Iterator<T> prev = null;

		public NestedIterator(Iterator<Map<K, T>> iter) {
			this.outer = iter;
			moveNext();
		}

		@Override
		public boolean hasNext() {
			if (this.inner != null) {
				return this.inner.hasNext();
			}
			return false;
		}

		@Override
		public T next() {
			if (this.inner == null) {
				throw new NoSuchElementException();
			}
			final T result = this.inner.next();
			this.prev = this.inner;
			if (!this.inner.hasNext()) {
				this.inner = null;
				moveNext();
			}
			return result;
		}

		private void moveNext() {
			while (this.inner == null) {
				if (!this.outer.hasNext()) {
					break;
				}
				this.inner = this.outer.next().values().iterator();
				if (!this.inner.hasNext()) {
					this.inner = null;
				}
			}
		}

		@Override
		public void remove() {
			if (this.prev == null) {
				throw new IllegalStateException("no next called");
			}
			this.prev.remove();
		}
	}
}
