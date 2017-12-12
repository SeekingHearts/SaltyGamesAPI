package me.aaron;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;

import me.aaron.utils.Util;

public class PrivateUtil {

	public static void loadArenaFromFileSYNC(final JavaPlugin pl, final Arena ar) {
		int failcount = 0;

		final ArrayList<ArenaBlock> failedBlocks = new ArrayList<>();
		final File f = new File(pl.getDataFolder() + "/" + ar.getInternalName());

		if (!f.exists()) {
			pl.getLogger().warning("Arena File für " + ar.getInternalName() + " konnte nicht gefunden werden.");
			ar.setArenaState(ArenaState.JOIN);
			Bukkit.getScheduler().runTask(pl, () -> Util.updateSign(pl, ar));
			return;
		}
		FileInputStream fis = null;
		BukkitObjectInputStream ois = null;

		try {
			fis = new FileInputStream(f);
			ois = new BukkitObjectInputStream(fis);
		} catch (final IOException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING,
					"Irgendwas läuft mit dem Arena File schief und der Reset war warscheinlich nicht erfolgreich. Außerdem nutzt du eine veraltete Reset Method!",
					e);
			return;
		}

		try {
			while (true) {
				Object o = null;
				try {
					o = ois.readObject();
				} catch (final EOFException e) {
					MinigamesAPI.getAPI().getLogger().info("Beende Wiederherstellung der Karte von "
							+ ar.getInternalName() + " mit einer alten Reset Method!");

					ar.setArenaState(ArenaState.JOIN);
					Bukkit.getScheduler().runTask(pl, () -> Util.updateSign(pl, ar));
				} catch (final ClosedChannelException e) {
					MinigamesAPI.getAPI().getLogger().log(Level.WARNING,
							"Irgendwas läuft mit dem Arena File schief und der Reset war warscheinlich nicht erfolgreich. Außerdem nutzt du eine veraltete Reset Method!",
							e);
				} catch (final Exception e) {
					MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
					ar.setArenaState(ArenaState.JOIN);
					Bukkit.getScheduler().runTask(pl, () -> Util.updateSign(pl, ar));
				}

				if (o != null) {
					final ArenaBlock arBlock = (ArenaBlock) o;

					try {
						final Block b_ = arBlock.getBlock().getWorld().getBlockAt(arBlock.getBlock().getLocation());

						if (!b_.getType().toString().equalsIgnoreCase(arBlock.getMaterial().toString())) {
							b_.setType(arBlock.getMaterial());
							b_.setData(arBlock.getData());
						}

						if (b_.getType() == Material.CHEST) {
							((Chest) b_.getState()).getInventory().setContents(arBlock.getInventory());
							((Chest) b_.getState()).update();
						}
					} catch (final IllegalStateException e) {
						failcount += 1;
						failedBlocks.add(arBlock);
					}
				} else {
					break;
				}
			}
		} catch (final Exception e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}

		try {
			ois.close();
		} catch (final IOException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "IO-Fehler", e);
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MinigamesAPI.getAPI(), () -> {

			for (final ArenaBlock arBlock : failedBlocks) {
				final Block b_ = arBlock.getBlock().getWorld().getBlockAt(arBlock.getBlock().getLocation());

				if (!b_.getType().toString().equalsIgnoreCase(arBlock.getMaterial().toString())) {
					b_.setType(arBlock.getMaterial());
					b_.setData(arBlock.getData());
				}

				if (b_.getType() == Material.CHEST) {
					((Chest) b_.getState()).getInventory().setContents(arBlock.getInventory());
					((Chest) b_.getState()).update();
				}
			}
		}, 40L);
		MinigamesAPI.getAPI().getLogger().info("Erfolgreich beendet!");
	}
}
