package me.aaron.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesSettings;

public abstract class DataBase {
	
	protected Set<BukkitRunnable> runnables = new HashSet<>();
	
	protected BukkitRunnable autoSave;
	
	
	public static final String PLAYER_PLAY_SOUNDS = "playSounds";
    public static final String GAMES_STATISTICS_NODE = "gameStatistics";
    public static final String TOKEN_PATH = "tokens";
    
    protected SaltyGames pl;
    
    public DataBase(SaltyGames pl) {
    	this.pl = pl;
    	
    	this.autoSave = new BukkitRunnable() {
			
			@Override
			public void run() {
				SaltyGames.debug(" auto saving...");
				if (pl == null || pl.getDatabase() == null)
					this.cancel();
				pl.getDatabase().save(true);
			}
		};
		
		int interval = SaltyGamesSettings.autoSaveInterval;
		if (interval > 0) {
			autoSave.runTaskTimer(pl, interval * 60 * 20, interval * 60 * 20);
		}
		
    }
    
    
    public abstract boolean load(boolean async);

    public abstract boolean getBoolean(UUID uuid, String path);

    public abstract boolean getBoolean(UUID uuid, String path, boolean defaultValue);

    public abstract void set(String uuid, String path, Object b);

    public abstract void save(boolean async);

    public abstract int getInt(UUID uuid, String path, int defaultValue);

    public abstract void addStatistics(UUID uuid, String gameID, String gameTypeID, double value, SaveType saveType);

    public abstract ArrayList<Stat> getTopList(String gameID, String gameTypeID, SaveType saveType, int maxNumber);

    public abstract boolean isSet(String path);

    public abstract void loadPlayer(GBPlayer player, boolean async);

    public abstract void savePlayer(GBPlayer player, boolean async);
    
    
    public void onShutDown() {
    	if (autoSave != null) {
    		autoSave.cancel();
    	}
    	save(false);
    	boolean waiting = !runnables.isEmpty();
    	if (waiting) pl.info(" warte auf async tasks...");
    	while (!runnables.isEmpty()) {}
    	if (waiting)
    		pl.info(" ... done");
    }
    
    public void removeRunnabe(BukkitRunnable runnable) {
    	runnables.remove(runnable);
    }
    
    public class Stat {
    	private double value;
    	private UUID uuid;
    	
    	private SaveType saveTyp;
    	
    	public Stat(UUID uuid, double value) {
    		this.uuid = uuid;
    		this.value = value;
    	}

		public double getValue() {
			return value;
		}

		public UUID getUuid() {
			return uuid;
		}

		public SaveType getSaveTyp() {
			return saveTyp;
		}
    }
}
