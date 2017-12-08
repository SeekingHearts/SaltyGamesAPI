package me.aaron.gui.guis.game;

import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.games.Game;
import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.gui.guis.AGui;
import me.aaron.main.PluginManager;
import me.aaron.main.SaltyGames;
import me.aaron.utils.ClickAction;
import me.aaron.utils.ItemStackUtil;

public class GameGui extends AGui {

	public GameGui(SaltyGames plugin, GUIManager guiManager, int slots, String gameID, String key, String title) {
		super(plugin, guiManager, slots, new String[]{gameID, key}, title);
		
		Map<Integer, ItemStack> hotBarButtons = plugin.getPluginManager().getHotBarButtons();
		
		if (hotBarButtons.containsKey(PluginManager.exit)) {
			AButton exit = new AButton(hotBarButtons.get(PluginManager.exit).getData(), 1);
			ItemMeta meta = hotBarButtons.get(PluginManager.exit).getItemMeta();
			exit.setItemMeta(meta);
			exit.setAction(ClickAction.CLOSE);
			setLowerButton(exit, PluginManager.exit);
		}
		
		if (hotBarButtons.containsKey(PluginManager.toMain)) {
			AButton main = new AButton(hotBarButtons.get(PluginManager.toMain).getData(), 1);
			ItemMeta meta = hotBarButtons.get(PluginManager.toMain).getItemMeta();
			main.setItemMeta(meta);
			main.setAction(ClickAction.OPEN_MAIN_GUI);
			setLowerButton(main, PluginManager.toMain);
		}
	}
	
	public GameGui(SaltyGames plugin, Game game, int slots) {
		this(plugin, plugin.getPluginManager().getGuiManager(), slots, game.getGameID(),
				GUIManager.MAIN_GAME_GUI, plugin.lang.TITLE_GAME_GUI);
	}
	
	public void setHelpButton(List<String> list) {
		AButton help = new AButton(plugin.getNMS().addGlow(ItemStackUtil.createBookWithText(list)));
		help.setAction(ClickAction.NOTHING);
		
		setButton(help, inventory.getSize() - 1);
	}
}
