package me.aaron.main;

import org.bukkit.entity.Player;

import me.aaron.games.Game;
import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderAPIHook extends EZPlaceholderHook {

	private SaltyGames pl;
	
	public PlaceholderAPIHook(SaltyGames pl, String identifier) {
		super(pl, identifier);
		
		this.pl = pl;
		
		this.hook();
	}

	@Override
	public String onPlaceholderRequest(Player p, String id) {
		
		String gameID;
		
		if (pl.getPluginManager().getGames().containsKey(id.split("_")[id.split("_").length - 1])) {
			gameID = id.split("_")[id.split("_").length - 1];
			
			id = id.replace("_" + gameID, "");
		}
		
		switch (id) {
		
		case "game_name":
			if (p == null)
				return null;
			Game game = pl.getPluginManager().getGame(p.getUniqueId());
			if (game == null)
				return null;
			return game.getGameLang().PLAIN_NAME;
		}
		return null;
	}

}
