package me.aaron.timer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.aaron.main.NMSUtil;
import me.aaron.main.PluginManager;
import me.aaron.main.SaltyGames;

public class TitleTimer extends BukkitRunnable {
	
	private String title;
	private long timestamp;
	private Player p;
	private NMSUtil nms;
	private PluginManager pManager;
	
	public TitleTimer(SaltyGames pl, String title, Player p, long timestamp) {
		this.title = title;
		this.timestamp = timestamp;
		this.p = p;
		this.nms = pl.getNMS();
		this.pManager = pl.getPluginManager();
		
		runTaskTimer(pl, 10, 10);
	}

	@Override
	public void run() {
		long currentTime = System.currentTimeMillis();
		if (currentTime > timestamp) {
			nms.updateInventoryTitle(p, title);
			pManager.removeTitleTimer(p.getUniqueId());
		}
	}
}
