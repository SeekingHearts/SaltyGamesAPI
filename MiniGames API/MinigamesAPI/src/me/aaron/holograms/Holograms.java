package me.aaron.holograms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.aaron.ArenaLogger;
import me.aaron.MinigamesAPI;
import me.aaron.PluginInstance;
import me.aaron.config.HologramsConfig;

public class Holograms {

	PluginInstance pli;
	HashMap<Location, Hologram> hg = new HashMap<>();

	public Holograms(final PluginInstance pli) {
		this.pli = pli;
		// this.
	}

	public void loadHolograms() {
		final HologramsConfig config = pli.getHologramsConfig();

		if (config.getConfig().isSet("holograms.")) {
			for (final String str : config.getConfig().getConfigurationSection("holograms.").getKeys(false)) {
				final String base = "holograms." + str;

				try {
					final Location loc = new Location(Bukkit.getWorld(config.getConfig().getString(base + ".world")),
							config.getConfig().getDouble(base + ".location.x"),
							config.getConfig().getDouble(base + ".location.y"),
							config.getConfig().getDouble(base + ".location.z"),
							(float) config.getConfig().getDouble(base + ".location.yaw"),
							(float) config.getConfig().getDouble(base + ".location.pitch"));

					if (loc != null && loc.getWorld() != null) {
						hg.put(loc, new Hologram(pli, loc));
					}
				} catch (final Exception e) {
					ArenaLogger.debug("Fehler beim Laden vom Hologram, da eine ungültige Position gefunden wurde: "
							+ e.getMessage());
				}
			}
		}
	}

	public void sendAllHolograms(final Player p) {
		for (final Hologram holo : hg.values()) {
			holo.send(p);
		}
	}

	public void addHologram(final Location loc) {
		final String base = "holograms." + Integer.toString((int) Math.round(Math.random() * 10000));
		final HologramsConfig config = pli.getHologramsConfig();

		config.getConfig().set(base + ".world", loc.getWorld().getName());
		config.getConfig().set(base + ".location.x", loc.getX());
		config.getConfig().set(base + ".location.y", loc.getY());
		config.getConfig().set(base + ".location.z", loc.getZ());
		config.getConfig().set(base + ".location.yaw", loc.getYaw());
		config.getConfig().set(base + ".location.pitch", loc.getPitch());

		final Hologram holo = new Hologram(pli, loc);
		hg.put(loc, holo);
	}

	public boolean removeHologram(final Location ploc) {
		final HologramsConfig config = pli.getHologramsConfig();

		if (config.getConfig().isSet("holograms.")) {
			for (final String str : config.getConfig().getConfigurationSection("holograms.").getKeys(false)) {
				final String base = "holograms." + str;

				final Location loc = new Location(Bukkit.getWorld(config.getConfig().getString(base + ".world")),
						config.getConfig().getDouble(base + ".location.x"),
						config.getConfig().getDouble(base + ".location.y"),
						config.getConfig().getDouble(base + ".location.z"),
						(float) config.getConfig().getDouble(base + ".location.yaw"),
						(float) config.getConfig().getDouble(base + ".location.pitch"));

				if (loc.distance(ploc) <= 2) {
					config.getConfig().set("holograms." + str, null);
					config.saveConfig();

					if (hg.containsKey(loc)) {
						for (final Player p : Bukkit.getOnlinePlayers()) {
//
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	public void destroyHologram(final Player p, final Hologram holo) {
		
		final String version = MinigamesAPI.getAPI().internalServerVersion;
		
		try {
			final Method getPlayerHandle = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer").getMethod("getHandle");
			final Field playerCon = Class.forName("net.minecraft.server." + version + ".EntityPlayer").getField("playerConnection");
			playerCon.setAccessible(true);
			final Method sendPacket = playerCon.getType().getMethod("sendPacket", Class.forName("net.minecraft.server" + version + ".Packet"));
			
			final Constructor<?> packetPlayOutEntityDestroyConstructor = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityDestroy").getConstructor(int[].class);

			final Object destroyPacket = packetPlayOutEntityDestroyConstructor.newInstance(convertIntegers(holo.getIds()));
			
			sendPacket.invoke(playerCon.get(getPlayerHandle.invoke(p)), destroyPacket);
		} catch (final Exception e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
	}
	
	public int[] convertIntegers(final ArrayList<Integer> ints) {
		final int[] ret = new int[ints.size()];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ints.get(i).intValue();
		}
		return ret;
	}
	
}
