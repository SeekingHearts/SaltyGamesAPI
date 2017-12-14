package main.java.me.aaron.saltygamesapi.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.aaron.saltygamesapi.MinigamesAPI;

public class GunsConfig {

	private FileConfiguration classesConfig = null;
	private File classesFile = null;
	private JavaPlugin pl = null;

	public GunsConfig(final JavaPlugin pl, final boolean custom) {
		this.pl = pl;

		if (!custom) {
			this.getConfig().options().header("Sei dir bewusst, dass diese Config selten benutzt wird. \nSie wird benutzt um \"Gun Klassen\" zu speichern. Standart:");
            this.getConfig().addDefault("config.guns.pistol.name", "Pistole");
            this.getConfig().addDefault("config.guns.pistol.items", "256#DAMAGE_ALL:1#KNOCKBACK*1");
            this.getConfig().addDefault("config.guns.pistol.icon", "256#DAMAGE_ALL:1#KNOCKBACK*1");
            this.getConfig().addDefault("config.guns.pistol.lore", "Die Pistole.");
            this.getConfig().addDefault("config.guns.pistol.speed", 1D);
            this.getConfig().addDefault("config.guns.pistol.durability", 50);
            this.getConfig().addDefault("config.guns.pistol.shoot_amount", 1);
            this.getConfig().addDefault("config.guns.pistol.knockback_multiplier", 1.1D);
            this.getConfig().addDefault("config.guns.pistol.permission_node", MinigamesAPI.getAPI().getPermissionGunPrefix() + ".pistol");
            
            this.getConfig().addDefault("config.guns.sniper.name", "Sniper");
            this.getConfig().addDefault("config.guns.sniper.items", "292#DAMAGE_ALL:1#KNOCKBACK*1");
            this.getConfig().addDefault("config.guns.sniper.icon", "292#DAMAGE_ALL:1#KNOCKBACK*1");
            this.getConfig().addDefault("config.guns.sniper.lore", "Die Sniper.");
            this.getConfig().addDefault("config.guns.sniper.speed", 0.5D);
            this.getConfig().addDefault("config.guns.sniper.durability", 10);
            this.getConfig().addDefault("config.guns.sniper.shoot_amount", 1);
            this.getConfig().addDefault("config.guns.sniper.knockback_multiplier", 3D);
            this.getConfig().addDefault("config.guns.sniper.permission_node", MinigamesAPI.getAPI().getPermissionGunPrefix() + ".sniper");
            
            this.getConfig().addDefault("config.guns.grenade.name", "Granatenwerfer");
            this.getConfig().addDefault("config.guns.grenade.items", "257#DAMAGE_ALL:1#KNOCKBACK*1");
            this.getConfig().addDefault("config.guns.grenade.icon", "257#DAMAGE_ALL:1#KNOCKBACK*1");
            this.getConfig().addDefault("config.guns.grenade.lore", "Der Granatenwerfer.");
            this.getConfig().addDefault("config.guns.grenade.speed", 0.1D);
            this.getConfig().addDefault("config.guns.grenade.durability", 10);
            this.getConfig().addDefault("config.guns.grenade.shoot_amount", 1);
            this.getConfig().addDefault("config.guns.grenade.knockback_multiplier", 2.5D);
            this.getConfig().addDefault("config.guns.grenade.permission_node", MinigamesAPI.getAPI().getPermissionGunPrefix() + ".grenade");
            
            this.getConfig().addDefault("config.guns.freeze.name", "Freeze Gun");
            this.getConfig().addDefault("config.guns.freeze.items", "258#DAMAGE_ALL:1#KNOCKBACK*1");
            this.getConfig().addDefault("config.guns.freeze.icon", "258#DAMAGE_ALL:1#KNOCKBACK*1");
            this.getConfig().addDefault("config.guns.freeze.lore", "Die Freeze Gun.");
            this.getConfig().addDefault("config.guns.freeze.speed", 0.8D);
            this.getConfig().addDefault("config.guns.freeze.durability", 5);
            this.getConfig().addDefault("config.guns.freeze.shoot_amount", 1);
            this.getConfig().addDefault("config.guns.freeze.knockback_multiplier", 0.5D);
            this.getConfig().addDefault("config.guns.freeze.permission_node", MinigamesAPI.getAPI().getPermissionGunPrefix() + ".freeze");
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
			this.classesFile = new File(this.pl.getDataFolder(), "guns.yml");
		}
		this.classesConfig = YamlConfiguration.loadConfiguration(this.classesFile);

		final InputStream defConfigStream = this.pl.getResource("guns.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.classesConfig.setDefaults(defConfig);
		}
	}
}
