package me.aaron.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class AchievementsConfig {

	private FileConfiguration classesConfig = null;
	private File classesFile = null;
	private JavaPlugin pl = null;

	public AchievementsConfig(final JavaPlugin pl) {
		this.pl = pl;

		getConfig().options().header("Diese Datei wird benutzt um Errungenschaften zu speichern.");
		
		getConfig().addDefault("config.enabled", true);
		
		getConfig().addDefault("config.achievements.first_blood.enabled", true);
        getConfig().addDefault("config.achievements.first_blood.name", "Erstes Blut!");
        getConfig().addDefault("config.achievements.first_blood.reward.economy_reward", true);
        getConfig().addDefault("config.achievements.first_blood.reward.econ_reward_amount", 20);
        getConfig().addDefault("config.achievements.first_blood.reward.command_reward", false);
        getConfig().addDefault("config.achievements.first_blood.reward.cmd", "tell <player> Gut gemacht!");
        
        getConfig().addDefault("config.achievements.ten_kills.enabled", true);
        getConfig().addDefault("config.achievements.ten_kills.name", "Zehn Kills!");
        getConfig().addDefault("config.achievements.ten_kills.reward.economy_reward", true);
        getConfig().addDefault("config.achievements.ten_kills.reward.econ_reward_amount", 50);
        getConfig().addDefault("config.achievements.ten_kills.reward.command_reward", false);
        getConfig().addDefault("config.achievements.ten_kills.reward.cmd", "tell <player> Gut gemacht!");
        
        getConfig().addDefault("config.achievements.hundred_kills.enabled", true);
        getConfig().addDefault("config.achievements.hundred_kills.name", "Hundert Kills!");
        getConfig().addDefault("config.achievements.hundred_kills.reward.economy_reward", true);
        getConfig().addDefault("config.achievements.hundred_kills.reward.econ_reward_amount", 1000);
        getConfig().addDefault("config.achievements.hundred_kills.reward.command_reward", false);
        getConfig().addDefault("config.achievements.hundred_kills.reward.cmd", "tell <player> Gut gemacht!");
        
        getConfig().addDefault("config.achievements.first_win.enabled", true);
        getConfig().addDefault("config.achievements.first_win.name", "Dein erster Sieg!");
        getConfig().addDefault("config.achievements.first_win.reward.economy_reward", true);
        getConfig().addDefault("config.achievements.first_win.reward.econ_reward_amount", 30);
        getConfig().addDefault("config.achievements.first_win.reward.command_reward", false);
        getConfig().addDefault("config.achievements.first_win.reward.cmd", "tell <player> Gut gemacht!");
        
        getConfig().addDefault("config.achievements.ten_wins.enabled", true);
        getConfig().addDefault("config.achievements.ten_wins.name", "Zehnmal gewonnen!");
        getConfig().addDefault("config.achievements.ten_wins.reward.economy_reward", true);
        getConfig().addDefault("config.achievements.ten_wins.reward.econ_reward_amount", 30);
        getConfig().addDefault("config.achievements.ten_wins.reward.command_reward", false);
        getConfig().addDefault("config.achievements.ten_wins.reward.cmd", "tell <player> Gut gemacht!");
        
        getConfig().addDefault("config.achievements.achievement_guy.enabled", true);
        getConfig().addDefault("config.achievements.achievement_guy.name", "Achievement Jäger!");
        getConfig().addDefault("config.achievements.achievement_guy.reward.economy_reward", true);
        getConfig().addDefault("config.achievements.achievement_guy.reward.econ_reward_amount", 30);
        getConfig().addDefault("config.achievements.achievement_guy.reward.command_reward", false);
        getConfig().addDefault("config.achievements.achievement_guy.reward.cmd", "tell <player> Gut gemacht!");

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public FileConfiguration getConfig() {
		if (classesConfig == null)
			reloadConfig();
		return classesConfig;
	}

	public void saveConfig() {
		if (classesConfig == null || classesFile == null)
			return;
		try {
			this.getConfig().save(classesFile);
		} catch (final IOException ex) {
			;
		}
	}

	public void reloadConfig() {

		if (this.classesFile == null) {
			this.classesFile = new File(this.pl.getDataFolder(), "achievements.yml");
		}
		this.classesConfig = YamlConfiguration.loadConfiguration(this.classesFile);

		final InputStream defConfigStream = this.pl.getResource("achievements.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.classesConfig.setDefaults(defConfig);
		}
	}
}
