package me.aaron.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.NMSUtil_1_8_R3;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.greatmancode.craftconomy3.Common;

import me.aaron.commands.AdminCommand;
import me.aaron.commands.MainCommand;
import me.aaron.data.DataBase;
import me.aaron.data.FileDB;
import me.aaron.data.MysqlDB;
import me.aaron.games.Game;
import me.aaron.games.GameLang;
import me.aaron.gui.GUIManager;
import me.aaron.input.HandleInvitations;
import me.aaron.input.HandleInviteInput;
import me.aaron.listeners.EnterSaltyGamesListener;
import me.aaron.listeners.LeftSaltyGamesListener;
import me.aaron.nms.NMSUtil;
import me.aaron.utils.FileUtils;

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
	private AdminCommand adminCommand;

	private DataBase db;
	
	private LeftSaltyGamesListener leftSGListener;
	private EnterSaltyGamesListener enterSGListener;

	@Deprecated
	public static boolean playSounds = SaltyGamesSettings.playSounds;

	private GameReg gameRegistery;

	@Override
	public void onEnable() {
		
		if (!setupNMS()) {
			getLogger().severe(" Problem mit der Server Version. Wende dich an Aaron!");
			getLogger().severe(" Das Plugin wird nun deaktivert!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		this.gameRegistery = new GameReg(this);
		
		new Module(this, MODULE_SALTYGAMES);
		
		if (!reload()) {
			getLogger().severe(" Problem mit dem Laden des Plugins! Plugin wurde deaktiviert!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		registerGames();
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new PlaceholderAPIHook(this, MODULE_SALTYGAMES);
			Bukkit.getConsoleSender().sendMessage(lang.PREFIX + " Hokked into PlaceholderAPI!");
		}
		
		econ.getCurrencyManager().addCurrency("LegendKingdomDollars", "LKD", "LKDs", "LKD", "LKDs", true);
		
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				debug(" überprüfung wird durchgeführt...");
				runLateChecks();
			}
		}.runTask(this);
		
	}
	
	private void runLateChecks() {
		if (SaltyGamesSettings.checkInventoryLenght) {
			info(ChatColor.RED + " Dein Server kann nicht mehr als 32 Zeichen als Inventar Titel haben!");
			info(ChatColor.RED + " SaltyGames wird zu lange titel kürzen (markiert mit '...') um Fehler zu vermeiden.");
			info(ChatColor.RED + " Um dies zu beheben: ('...'), erstelle eine neue Language Datein mit kürzeren Titeln!");
		}
		
		adminCommand.printIncompleteLangFilesInfo();
		
		if (PluginManager.gamesRegistered == 0) {
			info(ChatColor.RED + "+ - + - + - + - + - + - + - + - + - + - + - + - + - + - +");
			info(ChatColor.RED + " Es sind keine registrierten Spiele vorhanden!");
			info(ChatColor.RED + "+ - + - + - + - + - + - + - + - + - + - + - + - + - + - +");
		} else {
			info(ChatColor.GREEN + " " + PluginManager.gamesRegistered + " Spiele wurden registriert. Viel Spaß =)");
			info(ChatColor.GOLD + " Und danke an eueren Developer §eAaron (TheSaltyOne, Sistogy) §6^^");
		}
		
	}
	
	private void registerGames() {
		
		
		
	}
	
	public boolean reload() {
		
		if (!reloadConfiguration()) {
			getLogger().severe(" Fehler beim Laden der Config!");
			return false;
		}
		
		FileUtils.copyDefaultLanguageFiles();
		
		this.lang = new SaltyGamesLang(this);
		this.api = new SaltyGamesAPI(this);
		
		SaltyGamesSettings.loadSettings(this);
		
		if (pManager != null) {
			pManager.shutDown();
			HandlerList.unregisterAll(pManager);
			pManager = null;
		}
		
		if (SaltyGamesSettings.useMySQL) {
			if (db != null)
				db.save(false);
			
			this.db = new MysqlDB(this);
			if (!db.load(false)) {
				getLogger().log(Level.SEVERE, " Ändere auf Datei Speicherung...");
				SaltyGamesSettings.useMySQL = false;
				db = null;
			}
		}
		
		if (!SaltyGamesSettings.useMySQL) {
			
			if (db != null)
				db.save(false);
			
			this.db = new FileDB(this);
			if (!db.load(false)) {
				getLogger().log(Level.SEVERE, " Irgendwas lief schief mit dem Data File");
				return false;
			}
		}
		
		if (SaltyGamesSettings.econEnabled) {
			if (!setupEconomy()) {
				getLogger().log(Level.SEVERE, "Kein CraftConomy gefunden!");
				getLogger().log(Level.SEVERE, "Obwohl es in der Config aktiviert ist...");
				SaltyGamesSettings.econEnabled = false;
				return false;
			}
		}
		reloadListeners();
		
		pManager = new PluginManager(this);
		pManager.setGuiManager(new GUIManager(this));
		
		pManager.setHandleInviteInput(new HandleInviteInput(this));
		pManager.setHandleInvitations(new HandleInvitations(this));
		
		mainCommand = new MainCommand(this);
		adminCommand = new AdminCommand(this);
		this.getCommand("saltygames").setExecutor(mainCommand);
		this.getCommand("saltygamesadmin").setExecutor(adminCommand);
		
		pManager.loadPlayers();
		
		return true;
	}
	
	private void reloadListeners() {
		if (leftSGListener != null) {
			HandlerList.unregisterAll(leftSGListener);
			leftSGListener = null;
		}
		leftSGListener = new LeftSaltyGamesListener(this);
		
		if (enterSGListener != null) {
			HandlerList.unregisterAll(enterSGListener);
			enterSGListener = null;
		}
		enterSGListener = new EnterSaltyGamesListener(this);
	}
	
	public DataBase getDataBase() {
		return this.db;
	}
	
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null)
			return false;
		
		RegisteredServiceProvider<Common> rsp = getServer().getServicesManager().getRegistration(Common.class);
		if (rsp == null)
			return false;
		econ = rsp.getProvider();
		return econ != null;
	}
	
	private boolean setupNMS() {
		nms = new NMSUtil_1_8_R3();
		SaltyGamesSettings.delayedInventoryUpdate = true;
		
		return nms != null;
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
	
	@Override
	public void onDisable() {
		if (pManager != null)
			pManager.shutDown();
		if (db != null) {
			db.onShutDown();
		}
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
	
	public boolean reloadConfiguration() {
		
		File con = new File(this.getDataFolder().toString() + File.separatorChar + "config.yml");
		if (!con.exists()) {
			this.saveResource("config.yml", false);
		}
		
		try {
			this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(con)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean wonTokens(UUID p, int tokens, String gameID) {
		if (!SaltyGamesSettings.tokensEnabled)
			return false;
		return pManager.wonTokens(p, tokens, gameID);
	}
	
	public SaltyGamesAPI getAPI() {
		return api;
	}
	
	private String getOriginalGameName(String gameID) {
		GameLang gl = getPluginManager().getGame(gameID).getGameLang();
		if (gl != null)
			return gl.DEFAULT_PLAIN_NAME;
		
		return "Other (custom game)";
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
	
	public Language getLanguage(Module mod) {
		return getLanguage(mod.getModuleID());
	}
	
	public Language getLanguage(String modID) {
		if (modID.equals(MODULE_SALTYGAMES)) {
			return lang;
		}
		
		Game game = getPluginManager().getGame(modID);
		
		if (game == null)
			return null;
		
		return game.getGameLang();
	}
}