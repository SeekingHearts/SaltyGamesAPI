package me.aaron.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import me.aaron.main.LobbyPlugin;

public class PlayerDoubleJump implements Listener {

	private LobbyPlugin pl;

	public PlayerDoubleJump(LobbyPlugin pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onPlayerFlying(PlayerToggleFlightEvent e) {

		Player p = e.getPlayer();

		if (p.getGameMode() == GameMode.CREATIVE)
			return;

		e.setCancelled(true);
		p.setFlying(false);
		p.setAllowFlight(false);

		p.setVelocity(p.getLocation().getDirection().multiply(1.5).setY(1));

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

		Player p = e.getPlayer();

		if (p.getGameMode() != GameMode.CREATIVE
				&& p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR && (!p.isFlying())) {
					p.setAllowFlight(true);
		}
	}
}
