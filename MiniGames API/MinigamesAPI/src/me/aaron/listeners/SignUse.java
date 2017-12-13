package me.aaron.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.aaron.MinigamesAPI;
import me.aaron.Party;
import me.aaron.PluginInstance;
import me.aaron.utils.Validator;

public class SignUse implements Listener {
	
	@EventHandler
	public void onSignUse(PlayerInteractEvent e) {
		if (e.hasBlock()) {
			if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
				if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
					return;
				}
				final Sign s = (Sign) e.getClickedBlock().getState();
				String server = MinigamesAPI.getAPI().getServerBySignLocation(s.getLocation());
				if (server != null && server != "") {
					final Player p = e.getPlayer();
					final String signInfo = MinigamesAPI.getAPI().getInfoBySignLocation(s.getLocation());
					
					if (MinigamesAPI.getAPI().globalParty.containsKey(p.getUniqueId())) {
						final Party party = MinigamesAPI.getAPI().globalParty.remove(p.getUniqueId());
						
						for (final UUID p_ : party.getPlayers()) {
							if (Validator.isPlayerOnline(p_)) {
								boolean cont = true;
								MinigamesAPI.getAPI();
								for (final PluginInstance pli : MinigamesAPI.pinstances.values()) {
									if (pli.containsGlobalPlayer(Bukkit.getPlayer(p_).getName())) {
										cont = false;
									}
								}
								if (cont) {
									MinigamesAPI.getAPI().letPlayerJoinServer(server, Bukkit.getPlayer(p_), signInfo);
								}
							}
						}
					}
					MinigamesAPI.getAPI().letPlayerJoinServer(server, p, signInfo);
				}
			}
		}
	}

}
