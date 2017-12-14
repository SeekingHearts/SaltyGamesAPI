package main.me.aaron.arcarde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import main.me.aaron.Arena;
import main.me.aaron.ArenaConfigStrings;
import main.me.aaron.ArenaState;
import main.me.aaron.MinigamesAPI;
import main.me.aaron.PluginInstance;
import main.me.aaron.utils.Util;
import main.me.aaron.utils.Validator;

public class ArcadeInstance {

	public ArrayList<PluginInstance> minigames = new ArrayList<>();
	int currentindex = 0;
	public ArrayList<String> players = new ArrayList<>();
	Arena arena;
	JavaPlugin pl;

	boolean in_a_game = false;
	Arena currentarena = null;
	boolean started;

	public ArcadeInstance(final JavaPlugin pl, final ArrayList<PluginInstance> minigames, final Arena arena) {
		this.minigames = minigames;
		this.arena = arena;
		this.pl = pl;
	}

	public void joinArcade(final String playerName) {
		final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(this.pl);
		if (!this.players.contains(playerName)) {
			this.players.add(playerName);
			this.arena.addPlayer(playerName);
		}

		final Player p = Bukkit.getPlayer(playerName);
		if (p == null)
			return;

		if (players.size() >= pl.getConfig().getInt(ArenaConfigStrings.CONFIG_ARCADE_MIN_PLAYERS)) {
			boolean msg = true;

			if (!started) {
				this.startArcade();
			} else {
				if (currentindex < minigames.size()) {
					if (in_a_game) {
						if (currentarena != null) {
							if (p != null) {
								final PluginInstance pli_ = minigames.get(currentindex);

								if (currentarena.getArenaState() != ArenaState.INGAME
										&& currentarena.getArenaState() != ArenaState.RESTARTING) {
									currentarena.joinPlayerLobby(playerName, this, false, true);
								} else {
									msg = false;
									currentarena.spectateArcade(playerName);
								}
								pli_.scoreboardManager.updateScoreboard(pli_.getPlugin(), currentarena);
							}
						}
					}
				}
			}
			if (msg) {
				p.sendMessage(MinigamesAPI.getAPI().getPluginInstance(pl).getMessagesConfig().arcade_joined_waiting
						.replaceAll("<count>", "0"));
			} else {
				p.sendMessage(MinigamesAPI.getAPI().getPluginInstance(pl).getMessagesConfig().arcade_joined_spectator);
			}
		} else {
			p.sendMessage(MinigamesAPI.getAPI().getPluginInstance(pl).getMessagesConfig().arcade_joined_waiting
					.replaceAll("<count>", Integer.toString(
							pl.getConfig().getInt(ArenaConfigStrings.CONFIG_ARCADE_MIN_PLAYERS) - players.size())));
		}
	}

	public void leaveArcade(final String p) {
		leaveArcade(p, true);
	}

	public void leaveArcade(final String p_, final boolean endOfGame) {
		final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(pl);

		if (players.contains(p_))
			players.remove(p_);

		if (arena.containsPlayer(p_))
			arena.removePlayer(p_);

		if (minigames.get(currentindex).getArenas().size() > 0) {
			if (minigames.get(currentindex).getArenas().get(0).containsPlayer(p_))
				minigames.get(currentindex).getArenas().get(0).leavePlayer(p_, false, false);
		}
		Bukkit.getScheduler().runTaskLater(MinigamesAPI.getAPI(), () -> {
			final Player p = Bukkit.getPlayer(p_);

			if (p != null) {
				Util.teleportPlayerFixed(p, arena.getMainLobbyTemp());
				pli.getSpectatorManager().setSpectate(p, false);

				if (!p.isOp()) {
					p.setFlying(false);
					p.setAllowFlight(false);
				}
			}
		}, 20L);
		this.clean();

		if (pli.containsGlobalPlayer(p_))
			pli.global_players.remove(p_);
		if (pli.containsGlobalLost(p_))
			pli.global_lost.remove(p_);

		if (currentarena != null) {
			MinigamesAPI.getAPI();
			final PluginInstance pli_ = MinigamesAPI.pinstances.get(currentarena.getPlugin());

			if (pli_ != null) {
				if (pli_.containsGlobalPlayer(p_))
					pli_.global_players.remove(p_);
				if (pli_.containsGlobalLost(p_))
					pli_.global_lost.remove(p_);
			}
		}

		Util.updateSign(pl, arena);

		if (endOfGame) {
			if (players.size() < 2)
				stopArcade(false);
		}
	}

	int currentlobbycount = 31;
	int currenttaskid = 0;

	public void startArcade() {
		if (started)
			return;

		started = true;
		Collections.shuffle(minigames);

		currentlobbycount = pl.getConfig().getInt(ArenaConfigStrings.CONFIG_ARCADE_LOBBY_COUNTDOWN) + 1;
		final ArcadeInstance ai = this;
		final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(pl);

		currenttaskid = Bukkit.getScheduler().runTaskTimer(MinigamesAPI.getAPI(), () -> {

			currentlobbycount--;
			if (currentlobbycount == 60 || currentlobbycount == 30 || currentlobbycount == 15 || currentlobbycount == 10
					|| currentlobbycount < 6) {
				for (final String p_ : ai.players) {
					if (Validator.isPlayerOnline(p_)) {
						final Player p = Bukkit.getPlayer(p_);
						p.sendMessage(pli.getMessagesConfig().starting_in.replaceAll("<count>",
								Integer.toString(currentlobbycount)));
					}
				}
			}

			if (currentlobbycount < 1) {
				currentindex--;
				ai.nextMinigame();
				try {
					Bukkit.getScheduler().cancelTask(currenttaskid);
				} catch (final Exception e) {
					;
				}
			}
		}, 5L, 20).getTaskId();
	}

