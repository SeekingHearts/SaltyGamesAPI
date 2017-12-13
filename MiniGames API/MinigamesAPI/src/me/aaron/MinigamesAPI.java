package me.aaron;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.aaron.commands.CommandHandler;
import me.aaron.commands.CommandStrings;
import me.aaron.config.ArenasConfig;
import me.aaron.config.ClassesConfig;
import me.aaron.config.DefaultConfig;
import me.aaron.config.MessagesConfig;
import me.aaron.config.PartyMessagesConfig;
import me.aaron.config.StatsConfig;
import me.aaron.config.StatsGlobalConfig;
import me.aaron.guns.Guns;
import me.aaron.listeners.BlockBreak;
import me.aaron.listeners.ServerPing;
import me.aaron.listeners.SignBreak;
import me.aaron.listeners.SignChange;
import me.aaron.listeners.SignUse;
import me.aaron.utils.ArenaScoreboard;
import me.aaron.utils.BungeeUtil;
import me.aaron.utils.ParticleEffectNew;
import me.aaron.utils.Signs;
import me.aaron.utils.Util;
import net.milkbowl.vault.economy.Economy;

public class MinigamesAPI extends JavaPlugin implements PluginMessageListener {

	static MinigamesAPI instance = null;

	public static Locale LOCALE = Locale.GERMAN;

	public static Economy econ = null;
	public static boolean econEnabled = true;
	public boolean crackshot = false;

	public static boolean debug = false;

	public PartyMessagesConfig partyMessages;

	public StatsGlobalConfig statsglobal;

	public final LobbySignManager signManager = new LobbySignManager(this);

	public String internalServerVersion = "";

	public String motd;

	private Iterator<Supplier<String>> motdStrings = Collections.emptyIterator();
	public HashMap<UUID, Party> globalParty = new HashMap<>();
	public HashMap<UUID, ArrayList<Party>> global_party_invites = new HashMap<>();
	public static HashMap<JavaPlugin, PluginInstance> pinstances = new HashMap<>();

	@Override
	public void onEnable() {

		MinigamesAPI.instance = this;

		internalServerVersion = Bukkit.getServer().getClass().getPackage().getName()
				.substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);

		getLogger().info(String.format("§c§lMinigamesAPI von Aaron wurde geladen."));

		getServer().getMessenger().registerOutgoingPluginChannel(this, ChannelStrings.CHANNEL_BUNGEE_CORD);
		getServer().getMessenger().registerIncomingPluginChannel(this, ChannelStrings.CHANNEL_BUNGEE_CORD, this);

		if (!setupEconomy()) {
			getLogger().info(String.format("[%s] - Economy (Vault) wurde nicht gefunden! Economy wird deaktiviert.",
					this.getDescription().getName()));
			MinigamesAPI.econEnabled = false;
		}

		getConfig().options().header("");
		getConfig().addDefault(PluginConfigStrings.SIGNS_UPDATE_TIME, 20);
		getConfig().addDefault(PluginConfigStrings.PARTY_COMMAND_ENABLED, true);
		getConfig().addDefault(PluginConfigStrings.DEBUG, false);

		getConfig().addDefault(PluginConfigStrings.PERMISSION_PREFIX, "ancient.core");
		getConfig().addDefault(PluginConfigStrings.PERMISSION_KITS_PREFIX, "ancient.core.kits");
		getConfig().addDefault(PluginConfigStrings.PERMISSION_GUN_PREFIX, "ancient.core.guns");
		getConfig().addDefault(PluginConfigStrings.PERMISSION_SHOP_PREFIX, "ancient.core.shopitems");
		getConfig().addDefault(PluginConfigStrings.PERMISSION_GAME_PREFIX, "ancient.");

