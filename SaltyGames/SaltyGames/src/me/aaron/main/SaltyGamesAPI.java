package me.aaron.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.tools.interfaces.Loader;

public class SaltyGamesAPI {

	private SaltyGames pl;

	public SaltyGamesAPI(SaltyGames pl) {
		this.pl = pl;
	}

	Plugin eco = Bukkit.getPluginManager().getPlugin("Craftconomy3");
	Common craftconomy = (Common) ((Loader) eco).getCommon();
	
//	public boolean giveTokens(Player p, int count) {
//		if (p == null)
//			return false;
//		if (count < 0)
//			return false;
//		
//		TODO
//		if (p.isOnline()) {
//			
//		}
//	}
}