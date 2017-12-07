package me.aaron.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {
	
	private SettingsManager() {}
	
	static SettingsManager instance = new SettingsManager();
	
	public static SettingsManager getInstance() {
		return instance;
	}
	
	

	Plugin pl;
	
	FileConfiguration config;
	File configF;
	
	FileConfiguration data;
	File dataF;
	
	public void setup(Player p) {
		configF = new File(pl.getDataFolder(), "config.yml");
		config = pl.getConfig();
		
		if (!pl.getDataFolder().exists()) {
			pl.getDataFolder().mkdir();
		}
		
		dataF = new File(pl.getDataFolder(), "data.yml");
		
		if (!dataF.exists()) {
			try {
				
				dataF.createNewFile();
				
			} catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Data File konnte nicht erstellt werden!");
			}
		}
		data = YamlConfiguration.loadConfiguration(dataF);
	}
	
	
	public void saveData() {
		try {
			data.save(dataF);
		}
		catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Data File konnte nicht gespeichert!");
		}
	}
	
	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dataF);
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public void saveConfig() {
		try {
			
			config.save(configF);
			
		} catch (Exception e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Config konnte nicht gespeichert werden!");
		}
	}
	
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configF);
	}
	
	public PluginDescriptionFile getDescription() {
		return pl.getDescription();
	}
}
