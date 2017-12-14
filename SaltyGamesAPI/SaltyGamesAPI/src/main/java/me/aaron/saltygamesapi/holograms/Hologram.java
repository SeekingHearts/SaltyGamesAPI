package main.java.me.aaron.saltygamesapi.holograms;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import main.java.me.aaron.saltygamesapi.Effects;
import main.java.me.aaron.saltygamesapi.MinigamesAPI;
import main.java.me.aaron.saltygamesapi.PluginInstance;

public class Hologram {
	
	PluginInstance pli;
	Location loc;
	
	ArrayList<Integer> ids = new ArrayList<>();

	public Hologram(final PluginInstance pli, final Location loc) {
		this.pli = pli;
		this.loc = loc;
	}
	
	public void send(final Player p) {
		if (pli.getMessagesConfig().getConfig().isSet("messages.stats")) {
			
			double ydelta = 0.25D;
			
			final int kills_ = pli.getStatsInstance().getKills(p.getName());
			final int deaths_ = pli.getStatsInstance().getDeaths(p.getName());
			final int money_ = (int) MinigamesAPI.econ.getBalance(p.getName());
			
			final String wins = Integer.toString(pli.getStatsInstance().getWins(p.getName()));
			final String loses = Integer.toString(pli.getStatsInstance().getLoses(p.getName()));
			
			final String kills = Integer.toString(kills_);
			final String deaths = Integer.toString(deaths_);
			final String money = Integer.toString(money_);
			
			final String points = Integer.toString(pli.getStatsInstance().getPoints(p.getName()));
			final String kdr = Integer.toString(Math.max(kills_, 1) / Math.max(deaths_, 1));
			
			final ArrayList<String> str = new ArrayList<>(pli.getMessagesConfig().getConfig().getConfigurationSection("messages.stats").getKeys(false));

			Collections.reverse(str);
			
			for (final String key : str) {
				final String msg = pli.getMessagesConfig().getConfig().getString("messages.stats." + key)
						.replaceAll("<wins>", wins).replaceAll("<loses>", loses).replaceAll("<alltime_kills>"
								, kills).replaceAll("<alltime_deaths>", deaths).replaceAll("<point>", points)
						.replaceAll("kdr", kdr).replaceAll("<money>", money);
				ids.addAll(Effects.playHologram(p, loc.clone().add(0D, ydelta, 0D)
						, ChatColor.translateAlternateColorCodes('&', msg), false, false));
				ydelta += 0.25D;
			}
		}
	}

	public ArrayList<Integer> getIds() {
		return ids;
	}
}
