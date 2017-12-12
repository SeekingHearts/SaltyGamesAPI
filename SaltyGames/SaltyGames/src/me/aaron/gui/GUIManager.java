package me.aaron.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import me.aaron.data.SGPlayer;
import me.aaron.events.EnterSaltyGamesEvent;
import me.aaron.games.GameGui;
import me.aaron.gui.buttons.AButton;
import me.aaron.gui.guis.AGui;
import me.aaron.gui.guis.MainGui;
import me.aaron.gui.guis.game.GameGuiPage;
import me.aaron.gui.guis.game.TopListPage;
import me.aaron.gui.shop.Page;
import me.aaron.gui.shop.ShopManager;
import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesLang;
import me.aaron.main.SaltyGamesSettings;
import me.aaron.nms.NMSUtil;
import me.aaron.utils.ClickAction;
import me.aaron.utils.Permission;

public class GUIManager {

	public static final String TOP_LIST_KEY_ADDON = "topList";
	private SaltyGames plugin;
	private Map<String, Map<String, GameGui>> gameGuis;
	private NMSUtil nms;
	private SaltyGamesLang lang;
	
	private MainGui mainGui;

	private int titleMessageSeconds = 3;

	private ShopManager shopManager;

	public static final String MAIN_GAME_GUI = "main";


	public GUIManager(SaltyGames plugin){
		this.plugin = plugin;
		this.nms = plugin.getNMS();
		this.lang = plugin.lang;
		this.gameGuis = new HashMap<>();

		this.mainGui = new MainGui(plugin, this);
		shopManager = new ShopManager(plugin, this);
		if(SaltyGamesSettings.tokensEnabled) mainGui.registerShop();
	}
	
	
	public void onInvClick(InventoryClickEvent event) {
		// get the uuid and check where the click should go
		UUID uuid = event.getWhoClicked().getUniqueId();
		boolean topInv = event.getSlot() == event.getRawSlot();
		if (mainGui.isInGui(uuid)) {
			event.setCancelled(true);
			if(topInv)mainGui.onInvClick(event);
			if(!topInv)mainGui.onBottomInvClick(event);
			return;
		}
		for (String gameID : gameGuis.keySet()) {
			Map<String, GameGui> guis = gameGuis.get(gameID);
			for (GameGui gui : guis.values()) {
				if (gui.isInGui(uuid)) {
					event.setCancelled(true);
					if(topInv)gui.onInvClick(event);
					if(!topInv)gui.onBottomInvClick(event);
					return;
				}
			}
		}
		SaltyGames.debug("Not in a SaltyGames GUI, checking shops now");
		if(shopManager.inShop(event.getWhoClicked().getUniqueId())){
			event.setCancelled(true);
			shopManager.onClick(event);
			return;
		}
		SaltyGames.debug("Not in a Shop...");
	}



	public void onInvClose(InventoryCloseEvent event) {
		// get the uuid and check where the event should go
		if(!(event.getPlayer() instanceof Player)) return;
		UUID uuid = event.getPlayer().getUniqueId();
		if(mainGui.isInGui(uuid)){
			mainGui.onInvClose(event);
			plugin.getPluginManager().restoreInventory((Player)event.getPlayer());
			return;
		}
		for(String gameID : gameGuis.keySet()){
			Map<String, GameGui> guis = gameGuis.get(gameID);
			for(GameGui gui : guis.values()){
				if(gui.isInGui(uuid)){
					gui.onInvClose(event);
					plugin.getPluginManager().restoreInventory((Player)event.getPlayer());
					return;
				}
			}
		}
		if(SaltyGames.debug)Bukkit.getConsoleSender().sendMessage("Not in a SaltyGames GUI, checking shops now");
		if(shopManager.inShop(event.getPlayer().getUniqueId())){
			shopManager.onInvClose(event);
			plugin.getPluginManager().restoreInventory((Player)event.getPlayer());
			return;
		}
		if(SaltyGames.debug)Bukkit.getConsoleSender().sendMessage("Not in a Shop...");
	}
	
	public boolean isInGUI(UUID uuid){
		if(isInMainGUI(uuid)) return true;
		if(isInGameGUI(uuid)) return true;
		return false;
	}

	public boolean isInMainGUI(UUID uuid){
		if(mainGui.isInGui(uuid)){
			return true;
		}
		return false;
	}

