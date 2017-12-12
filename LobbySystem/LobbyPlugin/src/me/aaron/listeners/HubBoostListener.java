package me.aaron.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import me.aaron.main.LobbyPlugin;

public class HubBoostListener implements Listener {

	private LobbyPlugin pl;

	public HubBoostListener(LobbyPlugin pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

		Player p = e.getPlayer();

		// TODO: Config

		String topstr = pl.getConfig().getString("booster.top");
		Material top = Material.getMaterial(topstr);

		String bottomstr = pl.getConfig().getString("booster.bottom");
		Material bottom = Material.getMaterial(bottomstr);

		if (p.getLocation().getBlock().getType() == top) {
			if (p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == bottom) {

				Vector v = p.getLocation().getDirection().multiply(5D).setY(1D);

				p.setVelocity(v);
				p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 3, 2);
			}
		}
	}
}


