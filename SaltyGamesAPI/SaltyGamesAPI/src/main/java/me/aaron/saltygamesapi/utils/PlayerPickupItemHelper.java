package main.java.me.aaron.saltygamesapi.utils;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;

public class PlayerPickupItemHelper {
	
	final Consumer<CustomPickupEvent> handler;
	
	public PlayerPickupItemHelper(Plugin pl, Consumer<CustomPickupEvent> handler) {
		this.handler = handler;
		
		Bukkit.getPluginManager().registerEvents(new PlayerPickup(), pl);
	}

	private final class PlayerPickup implements Listener {
		
		@EventHandler
		public void onPlayerPickupItem(PlayerPickupItemEvent e) {
			handler.accept(new CustomPickupEvent() {
				
				@Override
				public void setCancelled(boolean cancel) {
					e.setCancelled(cancel);
				}
				
				@Override
				public boolean isCancelled() {
					return e.isCancelled();
				}
				
				@Override
				public int getRemaining() {
					return e.getRemaining();
				}
				
				@Override
				public Player getPlayer() {
					return e.getPlayer();
				}
				
				@Override
				public Item getItem() {
					return e.getItem();
				}
			});
		}
		
	}
	
	public interface CustomPickupEvent {
		Player getPlayer();
		Item getItem();
		int getRemaining();
		boolean isCancelled();
		void setCancelled(boolean cancel);
	}
	
}
