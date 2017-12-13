package main.me.aaron.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import main.me.aaron.Arena;
import main.me.aaron.ArenaConfigStrings;
import main.me.aaron.MinigamesAPI;
import main.me.aaron.PluginInstance;
import net.md_5.bungee.api.ChatColor;

public class ArenaScoreboard {

	PluginInstance pli;

	int init = 0;
	boolean custom = false;

	ArrayList<String> loaded_custom_strings = new ArrayList<>();
	HashMap<String, Scoreboard> SB = new HashMap<>();
	HashMap<String, Objective> objectiveSB = new HashMap<>();
	HashMap<String, Integer> currentscore = new HashMap<>();

	public ArenaScoreboard() {
	}

	public ArenaScoreboard(final PluginInstance pli, final JavaPlugin pl) {
		this.pli = pli;
		this.init = 1;
		this.custom = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_USE_CUSTOM_SCOREBOARD);

		if (pli.getMessagesConfig().getConfig().isSet("messages.custom_scoreboard")) {
			for (final String configline : pli.getMessagesConfig().getConfig()
					.getConfigurationSection("messages_custom_scoreboard.").getKeys(false)) {
				final String line = ChatColor.translateAlternateColorCodes('&',
						pli.getMessagesConfig().getConfig().getString("messages.custom_scoreboard." + configline));
				loaded_custom_strings.add(line);
			}
		}
	}

	public void updateScoreboard(final JavaPlugin pl, final Arena ar) {
		if (!ar.getShowScoreboard())
			return;

		if (init != 1)
			custom = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_USE_CUSTOM_SCOREBOARD);

		if (pli == null)
			pli = MinigamesAPI.getAPI().getPluginInstance(pl);

		Bukkit.getScheduler().runTask(MinigamesAPI.getAPI(), () -> {
			for (final String playername : ar.getAllPlayers()) {
				if (!Validator.isPlayerValid(pl, playername, ar))
					return;

				final Player p = Bukkit.getPlayer(playername);
				if (!ArenaScoreboard.this.custom) {
					if (!ArenaScoreboard.this.SB.containsKey(ar.getInternalName()))
						ArenaScoreboard.this.SB.put(ar.getInternalName(),
								Bukkit.getScoreboardManager().getNewScoreboard());

					if (!ArenaScoreboard.this.objectiveSB.containsKey(ar.getInternalName())) {
						ArenaScoreboard.this.objectiveSB.put(ar.getInternalName(), ArenaScoreboard.this.SB
								.get(ar.getInternalName()).registerNewObjective(ar.getInternalName(), "dummy"));
						ArenaScoreboard.this.objectiveSB.get(ar.getInternalName()).setDisplaySlot(DisplaySlot.SIDEBAR);
						ArenaScoreboard.this.objectiveSB.get(ar.getInternalName())
								.setDisplayName(ArenaScoreboard.this.pli.getMessagesConfig().scoreboard_title
										.replaceAll("<arena>", ar.getInternalName()));
					}
				} else {
					if (!ArenaScoreboard.this.SB.containsKey(playername))
						ArenaScoreboard.this.SB.put(playername, Bukkit.getScoreboardManager().getNewScoreboard());

					if (!ArenaScoreboard.this.objectiveSB.containsKey(playername)) {
						ArenaScoreboard.this.objectiveSB.put(playername,
								ArenaScoreboard.this.SB.get(playername).registerNewObjective(playername, "dummy"));
						ArenaScoreboard.this.objectiveSB.get(playername).setDisplaySlot(DisplaySlot.SIDEBAR);
						ArenaScoreboard.this.objectiveSB.get(playername)
								.setDisplayName(ArenaScoreboard.this.pli.getMessagesConfig().scoreboard_title
										.replaceAll("<arena>", ar.getInternalName()));
					}
				}

				if (ArenaScoreboard.this.custom) {
					try {
						for (final String line : ArenaScoreboard.this.loaded_custom_strings) {
							final String[] line_arr = line.split(":");
							final String line_ = line_arr[0];
							final String score_identifier = line_arr[1];
							int score = 0;

							if (score_identifier.equalsIgnoreCase("<playercount>")) {
								score = ar.getAllPlayers().size();
							} else if (score_identifier.equalsIgnoreCase("<lostplayercount>")) {
								score = ar.getAllPlayers().size() - ar.getPlayerAlive();
							} else if (score_identifier.equalsIgnoreCase("<playeralivecount>")) {
								score = ar.getPlayerAlive();
							} else if (score_identifier.equalsIgnoreCase("<points>")) {
								score = ArenaScoreboard.this.pli.getStatsInstance().getPoints(playername);
							} else if (score_identifier.equalsIgnoreCase("<wins>")) {
								score = ArenaScoreboard.this.pli.getStatsInstance().getWins(playername);
							} else if (score_identifier.equalsIgnoreCase("<money>")) {
								score = (int) MinigamesAPI.econ.getBalance(playername);
							}

							if (line_.length() < 15) {
								Util.resetScores(ArenaScoreboard.this.SB.get(playername), ChatColor.GREEN + line_);
								Util.getScore(ArenaScoreboard.this.objectiveSB.get(playername), ChatColor.GREEN + line_)
										.setScore(score);
							} else {
								Util.resetScores(ArenaScoreboard.this.SB.get(playername),
										ChatColor.GREEN + line_.substring(0, Math.min(line_.length() - 3, 13)));
								Util.getScore(ArenaScoreboard.this.objectiveSB.get(playername),
										ChatColor.GREEN + line_.substring(0, Math.min(line_.length() - 3, 13)))
										.setScore(score);
							}
						}
					} catch (final Exception e) {
						pli.getPlugin().getLogger().log(Level.WARNING, "Fehler beim erstellen eines Scoreboards", e);
					}
				} else {
					for (final String player : ar.getAllPlayers()) {
						if (!Validator.isPlayerOnline(player))
							continue;

						final Player p_ = Bukkit.getPlayer(player);
						if (!ArenaScoreboard.this.pli.global_lost.containsKey(p_)) {
							int score = 0;
							if (ArenaScoreboard.this.currentscore.containsKey(p_)) {
								final int oldscore = ArenaScoreboard.this.currentscore.get(p_);
								if (score > oldscore) {
									ArenaScoreboard.this.currentscore.put(player, score);
								} else {
									score = oldscore;
								}
							} else {
								ArenaScoreboard.this.currentscore.put(player, score);
							}

							try {
								if (p_.getName().length() < 15) {
									Util.getScore(ArenaScoreboard.this.objectiveSB.get(ar.getInternalName()),
											ChatColor.GREEN + p_.getName()).setScore(0);
								} else {
									Util.getScore(ArenaScoreboard.this.objectiveSB.get(ar.getInternalName()),
											ChatColor.GREEN + p_.getName().substring(0, p_.getName().length() - 3))
											.setScore(0);
								}
							} catch (final Exception e) {
								;
							}
						} else if (ArenaScoreboard.this.pli.global_lost.containsKey(player)) {
							try {
								if (ArenaScoreboard.this.currentscore.containsKey(player)) {
									final int score = ArenaScoreboard.this.currentscore.get(player);
									if (p_.getName().length() < 15) {
										Util.resetScores(ArenaScoreboard.this.SB.get(ar.getInternalName()), ChatColor.GREEN + p_.getName());
                                        Util.getScore(ArenaScoreboard.this.objectiveSB.get(ar.getInternalName()), ChatColor.RED + p_.getName()).setScore(0);
                                    }
                                    else
                                    {
                                        Util.resetScores(ArenaScoreboard.this.SB.get(ar.getInternalName()), ChatColor.GREEN + p_.getName().substring(0, p_.getName().length() - 3));
                                        Util.getScore(ArenaScoreboard.this.objectiveSB.get(ar.getInternalName()), ChatColor.RED + p_.getName().substring(0, p_.getName().length() - 3)).setScore(0);
                                    }
								}
							} catch (final Exception e) {
								;
							}
						}
					}
				}
			}
		});
	}
	
	public void removeScoreboard(final String ar, final Player p) {
		try {
			final ScoreboardManager manager = Bukkit.getScoreboardManager();
			final Scoreboard sc = manager.getNewScoreboard();
			
			p.setScoreboard(sc);
		} catch (final Exception e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
	}
	
	public void clearScoreboard(final String arenaname) {
		if (this.SB.containsKey(arenaname)) {
			try {
				final Scoreboard sc = this.SB.get(arenaname);
				for (final OfflinePlayer p : sc.getPlayers()) {
					sc.resetScores(p);
				}
			} catch (final Exception e) {
				if (MinigamesAPI.debug)
					MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
			}
			this.SB.remove(arenaname);
			if (this.objectiveSB.containsKey(arenaname))
				this.objectiveSB.remove(arenaname);
		}
	}
	
	public void setCurrentScoreMap(final HashMap<String, Integer> newcurrentscore) {
		currentscore = newcurrentscore;
	}
	
}