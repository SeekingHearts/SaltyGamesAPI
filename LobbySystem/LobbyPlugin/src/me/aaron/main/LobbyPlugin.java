package me.aaron.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.commands.BuildModeCmd;
import me.aaron.commands.CheckArrays;
import me.aaron.commands.FastbowCmd;
import me.aaron.commands.GetBoosterCmd;
import me.aaron.commands.GmCmds;
import me.aaron.commands.HubCmd;
import me.aaron.commands.InvSeeCmd;
import me.aaron.commands.PvPOff;
import me.aaron.commands.SetBoosterBlocksCmd;
import me.aaron.commands.SetHubCmd;
import me.aaron.commands.SetWarps;
import me.aaron.commands.SilentLobby;
import me.aaron.commands.SpyCmd;
import me.aaron.listeners.BlockBreakPlaceListener;
import me.aaron.listeners.GameModeListener;
import me.aaron.listeners.HubBoostListener;
import me.aaron.listeners.HungerListener;
import me.aaron.listeners.InventoryListener;
import me.aaron.listeners.ItemDropListener;
import me.aaron.listeners.JoinListener;
import me.aaron.listeners.LeaveListener;
import me.aaron.listeners.PlayerChangeWorld;
import me.aaron.listeners.PlayerChatListener;
import me.aaron.listeners.PlayerDamageListener;
import me.aaron.listeners.PlayerDeathListener;
import me.aaron.listeners.PlayerDoubleJump;
import me.aaron.listeners.PlayerInteractListener;
import me.aaron.listeners.RespawnListener;
import me.aaron.listeners.inventories.Compass;
import me.aaron.listeners.inventories.HidePlayer;
import me.aaron.listeners.inventories.Profil;
import me.aaron.methods.InvsClick;

public class LobbyPlugin extends JavaPlugin {

	public static LobbyPlugin instance;
	
	public static boolean settingsActivated = false;
	
	public World hubWorld;
	
