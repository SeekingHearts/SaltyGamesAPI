package me.aaron.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.aaron.main.LobbyPlugin;
import me.aaron.utils.UserData;

public class JoinListener implements Listener {

	private LobbyPlugin pl;

	public JoinListener(LobbyPlugin pl) {
		this.pl = pl;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {

		Player p = e.getPlayer();
		try {

			for (String playerstr : pl.inSilentLobby) {
				Player player = Bukkit.getServer().getPlayer(playerstr);
				player.hidePlayer(p);
				p.hidePlayer(p);
			}

			World w = Bukkit.getServer().getWorld(pl.getConfig().getString("hub.world"));
			double x = pl.getConfig().getDouble("hub.x");
			double y = pl.getConfig().getDouble("hub.y");
			double z = pl.getConfig().getDouble("hub.z");
			double pitch = pl.getConfig().getDouble("hub.pitch");
			double yaw = pl.getConfig().getDouble("hub.yaw");

			p.teleport(new Location(w, x, y, z));
			p.getLocation().setPitch((float) pitch);
			p.getLocation().setYaw((float) yaw);

			p.getInventory().clear();

			giveSpawnItems(p);

			e.setJoinMessage("");

//			if (p.hasPermission("lobby.join.dev")) {
//				Bukkit.broadcastMessage(pl.serverpre + ChatColor.LIGHT_PURPLE + "[" + ChatColor.AQUA + "Developer"
//						+ ChatColor.LIGHT_PURPLE + "] " + p.getName() + ChatColor.GOLD
//						+ " ist nun auf dem Netzwerk online");
//			} else if (p.hasPermission("lobby.join.srdev")) {
//				Bukkit.broadcastMessage(pl.serverpre + ChatColor.AQUA + "[" + ChatColor.AQUA + "Sr-Developer"
//						+ ChatColor.AQUA + "] " + p.getName() + ChatColor.GOLD + " ist nun auf dem Netzwerk online");
//			} else if (p.hasPermission("lobby.join.coadmin")) {
//				Bukkit.broadcastMessage(
//						pl.serverpre + ChatColor.DARK_RED + "[" + ChatColor.DARK_RED + "Co.Admin" + ChatColor.DARK_RED
//								+ "] " + p.getName() + ChatColor.GOLD + " ist nun auf dem Netzwerk online");
//			} else if (p.hasPermission("lobby.join.admin")) {
//				Bukkit.broadcastMessage(
//						pl.serverpre + ChatColor.DARK_RED + "[" + ChatColor.DARK_RED + "Admin" + ChatColor.DARK_RED
//								+ "] " + p.getName() + ChatColor.GOLD + " ist nun auf dem Netzwerk online");
//			} else if (p.hasPermission("lobby.join.techniker")) {
//				Bukkit.broadcastMessage(pl.serverpre + ChatColor.LIGHT_PURPLE + "[" + ChatColor.AQUA + "Techniker"
//						+ ChatColor.LIGHT_PURPLE + "] " + p.getName() + ChatColor.GOLD
//						+ " ist nun auf dem Netzwerk online");
//			} else if (p.hasPermission("lobby.join.servermanager")) {
//				Bukkit.broadcastMessage(pl.serverpre + ChatColor.DARK_RED + "[" + ChatColor.DARK_RED + "Server-Manager"
//						+ ChatColor.DARK_RED + "] " + p.getName() + ChatColor.GOLD
//						+ " ist nun auf dem Netzwerk online");
//			} else {
				sendJoinMsg(p);
//			}

			if (!pl.jumpPads.containsKey(p.getName())) {
				pl.jumpPads.put(p.getName(), true);
			}
			if (!pl.chat.containsKey(p.getName())) {
				pl.chat.put(p.getName(), true);
			}
			if (!pl.doubleJump.containsKey(p.getName())) {
				pl.doubleJump.put(p.getName(), true);
			}
			if (!pl.pvp.containsKey(p.getName())) {
				pl.pvp.put(p.getName(), false);
			}

			UserData.create(p);
			UserData.save();
			
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setGameMode(GameMode.ADVENTURE);

			for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
				if (onlinePlayer.hasPermission("lobby.sound")) {
					onlinePlayer.getWorld().playSound(onlinePlayer.getLocation(), Sound.AMBIENCE_THUNDER, 1, 1);
				}
			}

		} catch (Exception ex) {
			p.sendMessage(pl.prefix + ChatColor.RED
					+ "Kein Spawn festgelegt. Bitte wende dich umgehend an ein TeamMitglied!");
			p.sendMessage(pl.prefix + ChatColor.GOLD + "Benutze default Spawn...");
			ex.printStackTrace();
		}
	}

	public static void giveSpawnItems(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		ItemStack cp = new ItemStack(Material.COMPASS);
		{
			ItemMeta meta = cp.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_PURPLE + "Teleportierer");
			cp.setItemMeta(meta);
		}
		// ------------------------------------------------------
		ItemStack hp = new ItemStack(Material.BLAZE_ROD);
		{
			ItemMeta meta = hp.getItemMeta();
			meta.setDisplayName("§6Hide Players");
			hp.setItemMeta(meta);
		}
		// ------------------------------------------------------
		ItemStack ps = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		{
			SkullMeta meta = (SkullMeta) ps.getItemMeta();
			meta.setOwner(p.getName());
			meta.setDisplayName("§6Profil");
			ps.setItemMeta(meta);
		}
		// ------------------------------------------------------

		if (p.hasPermission("lobby.silent")) {
			ItemStack sl = new ItemStack(Material.TNT);
			{
				ItemMeta meta = sl.getItemMeta();
				meta.setDisplayName(ChatColor.RED + "Silent Lobby");
				sl.setItemMeta(meta);
			}
			p.getInventory().setItem(7, sl);
		}

		p.getInventory().setItem(0, cp);
		p.getInventory().setItem(1, hp);
		p.getInventory().setItem(8, ps);

	}

	public void sendJoinMsg(Player p) {
		p.sendMessage(pl.serverpre + "Willkommen auf" + ChatColor.GOLD + " Legend" + ChatColor.GREEN + "Kingdom");
		p.sendMessage(pl.serverpre + "Hiermit Akzeptiert du automatisch die" + ChatColor.RED + " Regeln!");
	}
}
