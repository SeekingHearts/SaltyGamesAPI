package me.aaron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.achievements.ArenaAchievements;
import me.aaron.config.AchievementsConfig;
import me.aaron.config.ArenasConfig;
import me.aaron.config.ClassesConfig;
import me.aaron.config.GunsConfig;
import me.aaron.config.HologramsConfig;
import me.aaron.config.MessagesConfig;
import me.aaron.config.ShopConfig;
import me.aaron.config.StatsConfig;
import me.aaron.guns.Gun;
import me.aaron.holograms.Holograms;
import me.aaron.sql.MainSQL;
import me.aaron.utils.AClass;
import me.aaron.utils.ArenaLobbyScoreboard;
import me.aaron.utils.ArenaScoreboard;
import me.aaron.utils.Util;
import me.aaron.utils.Validator;

public class PluginInstance {
	
	public HashMap<String, Arena> global_players = new HashMap<>();
	
	public HashMap<String, Arena> global_lost = new HashMap<>();
	
	public HashMap<String, Arena> global_arcade_spectator = new HashMap<>();
	
	private ArenaListener arenalistener = null;
	
	private ArenasConfig arenasconfig = null;
	
	private ClassesConfig classesconfig = null;
	
	private MessagesConfig messagesconfig = null;
	
	private StatsConfig statsconfig = null;

	private GunsConfig gunsconfig = null;
	
	private AchievementsConfig achievementsconfig = null;
	
	private ShopConfig shopconfig = null;
	
	private HologramsConfig hologramsconfig = null;
	
	private JavaPlugin pl = null;
	
	private ArrayList<Arena> arenas = new ArrayList<>();
	
	private final HashMap<String, AClass> pclass = new HashMap<>();
	
	private final LinkedHashMap<String, AClass> aclasses = new LinkedHashMap<>();
	
	private final HashMap<String, Gun> guns = new HashMap<>();
	
	public ArenaSetup arenaSetup = new ArenaSetup();
	
	private Rewards rew = null;
	
	private MainSQL sql = null;
	
	private Stats stats = null;
	
	private Classes classes = null;
	
	private Shop shop = null;
	
	private SpectatorManager spectatormanager;
	
	private ArenaAchievements achievements = null;
	
	private Holograms holograms = null;

	public HashMap<String, ArrayList<String>> cached_sign_states = new HashMap<>();
	
	
	public boolean color_background_wool_of_signs;
	
	
	boolean last_man_standing = true;
	
	boolean old_reset = false;
	
	private boolean achievement_gui_enabled = false;
	
	private int lobby_countdown = 30;
	
	private int ingame_countdown = 10;
	

	boolean spectator_move_y_lock = true;
	
	boolean use_xp_bar_level = true;
	
	boolean blood_effects = true;
	
	boolean dead_in_fake_bed_effects = true;
	
	boolean spectator_mode_1_8 = true;
	
	boolean damage_identifier_effects = true;
	
	public ArenaScoreboard scoreboardManager;
	
	public ArenaLobbyScoreboard scoreboardLobbyManager;
	
	public boolean show_classes_without_usage_permission = true;
	
	public boolean chat_enabled = true;
	
	public PluginInstance(final JavaPlugin pl, final ArenasConfig arenasConfig, final MessagesConfig messagesConfig, final ClassesConfig classesConfig, final StatsConfig statsConfig, final ArrayList<Arena> arenas) {
		
		this.arenasconfig = arenasConfig;
		this.messagesconfig = messagesConfig;
		this.classesconfig = classesConfig;
		this.statsconfig = statsConfig;
		this.gunsconfig = new GunsConfig(pl, false);
		this.achievementsconfig = new AchievementsConfig(pl);
		this.shopconfig = new ShopConfig(pl, false);
		this.hologramsconfig = new HologramsConfig(pl, false);
		this.arenas = arenas;
		this.pl = pl;
		this.rew = new Rewards(pl);
		this.stats = new Stats(this, pl);
		this.sql = new MainSQL(pl, true);
		this.classes = new Classes(pl, this);
		this.shop = new Shop(pl, this);
		this.spectatormanager = new SpectatorManager(pl);
		this.achievements = new ArenaAchievements(this, pl);
		this.holograms = new Holograms(this);
		this.scoreboardManager = new ArenaScoreboard(this, pl);
		this.scoreboardLobbyManager = new ArenaLobbyScoreboard(this, pl);
//		this.reload
	}
	
