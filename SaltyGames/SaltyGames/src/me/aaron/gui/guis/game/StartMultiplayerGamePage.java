package me.aaron.gui.guis.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.main.SaltyGames;
import me.aaron.utils.ClickAction;
import me.aaron.utils.InvUtil;

public class StartMultiplayerGamePage extends GameGuiPage {

	private Map<UUID, ArrayList<UUID>> invitations = new HashMap<>();
	private Map<UUID, AButton[]> invitationButtons = new HashMap<>();
	
	private SaltyGames pl;
	
	public StartMultiplayerGamePage(SaltyGames pl, GUIManager guiManager, int slots, String gameID, String key,
			String title) {
		super(pl, guiManager, slots, gameID, key, title);
		
		AButton button = new AButton(new MaterialData(Material.IRON_BLOCK), 1);
		button.setAction(ClickAction.START_PLAYER_INPUT);
		button.setArgs(gameID, key);
		ItemMeta meta = button.getItemMeta();
		meta.setDisplayName(pl.lang.BUTTON_INVITE_BUTTON_NAME);
		meta.setLore(pl.lang.BUTTON_INVITE_BUTTON_LORE);
		button.setItemMeta(meta);
		
		setButton(button, 0);
	}
	
	@Override
	public boolean open(Player p) {
		SaltyGames.debug("open called in StartMultiplayerGamePage");
		if (!openInventories.containsKey(p.getUniqueId())) {
			loadInvites(p.getUniqueId());
		}
		if (super.open(p)) {
			pl.getNMS().updateInventoryTitle(p, pl.lang.TITLE_MAIN_GUI.replace("%player%", p.getName()));
			return true;
		}
		return false;
	}
	
	private void loadInvites(UUID uuid) {
		SaltyGames.debug("loading inventory");
		invitations.put(uuid, new ArrayList<>());
		invitationButtons.put(uuid, new AButton[inventory.getSize()]);
		Inventory inv = InvUtil.createInv(null, 54, "Inventar Titel.");
		inv.setContents(inventory.getContents().clone());
		
		openInventories.put(uuid, inv);
	}
	
	public void addInvite(UUID uuid1, UUID uuid2) {
		if (!invitations.keySet().contains(uuid2)) {
			invitations.put(uuid2, new ArrayList<>());
		}
		if (!invitations.keySet().contains(uuid2)) {
			invitationButtons.put(uuid2, new AButton[inventory.getSize()]);
		}
		invitations.get(uuid2).add(uuid1);
		
	}
	
	private void updateInvitations(UUID uuid2) {
		if (!openInventories.containsKey(uuid2)) {
			openInventories.put(uuid2, InvUtil.createInv(null, 54, "Inventar Titel."));
		}
		Inventory inv = openInventories.get(uuid2);
		inv.setContents(inventory.getContents().clone());
		int i = 1;
		for (UUID uuid1 : invitations.get(uuid2)) {
			if (i >= inventory.getSize())
				break;
			Player p1 = Bukkit.getPlayer(uuid1);
			if (p1 == null)
				continue;
			AButton skull = new AButton(new MaterialData(Material.SKULL_ITEM), 1);
			skull.setDurability((short) 3);
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(p1.getName());
			meta.setDisplayName(pl.lang.BUTTON_INVITE_SKULL_NAME.replace("%player%", p1.getName()));
			meta.setLore(pl.lang.BUTTON_INVITE_SKULL_LORE);
			skull.setItemMeta(meta);
			skull.setAction(ClickAction.START_GAME);
			skull.setArgs(args[0], args[1], uuid1.toString());
			inv.setItem(i, skull);
			invitationButtons.get(uuid2)[i] = skull;
			i++;
		}
	}
	
	@Override
	public void removePlayer(UUID uuid) {
		invitations.remove(uuid);
		super.removePlayer(uuid);
	}
	
	public void removeInvite(UUID uuid1, UUID uuid2) {
		if (!invitations.keySet().contains(uuid2)) {
			return;
		}
		invitations.get(uuid2).remove(uuid1);
		updateInvitations(uuid2);
	}
	
	public AButton getButton(UUID uuid, int slot) {
		if (invitationButtons.containsKey(uuid)) {
			return invitationButtons.get(uuid)[slot];
		}
		return null;
	}
}
