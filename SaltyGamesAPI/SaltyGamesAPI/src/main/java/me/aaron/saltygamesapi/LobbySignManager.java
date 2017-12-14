package main.java.me.aaron.saltygamesapi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import main.java.me.aaron.saltygamesapi.utils.Signs;

public class LobbySignManager {

	private final Map<Location, SignData> signs = new HashMap<>();

	final Plugin pl;

	public LobbySignManager(Plugin pl) {
		this.pl = pl;
	}

	public void attachSign(Location loc, String serverName, String minigamesName, String arName, boolean spec) {

		final SignData data = new SignData(loc, serverName, minigamesName, arName, spec);
		signs.put(loc, data);
		data.setSignData(null);
	}
	
	public void attachSign(Location loc, String serverName, String minigamesName, String arName, boolean spec, SignChangeEvent e) {
		
		final SignData data = new SignData(loc, serverName, minigamesName, arName, spec);
		signs.put(loc, data);
		data.setSignData(e);
	}
	
	public void requestSignUpdate(Location loc) {
		final SignData data = this.signs.get(loc);
		
		if (data != null) {
			data.requestServerSign();
		}
	}
	
	public void detachSign(Location loc) {
		this.signs.remove(loc);
	}
	
	public void updateSign(Location loc, String arState, int count, int maxcount) {
		final SignData data = this.signs.get(loc);
		
		if (data != null) {
			data.setSignData(count, maxcount, arState, null);
			data.updateResponseData();
		}
	}
	

	private final class SignData {
		private final Location loc;
		private final String serverName;
		private final String arenaName;
		private final String minigameName;
		private final boolean spec;

		int lastMaxPlayers = 10;

		LocalDateTime lastRequest = LocalDateTime.now();
		LocalDateTime lastResponse = lastRequest;

		LocalDateTime lastSignWarning = null;

		public SignData(Location loc, String serverName, String minigameName, String arName, boolean spec) {
			this.loc = loc;
			this.serverName = serverName;
			this.minigameName = minigameName;
			this.arenaName = arName;
			this.spec = spec;
		}

		public void updateResponseData() {
			lastResponse = LocalDateTime.now();
		}

		public void requestServerSign() {
			lastRequest = LocalDateTime.now();

			try {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();

				try {
					out.writeUTF("Forward");
					out.writeUTF(this.serverName);
					out.writeUTF(ChannelStrings.SUBCHANNEL_MINIGAMESLIB_REQUEST);

					ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
					DataOutputStream msgout = new DataOutputStream(msgbytes);
					msgout.writeUTF(minigameName + ":" + arenaName);

					out.writeShort(msgbytes.toByteArray().length);
					out.write(msgbytes.toByteArray());

					Bukkit.getServer().sendPluginMessage(LobbySignManager.this.pl, ChannelStrings.CHANNEL_BUNGEE_CORD,
							out.toByteArray());
				} catch (Exception e) {
					MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
				}
			} catch (Exception e) {
				LobbySignManager.this.pl.getLogger().log(Level.WARNING,
						"Fehler beim Senden einer extra Schildanfrage: ", e);
			}
		}

		public void setSignData(SignChangeEvent e) {

		}

		public void setSignData(int count, int maxcount, String arState, SignChangeEvent e) {
			this.lastMaxPlayers = maxcount;

			final FileConfiguration config = LobbySignManager.this.pl.getConfig();

			final String line0 = Signs.format(config.getString("signs." + arState.toLowerCase() + ".0")
					.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
					.replace("<arena>", this.arenaName).replace("<minigame>", this.minigameName));
			final String line1 = Signs.format(config.getString("signs." + arState.toLowerCase() + ".1")
					.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
					.replace("<arena>", this.arenaName).replace("<minigame>", this.minigameName));
			final String line2 = Signs.format(config.getString("signs." + arState.toLowerCase() + ".2")
					.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
					.replace("<arena>", this.arenaName).replace("<minigame>", this.minigameName));
			final String line3 = Signs.format(config.getString("signs." + arState.toLowerCase() + ".3")
					.replace("<count>", Integer.toString(count)).replace("<maxcount>", Integer.toString(maxcount))
					.replace("<arena>", this.arenaName).replace("<minigame>", this.minigameName));

			if (e == null) {
				final BlockState state = this.loc.getBlock().getState();

				if (state instanceof Sign) {
					final Sign sign = (Sign) state;

					sign.setLine(0, line0);
					sign.setLine(1, line1);
					sign.setLine(2, line2);
					sign.setLine(3, line3);
					sign.getBlock().getChunk().load();
					sign.update();
				} else {
					if (this.lastSignWarning == null
							|| this.lastSignWarning.until(LocalDateTime.now(), ChronoUnit.SECONDS) > 30) {
						this.lastSignWarning = LocalDateTime.now();
						LobbySignManager.this.pl.getLogger().log(Level.WARNING, "Lobby-Schild für " + this.minigameName
								+ "/" + this.arenaName + " an " + this.loc + " konnte nicht gefunden werden!");
					}
				}
			} else {
				e.setLine(0, line0);
				e.setLine(1, line1);
				e.setLine(2, line2);
				e.setLine(3, line3);
			}
		}
	}

	public void ping() {
		for (final SignData data : this.signs.values()) {
			if (data.lastResponse.isBefore(data.lastRequest)) {

				if (data.lastResponse.until(data.lastRequest, ChronoUnit.SECONDS) > 5) {
					// == KEINE SPIELER
					data.setSignData(0, data.lastMaxPlayers, "JOIN", null);
					data.updateResponseData();
				}
			} else {
				if (data.lastResponse.until(LocalDateTime.now(), ChronoUnit.SECONDS) > 60) {
					data.requestServerSign();
				}
			}
		}
	}
}
