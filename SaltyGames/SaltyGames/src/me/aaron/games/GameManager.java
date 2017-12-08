package me.aaron.games;

import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public interface GameManager {

	boolean onInventoryClick(InventoryClickEvent e);
	boolean onInventoryClose(InventoryCloseEvent e);
	
	boolean isInGame(UUID uuid);
	
	int startGame(Player[] ps, boolean playSounds, String... args);
	
	void removeFromGame(UUID uuid);
	
	void loadGameRules(ConfigurationSection buttonSec, String buttonID);
	
	Map<String, ? extends GameRule> getGameRules();
	
}
