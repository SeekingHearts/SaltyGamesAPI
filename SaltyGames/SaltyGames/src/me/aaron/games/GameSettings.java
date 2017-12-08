package me.aaron.games;

import me.aaron.main.SaltyGamesSettings;

public class GameSettings {

	private boolean handleClicksOnHotbar;
	
	private boolean playSounds;
	
	private boolean econEnabled;
	
	private GameType gameType;
	
	private int gameGuiSize;
	
	public GameSettings() {
		this.handleClicksOnHotbar = false;
		this.playSounds = SaltyGamesSettings.playSounds;
		this.gameType = GameType.SINGLE_PLAYER;
		this.gameGuiSize = 54;
	}
	
	
	public boolean isHandleClicksOnHotbar() {
		return handleClicksOnHotbar;
	}

	public void setHandleClicksOnHotbar(boolean handleClicksOnHotbar) {
		this.handleClicksOnHotbar = handleClicksOnHotbar;
	}

	public boolean isPlaySounds() {
		return playSounds;
	}

	public void setPlaySounds(boolean playSounds) {
		this.playSounds = playSounds;
	}

	public boolean isEconEnabled() {
		return econEnabled;
	}

	public void setEconEnabled(boolean econEnabled) {
		this.econEnabled = econEnabled;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public int getGameGuiSize() {
		return gameGuiSize;
	}

	public void setGameGuiSize(int gameGuiSize) {
		this.gameGuiSize = gameGuiSize;
	}


	public enum GameType{
		SINGLE_PLAYER(1), TWO_PLAYER(2);
		
		int playerNumber;
		
		GameType(int playerNumber) {
			this.playerNumber = playerNumber;
		}
		
		public int getPlayerNumber() {
			return this.playerNumber;
		}
		
	}
	
}