package main.java.me.aaron.saltygamesapi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.aaron.saltygamesapi.config.ArenasConfig;
import main.java.me.aaron.saltygamesapi.utils.Util;
import main.java.me.aaron.saltygamesapi.utils.Validator;

public class ArenaSetup {

	// SET SPAWN(S)
	public void setSpawn(final JavaPlugin pl, final String arName, final Location loc) {
		Util.saveComponentForArena(pl, arName, "spawns.spawn0", loc);
	}

	public int autoSetSpawn(final JavaPlugin pl, final String arName, final Location loc) {
		final int count = Util.getAllSpawns(pl, arName).size();
		Util.saveComponentForArena(pl, arName, "spawns.spawn" + Integer.toString(count), loc);
		return count;
	}

	public void setSpawn(final JavaPlugin pl, final String arName, final Location loc, final int count) {
		Util.saveComponentForArena(pl, arName, "spawns.spawn" + Integer.toString(count), loc);
	}

	// REMOVE SPAWN
	public boolean removeSpawn(final JavaPlugin pl, final String arName, final int count) {
		final ArenasConfig config = MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig();
		final String path = ArenaConfigStrings.ARENAS_PREFIX + arName + ".spawns.spawn" + Integer.toString(count);
		boolean ret = false;

		if (config.getConfig().isSet(path))
			ret = true;

		config.getConfig().set(path, null);
		config.saveConfig();
		return ret;
	}

	// SET LOBBY / MAINLOBBY
	public void setLobby(final JavaPlugin pl, final String arName, final Location loc) {
		Util.saveComponentForArena(pl, arName, "lobby", loc);
	}

	public void setMainLobby(final JavaPlugin pl, final Location loc) {
		Util.saveMainLobby(pl, loc);
	}

	// UMGRENZUNGEN
	public void setBoundaries(final JavaPlugin pl, final String arName, final Location loc, final boolean low) {
		if (low)
			Util.saveComponentForArena(pl, arName, ArenaConfigStrings.BOUNDS_LOW, loc);
		else
			Util.saveComponentForArena(pl, arName, ArenaConfigStrings.BOUNDS_HIGH, loc);
	}

	public void setBoundaries(final JavaPlugin pl, final String arName, final Location loc, final boolean low,
			final String extra_comp) {
		if (low)
			Util.saveComponentForArena(pl, arName, extra_comp + ".bounds.low", loc);
		else
			Util.saveComponentForArena(pl, arName, extra_comp + ".bounds.high", loc);
	}

	// SAVE ARENA
	public Arena saveArena(final JavaPlugin pl, final String arName) {
		if (!Validator.isArenaValid(pl, arName)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Arena " + arName + " scheint ungültig zu sein.");
			return null;
		}

		final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(pl);
		if (pli.getArenaByName(arName) != null) {
			pli.removeArenaByName(arName);
		}

		final Arena a = Util.initArena(pl, arName);
		if (a.getArenaType() == ArenaType.REGENERATION) {
			if (Util.isComponentForArenaValid(pl, arName, "bounds")) {
				Util.saveArenaToFile(pl, arName);
			} else {
				Bukkit.getConsoleSender().sendMessage(
						ChatColor.RED + "Could not save arena to file because boundaries were not set up.");
			}
		}

		setArenaVIP(pl, arName, false);
		pli.addArena(a);

		// experimental:
		final Class<? extends JavaPlugin> clazz = pl.getClass();
		try {
			final Method method = clazz.getDeclaredMethod("loadArenas", JavaPlugin.class,
					pli.getArenasConfig().getClass());
			if (method != null) {
				method.setAccessible(true);
				final Object ret = method.invoke(this, pl, pli.getArenasConfig());
				pli.clearArenas();
				pli.addLoadedArenas((ArrayList<Arena>) ret);
			}
		} catch (final Exception e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING,
					"Failed to update Arena list, please reload the server.", e);
		}

		final String path = ArenaConfigStrings.ARENAS_PREFIX + arName + ArenaConfigStrings.DISPLAYNAME_SUFFIX;
		if (!pli.getArenasConfig().getConfig().isSet(path)) {
			pli.getArenasConfig().getConfig().set(path, arName);
			pli.getArenasConfig().saveConfig();
		}

		return a;
	}

	public void setPlayerCount(final JavaPlugin pl, final String ar, final int count, final boolean max) {
		String comp = "max_players";

		if (!max)
			comp = "min_players";

		final String base = ArenaConfigStrings.ARENAS_PREFIX + ar + "." + comp;
		MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().getConfig().set(base, count);
		MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().saveConfig();
	}

	public int getPlayerCount(final JavaPlugin plugin, final String arena, final boolean max) {
		if (!max) {
			if (!MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig().getConfig()
					.isSet(ArenaConfigStrings.ARENAS_PREFIX + arena + ".min_players")) {
				setPlayerCount(plugin, arena,
						plugin.getConfig().getInt(ArenaConfigStrings.CONFIG_DEFAULT_MIN_PLAYERS), max);
				return plugin.getConfig().getInt(ArenaConfigStrings.CONFIG_DEFAULT_MIN_PLAYERS);
			}
			return MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig().getConfig()
					.getInt(ArenaConfigStrings.ARENAS_PREFIX + arena + ".min_players");
		}
		if (!MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig().getConfig()
				.isSet(ArenaConfigStrings.ARENAS_PREFIX + arena + ".max_players")) {
			setPlayerCount(plugin, arena, plugin.getConfig().getInt(ArenaConfigStrings.CONFIG_DEFAULT_MAX_PLAYERS),
					max);
			return plugin.getConfig().getInt(ArenaConfigStrings.CONFIG_DEFAULT_MAX_PLAYERS);
		}
		return MinigamesAPI.getAPI().getPluginInstance(plugin).getArenasConfig().getConfig()
				.getInt(ArenaConfigStrings.ARENAS_PREFIX + arena + ".max_players");
	}
	
	public void setArenaVIP(final JavaPlugin pl, final String ar, final boolean vip) {
		MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + ar + ".is_vip", vip);
		MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().saveConfig();
	}
	
	public boolean getArenaVIP(final JavaPlugin pl, final String ar) {
		return MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().getConfig().getBoolean(ArenaConfigStrings.ARENAS_PREFIX + ar + ".is_vip");
	}
	
	public void setArenaEnabled(final JavaPlugin pl, final String ar, final boolean enabled) {
		MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + ar + ".enabled", enabled);
		MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().saveConfig();
	}
	
	public boolean getArenaEnabled(final JavaPlugin pl, final String ar) {
		final FileConfiguration config = MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().getConfig();
		return config.isSet(ArenaConfigStrings.ARENAS_PREFIX + ar + ".enabled") ? config.getBoolean(ArenaConfigStrings.ARENAS_PREFIX + ar + ".enabled") : true;
	}
	
	public void setShowScoreboard(final JavaPlugin pl, final String ar, final boolean enabled) {
		MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + ar + ".showscoreboard", enabled);
		MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().saveConfig();
	}
	
	public boolean getShowScoreboard(final JavaPlugin pl, final String ar) {
		final FileConfiguration config = MinigamesAPI.getAPI().getPluginInstance(pl).getArenasConfig().getConfig();
		return config.isSet(ArenaConfigStrings.ARENAS_PREFIX + ar + ".showscoreboard") ? config.getBoolean(ArenaConfigStrings.ARENAS_PREFIX + ar + ".showscoreboard") : true;
	}

}
