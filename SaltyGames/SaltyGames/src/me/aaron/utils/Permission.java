package me.aaron.utils;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.aaron.main.SaltyGames;

public enum Permission {
	
	PLAY_SPECIFIC_GAME("play", true), PLAY_ALL_GAMES("play.*")
	, OPEN_GAME_GUI("gamegui", true), OPEN_ALL_GAME_GUI("gamegui.*")
	, USE("use"), ADMIN("admin"), CMD_INFO("info"), CMD_HELP("help")
	, BYPASS_ALL("bypass"), BYPASS_GAME("bypass", true), OPEN_SHOP("shop");

	private static ArrayList<String> gameID = new ArrayList<>();
	private boolean perGame;
	private String perm;
	private String preNode = SaltyGames.MODULE_SALTYGAMES;
	
	private Permission(String perm, boolean perGame) {
		this.perm = preNode + "." + perm + (perGame ? ".%gameID%" : "");
		this.perGame = perGame;
	}
	
	private Permission(String perm) {
		this(perm, false);
	}
	
	public String getPermission(String gameID) {
		if (!gameID.contains(gameID))
			Bukkit.getLogger().log(Level.WARNING, "Permission konnte das folgende Spiel nicht finden: " + gameID);
		if (!perGame)
			Bukkit.getLogger().log(Level.WARNING, "accessing a per game permission without a gameID");
		return perm.replace("%gameID%", gameID);
	}
	
	public String getPermissions() {
		if (perGame)
			Bukkit.getLogger().log(Level.WARNING, "accessing a per game permission without a gameID");
		return perm;
	}
	
	public static void addGameID(String gameID) {
		Permission.gameID.add(gameID);
		SaltyGames.debug("Permissions für das Spiel: " + gameID + " wurde gesetzt");
	}
	
}