	public static String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Lobby" + ChatColor.GRAY + "] "
			+ ChatColor.AQUA;
	public static String serverpre = ChatColor.BLUE + "[" + ChatColor.DARK_RED + "LegendSystem" + ChatColor.BLUE + "] "
			+ ChatColor.AQUA;

	// COMPASS
	public ArrayList<String> fastBow = new ArrayList<String>();
	public ArrayList<String> usingSpy = new ArrayList<String>();

	// HIDEPLAYERS
	public ArrayList<String> usingHide = new ArrayList<String>();

	// SILENT-LOBBY
	public ArrayList<String> inSilentLobby = new ArrayList<String>();

	// PROFILE \\ SETTINGS
	/*
	 * public ArrayList<String> jumpPadsOff = new ArrayList<String>(); public
	 * ArrayList<String> doubleJumpOff = new ArrayList<String>(); public
	 * ArrayList<String> firstTimeStgs = new ArrayList<String>(); public
	 * ArrayList<String> hasChatDisabled = new ArrayList<String>();
	 */
	public ArrayList<String> toggledPvP = new ArrayList<String>();
	public ArrayList<String> willStopPvP = new ArrayList<String>();
	public static HashMap<String, Boolean> jumpPads = new HashMap<String, Boolean>();
	public static HashMap<String, Boolean> chat = new HashMap<String, Boolean>();
	public static HashMap<String, Boolean> pvp = new HashMap<String, Boolean>();
	public static HashMap<String, Boolean> doubleJump = new HashMap<String, Boolean>();

	// MISC
	public ArrayList<Player> inBuildMode = new ArrayList<Player>();

	
	@Override
	public void onEnable() {
		
		createConfigs();
		instance = this;
		
		reloadConfig();
		this.fixConfig();
		this.registerCommands();
		this.registerListeners();
		this.setCmdAliases();

		
		this.willStopPvP.clear();
		this.toggledPvP.clear();
		this.inSilentLobby.clear();

	}
	
	public void createConfigs() {
		try {
			
		
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		
//		if (!UserData.getFolder().exists()) {
//			UserData.getFolder().mkdirs();
//		}
		
		File cfg = new File(getDataFolder(), "config.yml");
		if (!cfg.exists()) {
			cfg.createNewFile();
		}
		
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void fixConfig() {
		if (!getConfig().contains("booster.top")) {
			getConfig().set("booster.top", Material.GOLD_PLATE.toString());
			saveConfig();
		} else if (!getConfig().contains("booster.bottom")) {
			getConfig().set("booster.bottom", Material.SLIME_BLOCK.toString());
			saveConfig();
		}
		reloadConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}

	public void registerCommands() {
		getCommand("fastbow").setExecutor(new FastbowCmd(this));
		getCommand("booster").setExecutor(new GetBoosterCmd(this));
		getCommand("gm").setExecutor(new GmCmds(this));
		getCommand("hub").setExecutor(new HubCmd(this));
		getCommand("inv").setExecutor(new InvSeeCmd(this));
		getCommand("setboosterblocks").setExecutor(new SetBoosterBlocksCmd(this));
		getCommand("sethub").setExecutor(new SetHubCmd(this));
		getCommand("spy").setExecutor(new SpyCmd(this));
		getCommand("setwarps").setExecutor(new SetWarps(this));
		getCommand("checkarrays").setExecutor(new CheckArrays(this));
		getCommand("silentlobby").setExecutor(new SilentLobby(this));
		getCommand("pvpoff").setExecutor(new PvPOff(this));
		getCommand("buildmode").setExecutor(new BuildModeCmd(this));
	}

	public void setCmdAliases() {
		getCommand("fastbow").setAliases(Arrays.asList("fb", "fastb", "fbow"));
		getCommand("booster").setAliases(Arrays.asList("gb", "getb"));
		getCommand("hub").setAliases(Arrays.asList("spawn", "l", "leave"));
		getCommand("setboosterblocks").setAliases(Arrays.asList("sb", "setb"));
		getCommand("togglehunger").setAliases(Arrays.asList("th", "toggleh", "hunger"));
		getCommand("buildmode").setAliases(Arrays.asList("build"));
	}

	public void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();

		pm.registerEvents(new PlayerInteractListener(this), this);
		pm.registerEvents(new GameModeListener(this), this);
		pm.registerEvents(new HubBoostListener(this), this);
		pm.registerEvents(new HungerListener(this), this);
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new JoinListener(this), this);
		pm.registerEvents(new PlayerChatListener(this), this);
		pm.registerEvents(new PlayerDeathListener(this), this);
		pm.registerEvents(new RespawnListener(this), this);
		pm.registerEvents(new LeaveListener(this), this);
		pm.registerEvents(new PlayerDoubleJump(this), this);
		pm.registerEvents(new PlayerDamageListener(this), this);
		pm.registerEvents(new PlayerChangeWorld(this), this);
		pm.registerEvents(new BlockBreakPlaceListener(this), this);
		pm.registerEvents(new InvsClick(this), this);
		pm.registerEvents(new ItemDropListener(), this);
		
//		INVS
		pm.registerEvents(new Profil(), this);
		pm.registerEvents(new Compass(), this);
		pm.registerEvents(new HidePlayer(), this);
	}

	public void clearChat(Player p) {
		for (int i = 1; i < 26; i++) {
			p.sendMessage("");
			if (i == 20) {
				p.sendMessage(this.prefix + ChatColor.GREEN + "Du befindest dich nun in der Silent Lobby");
				break;
			}
		}
	}
	
	public FileConfiguration getUserData(Player p) {
		File userdata = new File(getDataFolder() + File.separator + "UserData");
		File uf = new File(userdata, File.separator + p.getName() + ".yml");
		FileConfiguration userfile = YamlConfiguration.loadConfiguration(uf);
		return userfile;
	}
	
	public World getHubWorld() {
		if (hubWorld != null) {
			return hubWorld;
		} else {
			return null;
		}
	}
	
	public static LobbyPlugin getInstance() {
		return instance;
	}
	
	/*public void createUserData(Player p) {
		String playerName = p.getName();
		File ud = new File(getDataFolder(), File.separator + "PlayerDatabase");
		File f = new File(ud, File.separator + playerName + ".yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		
		if (!f.exists()) {
			try {
				
				playerData.createSection("Settings");
				
				playerData.save(f);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/
}
