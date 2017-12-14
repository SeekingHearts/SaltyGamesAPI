package main.me.aaron.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import main.me.aaron.MinigamesAPI;

public class ShopConfig {

	private FileConfiguration classesConfig = null;
	private File classesFile = null;
	private JavaPlugin pl = null;

	public ShopConfig(final JavaPlugin pl, final boolean custom) {
		this.pl = pl;

		if (!custom) {
			getConfig().options().header("Diese Datei wird benutzt um Shop Items zu speichern. Standart Shop Items:");
			getConfig().addDefault("config.shop_items.coin_boost2.name", "Münzen Boost * 2");
            getConfig().addDefault("config.shop_items.coin_boost2.enabled", true);
            getConfig().addDefault("config.shop_items.coin_boost2.uses_items", false);
            getConfig().addDefault("config.shop_items.coin_boost2.items", "388*1");
            getConfig().addDefault("config.shop_items.coin_boost2.icon", "388*1");
            getConfig().addDefault("config.shop_items.coin_boost2.lore", "Alle Gewinner erhalten doppelt so viel Geld.");
            getConfig().addDefault("config.shop_items.coin_boost2.requires_money", true);
            getConfig().addDefault("config.shop_items.coin_boost2.requires_permission", false);
            getConfig().addDefault("config.shop_items.coin_boost2.money_amount", 1500);
            getConfig().addDefault("config.shop_items.coin_boost2.permission_node", MinigamesAPI.getAPI().getPermissionShopPrefix() + ".coin_boost2");
            
            getConfig().addDefault("config.shop_items.coin_boost3.name", "Münzen Boost * 3");
            getConfig().addDefault("config.shop_items.coin_boost3.enabled", true);
            getConfig().addDefault("config.shop_items.coin_boost3.uses_items", false);
            getConfig().addDefault("config.shop_items.coin_boost3.items", "388*2");
            getConfig().addDefault("config.shop_items.coin_boost3.icon", "388*2");
            getConfig().addDefault("config.shop_items.coin_boost3.lore", "Alle Gewinner erhalten dreimal so viel Geld.");
            getConfig().addDefault("config.shop_items.coin_boost3.requires_money", true);
            getConfig().addDefault("config.shop_items.coin_boost3.requires_permission", false);
            getConfig().addDefault("config.shop_items.coin_boost3.money_amount", 3000);
            getConfig().addDefault("config.shop_items.coin_boost3.permission_node", MinigamesAPI.getAPI().getPermissionShopPrefix() + ".coin_boost3");
            
            getConfig().addDefault("config.shop_items.coin_boost2_solo.name", "Münzen Boost * 2 Solo");
            getConfig().addDefault("config.shop_items.coin_boost2_solo.enabled", true);
            getConfig().addDefault("config.shop_items.coin_boost2_solo.uses_items", false);
            getConfig().addDefault("config.shop_items.coin_boost2_solo.items", "264*1");
            getConfig().addDefault("config.shop_items.coin_boost2_solo.icon", "264*1");
            getConfig().addDefault("config.shop_items.coin_boost2_solo.lore", "Du erhälst doppelt so viel Geld.");
            getConfig().addDefault("config.shop_items.coin_boost2_solo.requires_money", true);
            getConfig().addDefault("config.shop_items.coin_boost2_solo.requires_permission", false);
            getConfig().addDefault("config.shop_items.coin_boost2_solo.money_amount", 1000);
            getConfig().addDefault("config.shop_items.coin_boost2_solo.permission_node", MinigamesAPI.getAPI().getPermissionShopPrefix() + ".coin_boost2_solo");
            
            getConfig().addDefault("config.shop_items.coin_boost3_solo.name", "Münzen Boost * 3 Solo");
            getConfig().addDefault("config.shop_items.coin_boost3_solo.enabled", true);
            getConfig().addDefault("config.shop_items.coin_boost3_solo.uses_items", false);
            getConfig().addDefault("config.shop_items.coin_boost3_solo.items", "264*2");
            getConfig().addDefault("config.shop_items.coin_boost3_solo.icon", "264*2");
            getConfig().addDefault("config.shop_items.coin_boost3_solo.lore", "Du erhälst dreimal so viel Geld.");
            getConfig().addDefault("config.shop_items.coin_boost3_solo.requires_money", true);
            getConfig().addDefault("config.shop_items.coin_boost3_solo.requires_permission", false);
            getConfig().addDefault("config.shop_items.coin_boost3_solo.money_amount", 2000);
            getConfig().addDefault("config.shop_items.coin_boost3_solo.permission_node", MinigamesAPI.getAPI().getPermissionShopPrefix() + ".coin_boost3_solo");
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
			this.classesFile = new File(this.pl.getDataFolder(), "shop.yml");
		}
		this.classesConfig = YamlConfiguration.loadConfiguration(this.classesFile);

		final InputStream defConfigStream = this.pl.getResource("shop.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.classesConfig.setDefaults(defConfig);
		}
	}
}
