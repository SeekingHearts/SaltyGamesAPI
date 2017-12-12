package me.aaron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import me.aaron.utils.IconMenu;
import me.aaron.utils.Util;
import me.aaron.utils.Validator;

public class SpectatorManager {

	JavaPlugin pl;

	private final HashMap<String, IconMenu> lasticonMenu = new HashMap<>();

	public SpectatorManager(final JavaPlugin pl) {
		this.pl = pl;
		this.setup();
	}

	public void setup() {
		if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("spectators") == null)
			Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("spectators");
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("spectators").setCanSeeFriendlyInvisibles(true);
		this.clear();
	}

	public void setSpectate(final Player p, final boolean spectate) {
		try {
			if (spectate) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 5), true);
				Bukkit.getScoreboardManager().getMainScoreboard().getTeam("spectators").addPlayer(p);
			} else {
				if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam("spectators").hasPlayer(p)) {
					Bukkit.getScoreboardManager().getMainScoreboard().getTeam("spectators").removePlayer(p);
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
			}
		} catch (final Exception e) {
			;
		}
	}

	public static boolean isSpectating(final Player p) {
		return Bukkit.getScoreboardManager().getMainScoreboard().getTeam("spectators").hasPlayer(p);
	}

	private void clear() {
		final Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("spectators");
		final ArrayList<OfflinePlayer> offp_set = new ArrayList<>(t.getPlayers());

		for (final OfflinePlayer offp : offp_set) {
			t.removePlayer(offp);
		}
	}

	public void openSpectatorGUI(final Player p, final Arena ar) {
		IconMenu iconMenu;
		final int mincount = ar.getAllPlayers().size();

		if (lasticonMenu.containsKey(p.getName())) {
			iconMenu = lasticonMenu.get(p.getName());
		} else {
			iconMenu = new IconMenu(MinigamesAPI.getAPI().getPluginInstance(pl).getMessagesConfig().spectator_item,
					(9 > mincount - 1) ? 9 : Math.round(mincount / 9) * 9 + 9, e -> {
						if (e.getPlayer().getName().equalsIgnoreCase(p.getName())) {
							final String d = e.getName();
							final Player p_ = e.getPlayer();

							final Player p__ = Bukkit.getPlayer(d);

							if (p__ != null && p_ != null)
								Util.teleportPlayerFixed(p_, new Location(p_.getWorld(), p__.getLocation().getX(),
										p__.getLocation().getY(), p__.getLocation().getZ()));
						}
						e.setWillClose(true);
					}, pl);
		}
		iconMenu.clear();
		
		final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(pl);
		int c = 0;
		for (final String p__ : ar.getAllPlayers()) {
			final Player p_ = Bukkit.getPlayer(p__);
			if (p_ != null) {
				if (pli.global_players.containsKey(p__) && !pli.global_lost.containsKey(p__)) {
					if (ar.getInternalName().equalsIgnoreCase(pli.global_players.get(p__).getInternalName())) {
						iconMenu.setOption(c, Util.getCustomHead(p__), p__, "");
						c++;
					}
				}
			}
		}
		iconMenu.open(p);
		lasticonMenu.put(p.getName(), iconMenu);
	}

	HashMap<String, ArrayList<String>> pspecs = new HashMap<>();
	HashMap<String, ArrayList<String>> splayers = new HashMap<>();
	
	public void hideSpectator(final Player spec, final ArrayList<String> players) {
		for (final String p_ : players) {
			if (Validator.isPlayerOnline(p_)) {
				final Player p = Bukkit.getPlayer(p_);
				spec.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 600, 1));
				if (pspecs.containsKey(p_)) {
					final ArrayList<String> t = pspecs.get(p_);
					t.add(spec.getName());
					pspecs.put(p_, t);
				} else {
					pspecs.put(p_, new ArrayList<>(Arrays.asList(spec.getName())));
				}
			}
		}
		splayers.put(spec.getName(), players);
	}
	
	public void showSpectator(final Player spec) {
		if (splayers.containsKey(spec.getName())) {
			for (final String p_ : splayers.get(spec.getName())) {
				if (Validator.isPlayerOnline(p_)) {
					final Player p = Bukkit.getPlayer(p_);
					if (pspecs.containsKey(p_)) {
						final ArrayList<String> t = pspecs.get(p_);
						t.remove(spec.getName());
						spec.removePotionEffect(PotionEffectType.INVISIBILITY);
						pspecs.put(p_, t);
					}
				}
			}
			splayers.remove(spec.getName());
		}
	}
	
	public void showSpectators(final Player p) {
		if (pspecs.containsKey(p.getName())) {
			for (final String p_ : pspecs.get(p.getName())) {
				if (Validator.isPlayerOnline(p_)) {
					final Player spec = Bukkit.getPlayer(p_);
					
					if (splayers.containsKey(p_)) {
						final ArrayList<String> t = splayers.get(p_);
						t.remove(spec.getName());
						spec.removePotionEffect(PotionEffectType.INVISIBILITY);
						splayers.put(p_, t);
					}
				}
			}
			pspecs.remove(p.getName());
		}
	}
}
