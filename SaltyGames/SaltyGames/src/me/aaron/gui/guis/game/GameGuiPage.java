package me.aaron.gui.guis.game;

import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.games.GameGui;
import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.main.PluginManager;
import me.aaron.main.SaltyGames;
import me.aaron.utils.ClickAction;

public class GameGuiPage extends GameGui {

	public GameGuiPage(SaltyGames pl, GUIManager guiManager, int slots, String gameID, String key, String title) {
		super(pl, guiManager, slots, gameID, key, title);
		
		Map<Integer, ItemStack> hotBarButtons = pl.getPluginManager().getHotBarButtons();
		
		if (hotBarButtons.get(PluginManager.toGame) != null) {
			AButton gameGUI = new AButton(hotBarButtons.get(PluginManager.toGame).getData(), 1);
			ItemMeta meta = hotBarButtons.get(PluginManager.toGame).getItemMeta();
			gameGUI.setItemMeta(meta);
			gameGUI.setAction(ClickAction.OPEN_GAME_GUI);
			gameGUI.setArgs(gameID, GUIManager.MAIN_GAME_GUI);
			setLowerButton(gameGUI, PluginManager.toGame);
		}
	}
}