	public void stopArcade(final boolean stopOfGame) {
		try {
			Bukkit.getScheduler().cancelTask(currenttaskid);
		} catch (final Exception e) {
			;
		}

		final ArrayList<String> tmp = new ArrayList<>(players);
		for (final String p_ : tmp) {
			leaveArcade(p_, false);
		}

		this.players.clear();
		this.started = false;
		this.in_a_game = false;
		this.currentarena = null;
		this.currentindex = 0;

		final HashSet hs = new HashSet<>();
		hs.addAll(tmp);
		tmp.clear();
		tmp.addAll(hs);

		final ArcadeInstance ai = this;

		if (stopOfGame && pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_ARCADE_INFINITE_ENABLED)) {
			if (tmp.size() > 1) {
				for (final String p_ : tmp) {
					Util.sendMessage(pl, Bukkit.getPlayer(p_),
							MinigamesAPI.getAPI().getPluginInstance(pl).getMessagesConfig().arcade_new_round
									.replaceAll("<count>", Integer.toString(pl.getConfig()
											.getInt(ArenaConfigStrings.CONFIG_ARCADE_INFINITE_SECONDS_TO_NEW_ROUND))));
				}
				Bukkit.getScheduler().runTaskLater(pl, () -> {

					for (final String p_ : tmp) {
						if (!players.contains(p_)) {
							players.add(p_);
						}
					}

				}, Math.max(40L,
						20L * pl.getConfig().getInt(ArenaConfigStrings.CONFIG_ARCADE_INFINITE_SECONDS_TO_NEW_ROUND)));
			}
		}
	}

	public void stopArcade() {
		stopArcade(false);
	}

	public void stopCurrentMinigame() {
		if (currentindex < minigames.size()) {
			final PluginInstance mg = minigames.get(currentindex);

			if (mg.getArenas().size() > 0) {
				if (mg.getPlugin().getConfig().getBoolean(ArenaConfigStrings.CONFIG_ARCADE_ARENA_TO_PREFER_ENABLED)) {
					final String arName = mg.getPlugin().getConfig()
							.getString(ArenaConfigStrings.CONFIG_ARCADE_ARENA_TO_PREFER_ARENA);
					final Arena ar = mg.getArenaByName(arName);

					if (ar != null) {
						ar.stopArena();
					}
				} else {
					minigames.get(currentindex).getArenas().get(0).stopArena();
				}
			}
		}
	}

	public void nextMinigame() {

	}

	public void nextMinigame(final long delay) {
		in_a_game = false;

		if (currentindex < minigames.size() - 1) {
			currentindex++;
		} else {
			arena.stopArena();
			return;
		}
		final ArcadeInstance ai = this;

		Bukkit.getScheduler().runTaskLater(MinigamesAPI.getAPI(), () -> {

			final ArrayList<String> tmp = new ArrayList<>(players);
			final PluginInstance mg = minigames.get(currentindex);

			if (mg.getPlugin().getConfig().getBoolean(ArenaConfigStrings.CONFIG_ARCADE_ENABLED)) {
				Arena ar = null;

				if (mg.getPlugin().getConfig().getBoolean(ArenaConfigStrings.CONFIG_ARCADE_ARENA_TO_PREFER_ENABLED)) {
					final String arName = mg.getPlugin().getConfig()
							.getString(ArenaConfigStrings.CONFIG_ARCADE_ARENA_TO_PREFER_ARENA);
					ar = mg.getArenaByName(arName);

					if (ar == null) {
						for (final Arena ar1 : mg.getArenas()) {
							if (ar1.getArenaState() == ArenaState.JOIN || ar1.getArenaState() == ArenaState.STARTING) {
								ar = ar1;
								break;
							}
						}
					}
				} else {
					for (final Arena ar1 : mg.getArenas()) {
						if (ar1.getArenaState() == ArenaState.JOIN || ar1.getArenaState() == ArenaState.STARTING) {
							ar = ar1;
							break;
						}
					}
				}
				if (ar != null) {
					in_a_game = true;
					currentarena = ar;

					final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(pl);

					for (final String p_ : tmp) {
						if (Validator.isPlayerOnline(p_)) {
							final String minigame = mg.getArenaListener().getName();

							if (!ar.containsPlayer(p_)) {
								Bukkit.getPlayer(p_)
										.sendMessage(mg.getMessagesConfig().arcade_next_minigame.replaceAll(
												"<minigame>", Character.toUpperCase(minigame.charAt(0))
														+ minigame.substring(1)));
								ar.joinPlayerLobby(p_, ai, pl.getConfig()
										.getBoolean(ArenaConfigStrings.CONFIG_ARCADE_SHOW_EACH_LOBBY_COUNTDOWN), false);
							}
							pli.getSpectatorManager().setSpectate(Bukkit.getPlayer(p_), false);
						}
					}
				} else {
					nextMinigame(5L);
				}
			} else {
				nextMinigame(5L);
			}
		}, delay);
	}
	
	public void clean() {
		final ArrayList<String> rem = new ArrayList<>();
		
		for (final String p_ : players) {
			if (!Validator.isPlayerOnline(p_)) {
				rem.add(p_);
			}
		}
		
		for (final String r : rem) {
			if (players.contains(r)) {
				players.remove(r);
			}
		}
	}
}