	public boolean isInGameGUI(UUID uuid){
		for(String gameID : gameGuis.keySet()){
			if(isInGameGUI(uuid,gameID)) return true;
		}
		return false;
	}

	public boolean isInGameGUI(UUID uuid, String gameID){
		Map<String, GameGui> guis = gameGuis.get(gameID);
		for(GameGui gui : guis.values()){
			if(gui.isInGui(uuid)){
				return true;
			}
		}
		return false;
	}
	
	public boolean openGameGui(Player whoClicked, String... args) {
		if(!plugin.getPluginManager().hasSavedContents(whoClicked.getUniqueId())){
			EnterSaltyGamesEvent enterEvent = new EnterSaltyGamesEvent(whoClicked, args[0], args[1]);
			if(!enterEvent.isCancelled()){
				plugin.getPluginManager().saveInventory(whoClicked);
			} else {
				whoClicked.sendMessage("A game was canceled with the reason: " + enterEvent.getCancelMessage());
			}
		}

		if(args.length != 2) {
			Bukkit.getConsoleSender().sendMessage("unknown number of arguments in GUIManager.openGameGui");
			if(!isInGUI(whoClicked.getUniqueId()) && !plugin.getPluginManager().isInGame(whoClicked.getUniqueId())) plugin.getPluginManager().restoreInventory(whoClicked);
			return false;
		}

		String gameID = args[0], key = args[1];
		if (whoClicked.hasPermission(Permission.OPEN_ALL_GAME_GUI.getPermissions())|| whoClicked.hasPermission(Permission.OPEN_GAME_GUI.getPermission(gameID))) {
			AGui gui = gameGuis.get(gameID).get(key);
			SaltyGames.openingNewGUI = true;
			boolean opened = gui.open(whoClicked);
			SaltyGames.openingNewGUI = false;
			if(opened){
				nms.updateInventoryTitle(whoClicked, gui.getTitle().replace("%game%", plugin.getPluginManager().getGame(gameID).getGameLang().PLAIN_NAME).replace("%player%", whoClicked.getName()));
			} else {
				if(whoClicked.getOpenInventory() != null){
					whoClicked.closeInventory();
				}
				plugin.getPluginManager().restoreInventory(whoClicked);
			}
			return opened;
		} else {
			if(isInGUI(whoClicked.getUniqueId())){
				// player is in main or in a game gui of a multi-player game
				sentInventoryTitleMessage(whoClicked, plugin.lang.TITLE_NO_PERM, null);
			} else {
				if(whoClicked.getOpenInventory() != null){
					whoClicked.closeInventory();
				}
				plugin.getPluginManager().restoreInventory(whoClicked);
			}
			whoClicked.sendMessage(lang.PREFIX + lang.CMD_NO_PERM);
			return false;
		}
	}

	
	public boolean openMainGui(Player whoClicked) {
		if(!whoClicked.hasPermission(Permission.USE.getPermissions())) {
			whoClicked.sendMessage(lang.PREFIX + lang.CMD_NO_PERM);
			return false;
		}

		if(!plugin.getPluginManager().hasSavedContents(whoClicked.getUniqueId())){
			EnterSaltyGamesEvent enterEvent = new EnterSaltyGamesEvent(whoClicked, "main");
			if(!enterEvent.isCancelled()){
				plugin.getPluginManager().saveInventory(whoClicked);
			} else {
				whoClicked.sendMessage("A game was canceled with the reason: " + enterEvent.getCancelMessage());
				return false;
			}
		}

		SaltyGames.openingNewGUI = true;
		boolean open = mainGui.open(whoClicked);
		SaltyGames.openingNewGUI = false;
		if(open) return true;

		// the gui didn't open. Make sure to restore all inventory content
		if(whoClicked.getOpenInventory() != null){
			whoClicked.closeInventory();
		}
		plugin.getPluginManager().restoreInventory(whoClicked);

		return false;
	}


	
	public void registerGameGUI(GameGui gui){
		if(gui.getArgs() == null || gui.getArgs().length != 2){
			Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Error while registering a gui");
			Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + "   missing args");
			return;
		}

		String[] args = gui.getArgs();

		gameGuis.computeIfAbsent(args[0], k -> new HashMap<>());

