package me.aaron.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StatsConfig {

	private FileConfiguration classesConfig = null;
	private File classesFile = null;
	private JavaPlugin pl = null;

	public StatsConfig(final JavaPlugin pl, final boolean custom) {
		this.pl = pl;

		if (!custom) {
			getConfig().options().header("Diese Datei wird benutzt um User Statistiken zu speichern. Beispiel:");
			getConfig().addDefault("players.0daaad9b-818f-439f-9722-4a370ba0c484.wins", 1);
			getConfig().addDefault("players.0daaad9b-818f-439f-9722-4a370ba0c484.loses", 1);
			getConfig().addDefault("players.0daaad9b-818f-439f-9722-4a370ba0c484.points", 10);
			getConfig().addDefault("players.0daaad9b-818f-439f-9722-4a370ba0c484.playername", "TheSaltyOne");
		}
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public FileConfiguration getConfig() {
		if (classesConfig == null)
			reloadConfig();
		return classesConfig;
	}
	
	public void saveConfig()
    {
        if (classesConfig == null || classesFile == null)
        	return;
        try
        {
            this.getConfig().save(classesFile);
        }
        catch (final IOException ex)
        {
            ;
        }
    }

	public void reloadConfig() {

		if (this.classesFile == null) {
			this.classesFile = new File(this.pl.getDataFolder(), "stats.yml");
		}
		this.classesConfig = YamlConfiguration.loadConfiguration(this.classesFile);

		final InputStream defConfigStream = this.pl.getResource("stats.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.classesConfig.setDefaults(defConfig);
		}
	}
}
