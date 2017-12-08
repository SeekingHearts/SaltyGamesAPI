package me.aaron.main;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

public class SaltyGamesSettings {

	public static boolean exceptInvitesWithoutPlayPermission = false;

	public static boolean playSounds = true;

	public static Sound successfulClick, unsuccessfulClick;

	public static boolean checkInventoryLenght = false;

	public static boolean useMySQL = false;

	public static boolean delayedInventoryUpdate = false;

	public static int timeForPlayerInput = 30;
	public static int timeForInvitations = 30;

	public static boolean econEnabled = false;
	public static boolean tokensEnabled = false;

	public static boolean keepArmor = false;

	public static boolean sendInviteClickMessage = true;

	public static boolean bStats = true;

	public static boolean hubMode = false;
	
	public static boolean closeInventoryOnDmg = true;
	
	public static int autoSaveInterval = 5;
	
	
	public static void loadSettings(SaltyGames pl) {
		FileConfiguration config = pl.getConfig();
		
		sendInviteClickMessage = config.getBoolean("settings.invitations.clickMessage.enabled", true);
        tokensEnabled = config.getBoolean("economy.tokens.enabled", false);
        econEnabled = config.getBoolean("economy.enabled", false);
        playSounds = config.getBoolean("guiSettings.playSounds", true);
        timeForInvitations = config.getInt("timeForInvitations", 30);
        timeForPlayerInput = config.getInt("timeForPlayerInput", 30);
        useMySQL = config.getBoolean("mysql.enabled", false);
        exceptInvitesWithoutPlayPermission = config.getBoolean("settings.exceptInvitesWithoutPlayPermission", false);
        bStats = config.getBoolean("settings.bStats", true);
        closeInventoryOnDmg = config.getBoolean("settings.closeInventoryOnDamage", true);

        autoSaveInterval = config.getInt("settings.autoSaveInterval", 5);

        keepArmor = config.getBoolean("settings.keepArmor", false);
        
        
        try {
			
        	successfulClick = Sound.valueOf(config.getString("guiSettings.standardSounds.successfulClick", "CLICK"));
        	
		} catch (IllegalArgumentException e) {
			successfulClick = Sound.CLICK;
		}
        
        try {
			
        	unsuccessfulClick = Sound.valueOf(config.getString("guiSettings.standardSounds.unsuccessfulClick", "VILLAGER_NO"));
        	
		} catch (IllegalArgumentException e) {
			unsuccessfulClick = Sound.VILLAGER_NO;
		}
        
        checkInventoryLenght = checkInventoryTitleLenght();
        		
        hubMode = config.getBoolean("hubMode.enabled", false);
        
	}
	
	private static boolean checkInventoryTitleLenght() {
		try {
			Inventory inv = Bukkit.createInventory(null, 27, "Dieser Titel ist länger als 32 Zeichen!");
		} catch (Exception e) {
			return true;
		}
		return false;
	}

}
