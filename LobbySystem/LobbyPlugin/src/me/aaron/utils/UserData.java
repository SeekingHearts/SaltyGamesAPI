package me.aaron.utils;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class UserData {

	private static LobbyPlugin pl;

	public UserData(LobbyPlugin pl) {
		this.pl = pl;
	}

	static File cfile;
	static FileConfiguration config;
	static File folder = new File(LobbyPlugin.getInstance().getDataFolder(), "UserData" + File.separator);
	static File df = LobbyPlugin.getInstance().getDataFolder();

	public static void create(Player p) {
		cfile = new File(df, "UserData" + File.separator + p.getUniqueId() + ".yml");
		if (!df.exists())
			df.mkdir();
		if (!cfile.exists()) {
			try {
				cfile.createNewFile();
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error creating " + cfile.getName() + "!");
			}
		}
		config = YamlConfiguration.loadConfiguration(cfile);
	}

	public static File getFolder() {
		return folder;
	}

	public static File getFile() {
		return cfile;
	}

	public static void load(Player p) {
		cfile = new File(df, "UserData" + File.separator + p.getUniqueId() + ".yml");
		config = YamlConfiguration.loadConfiguration(cfile);
	}

	public static FileConfiguration get() {
		return config;
	}

	public static void save() {
		try {
			config.save(cfile);
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error saving " + cfile.getName() + "!");
		}
	}

}
