package me.aaron.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.aaron.main.LobbyPlugin;

@SuppressWarnings("deprecation")
public class PlayerChatListener implements Listener {

	private LobbyPlugin pl;

	public PlayerChatListener(LobbyPlugin pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onPlayerChat(final AsyncPlayerChatEvent e) {

		Player p = e.getPlayer();

		if (e.getMessage().startsWith("/")) {

			if (!p.hasPermission("lobby.ignorespy")) {

				for (int i = 1; i < pl.usingSpy.size(); i++) {

					Player spyer = Bukkit.getServer().getPlayer(pl.usingSpy.get(i));

					spyer.sendMessage(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + p.getName() + ChatColor.RED
							+ ">> " + ChatColor.LIGHT_PURPLE + e.getMessage());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChatInSilent(AsyncPlayerChatEvent e) {

		Player p = e.getPlayer();
		
		if (pl.inSilentLobby.contains(p.getName()) || pl.chat.get(p.getName()) == false) {
			e.getRecipients().clear();
			e.setCancelled(true);
			p.sendMessage(pl.prefix + ChatColor.RED + "In der Silent Lobby kannst du nicht schreiben!");
			return;
		}

		for (Player player : e.getRecipients()) {
			if (pl.inSilentLobby.contains(player.getName()) || pl.chat.get(p.getName()) == false) {
					e.getRecipients().remove(player);
			}
		}

	}
}