		gameGuis.get(args[0]).put(args[1], gui);
		SaltyGames.debug("registered gamegui: " + args[0] + ", " + args[1]);
	}

	
	public void registerMainGameGUI(GameGui gui, ItemStack button, String... subCommand){
		if(gui.getArgs() == null || gui.getArgs().length != 2){
			Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + " Error while registering a gui");
			Bukkit.getConsoleSender().sendMessage(lang.PREFIX + ChatColor.RED + "   missing args");
			return;
		}

		String[] args = gui.getArgs();

		gameGuis.computeIfAbsent(args[0], k -> new HashMap<>());

		gameGuis.get(args[0]).put(args[1], gui);
		SaltyGames.debug("registered gamegui: " + args[0] + ", " + args[1]);
		AButton gameButton = new AButton(button.getData(), 1);
		gameButton.setItemMeta(button.getItemMeta());
		gameButton.setAction(ClickAction.OPEN_GAME_GUI);
		gameButton.setArgs(args[0], args[1]);
		mainGui.setButton(gameButton);
		plugin.getMainCommand().registerSubCommands(args[0], subCommand);
	}

	@Deprecated
	public void registerGameGUI(String gameID, String key, GameGui gui, ItemStack button, String... subCommand){
		Bukkit.getConsoleSender().sendMessage(lang.PREFIX + " Your version of " + plugin.getPluginManager().getGame(gameID).getGameLang().PLAIN_NAME + " is outdated!");
		if(key.equals(MAIN_GAME_GUI)){
			registerMainGameGUI(gui, button, subCommand);
		} else {
			registerGameGUI(gui);
		}
	}

	@Deprecated
	public void registerGameGUI(String gameID, String key, GameGui gui){
		Bukkit.getConsoleSender().sendMessage(lang.PREFIX + " Your version of " + plugin.getPluginManager().getGame(gameID).getGameLang().PLAIN_NAME + " is outdated!");
		registerGameGUI(gui);
	}

	@Deprecated
	public void registerTopList(String gameID, String key, TopListPage gui){
		Bukkit.getConsoleSender().sendMessage(lang.PREFIX + " Your version of " + plugin.getPluginManager().getGame(gameID).getGameLang().PLAIN_NAME + " is outdated!");
		registerGameGUI(gui);
	}

	public AGui getCurrentGui(UUID uuid){
		if(mainGui.isInGui(uuid)){
			return mainGui;
		}
		for(String gameID : gameGuis.keySet()){
			for(GameGui gui : gameGuis.get(gameID).values()){
				if(gui.isInGui(uuid)){
					return gui;
				}
			}
		}
		return shopManager.getShopGui(uuid);
	}

	public MainGui getMainGui(){
		return this.mainGui;
	}

	public void removePlayer(UUID uuid){

		for(String gameID : gameGuis.keySet()){
			Map<String, GameGui> guis = gameGuis.get(gameID);
			for(GameGui gui : guis.values()){
				gui.removePlayer(uuid);
			}
		}

		this.mainGui.removePlayer(uuid);

	}

	public AGui getGameGui(String gameID, String key){
		return gameGuis.get(gameID) == null? null : gameGuis.get(gameID).get(key);
	}

	public void sentInventoryTitleMessage(Player player, String message, String gameID) {
		String currentTitle = plugin.lang.TITLE_MAIN_GUI.replace("%player%", player.getName());
		AGui gui = getCurrentGui(player.getUniqueId());
		if (gui != null) {
			if (gui instanceof GameGuiPage) {
				currentTitle = ((GameGuiPage) gui).getTitle().replace("%player%", player.getName());
			} else if (gui instanceof GameGui) {
				currentTitle = plugin.lang.TITLE_GAME_GUI.replace("%game%", plugin.getPluginManager().getGame(gameID).getGameLang().PLAIN_NAME).replace("%player%", player.getName());
			} else if (gui instanceof Page){
				currentTitle = plugin.lang.SHOP_TITLE_PAGE_SHOP.replace("%page%", String.valueOf(((Page)gui).getPage() + 1));
			}
		}
		plugin.getPluginManager().startTitleTimer(player, currentTitle, titleMessageSeconds);
		plugin.getNMS().updateInventoryTitle(player, message);
	}

	public boolean openShopPage(Player whoClicked, String[] args) {
		return shopManager.openShopPage(whoClicked, args);
	}

	public ShopManager getShopManager() {
		return shopManager;
	}

	public void updateTokens(SGPlayer gbPlayer) {
		mainGui.updateTokens(gbPlayer);
		shopManager.updateTokens(gbPlayer);
	}
	
}
