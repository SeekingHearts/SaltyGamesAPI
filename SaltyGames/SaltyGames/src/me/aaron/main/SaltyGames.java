package me.aaron.main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.commands.MainCommand;
import me.aaron.data.DataBase;
import me.aaron.games.Game;

import com.greatmancode.craftconomy3.Common;

public class SaltyGames extends JavaPlugin {

	public static final String MODULE_SALTYGAMES = "saltygames";

	public static final String MODULE_CONNECTFOUR = "connectfour";
	public static final String MODULE_COOKIECLICKER = "cookieclicker";
	public static final String MODULE_MATCHIT = "matchit";

	public static boolean debug = false;

	public static boolean openingNewGUI = false;

	public static final int GAME_STARTED = 1, GAME_NOT_STARTED_ERROR = 0, GAME_NOT_ENOUGH_MONEY = 2,
			GAME_NOT_ENOUGH_MONEY_1 = 3, GAME_NOT_ENOUGH_MONEY_2 = 4;

	private FileConfiguration config;

	private NMSUtil nms;

	private SaltyGamesAPI api;

	public static Common econ = null;

	public SaltyGamesLang lang;

	private PluginManager pManager;

	private MainCommand mainCommand;
//	private InfiniteCommand adminCommand;

	private DataBase db;

	@Deprecated
	public static boolean playSounds = SaltyGamesSettings.playSounds;

	private GameReg gameRegistery;

	public DataBase getDataBase() {
		return this.db;
	}

	public PluginManager getPluginManager() {
		return pManager;
	}

	public static void debug(String msg) {
		if (debug)
			Bukkit.getConsoleSender().sendMessage(msg);
	}

	public GameReg getGameReg() {
		return gameRegistery;
	}

	public NMSUtil getNMS() {
		return nms;
	}

	public void info(String msg) {
		Bukkit.getConsoleSender().sendMessage(lang.PREFIX + msg);
	}

	public void warning(String msg) {
		getLogger().warning(msg);

	}
	
	public MainCommand getMainCommand() {
		return this.mainCommand;
	}
	
	@Override
	public void onEnable() {
		
		econ.getCurrencyManager().addCurrency("LegendKingdomDollars", "LKD", "LKDs", "LKD", "LKDs", true);
	}
	
	@Override
	public void onDisable() {
	}
	
	@Override
	public FileConfiguration getConfig() {
		return config;
	}
	
	public FileConfiguration getConfig(Module module) {
		return getConfig(module.getModuleID());
	}
	
	public FileConfiguration getConfig(String moduleID) {
		if (moduleID.equals(MODULE_SALTYGAMES))
			return getConfig();
		
		Game game = getPluginManager().getGame(moduleID);
		
		if (game == null)
			return null;
		
		return game.getConfig();
	}
	
}