	public PluginInstance(final JavaPlugin pl, final ArenasConfig arenasConfig, final MessagesConfig messagesConfig, final ClassesConfig classesConfig, final StatsConfig statsConfig) {
		this(pl, arenasConfig, messagesConfig, classesConfig, statsConfig, new ArrayList<Arena>());
	}
	
	public void reloadVars() {
		this.lobby_countdown = this.pl.getConfig().getInt(ArenaConfigStrings.CONFIG_LOBBY_COUNTDOWN) + 1;
        this.ingame_countdown = this.pl.getConfig().getInt(ArenaConfigStrings.CONFIG_INGAME_COUNTDOWN) + 1;
        this.spectator_move_y_lock = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_SPECTATOR_MOVE_Y_LOCK);
        this.use_xp_bar_level = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_USE_XP_BAR_LEVEL);
        this.blood_effects = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_EFFECTS_BLOOD);
        this.damage_identifier_effects = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_EFFECTS_DMG_IDENTIFIER_HOLO);
        this.dead_in_fake_bed_effects = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_EFFECTS_DEAD_IN_FAKE_BED);
        this.color_background_wool_of_signs = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_COLOR_BACKGROUND_WOOL);
        this.spectator_mode_1_8 = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_EFFECTS_1_8_SPECTATOR_MODE);
        this.last_man_standing = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_LAST_MAN_STANDING_WINS);
        this.old_reset = this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_USE_OLD_RESET_METHOD);
        
        if (old_reset) {
        	pl.getLogger().severe("SEVERE!");
        }
        show_classes_without_usage_permission = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_SHOW_CLASSES_WITHOUT_PERM);
        chat_enabled = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_CHAT_ENABLED);
        
        for (final String state : ArenaState.getAllStateNames()) {
        	cached_sign_states.put(state, new ArrayList<>(Arrays.asList(messagesconfig.getConfig().getString("signs." + state.toLowerCase() + ".0")
        			, messagesconfig.getConfig().getString("signs." + state.toLowerCase() + ".1")
        			, messagesconfig.getConfig().getString("signs." + state.toLowerCase() + ".2")
        			, messagesconfig.getConfig().getString("signs." + state.toLowerCase() + ".3"))));
        }
	}
	
	public void reloadAllArenas() {
		for (final Arena ar : getArenas()) {
			if (ar != null) {
				final String arName = ar.getInternalName();
				final ArenaSetup as = arenaSetup;
				
				ar.init(Util.getSignLocationFromArena(pl, arName), Util.getAllSpawns(pl, arName)
						, Util.getMainLobby(pl), Util.getComponentForArena(pl, arName, "lobby")
						, as.getPlayerCount(pl, arName, true), as.getPlayerCount(pl, arName, false)
						, as.getArenaVIP(pl, arName));
				
				if (ar.isSuccessfullyInit()) {
					Util.updateSign(pl, ar);
				}
			}
		}
	}
	
	public void reloadArena(final String arName) {
		if (Validator.isArenaValid(pl, arName)) {
			final Arena ar = getArenaByName(arName);
			
			if (ar != null) {
				final ArenaSetup as = arenaSetup;
				
				ar.init(Util.getSignLocationFromArena(pl, arName), Util.getAllSpawns(pl, arName)
						, Util.getMainLobby(pl), Util.getComponentForArena(pl, arName, "lobby")
						, as.getPlayerCount(pl, arName, true), as.getPlayerCount(pl, arName, false)
						, as.getArenaVIP(pl, arName));
			}
		}
	}
	
