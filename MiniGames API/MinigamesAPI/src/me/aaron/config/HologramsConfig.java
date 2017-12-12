package me.aaron.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramsConfig {

	private FileConfiguration classesConfig = null;
	private File classesFile = null;
	private JavaPlugin pl = null;

	public HologramsConfig(final JavaPlugin pl, final boolean custom) {
		this.pl = pl;

		if (!custom) {
			getConfig().options().header("Diese Datei wird benutzt um Statistik-Hologramme zu speichern.");
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
			this.classesFile = new File(this.pl.getDataFolder(), "holograms.yml");
		}
		this.classesConfig = YamlConfiguration.loadConfiguration(this.classesFile);

		final InputStream defConfigStream = this.pl.getResource("holograms.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.classesConfig.setDefaults(defConfig);
		}
	}
}
