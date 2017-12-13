package main.me.aaron;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.me.aaron.utils.AClass;

public class ArenaPlayer {
	
	private ItemStack[] inv;
	private ItemStack[] armor;
	
	private GameMode org_gm = GameMode.SURVIVAL;
	
	private String playername;
	private int org_xp1v1 = 0;
	private boolean noreward = false;
	
	private Arena currentAr;
	private AClass currentClass;
	
	private static final HashMap<String, ArenaPlayer> players = new HashMap<>();

	
	public ArenaPlayer(final String p) {
		this.playername = p;
		ArenaPlayer.players.put(p, this);
	}
	
	public static ArenaPlayer getPlayerInstance(final String p) {
		if (!ArenaPlayer.players.containsKey(p))
			return new ArenaPlayer(p);
		return ArenaPlayer.players.get(p);
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(playername);
	}
	
	public void setInvs(final ItemStack[] inv, final ItemStack[] armor) {
		this.inv = inv;
		this.armor = armor;
	}

	public boolean isNoreward() {
		return noreward;
	}

	public void setNoreward(final boolean noreward) {
		this.noreward = noreward;
	}

	public Arena getCurrentArena() {
		return currentAr;
	}

	public void setCurrentArena(final Arena currentAr) {
		this.currentAr = currentAr;
	}

	public AClass getCurrentClass() {
		return currentClass;
	}

	public void setCurrentClass(final AClass currentClass) {
		this.currentClass = currentClass;
	}

	public ItemStack[] getInventory() {
		return inv;
	}

	public ItemStack[] getArmorInventory() {
		return armor;
	}

	public GameMode getOrginalGameMode() {
		return org_gm;
	}

	public int getOriginalXP1v1() {
		return org_xp1v1;
	}

	public void setOriginalXplvl(final int org_xp1v1) {
		this.org_xp1v1 = org_xp1v1;
	}

	public void setOriginalGameMode(final GameMode org_gm) {
		this.org_gm = org_gm;
	}
}
