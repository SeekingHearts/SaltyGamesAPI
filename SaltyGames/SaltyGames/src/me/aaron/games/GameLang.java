package me.aaron.games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.aaron.main.Language;
import me.aaron.main.Module;
import me.aaron.main.SaltyGames;

public abstract class GameLang extends Language {
	
	public List<String> GAME_HELP = new ArrayList<>(Arrays.asList("Viel Spaﬂ!"));
	
	public String GAME_PAYED = " Du hast %cost%", GAME_NOT_ENOUGH_MONEY = " Du hast nicht genug Geld (%cost%)";
	
	public GameLang(SaltyGames pl, Module mod) {
		super(pl, mod);
		
		this.GAME_HELP = getStringList("gameHelp");
	}
	
	public GameLang(SaltyGames pl, String modID) {
		super(pl, modID);
		
		this.GAME_HELP = getStringList("gameHelp");
	}

}
