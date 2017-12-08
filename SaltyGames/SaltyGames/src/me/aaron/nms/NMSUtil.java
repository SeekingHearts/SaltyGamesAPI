package me.aaron.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSUtil {

	void updateInventoryTitle(Player p, String newTitle);
	
	void sendTitle(Player p, String title, String subTitle);
	
	void sendActionbar(Player p, String msg);
	
	void sendListHeader(Player p, String header);
	
	void sendListFooter(Player p, String footer);
	
	ItemStack removeGlow(ItemStack itm);
	
	ItemStack addGlow(ItemStack itm);
}
