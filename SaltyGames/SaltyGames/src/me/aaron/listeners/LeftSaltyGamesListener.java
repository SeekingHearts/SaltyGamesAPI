package me.aaron.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import com.greatmancode.craftconomy3.tools.events.interfaces.EventHandler;

import me.aaron.events.LeftSaltyGamesEvent;
import me.aaron.main.SaltyGames;

public class LeftSaltyGamesListener implements Listener {
	
	private SaltyGames pl;
	private List<String> commands;
	
	public LeftSaltyGamesListener(SaltyGames pl) {
		this.pl = pl;
		
		if (pl.getConfig().isSet("listeners.leftSaltyGames")) {
			ConfigurationSection listener = pl.getConfig().getConfigurationSection("listeners.leftSaltyGames");
			
			if (listener.isList("commands")) {
				this.commands = listener.getStringList("commands");
			}
		}
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler
	public void onLeftSaltyGames(LeftSaltyGamesEvent e) {
		if (commands != null) {
			for (String cmd : commands) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", e.getPlayer().getName()));
			}
		}
	}

}
