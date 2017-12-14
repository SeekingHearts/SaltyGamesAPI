package main.java.me.aaron.saltygamesapi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import main.java.me.aaron.saltygamesapi.Arena;
import main.java.me.aaron.saltygamesapi.ArenaConfigStrings;
import main.java.me.aaron.saltygamesapi.MinigamesAPI;
import main.java.me.aaron.saltygamesapi.PluginInstance;
import net.md_5.bungee.api.ChatColor;

public class ArenaLobbyScoreboard {

	PluginInstance pli;
	
	int init = 0;
	boolean custom = false;
	
	ArrayList<String> loaded_custom_strings = new ArrayList<>();
	HashMap<String, Scoreboard> score = new HashMap<>();
	HashMap<String, Objective> objSC = new HashMap<>();
	
	public ArenaLobbyScoreboard(final PluginInstance pli, final JavaPlugin pl) {
		this.pli = pli;
		
		this.init = 1;
		this.custom = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_USE_CUSTOM_SCOREBOARD);
		
		if (pli.getMessagesConfig().getConfig().isSet("messages.custom_lobby_scoreboard.")) {
			for (final String configLine : pli.getMessagesConfig().getConfig().getConfigurationSection("messages.custom_lobby_scoreboard.").getKeys(false)) {
				final String line = ChatColor.translateAlternateColorCodes('&', pli.getMessagesConfig().getConfig().getString("messages.custom_lobby_scoreboard." + configLine));
				loaded_custom_strings.add(line);
			}
		}
	}
	
	public void updateScoreboard(final JavaPlugin pl, final Arena ar) {
		
		if (!ar.getShowScoreboard())
			return;
		if (pli == null)
			pli = MinigamesAPI.getAPI().getPluginInstance(pl);
		
		Bukkit.getScheduler().runTask(MinigamesAPI.getAPI(), () -> {
			
			for (final String playername : ar.getAllPlayers())
            {
                if (!Validator.isPlayerValid(pl, playername, ar))
                {
                    return;
                }
                final Player p = Bukkit.getPlayer(playername);
                if (!ArenaLobbyScoreboard.this.score.containsKey(playername))
                {
                    ArenaLobbyScoreboard.this.score.put(playername, Bukkit.getScoreboardManager().getNewScoreboard());
                }
                if (!ArenaLobbyScoreboard.this.objSC.containsKey(playername))
                {
                    ArenaLobbyScoreboard.this.objSC.put(playername, ArenaLobbyScoreboard.this.score.get(playername).registerNewObjective(playername, "dummy"));
                    ArenaLobbyScoreboard.this.objSC.get(playername).setDisplaySlot(DisplaySlot.SIDEBAR);
                    ArenaLobbyScoreboard.this.objSC.get(playername)
                            .setDisplayName(ArenaLobbyScoreboard.this.pli.getMessagesConfig().scoreboard_lobby_title.replaceAll("<arena>", ar.getInternalName()));
                }
                
                try
                {
                    if (ArenaLobbyScoreboard.this.loaded_custom_strings.size() < 1)
                    {
                        return;
                    }
                    for (final String line : ArenaLobbyScoreboard.this.loaded_custom_strings)
                    {
                        final String[] line_arr = line.split(":");
                        String line_ = line_arr[0];
                        final String score_identifier = line_arr[line_arr.length - 1];
                        if (line_arr.length > 2)
                        {
                            line_ += ":" + line_arr[1];
                        }
                        int score = 0;
                        if (score_identifier.equalsIgnoreCase("<playercount>"))
                        {
                            score = ar.getAllPlayers().size();
                        }
                        else if (score_identifier.equalsIgnoreCase("<maxplayercount>"))
                        {
                            score = ar.getMaxPlayers();
                        }
                        else if (score_identifier.equalsIgnoreCase("<points>"))
                        {
                            score = ArenaLobbyScoreboard.this.pli.getStatsInstance().getPoints(playername);
                        }
                        else if (score_identifier.equalsIgnoreCase("<wins>"))
                        {
                            score = ArenaLobbyScoreboard.this.pli.getStatsInstance().getWins(playername);
                        }
                        else if (score_identifier.equalsIgnoreCase("<money>"))
                        {
                            score = (int) MinigamesAPI.econ.getBalance(playername);
                        }
                        else if (score_identifier.equalsIgnoreCase("<kills>"))
                        {
                            score = ArenaLobbyScoreboard.this.pli.getStatsInstance().getKills(playername);
                        }
                        if (line_.length() < 15)
                        {
                            // score.get(arena.getInternalName()).resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + line_));
                            Util.getScore(ArenaLobbyScoreboard.this.objSC.get(playername), ChatColor.GREEN + line_).setScore(score);
                        }
                        else
                        {
                            // score.get(arena.getInternalName()).resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + line_.substring(0,
                            // Math.min(line_.length() - 3, 13))));
                            Util.getScore(ArenaLobbyScoreboard.this.objSC.get(playername), ChatColor.GREEN + line_.substring(0, Math.min(line_.length() - 3, 13))).setScore(score);
                        }
                    }
                    p.setScoreboard(ArenaLobbyScoreboard.this.score.get(playername));
                }
                catch (final Exception e)
                {
                    pli.getPlugin().getLogger().log(Level.SEVERE, "Failed to set custom scoreboard", e);
                }
            }
			
		});
	}
	
	public void removeScoreboard(final String ar, final Player p) {
		try {
			final ScoreboardManager mgr = Bukkit.getScoreboardManager();
			final Scoreboard sc = mgr.getNewScoreboard();
			
			p.setScoreboard(sc);
		} catch (final Exception e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
	}
	
	public void clearScoreboard(final String arName) {
		;
	}
	
}
