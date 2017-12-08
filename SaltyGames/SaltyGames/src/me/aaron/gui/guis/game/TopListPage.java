package me.aaron.gui.guis.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.aaron.data.DataBase;
import me.aaron.data.SaveType;
import me.aaron.gui.GUIManager;
import me.aaron.main.SaltyGames;
import me.aaron.utils.NumberUtil;

public class TopListPage extends GameGuiPage {

	private SaveType saveTyp;
	private ArrayList<String> skullLore;
	
	public TopListPage(SaltyGames pl, GUIManager guiManager, int slots, String gameID, String key, String title, SaveType saveType, ArrayList<String> skullLore) {
		super(pl, guiManager, slots, gameID, key, title);
		this.saveTyp = saveType;
		this.skullLore = skullLore;
	}
	
	@Override
	public boolean open(Player player) {
		return super.open(player);
	}
	
	public void update() {
		
		ArrayList<DataBase.Stat> topList = plugin.getDataBase().getTopList(args[0], args[1].replace(GUIManager.TOP_LIST_KEY_ADDON, ""), saveTyp, inventory.getSize());
		
		DataBase.Stat stat;
		ItemStack skull;
		SkullMeta meta;
		
		ArrayList<String> skullLore;
		OfflinePlayer p;
		
		for (int rank = 0; rank < topList.size();) {
			stat = topList.get(rank);
			rank ++;
			skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			meta = (SkullMeta) skull.getItemMeta();
			skullLore = new ArrayList<>(this.skullLore);
			
			p = Bukkit.getOfflinePlayer(stat.getUuid());
			
			if (p == null) {
				SaltyGames.debug(" UUID konnte keinem Spieler zugeordnet werden, während die Top Liste geladen wurde.");
				continue;
			}
			
			if (!p.hasPlayedBefore() || p.getName() == null) {
				continue;
			}
			
			for (int i = 0; i < skullLore.size(); i++) {
				skullLore.set(i, skullLore.get(i).replace("%player%", p.getName()).replace("%rank%", String.valueOf(rank)));
				
			}
			
			switch(saveTyp) {
			case TIME_LOW:
			case TIME_HIGH:
				
				int seconds = (int) stat.getValue();
				int min = seconds / 60;
				int secsLeft = seconds % 60;
				
				String time = (min < 9 ? "0" + String.valueOf(min): String.valueOf(min)) + ":" + (secsLeft > 9 ? String.valueOf(secsLeft): "0" + String.valueOf(secsLeft));
				
				for (int i = 0; i < skullLore.size(); i++) {
					skullLore.set(i, skullLore.get(i).replace("%time%", time));
				}
				break;
			case SCORE:
				for (int i = 0; i < skullLore.size(); i++) {
					skullLore.set(i, skullLore.get(i).replace("%score%", String.format("%.0f", stat.getValue())));
				}
				break;
			case HIGH_NUMBER_SCORE:
				for (int i = 0; i < skullLore.size(); i++) {
					skullLore.set(i, skullLore.get(i).replace("%score%", NumberUtil.convertHugeNumber(stat.getValue())));
				}
				break;
			case WINS:
				for (int i = 0; i < skullLore.size(); i++) {
					skullLore.set(i, skullLore.get(i).replace("%wins", String.valueOf((int) stat.getValue())));
				}
				break;
			}
			
			meta.setOwner(p.getName());
			meta.setLore(skullLore);
			meta.setDisplayName(ChatColor.BLUE + p.getName());
			
			skull.setItemMeta(meta);
			
			inventory.setItem(rank - 1, skull);
		}
		
	}
	
}