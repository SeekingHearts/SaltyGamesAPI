package me.aaron.games;

import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.gui.guis.AGui;
import me.aaron.main.PluginManager;
import me.aaron.main.SaltyGames;
import me.aaron.utils.ClickAction;
import me.aaron.utils.ItemStackUtil;

public class GameGui extends AGui {

	private SaltyGames pl;
	
	public GameGui(SaltyGames pl, GUIManager guiManager, int slots, String gameID, String key, String title) {
		super(pl, guiManager, slots, new String[]{gameID, key}, title);
	
		Map<Integer, ItemStack> hotBarButtons = pl.getPluginManager().getHotBarButtons();
	
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
	
	public GameGui(SaltyGames pl, Game game, int slots) {
		this(pl, pl.getPluginManager().getGuiManager(), slots, game.getGameID(), GUIManager.MAIN_GAME_GUI, pl.lang.TITLE_GAME_GUI);
	}
	
	public void setHelpButton(List<String> list) {
		AButton help = new AButton(pl.getNMS().addGlow(ItemStackUtil.createBookWithText(list)));
		help.setAction(ClickAction.NOTHING);
		
		setButton(help, inventory.getSize() - 1);
	}
}
