package me.aaron.games;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.aaron.main.Module;
import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesLang;
import me.aaron.nms.NMSUtil;
import me.aaron.utils.FileUtils;

public abstract class Game {

	protected static boolean debug = false;
	
	protected SaltyGames sg;
	
	protected FileConfiguration config;
	
	protected Module mod;
	
	protected File dataFolder;
	protected File configFile;
	
	protected GameManager gameMgr;
	
	protected GameSettings gameSettings;
	
	protected GameLang gameLang;
	
	protected SaltyGamesLang sgLang;
	
	protected NMSUtil nms;
	
	protected String[] subCommands;
	
	protected Game(SaltyGames sg, String gameID, String[] subCommands) {
		this.mod = sg.getGameReg().getModule(gameID);
		Validate.notNull(mod, " Du kannst kein Spiel inizialisieren, ohne es zu zuvor registriert zu haben!");
		
		this.subCommands = subCommands;
		this.sg = sg;
		this.sgLang = sg.lang;
		this.nms = sg.getNMS();
		this.gameSettings = new GameSettings();
	}
	
	public abstract void onDisable();
	
	public void onEnable() {
		SaltyGames.debug(" spiel wird gestartet: " + mod.getModuleID());
		loadConfig();
		
		loadSettings();
		loadLanguage();
		
		init();
		
		loadGameManager();
		
		hook();
	}
	
	public abstract void init();
	public abstract void loadSettings();
	public abstract void loadLanguage();
	public abstract void loadGameManager();
	
	public boolean loadConfig() {
		SaltyGames.debug(" config wird geladen... (" + mod.getModuleID() + ")");
		configFile = new File(sg.getDataFolder()
				+ File.separator + "games" 
				+ File.separator + getGameID()
				+ File.separator + "config.yml");
		
		if (!configFile.exists()) {
			SaltyGames.debug(" standart config fehlt in GB ordner (" + mod.getModuleID() + ")");
			configFile.getParentFile().mkdirs();
			if (mod.getExternalPlugin() != null) {
				FileUtils.copyExternalResources(sg, mod);
			} else {
				sg.saveResource("games"
						+ File.separator + getGameID()
						+ File.separator + "config.yml", false);
			}
		}
		
		this.dataFolder = new File(sg.getDataFolder()
				+ File.separator + "games"
				+ File.separator + getGameID()
				+ File.separator);
		
		try {
			this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void hook() {
		
	}

	public SaltyGames getSg() {
		return sg;
	}

	public FileConfiguration getConfig() {
		return config;
	}
	
	public String getGameID() {
		return mod.getModuleID();
	}

	public Module getMod() {
		return mod;
	}

	public File getDataFolder() {
		return dataFolder;
	}

	public GameManager getGameMgr() {
		return gameMgr;
	}

	public GameSettings getGameSettings() {
		return gameSettings;
	}

	public GameLang getGameLang() {
		return gameLang;
	}
	
}
