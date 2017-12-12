package me.aaron.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.MinigamesAPI;

public class ClassesConfig {

	private FileConfiguration classesConfig = null;
	private File classesFile = null;
	private JavaPlugin pl = null;

	public ClassesConfig(final JavaPlugin pl, final boolean custom) {
		this.pl = pl;

		if (!custom) {
			getConfig().options().header("Diese Datei wird benutzt um Klassen zu speichern. Standart Klasse:");
			getConfig().addDefault("config.kits.default.name", "default");
			getConfig().addDefault("config.kits.default.enabled", true);
			getConfig().addDefault("config.kits.default.items", "351:5#DAMAGE_ALL:1#KNOCKBACK*1");
			getConfig().addDefault("config.kits.default.icon", "351:5#DAMAGE_ALL:1#KNOCKBACK*1");
			getConfig().addDefault("config.kits.default.lore", "Die Standart Klasse.;Zweite Reihe...");
			getConfig().addDefault("config.kits.default.requires_money", false);
			getConfig().addDefault("config.kits.default.requires_permission", false);
			getConfig().addDefault("config.kits.default.money_amount", 100);
			getConfig().addDefault("config.kits.default.permission_node", MinigamesAPI.getAPI().getPermissionKitPrefix() + ".default");
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
			this.classesFile = new File(this.pl.getDataFolder(), "classes.yml");
		}
		this.classesConfig = YamlConfiguration.loadConfiguration(this.classesFile);

		final InputStream defConfigStream = this.pl.getResource("classes.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.classesConfig.setDefaults(defConfig);
		}
	}
}
