package main.java.me.aaron.saltygamesapi.config;

import java.io.File;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StatsGlobalConfig {
	
	private FileConfiguration statsConfig = null;
	private File statsFile = null;
	private JavaPlugin pl;
	
	public StatsGlobalConfig(final JavaPlugin pl, final boolean custom) {
		this.pl = pl;
		if (!custom) {
			getConfig().options().header("Diese Datei wird benutzt um User Statistiken zu speichern. Beispiel:");
			getConfig().addDefault("players.0daaad9b-818f-439f-9722-4a370ba0c484.wins", 1);
			getConfig().addDefault("players.0daaad9b-818f-439f-9722-4a370ba0c484.loses", 1);
			getConfig().addDefault("players.0daaad9b-818f-439f-9722-4a370ba0c484.points", 10);
			getConfig().addDefault("players.0daaad9b-818f-439f-9722-4a370ba0c484.playername", "TheSaltyOne");
		}
		getConfig().options().copyDefaults(true);
		
	}

	public FileConfiguration getConfig() {
		if (this.statsConfig == null) {
			reloadConfig();
		}
		return statsConfig;
	}
	
	public void saveConfig() {
		if (statsConfig == null || statsFile == null) {
			return;
		}
		try {
			getConfig().save(statsFile);
		} catch (Exception e) {
			;
		}
	}
	
	public void reloadConfig() {
		if (this.statsFile == null) {
			this.statsFile = new File(this.pl.getDataFolder(), "global_stats.yml");
		}
		this.statsConfig = YamlConfiguration.loadConfiguration(this.statsFile);
		
		final InputStream is = this.pl.getResource("partymessages.yml");
		if (is != null) {
			final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(is);
			this.statsConfig.setDefaults(cfg);
		}
	}
}
