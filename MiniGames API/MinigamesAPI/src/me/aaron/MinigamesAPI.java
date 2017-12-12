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
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.aaron.config.PartyMessagesConfig;
import me.aaron.config.StatsGlobalConfig;
import me.aaron.utils.BungeeUtil;
import me.aaron.utils.Signs;
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
	
	public String internalServerVersion = "";
	
	private String motd;
	
	private Iterator<Supplier<String>> motdStrings = Collections.emptyIterator();
	
	
	public HashMap<String, Party> globalParty = new HashMap<>();
	public HashMap<String, ArrayList<Party>> global_party_invites = new HashMap<>();
	public static HashMap<JavaPlugin, PluginInstance> pinstances = new HashMap<>();
	
	
	@Override
	public void onEnable() {
		
		MinigamesAPI.instance = this;
		
		internalServerVersion = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);
		
		getLogger().info(String.format("§c§lMinigamesAPI wurde geladen."));
		
		getServer().getMessenger().registerOutgoingPluginChannel(this, ChannelStrings.CHANNEL_BUNGEE_CORD);
		getServer().getMessenger().registerIncomingPluginChannel(this, ChannelStrings.CHANNEL_BUNGEE_CORD, this);
		
		if (!setupEconomy()) {
			getLogger().info(String.format("[%s] - Economy (Vault) wurde nicht gefunden! Economy wird deaktiviert.", this.getDescription().getName()));
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
		getConfig().addDefault(PluginConfigStrings.MOTD_TEXT, "<minigame> arena <arena> <state>: <players>/<maxplayers>");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_JOIN, "JOIN");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_STARTING, "STARTING");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_INGAME, "INGAME");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_RESTARTING, "RESTARTING");
		getConfig().addDefault(PluginConfigStrings.MOTD_STATE_DISABLED, "DISABLED");
		
		for (final ArenaState state : ArenaState.values()) {
			getConfig().addDefault("signs." + state.name().toLowerCase() + ".0", state.getColorCode() + "<minigame>");
			getConfig().addDefault("signs." + state.name().toLowerCase() + ".1", state.getColorCode() + "<arena>");
			getConfig().addDefault("signs." + state.name().toLowerCase() + ".2", state.getColorCode() + "<count>/<maxcount>");
			getConfig().addDefault("signs." + state.name().toLowerCase() + ".3", state.getColorCode() + "");
		}
		
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
//				for (final Arena ar : pli.get)
			}
			
		}, 50L);
	}
	
	public PluginInstance getPluginInstance(final JavaPlugin pl) {
		return MinigamesAPI.pinstances.get(pl);
	}
	
	public static MinigamesAPI getAPI() {
		return MinigamesAPI.instance;
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
	
	public boolean economyAvailable() {
		return econEnabled;
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
		e.setLine(0, Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".0")
				.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
				.replace("<arena>", arName).replace("minigame", mg)));
		e.setLine(1, Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".1")
				.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
				.replace("<arena>", arName).replace("minigame", mg)));
		e.setLine(2, Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".2")
				.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
				.replace("<arena>", arName).replace("minigame", mg)));
		e.setLine(3, Signs.format(getConfig().getString("signs." + arState.toLowerCase() + ".3")
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
		
		Location loc = new Location(Bukkit.getServer().getWorld(getConfig().getString(
				ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + ".world")), getConfig().getInt(
						ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + "loc.x"), getConfig().getInt(
								ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + "loc.y"), getConfig().getInt(
										ArenaConfigStrings.ARENAS_PREFIX + mg + "." + ar + "loc.z"));
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
		if (!channel.equals(ChannelStrings.CHANNEL_BUNGEE_CORD))
        {
            return;
        }
        final ByteArrayDataInput in = ByteStreams.newDataInput(message);
        final String subchannel = in.readUTF();
        if (subchannel.equals(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_BACK))
        {
            final short len = in.readShort();
            final byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);
            
            final DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try
            {
                final String playerData = msgin.readUTF();
                final String[] split = playerData.split(":"); //$NON-NLS-1$
                final String plugin_ = split[0];
                final String arena = split[1];
                final String playername = split[2];
                
                if (debug)
                {
                    this.getLogger().info("channel message: " + ChannelStrings.SUBCHANNEL_MINIGAMESLIB_BACK + " -> " + playerData); //$NON-NLS-1$ //$NON-NLS-2$
                }
                
                JavaPlugin plugin = null;
                for (final JavaPlugin pl : MinigamesAPI.pinstances.keySet())
                {
                    if (pl.getName().contains(plugin_))
                    {
                        plugin = pl;
                        break;
                    }
                }
                if (plugin != null)
                {
                    final Arena a = MinigamesAPI.pinstances.get(plugin).getArenaByName(arena);
                    if (a != null)
                    {
                        if (a.getArenaState() != ArenaState.INGAME && a.getArenaState() != ArenaState.RESTARTING && !a.containsPlayer(playername))
                        {
                            Bukkit.getScheduler().runTaskLater(this, () -> {
                                if (!a.containsPlayer(playername))
                                {
                                    a.joinPlayerLobby(playername);
                                }
                            }, 20L);
                        }
                    }
                    else
                    {
                        this.getLogger().warning("Arena " + arena + " for MINIGAMESLIB_BACK couldn't be found, please fix your setup."); //$NON-NLS-1$//$NON-NLS-2$
                    }
                }
            }
            catch (final IOException e)
            {
                MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
            }
        }
        else if (subchannel.equals(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_REQUEST))
        {
            // Lobby requests sign data
            final short len = in.readShort();
            final byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);
            
            final DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try
            {
                final String requestData = msgin.readUTF();
                final String[] split = requestData.split(":"); //$NON-NLS-1$
                final String plugin_ = split[0];
                final String arena = split[1];
                
                if (debug)
                {
                    this.getLogger().info("channel message: " + ChannelStrings.SUBCHANNEL_MINIGAMESLIB_REQUEST + " -> " + requestData); //$NON-NLS-1$ //$NON-NLS-2$
                }
                
                for (final JavaPlugin pl : MinigamesAPI.pinstances.keySet())
                {
                    if (pl.getName().contains(plugin_))
                    {
                        final Arena a = MinigamesAPI.pinstances.get(pl).getArenaByName(arena);
                        if (a != null)
                        {
                            BungeeUtil.sendSignUpdateRequest(pl, pl.getName(), a);
                        }
                        else
                        {
                            this.getLogger().warning("Arena " + arena + " for MINIGAMESLIB_REQUEST couldn't be found, please fix your setup."); //$NON-NLS-1$//$NON-NLS-2$
                        }
                        break;
                    }
                }
            }
            catch (final IOException e)
            {
                MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
            }
        }
        else if (subchannel.equals(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_SIGN))
        {
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
                
                if (debug)
                {
                    this.getLogger().info("channel message: " + ChannelStrings.SUBCHANNEL_MINIGAMESLIB_SIGN + " -> " + signData); //$NON-NLS-1$ //$NON-NLS-2$
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
	
	
//	GET PLAYER
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
	
//	GET PERMISSION
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
	
//	GET STUFF
	public boolean crackshotAvailable() {
		return crackshot;
	}
	
}