		getConfig().addDefault(PluginConfigStrings.MOTD_ENABLED, false);
		getConfig().addDefault(PluginConfigStrings.MOTD_ROTATION_SECONDS, 15);
		getConfig().addDefault(PluginConfigStrings.MOTD_TEXT,
				"<minigame> arena <arena> <state>: <players>/<maxplayers>");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_JOIN, "JOIN");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_STARTING, "STARTING");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_INGAME, "INGAME");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_RESTARTING, "RESTARTING");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_DISABLED, "DISABLED");

		for (final ArenaState state : ArenaState.values()) {
			getConfig().addDefault("signs." + state.name().toLowerCase() + ".0", state.getColorCode() + "<minigame>");
			getConfig().addDefault("signs." + state.name().toLowerCase() + ".1", state.getColorCode() + "<arena>");
			getConfig().addDefault("signs." + state.name().toLowerCase() + ".2",
					state.getColorCode() + "<count>/<maxcount>");
			getConfig().addDefault("signs." + state.name().toLowerCase() + ".3", state.getColorCode() + "");
		}

		getConfig().addDefault("signs.spec.0", "&aZUSCHAUEN");
		getConfig().addDefault("signs.spec.1", "&a<minigame>");
		getConfig().addDefault("signs.spec.2", "&a<arena>");
		getConfig().addDefault("signs.spec.3", "&a<count>/<maxcount>");

		getConfig().options().copyDefaults(true);
		saveConfig();

		partyMessages = new PartyMessagesConfig(this);
		statsglobal = new StatsGlobalConfig(this, false);

		MinigamesAPI.debug = getConfig().getBoolean(PluginConfigStrings.DEBUG);

		if (getServer().getPluginManager().getPlugin("CrackShot") != null) {
			crackshot = true;
		}

		Bukkit.getScheduler().runTaskLater(this, () -> {

			int i = 0;
			MinigamesAPI.getAPI();

			for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
				for (final Arena ar : pli.getArenas()) {
					if (ar != null) {
						if (ar.isSuccessfullyInit()) {
							Util.updateSign(pli.getPlugin(), ar);
							ar.getSmartReset().loadSmartBlocksFromFile();
						} else {
							getLogger().log(Level.WARNING, "Arena " + pli.getPlugin().getName() + "/"
									+ ar.getInternalName() + " konnte nicht inizialisiert werden (onEnable)!");
						}
					}
					i++;
				}
			}
			getLogger().info(i + " Arenen wurden gefunden.");
		}, 50L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

			MinigamesAPI.getAPI();
			for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
				for (final Arena ar : pli.getArenas()) {
					Util.updateSign(pli.getPlugin(), ar);
				}
			}
		}, 0, 20 * getConfig().getInt(PluginConfigStrings.SIGNS_UPDATE_TIME));

		if (getConfig().getBoolean(PluginConfigStrings.MOTD_ENABLED)) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

				if (motdStrings.hasNext()) {
					motd = motdStrings.next().get();
				} else {
					final List<Supplier<String>> supps = new ArrayList<>();

					for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
						for (final Arena ar : pli.getArenas()) {
							supps.add(() -> {
								String motdStr = getConfig().getString(PluginConfigStrings.MOTD_TEXT);
								String stateStr = null;

								if (ar.isSuccessfullyInit()
										&& pli.arenaSetup.getArenaEnabled(pli.getPlugin(), ar.getInternalName())) {
									switch (ar.getArenaState()) {

									case INGAME:
										stateStr = getConfig().getString(PluginConfigStrings.MOTD_STATE_INGAME);
										break;
									case JOIN:
										stateStr = getConfig().getString(PluginConfigStrings.MOTD_STATE_JOIN);
										break;
									case RESTARTING:
										stateStr = getConfig().getString(PluginConfigStrings.MOTD_STATE_RESTARTING);
										break;
									case STARTING:
										stateStr = getConfig().getString(PluginConfigStrings.MOTD_STATE_STARTING);
										break;
									default:
										stateStr = getConfig().getString(PluginConfigStrings.MOTD_STATE_DISABLED);
										break;
									}
								} else {
									stateStr = getConfig().getString(PluginConfigStrings.MOTD_STATE_DISABLED);
								}

								motdStr = motdStr.replace("<minigame>",
										ar.getPluginInstance().getPlugin().getDescription().getName());
								motdStr = motdStr.replace("<arena>", ar.getDisplayName());
								motdStr = motdStr.replace("<stats>", stateStr);
								motdStr = motdStr.replace("<players>", String.valueOf(ar.getAllPlayers().size()));
								motdStr = motdStr.replace("<minplayers>", String.valueOf(ar.getMinPlayers()));
								motdStr = motdStr.replace("<maxplayers>", String.valueOf(ar.getMaxPlayers()));
								return motdStr;
							});
						}
					}
					motdStrings = supps.iterator();

					if (motdStrings.hasNext()) {
						motd = motdStrings.next().get();
					} else {
						motd = null;
					}
				}

			}, 0, 20 * getConfig().getInt(PluginConfigStrings.MOTD_ROTATION_SECONDS));
		}
		
		registerListeners();

		Bukkit.getScheduler().runTaskLater(this, () -> {

			final ConfigurationSection configSec = getConfig().getConfigurationSection("arenas");

			if (configSec != null) {
				for (final String mg : configSec.getKeys(false)) {
					for (String ar : getConfig().getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX + mg)
							.getKeys(false)) {
						final String server = getConfig()
								.getString(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".server");

						if (server != null) {
							final String world = getConfig()
									.getString(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".world");
							final int x = getConfig()
									.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".loc.x");
							final int y = getConfig()
									.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".loc.y");
							final int z = getConfig()
									.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".loc.z");
							this.signManager.attachSign(new Location(Bukkit.getWorld(world), x, y, z), server, mg, ar,
									false);
						}

						final String specServer = getConfig()
								.getString(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specserver");

						if (specServer != null) {
							final String world = getConfig()
									.getString(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specworld");
							final int x = getConfig()
									.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specloc.x");
							final int y = getConfig()
									.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specloc.y");
							final int z = getConfig()
									.getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".specloc.z");
							this.signManager.attachSign(new Location(Bukkit.getWorld(world), x, y, z), server, mg, ar,
									true);

						}
					}
				}
			}
		}, 1L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			this.signManager.ping();
		}, 60L, 26L);
	}

	private void registerListeners() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new SignUse(), this);
		pm.registerEvents(new SignBreak(), this);
		pm.registerEvents(new SignChange(), this);
		pm.registerEvents(new ServerPing(), this);
	}

	public Arena getArenaForPlayer(String playerName) {
		for (final PluginInstance pli : this.pinstances.values()) {
			final Arena arena = pli.global_players.get(playerName);
			if (arena != null)
				return arena;
		}
		return null;
	}

	@Override
	public void onDisable() {
		for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
			// RESET ARENAS
			for (final Arena ar : pli.getArenas()) {
				if (ar != null) {
					if (ar.isSuccessfullyInit()) {
						if (ar.getArenaState() != ArenaState.JOIN) {
							ar.getSmartReset().saveSmartBlockToFile();
						}
						final ArrayList<String> tmp = new ArrayList<>(ar.getAllPlayers());

						for (final String p_ : tmp) {
							ar.leavePlayer(p_, true);
						}
						try {
							ar.getSmartReset().resetRaw();
						} catch (final Exception e) {
							getLogger().log(Level.WARNING, "Fehler beim resetten der Arena" + pli.getPlugin().getName()
									+ "/" + ar.getInternalName() + " (onDisable)", e);
						}
					} else {
						getLogger().log(Level.WARNING, "Arena " + pli.getPlugin().getName() + "/" + ar.getInternalName()
								+ " wurde nicht inizialisiert und somit auch nicht resettet (onDisable)");
					}
				}
			}
			// SAVE CONFIGS
			pli.getArenasConfig().saveConfig();
			pli.getPlugin().saveConfig();
			pli.getMessagesConfig().saveConfig();
			pli.getClassesConfig().saveConfig();
		}
	}

	public static MinigamesAPI setupAPI(final JavaPlugin plugin, final String minigame, final Class<?> arenaClass,
			final ArenasConfig arenasConfig, final MessagesConfig messageConfig, final ClassesConfig classesConfig,
			final StatsConfig statsConfig, final DefaultConfig defaultConfig, final boolean customlistener) {

		MinigamesAPI.pinstances.put(plugin, new PluginInstance(plugin, arenasConfig, messageConfig, classesConfig,
				statsConfig, new ArrayList<Arena>()));

		if (!customlistener) {
			final ArenaListener al = new ArenaListener(plugin, MinigamesAPI.pinstances.get(plugin), minigame);
			MinigamesAPI.pinstances.get(plugin).setArenaListener(al);
			Bukkit.getPluginManager().registerEvents(al, plugin);
		}
		Classes.loadClasses(plugin);
		Guns.loadGuns(plugin);
		MinigamesAPI.pinstances.get(plugin).getShopHandler().loadShopItems();
		return MinigamesAPI.instance;
	}

	public static void registerArenaListenerLater(final JavaPlugin pl, final ArenaListener arenaListener) {
		Bukkit.getPluginManager().registerEvents(arenaListener, pl);
	}

	public static void registerArenaSetup(final JavaPlugin pl, final ArenaSetup arenaSetup) {
		MinigamesAPI.pinstances.get(pl).arenaSetup = arenaSetup;
	}

	public static void registerScoreboard(final JavaPlugin pl, final ArenaScoreboard board) {
		MinigamesAPI.pinstances.get(pl).scoreboardManager = board;
	}

	public static MinigamesAPI setupAPI(final JavaPlugin pl, final String minigame, final Class<?> arenaClass) {
		MinigamesAPI.setupRaw(pl, minigame);
		return MinigamesAPI.instance;
	}

	public static MinigamesAPI setupAPI(final JavaPlugin pl, final String minigame) {
		final PluginInstance pli = MinigamesAPI.setupRaw(pl, minigame);
		pli.addLoadedArenas(Util.loadArenas(pl, pli.getArenasConfig()));
		return MinigamesAPI.instance;
	}

	public static PluginInstance setupRaw(final JavaPlugin pl, final String minigame) {
		final ArenasConfig arenasConfig = new ArenasConfig(pl);
		final MessagesConfig messagesconfig = new MessagesConfig(pl);
		final ClassesConfig classesconfig = new ClassesConfig(pl, false);
		final StatsConfig statsconfig = new StatsConfig(pl, false);
		DefaultConfig.init(pl, false);
		final PluginInstance pli = new PluginInstance(pl, arenasConfig, messagesconfig, classesconfig, statsconfig);
		MinigamesAPI.pinstances.put(pl, pli);
		final ArenaListener al = new ArenaListener(pl, MinigamesAPI.pinstances.get(pl), minigame);
		MinigamesAPI.pinstances.get(pl).setArenaListener(al);
		Bukkit.getPluginManager().registerEvents(al, pl);
		Classes.loadClasses(pl);
		pli.getShopHandler().loadShopItems();
		Guns.loadGuns(pl);
		new DefaultConfig(pl, false);
		return pli;
	}

	public PluginInstance getPluginInstance(final JavaPlugin pl) {
		return MinigamesAPI.pinstances.get(pl);
	}

	public static MinigamesAPI getAPI() {
		return MinigamesAPI.instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase(CommandStrings.START)) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(Messages.getString("MinigamesAPI.ExecuteIngame", MinigamesAPI.LOCALE));
				return true;
			}
			if (!sender
					.hasPermission(MinigamesAPI.getAPI().getPermissionPrefix() + PermissionStrings.MINIGAMES_START)) {
				sender.sendMessage(Messages.getString("MinigamesAPI.NoPermissionForStart", MinigamesAPI.LOCALE));
				return true;
			}
			final Player p = (Player) sender;
			MinigamesAPI.getAPI();
			for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
				if (pli.containsGlobalPlayer(p.getName())) {
					final Arena ar = pli.global_players.get(p.getName());
					MinigamesAPI.getAPI().getLogger().info("Arena " + ar.getInternalName()
							+ " wurde auf Grund des \"start\" Befehles von " + p.getName() + " gestartet.");
					if (ar.getArenaState() == ArenaState.JOIN
							|| (ar.getArenaState() == ArenaState.STARTING && !ar.getIngameCountdownStarted())) {
						ar.start(true);
						sender.sendMessage(pli.getMessagesConfig().arena_action
								.replaceAll(ArenaMessageStrings.ARENA, ar.getDisplayName())
								.replaceAll(ArenaMessageStrings.ACTION,
										Messages.getString("MinigamesAPI.Started", MinigamesAPI.LOCALE)));
						return true;
					}
				}
			}
			sender.sendMessage(Messages.getString("MinigamesAPI.StartNoWithinArena", MinigamesAPI.LOCALE));
			return true;
		}

		if (cmd.getName().equalsIgnoreCase(CommandStrings.PARTY)) {

			if (!MinigamesAPI.getAPI().getConfig().getBoolean(PluginConfigStrings.PARTY_COMMAND_ENABLED))
				return true;

			final CommandHandler cmdhandler = new CommandHandler();
			if (!(sender instanceof Player)) {
				sender.sendMessage(Messages.getString("MinigamesAPI.ExecuteIngame", MinigamesAPI.LOCALE));
				return true;
			}

			final Player p = (Player) sender;
			if (args.length > 0) {
				final String action = args[0];
				if (action.equalsIgnoreCase(CommandStrings.PARTY_INVITE)) {
					cmdhandler.partyInvite(sender, args,
							MinigamesAPI.getAPI().getPermissionPrefix() + PermissionStrings.MINIGAMES_PARTY,
							"/" + cmd.getName(), action, this, p);
				} else if (action.equalsIgnoreCase(CommandStrings.PARTY_ACCEPT)) {
					cmdhandler.partyAccept(sender, args, getPermissionPrefix() + PermissionStrings.MINIGAMES_PARTY,
							"/" + cmd.getName(), action, this, p);
				} else if (action.equalsIgnoreCase(CommandStrings.PARTY_KICK)) {
					cmdhandler.partyKick(sender, args, getPermissionPrefix() + PermissionStrings.MINIGAMES_PARTY,
							"/" + cmd.getName(), action, this, p);
				} else if (action.equalsIgnoreCase(CommandStrings.PARTY_LIST)) {
					cmdhandler.partyList(sender, args, getPermissionPrefix() + PermissionStrings.MINIGAMES_PARTY,
							"/" + cmd.getName(), action, this, p);
				} else if (action.equalsIgnoreCase(CommandStrings.PARTY_DISBAND)) {
					cmdhandler.partyDisband(sender, args, getPermissionPrefix() + PermissionStrings.MINIGAMES_PARTY,
							"/" + cmd.getName(), action, this, p);
				} else if (action.equalsIgnoreCase(CommandStrings.PARTY_LEAVE)) {
					cmdhandler.partyLeave(sender, args, getPermissionPrefix() + PermissionStrings.MINIGAMES_PARTY,
							"/" + cmd.getName(), action, this, p);
				} else {
					CommandHandler.sendPartyHelp("/" + cmd.getName(), sender);
				}
			} else {
				CommandHandler.sendPartyHelp("/" + cmd.getName(), sender);
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase(CommandStrings.MGAPI) || cmd.getName().equalsIgnoreCase(CommandStrings.MGLIB)
				|| cmd.getName().equalsIgnoreCase(CommandStrings.MAPI)) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_INFO)) {
					if (args.length > 1) {
						final String p = args[1];
						sender.sendMessage(Messages.getString("MinigamesAPI.DebugInfoHeader", LOCALE));
						sender.sendMessage(Messages.getString("MinigamesAPI.DebugGlobalPlayers", LOCALE));

						for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
							if (pli.global_players.containsKey(p)) {
								sender.sendMessage(
										String.format(Messages.getString("MinigamesAPI.DebugGlobalPlayersLine", LOCALE),
												pli.getPlugin().getName()));
							}
						}
						sender.sendMessage(Messages.getString("MinigamesAPI.DebugGlobalLost", LOCALE));
						for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
							if (pli.global_lost.containsKey(p)) {
								sender.sendMessage(
										String.format(Messages.getString("MinigamesAPI.DebugGlobalLostLine", LOCALE),
												pli.getPlugin().getName()));
							}
						}
						sender.sendMessage(Messages.getString("MinigamesAPI.DebugSpectatorManager", LOCALE));
						for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
							pli.getSpectatorManager();
							if (SpectatorManager.isSpectating(Bukkit.getPlayer(p))) {
								sender.sendMessage(String.format(
										Messages.getString("MinigamesAPI.DebugSpectatorManagerLine", LOCALE),
										pli.getPlugin().getName()));
							}
						}
						sender.sendMessage(Messages.getString("MinigamesAPI.DebugArenas", LOCALE));
						for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
							if (pli.global_players.containsKey(p)) {
								sender.sendMessage(
										String.format(Messages.getString("MinigamesAPI.DebugArenasLine", LOCALE),
												pli.global_players.get(p).getInternalName(),
												pli.global_players.get(p).getArenaState().name()));
							}
						}
					} else {
						for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
							sender.sendMessage(String.format(Messages.getString("MinigamesAPI.DebugAllPlayers", LOCALE),
									pli.getPlugin().getName()));

							for (final Arena ar : pli.getArenas()) {
								if (ar != null) {
									for (final String p_ : ar.getAllPlayers()) {
										sender.sendMessage(String.format(
												Messages.getString("MinigamesAPI.DebugAllPlayerLine", LOCALE),
												pli.getPlugin().getName(), ar.getInternalName(), p_));
									}
								}
							}
						}
					}
				} else if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_DEBUG)) {
					MinigamesAPI.debug = !MinigamesAPI.debug;
					this.getConfig().set(PluginConfigStrings.DEBUG, MinigamesAPI.debug);
					this.saveConfig();
					sender.sendMessage(String.format(Messages.getString("MinigamesAPI.SetDebugMode", LOCALE),
							String.valueOf(MinigamesAPI.debug)));
				} else if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_LIST)) {
					int c = 0;
					MinigamesAPI.getAPI();
					for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
						c++;
						sender.sendMessage(String.format(Messages.getString("MinigamesAPI.ListArenasLine", LOCALE),
								pli.getPlugin().getName(), pli.getArenas().size()));
						return false;
					}
					if (c < 1) {
						sender.sendMessage(Messages.getString("MinigamesAPI.NoMinigamesFound", LOCALE));
					}
				} else if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_TITLE)) {
					if (args.length > 1) {
						if (sender instanceof Player) {
							Effects.playTitle((Player) sender, args[1], 0);
							return false;
						}
					}
				} else if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_SUBTITLE)) {
					if (args.length > 1) {
						if (sender instanceof Player) {
							Effects.playTitle((Player) sender, args[1], 1);
							return false;
						}
					}
				} else if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_SIGNS)) {
					if (sender instanceof Player) {
						MinigamesAPI.getAPI();

						for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
							for (final Arena ar : pli.getArenas()) {
								Util.updateSign(pli.getPlugin(), ar);
								sender.sendMessage(Messages.getString("MinigamesAPI.AllSignsUpdated", LOCALE));
								return false;
							}
						}
					} else if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_HOLOGRAM)) {
						if (sender instanceof Player) {
							final Player p = (Player) sender;
							p.sendMessage(Messages.getString("MinigamesAPI.PlayingHologram", LOCALE));
							Effects.playHologram(p, p.getLocation(),
									ChatColor.values()[(int) (Math.random() * ChatColor.values().length - 1)] + "TEST",
									true, true);
							return false;
						}
					} else if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_STATS_HOLOGRAM)) {
						if (sender instanceof Player) {
							final Player p = (Player) sender;

							if (args.length > 1) {
								final PluginInstance pli = this
										.getPluginInstance((JavaPlugin) Bukkit.getPluginManager().getPlugin(args[1]));
								p.sendMessage(Messages.getString("MinigamesAPI.PlayingStatsHologram", LOCALE));

								Effects.playHologram(p, p.getLocation().add(0D, 1D, 0D),
										ChatColor.values()[(int) (Math.random() * ChatColor.values().length - 1)]
												+ Messages.getString("MinigamesAPI.StatsWin", LOCALE)
												+ pli.getStatsInstance().getWins(p.getName()),
										false, false);
								Effects.playHologram(p, p.getLocation().add(0D, 0.75D, 0D),
										ChatColor.values()[(int) (Math.random() * ChatColor.values().length - 1)]
												+ Messages.getString("MinigamesAPI.StatsPotions", LOCALE)
												+ pli.getStatsInstance().getPoints(p.getName()),
										false, false);
								Effects.playHologram(p, p.getLocation().add(0D, 0.5D, 0D),
										ChatColor.values()[(int) (Math.random() * ChatColor.values().length - 1)]
												+ Messages.getString("MinigamesAPI.StatsKills", LOCALE)
												+ pli.getStatsInstance().getKills(p.getName()),
										false, false);
								Effects.playHologram(p, p.getLocation().add(0D, 0.25D, 0D),
										ChatColor.values()[(int) (Math.random() * ChatColor.values().length - 1)]
												+ Messages.getString("MinigamesAPI.StatsDeaths", LOCALE)
												+ pli.getStatsInstance().getDeaths(p.getName()),
										false, false);
								return false;
							}
						}
					} else if (args[0].equalsIgnoreCase(CommandStrings.MGLIB_JOIN)) {
						if (args.length > 3) {
							String game = args[1];
							String ar = args[2];
							String server = args[3];

							Player p = null;

							if (sender instanceof Player)
								p = (Player) sender;
							if (args.length > 3)
								p = Bukkit.getPlayer(args[3]);
							if (p == null)
								return true;

							ByteArrayDataOutput out = ByteStreams.newDataOutput();

							try {
								out.writeUTF("Forward");
								out.writeUTF("ALL");
								out.writeUTF(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_BACK);

								ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
								DataOutputStream msgout = new DataOutputStream(msgbytes);
								String info = game + ":" + ar + ":" + p.getName();
								getLogger().info("Spieler beigetreten: " + info);
								msgout.writeUTF(info);

								out.writeShort(msgbytes.toByteArray().length);
								out.write(msgbytes.toByteArray());

								Bukkit.getServer().sendPluginMessage(this, ChannelStrings.CHANNEL_BUNGEE_CORD,
										out.toByteArray());
							} catch (Exception e) {
								MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
							}
							connectToServer(this, p.getName(), server);
						} else {
							sender.sendMessage(ChatColor.GRAY + "Usage: &c/join <game> <arena> <server> [player]");
							sender.sendMessage(ChatColor.GRAY + "[player] ist optional!");
						}
					}
					return true;
				}
				if (args.length < 1) {
					sender.sendMessage(String.format(Messages.getString("MinigamesAPI.MinigamesLibHeader", LOCALE),
							this.getDescription().getVersion()));

					int c = 0;
					MinigamesAPI.getAPI();

					for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
						c++;
						sender.sendMessage(String.format(Messages.getString("MinigamesAPI.PluginArenaCount", LOCALE),
								pli.getPlugin().getName(), pli.getArenas().size()));
					}
					if (c < 1) {
						sender.sendMessage(String.format(Messages.getString("MinigamesAPI.NoMinigamesFound", LOCALE)));
					}

					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommands", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsInfo", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsDebug", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsList", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsTitle", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsSubtitle", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsHologram", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsSigns", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsPotionEffect", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsStatsHologram", LOCALE));
					sender.sendMessage(Messages.getString("MinigamesAPI.MgApiSubCommandsJoin", LOCALE));
				}
				if (sender instanceof Player && args.length > 0) {
					final Player p = (Player) sender;
					boolean cout = false;

					for (final ParticleEffectNew pe : ParticleEffectNew.values()) {
						if (pe.name().equalsIgnoreCase(args[0])) {
							cout = true;
						}
					}
					if (!cout) {
						sender.sendMessage(Messages.getString("MinigamesAPI.CannotFindParticleEffect", LOCALE));
						return true;
					}
					final ParticleEffectNew eff = ParticleEffectNew.valueOf(args[0]);
					eff.setId(152);

					for (float i = 0; i < 10; i++) {
						eff.animateReflected(p, p.getLocation().clone().add(i / 5F, i / 5F, i / 5F), 1F, 2);
					}

					p.getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, 152);
					p.getWorld().playEffect(p.getLocation().add(0D, 1D, 0D), Effect.STEP_SOUND, 152);
				}
			}
			return true;
		}
		return false;
	}

	private void connectToServer(JavaPlugin pl, String p, String server) {
		ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(streamOut);

		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
		Bukkit.getPlayer(p).sendPluginMessage(pl, ChannelStrings.CHANNEL_BUNGEE_CORD, streamOut.toByteArray());
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		MinigamesAPI.econ = rsp.getProvider();
		return MinigamesAPI.econ != null;
	}

	public void sendSignUpdate(final PluginInstance pli, final Arena ar) {
		String signString;

		if (ar == null) {
			signString = pli.getPlugin().getName() + ":null:JOIN:0:0";
		} else {
			signString = pli.getPlugin().getName() + ":" + ar.getInternalName() + ":" + ar.getArenaState().toString()
					+ ":" + ar.getAllPlayers().size() + ":" + ar.getMaxPlayers();
		}

		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();

			try {
				out.writeUTF("Forward");
				out.writeUTF("ALL");
				out.writeUTF(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_SIGN);

				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				msgout.writeUTF(signString);

				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());

				Bukkit.getServer().sendPluginMessage(this, ChannelStrings.CHANNEL_BUNGEE_CORD, out.toByteArray());
			} catch (Exception e) {
				MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Fehler beim senden der extra sign request", e);
		}
	}

	public void updateSign(String mg, String arName, String arState, SignChangeEvent e) {
		int count = 0;
		int maxcount = 10;
		e.setLine(0,
				Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".0")
						.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
						.replace("<arena>", arName).replace("minigame", mg)));
		e.setLine(1,
				Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".1")
						.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
						.replace("<arena>", arName).replace("minigame", mg)));
		e.setLine(2,
				Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".2")
						.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
						.replace("<arena>", arName).replace("minigame", mg)));
		e.setLine(3,
				Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".3")
						.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
						.replace("<arena>", arName).replace("minigame", mg)));
	}

	public void updateSign(String mg, String arName, String arState, int count, int maxcount) {
		Sign sign = getSignFromArena(mg, arName);
		if (sign != null) {
			sign.getBlock().getChunk().load();
			sign.setLine(0, Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".0")
					.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
					.replace("<arena>", arName).replace("<minigame>", mg)));
			sign.setLine(1, Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".1")
					.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
					.replace("<arena>", arName).replace("<minigame>", mg)));
			sign.setLine(2, Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".2")
					.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
					.replace("<arena>", arName).replace("<minigame>", mg)));
			sign.setLine(3, Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".3")
					.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
					.replace("<arena>", arName).replace("<minigame>", mg)));
			sign.update();
		}
	}

	private Sign getSignFromArena(String mg, String ar) {
		if (!getConfig().isSet(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".world"))
			return null;

		Location loc = new Location(
				Bukkit.getServer()
						.getWorld(getConfig().getString(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".world")),
				getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + "loc.x"),
				getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + "loc.y"),
				getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + "loc.z"));
		if (loc != null) {
			if (loc.getWorld() != null) {
				if (loc.getBlock().getState() != null) {
					BlockState bs = loc.getBlock().getState();
					Sign sign = null;
					if (bs instanceof Sign) {
						sign = (Sign) bs;
					}
					return sign;
				}
			}
		}
		return null;
	}

	@Override
	public void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
		if (!channel.equals(ChannelStrings.CHANNEL_BUNGEE_CORD)) {
			return;
		}
		final ByteArrayDataInput in = ByteStreams.newDataInput(message);
		final String subchannel = in.readUTF();
		if (subchannel.equals(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_BACK)) {
			final short len = in.readShort();
			final byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			final DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				final String playerData = msgin.readUTF();
				final String[] split = playerData.split(":"); //$NON-NLS-1$
				final String plugin_ = split[0];
				final String arena = split[1];
				final String playername = split[2];

				if (debug) {
					this.getLogger().info(
							"channel message: " + ChannelStrings.SUBCHANNEL_MINIGAMESLIB_BACK + " -> " + playerData); //$NON-NLS-1$ //$NON-NLS-2$
				}

				JavaPlugin plugin = null;
				for (final JavaPlugin pl : MinigamesAPI.pinstances.keySet()) {
					if (pl.getName().contains(plugin_)) {
						plugin = pl;
						break;
					}
				}
				if (plugin != null) {
					final Arena a = MinigamesAPI.pinstances.get(plugin).getArenaByName(arena);
					if (a != null) {
						if (a.getArenaState() != ArenaState.INGAME && a.getArenaState() != ArenaState.RESTARTING
								&& !a.containsPlayer(playername)) {
							Bukkit.getScheduler().runTaskLater(this, () -> {
								if (!a.containsPlayer(playername)) {
									a.joinPlayerLobby(playername);
								}
							}, 20L);
						}
					} else {
						this.getLogger().warning(
								"Arena " + arena + " for MINIGAMESLIB_BACK couldn't be found, please fix your setup."); //$NON-NLS-1$//$NON-NLS-2$
					}
				}
			} catch (final IOException e) {
				MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
			}
		} else if (subchannel.equals(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_REQUEST)) {
			// Lobby requests sign data
			final short len = in.readShort();
			final byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			final DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				final String requestData = msgin.readUTF();
				final String[] split = requestData.split(":"); //$NON-NLS-1$
				final String plugin_ = split[0];
				final String arena = split[1];

				if (debug) {
					this.getLogger().info("channel message: " + ChannelStrings.SUBCHANNEL_MINIGAMESLIB_REQUEST + " -> " //$NON-NLS-1$ //$NON-NLS-2$
							+ requestData);
				}

				for (final JavaPlugin pl : MinigamesAPI.pinstances.keySet()) {
					if (pl.getName().contains(plugin_)) {
						final Arena a = MinigamesAPI.pinstances.get(pl).getArenaByName(arena);
						if (a != null) {
							BungeeUtil.sendSignUpdateRequest(pl, pl.getName(), a);
						} else {
							this.getLogger().warning("Arena " + arena //$NON-NLS-1$
									+ " for MINIGAMESLIB_REQUEST couldn't be found, please fix your setup."); //$NON-NLS-1$
						}
						break;
					}
				}
			} catch (final IOException e) {
				MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
			}
		} else if (subchannel.equals(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_SIGN)) {
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				final String signData = msgin.readUTF();
				final String[] splitted = signData.split(":"); //$NON-NLS-1$
				final String plugin_ = splitted[0];
				final String arena = splitted[1];
				final String arenastate = splitted[2];
				final int count = Integer.parseInt(splitted[3]);
				final int maxcount = Integer.parseInt(splitted[4]);

				if (debug) {
					this.getLogger().info(
							"channel message: " + ChannelStrings.SUBCHANNEL_MINIGAMESLIB_SIGN + " -> " + signData); //$NON-NLS-1$ //$NON-NLS-2$
				}

				Bukkit.getScheduler().runTaskLater(this, new Runnable() {
					public void run() {
						updateSign(plugin_, arena, arenastate, count, maxcount);
					}
				}, 10L);
			} catch (IOException e) {
				MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
			}
		}
	}

	// HAS PARTY
	public boolean hasParty(UUID owner) {
		return globalParty.containsKey(owner);
	}

	public Party getParty(UUID owner) {
		return globalParty.get(owner);
	}

	public Party createParty(UUID owner) {
		final Party party = new Party(owner);
		this.globalParty.put(owner, party);
		return party;
	}

	public void removeParty(UUID owner) {
		this.globalParty.remove(owner);
	}

	public void addPartyInvite(UUID invedP, Party party) {
		this.global_party_invites.computeIfAbsent(invedP, u -> new ArrayList<>()).add(party);
	}

	public boolean hasPartyInvites(UUID invedP) {
		return this.global_party_invites.containsKey(invedP);
	}

	public Iterable<Party> getPartyInvites(UUID invedP) {
		return this.global_party_invites.get(invedP);
	}

	public void removePartyInvites(UUID invedP) {
		this.global_party_invites.remove(invedP);
	}
	
	public Iterable<Party> getParties() {
		return globalParty.values();
	}

	// GET PLAYER
	public static UUID playerToUUID(String p_) {
		final Player p = Bukkit.getPlayer(p_);
		if (p != null) {
			return playerToUUID(p);
		}
		return null;
	}

	public static UUID playerToUUID(Player p) {
		return p.getUniqueId();
	}

	public static Player UUIDToPlayer(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}

	// GET PERMISSION
	public String getPermissionPrefix() {
		return getConfig().getString(PluginConfigStrings.PERMISSION_PREFIX, "minigames.core");
	}

	public String getPermissionGamePrefix(String game) {
		return getConfig().getString(PluginConfigStrings.PERMISSION_GAME_PREFIX, "minigames.") + game;
	}

	public String getPermissionGunPrefix() {
		return getConfig().getString(PluginConfigStrings.PERMISSION_GUN_PREFIX, "minigames.guns");
	}

	public String getPermissionShopPrefix() {
		return getConfig().getString(PluginConfigStrings.PERMISSION_SHOP_PREFIX, "minigames.shopitems");
	}

	public String getPermissionKitPrefix() {
		return getConfig().getString(PluginConfigStrings.PERMISSION_KITS_PREFIX, "minigames.kits");
	}

	// GET STUFF
	public boolean crackshotAvailable() {
		return crackshot;
	}

	public boolean economyAvailable() {
		return econEnabled;
	}

	public String getServerBySignLocation(Location Signloc) {
		if (getConfig().isSet(ArenaConfigStrings.ARENAS_PREFIX)) {
			for (String mg_key : getConfig().getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX).getKeys(false)) {
				for (String ar_key : getConfig().getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX + mg_key + ".").getKeys(false)) {
					if (getConfig().isString(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".world")) {
						Location loc = new Location(Bukkit.getWorld(getConfig().getString(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".world")),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".loc.x"),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".loc.y"),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".loc.z"));
						
						if (loc.distance(Signloc) < 1) {
							return getConfig().getString(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".server");
						}
					}
					if (getConfig().isString(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specworld")) {
						Location loc = new Location(Bukkit.getWorld(getConfig().getString(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specworld")),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specloc.x"),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specloc.y"),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specloc.z"));
						if (loc.distance(Signloc) < 1) {
							return getConfig().getString(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + ar_key + ".specserver");
						}
					}
				}
			}
		}
		return null;
	}
	
	public String getInfoBySignLocation(Location sign)
    {
        if (getConfig().isSet(ArenaConfigStrings.ARENAS_PREFIX))
        {
            for (String mg_key : getConfig().getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX).getKeys(false))
            {
                for (String arena_key : getConfig().getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX + mg_key + ".").getKeys(false))
                {
                    final ConfigurationSection section = getConfig().getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key);
                    if (section.contains("world"))
                    {
                        Location l = new Location(Bukkit.getWorld(getConfig().getString(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key + ".world")),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key + ".loc.x"),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key + ".loc.y"),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key + ".loc.z"));
                        if (l.distance(sign) < 1)
                        {
                            return mg_key + ":" + arena_key + ":join";
                        }
                    }
                    if (section.contains("specworld"))
                    {
                        Location l = new Location(Bukkit.getWorld(getConfig().getString(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key + ".specworld")),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key + ".specloc.x"),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key + ".specloc.y"),
                                getConfig().getInt(ArenaConfigStrings.ARENAS_PREFIX + mg_key + "." + arena_key + ".specloc.z"));
                        if (l.distance(sign) < 1)
                        {
                            return mg_key + ":" + arena_key + ":spec";
                        }
                    }
                }
            }
        }
        return "";
    }
	
	public void letPlayerJoinServer(String server, final Player p, final String signInfo) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			
			try {
				out.writeUTF("Forward");
				out.writeUTF("ALL");
				out.writeUTF(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_BACK);
				
				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				String info = signInfo + ":" + p.getName();
				
				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());
				
				Bukkit.getServer().sendPluginMessage(this, ChannelStrings.CHANNEL_BUNGEE_CORD, out.toByteArray());
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Fehler beim Senden der Nachricht", e);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Fehler beim Senden der ersten Schilderanfrage - Fehlerhafte(r/s) arena/server/minigame?", e);
		}
		connectToServer(this, p.getName(), server);
	}

}
