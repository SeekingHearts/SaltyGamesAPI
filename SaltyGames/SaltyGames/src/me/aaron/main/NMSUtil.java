package me.aaron.main;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSUtil {
	
	void updateInventoryTitle(Player p, String nTitle);
	
	void sendTitle(Player p, String title, String subtitle);
	
	void sendActionbar(Player p, String msg);
	
	void sendListHeader(Player p, String header);
	
	void sendListFooter(Player p, String footer);
	
	ItemStack removeGlow(ItemStack itm);

	ItemStack addGlow(ItemStack itm);
}
