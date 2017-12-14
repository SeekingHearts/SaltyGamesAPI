package main.java.me.aaron.saltygamesapi.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.aaron.saltygamesapi.Arena;
import main.java.me.aaron.saltygamesapi.ChannelStrings;
import main.java.me.aaron.saltygamesapi.MinigamesAPI;
import main.java.me.aaron.saltygamesapi.PluginInstance;

public class BungeeUtil {
	
	public static void connectToServer(final JavaPlugin pl, final String p, final String server) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(stream);
		
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (final IOException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
		}
		Bukkit.getPlayer(p).sendPluginMessage(pl, ChannelStrings.CHANNEL_BUNGEE_CORD, stream.toByteArray());
	}
	
	public static void sendSignUpdateRequest(final JavaPlugin pl, final String mg, final Arena ar) {
		final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(pl);
		MinigamesAPI.getAPI().sendSignUpdate(pli, ar);
	}

}