//	SET / ADD STUFF
	public void addAClass(final String name, final AClass aClass) {
		aclasses.put(name, aClass);
	}
	
	public void setPClass(final String p, final AClass aClass) {
		pclass.put(p, aClass);
	}
	
	public void addGun(final String name, final Gun gun) {
		guns.put(name, gun);
	}
	
//	CONFIG
	public StatsConfig getStatsConfig() {
		return statsconfig;
	}

	public AchievementsConfig getAchievementsConfig() {
		return achievementsconfig;
	}
	
	public MessagesConfig getMessagesConfig() {
		return messagesconfig;
	}
	
	public ShopConfig getShopConfig() {
		return shopconfig;
	}

	public ArenasConfig getArenasConfig() {
		return arenasconfig;
	}
	
	public HologramsConfig getHologramsConfig() {
		return hologramsconfig;
	}
	
	public ClassesConfig getClassesConfig() {
		return classesconfig;
	}
	
	public GunsConfig getGunsConfig() {
		return gunsconfig;
	}
	
//	INSTANCES
	public MainSQL getSQLInstance() {
		return sql;
	}
	
	public Rewards getRewardsInstance() {
		return rew;
	}
	
	public Stats getStatsInstance() {
		return stats;
	}
	
	public ArenaListener getArenaListener() {
		return arenalistener;
	}
	
	
//	ARENA STUFF
	public ArenaAchievements getArenaAchievements() {
		return achievements;
	}
	
	public Arena getArenaByName(final String arName) {
		for (final Arena ar : getArenas()) {
			if (ar.getInternalName().equalsIgnoreCase(arName)) {
				return ar;
			}
		}
		return null;
	}
	
	public void clearArenas() {
		arenas.clear();
	}
	
	public Arena removeArenaByName(final String arName) {
		Arena ar = null;
		
		for (final Arena ar_ : this.getArenas()) {
			if (ar_.getInternalName().equalsIgnoreCase(arName))
				ar = ar_;
		}
		if (ar != null) {
			removeArena(ar);
		}
		return null;
	}
	
	public boolean removeArena(final Arena ar) {
		if (arenas.contains(ar)) {
			arenas.remove(ar);
			return true;
		}
		return false;
	}
	
	public ArrayList<Arena> addArena(final Arena ar) {
		arenas.add(ar);
		return getArenas();
	}
	
	public void addLoadedArenas(final ArrayList<Arena> ar) {
		arenas = ar;
	}
	
	
//	CONTAINS
	public boolean containsGlobalLost(final String p) {
		return global_lost.containsKey(p);
	}
	
	public boolean containsGlobalPlayer(final String p) {
		return global_players.containsKey(p);
	}
	
	
//	HANDLERS
	public Shop getShopHandler() {
		return shop;
	}
	
	public Holograms getHologramsHandler() {
		return holograms;
	}
	
	public Classes getClassesHandler() {
		return classes;
	}
	
//	Getting Stuff
	public JavaPlugin getPlugin() {
		return pl;
	}
	
	public HashMap<String, AClass> getAClasses() {
		return aclasses;
	}
	
	public HashMap<String, AClass> getPClasses() {
		return pclass;
	}

	public ArrayList<Arena> getArenas() {
		return arenas;
	}
	
	public int getLobbyCountdown() {
		return lobby_countdown;
	}
	
	public int getIngameCountdown() {
		return ingame_countdown;
	}
	
	public HashMap<String, Gun> getAllGuns() {
		return guns;
	}
	
	
	public boolean isAchievementGuiEnabled() {
		return achievement_gui_enabled;
	}
	
//	MANAGER
	public SpectatorManager getSpectatorManager() {
		return spectatormanager;
	}
	
//	LISTENER
	public void setArenaListener(final ArenaListener al) {
		arenalistener = al;
	}
}
