package main.java.me.aaron.saltygamesapi.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenasConfig {

	private FileConfiguration arenaConfig = null;
	private File arenaFile = null;
	private JavaPlugin pl = null;
	
	public ArenasConfig(final JavaPlugin pl) {
		this.pl = pl;
		
		
	}

	public FileConfiguration getConfig() {
		if (arenaConfig == null)
			reloadConfig();
		return arenaConfig;
	}
	
	public void reloadConfig() {
		if (arenaFile == null) {
			arenaFile = new File(pl.getDataFolder(), "arenas.yml");
		}
		arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
		
		final InputStream is = pl.getResource("arenas.yml");
		if (is != null) {
			final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(is);
			arenaConfig.setDefaults(cfg);
		}
	}
	
	public void saveConfig() {
		if (arenaConfig == null || arenaFile == null)
			return;
		try {
			getConfig().save(arenaFile);
		} catch (final IOException e) {
			;
		}
	}
	
}
