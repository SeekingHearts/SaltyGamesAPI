package me.aaron.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.scheduler.BukkitRunnable;

import me.aaron.main.SaltyGames;

public class FileDB extends DataBase {

	private File dFile;
	private FileConfiguration data;
	
	public FileDB(SaltyGames pl) {
		super(pl);
		this.dFile = new File(pl.getDataFolder().toString() + File.separatorChar + "data.yml");
	}

	@Override
	public boolean load(boolean async) {
		if (!dFile.exists()) {
			try {
				dFile.getParentFile().mkdir();
				dFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		try {
			this.data = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(dFile), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public boolean getBoolean(UUID uuid, String path) {
		return getBoolean(uuid, path, false);
	}

	@Override
	public boolean getBoolean(UUID uuid, String path, boolean defaultValue) {
		return data.getBoolean(uuid + "." + path, defaultValue);
	}

	@Override
	public void set(String uuid, String path, Object b) {
		data.set(uuid + "." + path, b);
	}

	@Override
	public void save(boolean async) {
		if (async) {
			final FileDB fileDB = this;
			BukkitRunnable runnable = new BukkitRunnable() {
				
				@Override
				public void run() {
					SaltyGames.debug(" speichere in das async file...");
					
					try {
						data.save(dFile);
					} catch (IOException e) {
						Bukkit.getLogger().log(Level.SEVERE, "statistiken konnten nicht gespeichert werden (async)");
						e.printStackTrace();
					}
					SaltyGames.debug(" ...done");
					fileDB.removeRunnabe(this);
				}
			};
			runnables.add(runnable);
			runnable.runTaskAsynchronously(pl);
		} else {
			try {
				data.save(dFile);
			} catch (Exception e) {
				Bukkit.getLogger().log(Level.SEVERE, "fehler beim speichern der statistiken");
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getInt(UUID uuid, String path, int defaultValue) {
		return data.getInt(uuid + "." + path, defaultValue);
	}

	@Override
	public void addStatistics(UUID uuid, String gameID, String gameTypeID, double value, SaveType saveType) {
		
		SaltyGames.debug("Statistiken werden hinzugefügt '" + uuid.toString() + "." + gameID + "." + gameTypeID + "." + saveType.toString().toLowerCase() + "' mit dem Wert: " + value);
		
		double oldScore;
		switch(saveType) {
		
		case SCORE:
		case TIME_HIGH:
		case HIGH_NUMBER_SCORE:
			oldScore = data.getDouble(uuid.toString() + "." + GAMES_STATISTICS_NODE + "." + gameID
					 + "." + gameTypeID + "." + saveType.toString().toLowerCase(), 0.);
			if (oldScore >= value)
				return;
			data.set(uuid.toString() + "." + GAMES_STATISTICS_NODE + "." + gameID + "."
					 + gameTypeID + "." + saveType.toString().toLowerCase(), value);
			break;
		case TIME_LOW:
			oldScore = data.getDouble(uuid.toString() + "." + GAMES_STATISTICS_NODE + "." + gameID
					 + "." + gameTypeID + "." + saveType.toString().toLowerCase(), Double.MAX_VALUE);
			if (oldScore >= value)
				return;
			data.set(uuid.toString() + "." + GAMES_STATISTICS_NODE + "." + gameID + "."
					 + gameTypeID + "." + saveType.toString().toLowerCase(), value);
			break;
		case WINS:
			oldScore = data.getDouble(uuid.toString() + "." + GAMES_STATISTICS_NODE + "." + gameID
					 + "." + gameTypeID + "." + saveType.toString().toLowerCase(), 0.);
			data.set(uuid.toString() + "." + GAMES_STATISTICS_NODE + "." + gameID + "."
					 + gameTypeID + "." + saveType.toString().toLowerCase(), value + oldScore);
			break;
		default:
			Bukkit.getLogger().log(Level.WARNING, "Versuchte nicht unterstützte Statistiken zu speichern: " + saveType.toString());
		}
	}

	@Override
	public ArrayList<Stat> getTopList(String gameID, String gameTypeID, SaveType saveType, int maxNumber) {
		ArrayList<Stat> toReturn = new ArrayList<>();
		Map<UUID, Double> valuesMap = new HashMap<>();
		
		for (String uuid : data.getKeys(false)) {
			if (!data.isSet(uuid.toString() + "." + GAMES_STATISTICS_NODE + "." + gameID + "."
					+ gameTypeID + "." + saveType.toString().toLowerCase()))
				continue;
			try {
				UUID uuid1 = UUID.fromString(uuid);
				valuesMap.put(uuid1, data.getDouble(uuid.toString() + "."+ GAMES_STATISTICS_NODE+"." + gameID +"."+gameTypeID + "." + saveType.toString().toLowerCase()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				Bukkit.getLogger().log(Level.WARNING, "fehler beim laden einer statistik für einen spieler aufgrund einer fehlerhaften UUID");
				continue;
			}
		}
		
		boolean higher;
		switch (saveType) {
		
		case TIME_LOW:
			higher = false;
			break;
		case WINS:
		case TIME_HIGH:
		case SCORE:
		case HIGH_NUMBER_SCORE:
			higher = true;
			break;
		default:
				higher = true;
				Bukkit.getLogger().log(Level.SEVERE, "es wurde ein nicht unterstützter SaveType während dem laden einer top list gefunden");
		}
		
		UUID currentBestUUID = null;
		double currentBestScore;
		
		int number = 0;
		while(number < maxNumber && !valuesMap.keySet().isEmpty()) {
			currentBestScore = higher ? 0. : Double.MAX_VALUE;
			
			for (Iterator<Map.Entry<UUID, Double>> entries = valuesMap.entrySet().iterator(); entries.hasNext();) {
				Map.Entry<UUID, Double> entry = entries.next();
				if (higher) {
					if (entry.getValue() > currentBestScore) {
						currentBestScore = entry.getValue();
						currentBestUUID = entry.getKey();
					}
				} else {
					if (entry.getValue() < currentBestScore) {
						currentBestScore = entry.getValue();
						currentBestUUID = entry.getKey();
					}
				}
			}
			SaltyGames.debug("Rang gefunden " + (number + 1) + " mit der Zeit: " + currentBestScore + "      higher: " + higher);
			toReturn.add(new Stat(currentBestUUID, valuesMap.get(currentBestUUID)));
			number++;
			valuesMap.remove(currentBestUUID);
		}
		return toReturn;
	}

	@Override
	public boolean isSet(String path) {
		return data.isSet(path);
	}

	@Override
	public void loadPlayer(SGPlayer player, boolean async) {
		boolean playSounds = getBoolean(player.getUuid(), DataBase.PLAYER_PLAY_SOUNDS, true);
		int token = getInt(player.getUuid(), DataBase.TOKEN_PATH, 0);
		
		player.setPlaySounds(playSounds);
		player.setTokens(token);
	}

	@Override
	public void savePlayer(SGPlayer player, boolean async) {
		String uuid = player.getUuid().toString();
		set(uuid, DataBase.PLAYER_PLAY_SOUNDS, player.isPlaySounds());
		set(uuid, DataBase.TOKEN_PATH, player.getTokens());
	}
}
