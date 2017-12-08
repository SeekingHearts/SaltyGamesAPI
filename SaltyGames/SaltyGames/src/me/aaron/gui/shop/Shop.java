package me.aaron.gui.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import me.aaron.data.SGPlayer;
import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.gui.guis.AGui;
import me.aaron.main.PluginManager;
import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesSettings;
import me.aaron.utils.ClickAction;
import me.aaron.utils.InvUtil;

public class Shop extends AGui {

	private SaltyGames pl;
	
	FileConfiguration shop;
	
	ShopManager shopManager;
	
	protected Map<UUID, AButton> tokenButtons = new HashMap<>();
	
	protected int tokenButtonSlot;
	
	public Shop(SaltyGames pl, GUIManager guiManager, int slots, ShopManager shopManager, String[] args, String title) {
		super(pl, guiManager, slots, args, title);
		
		this.shopManager = shopManager;
		this.shop = shopManager.getShop();
		this.pl = pl;
		
		Map<Integer, ItemStack> hotBarButtons = pl.getPluginManager().getHotBarButtons();
		
		if (hotBarButtons.get(PluginManager.exit) != null) {
            AButton exit = new AButton(hotBarButtons.get(PluginManager.exit).getData(), 1);
            ItemMeta meta = hotBarButtons.get(PluginManager.exit).getItemMeta();
            exit.setItemMeta(meta);
            exit.setAction(ClickAction.CLOSE);
            setLowerButton(exit, PluginManager.exit);
        }


        if (hotBarButtons.get(PluginManager.toMain) != null) {
            AButton main = new AButton(hotBarButtons.get(PluginManager.toMain).getData(), 1);
            ItemMeta meta = hotBarButtons.get(PluginManager.toMain).getItemMeta();
            main.setItemMeta(meta);
            main.setAction(ClickAction.OPEN_MAIN_GUI);
            setLowerButton(main, PluginManager.toMain);
        }
        
        tokenButtonSlot = slots - 9;
        
        if (SaltyGamesSettings.tokensEnabled) {
        	ItemStack tokensItem = new AButton(new MaterialData(Material.GOLD_NUGGET), 1);
        	tokensItem = pl.getNMS().addGlow(tokensItem);
        	AButton tokens = new AButton(tokensItem);
        	ItemMeta meta = tokens.getItemMeta();
        	meta.setDisplayName("Placeholder");
        	tokens.setItemMeta(meta);
        	tokens.setAction(ClickAction.NOTHING);
        	setButton(tokens, tokenButtonSlot);
        }
	}
	
	@Override
	public boolean open(Player p) {
		if (!openInventories.containsKey(p.getUniqueId())) {
			
		}
		return super.open(p);
	}
	
	void loadPlayerShop(SGPlayer p) {
		if (SaltyGamesSettings.tokensEnabled) {
			ItemStack tokensItem = new AButton(new MaterialData(Material.GOLD_NUGGET), 1);
			tokensItem = pl.getNMS().addGlow(tokensItem);
			AButton tokens = new AButton(tokensItem);
			tokens.setAction(ClickAction.NOTHING);
			tokenButtons.put(p.getUuid(), tokens);
		}
		
		Inventory inv = InvUtil.createInv(null, this.inventory.getSize(), "SaltyGames gui");
		inv.setContents(this.inventory.getContents().clone());
		
		openInventories.putIfAbsent(p.getUuid(), inventory);
		
		updateTokens(p);
	}
	
	void updateTokens(SGPlayer p) {
		if (!SaltyGamesSettings.tokensEnabled)
			return;
		if (!tokenButtons.keySet().contains(p.getUuid()))
			return;
		if (!openInventories.keySet().contains(p.getUuid()))
			return;
		
		ItemMeta meta = tokenButtons.get(p.getUuid()).getItemMeta();
		meta.setDisplayName(pl.lang.BUTTON_TOKENS.replace("%tokens%", String.valueOf(p.getTokens())));
		tokenButtons.get(p.getUuid()).setItemMeta(meta);
		
		openInventories.get(p.getUuid()).setItem(tokenButtonSlot, tokenButtons.get(p.getUuid()));
	}
	
	@Override
	public void removePlayer(UUID uuid) {
		tokenButtons.remove(uuid);
		super.removePlayer(uuid);
	}
}
