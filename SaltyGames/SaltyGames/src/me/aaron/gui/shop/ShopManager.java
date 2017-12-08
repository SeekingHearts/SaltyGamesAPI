package me.aaron.gui.shop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.data.GBPlayer;
import me.aaron.events.EnterSaltyGamesEvent;
import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.gui.guis.AGui;
import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesLang;
import me.aaron.main.SaltyGamesSettings;
import me.aaron.utils.ClickAction;
import me.aaron.utils.ItemStackUtil;
import me.aaron.utils.Permission;
import me.aaron.utils.StringUtil;

public class ShopManager {
	
	public static final String MAIN = "main" + UUID.randomUUID().toString();
	
	private File shopFile;
	FileConfiguration shop;
	
	private AButton mainButton;
	
	protected static Map<String, Category> categories;
	
	protected MainShop mainShop;
	
	protected GUIManager guiManager;
	
	private boolean closed;
	
	private SaltyGames pl;
	private SaltyGamesLang lang;
	
	private int mainSlots = 27, titleMessageSeconds = 3;
	
	public ShopManager(SaltyGames pl, GUIManager guiManager) {
		this.pl = pl;
		this.lang = pl.lang;
		this.guiManager = guiManager;
		
		categories = new HashMap<>();
		
		loadFile();
		if (!shop.isConfigurationSection("shop") || !shop.isConfigurationSection("shop.button") || !shop.isConfigurationSection("shop.categories")) {
			Bukkit.getLogger().log(Level.WARNING, "Der Shop wurde nicht richtig eingestellt!");
			Bukkit.getLogger().log(Level.WARNING, "Tokens werden deaktiviert!");
			SaltyGamesSettings.tokensEnabled = false;
			return;
		}
		
		this.closed = !shop.getBoolean("open");
		
		List<String> lore;
		ItemStack mainItem = ItemStackUtil.getItemStack(shop.getString("shop.button.materialData", Material.STORAGE_MINECART.toString()));
		if (shop.getBoolean("shop.button.glow"))
			mainItem = pl.getNMS().addGlow(mainItem);
		mainButton = new AButton(mainItem);
		ItemMeta meta = mainItem.getItemMeta();
		if (shop.isString("shop.button.displayName")) {
			meta.setDisplayName(StringUtil.color(shop.getString("shop.button.displayName")));
		}
		
		if (shop.isList("shop.button.lore"))
			meta.setLore(StringUtil.color(shop.getStringList("shop.button.lore")));
		
		mainButton.setItemMeta(meta);
		mainButton.setAction(ClickAction.OPEN_SHOP_PAGE);
		mainButton.setArgs(MAIN, "0");
		
		mainShop = new MainShop(pl, guiManager, mainSlots, this, new String[]{MAIN, "0"});
	}

	private void loadFile() {
		shopFile = new File(pl.getDataFolder().toString() + File.separatorChar + "tokenShop.yml");
		
		if (!shopFile.exists()) {
			shopFile.getParentFile().mkdirs();
			pl.saveResource("tokenShop.yml", false);
		}
		try {
			this.shop = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(shopFile), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public AButton getMainButton() {
		return mainButton;
	}
	
	public boolean openShopPage(Player whoClicked, String[] args) {
        boolean saved = false;

        if(!pl.getPluginManager().hasSavedContents(whoClicked.getUniqueId())){
            EnterSaltyGamesEvent enterEvent = new EnterSaltyGamesEvent(whoClicked, args[0], args[1]);
            if(!enterEvent.isCancelled()){
                pl.getPluginManager().saveInventory(whoClicked);
                saved = true;
            } else {
                whoClicked.sendMessage(lang.PREFIX + " A game was canceled with the reason: " + enterEvent.getCancelMessage());
                return false;
            }
        }

        if(whoClicked.hasPermission(Permission.OPEN_SHOP.getPermissions())){
            if(args[0].equals(ShopManager.MAIN) && args[1].equals("0")) {
                SaltyGames.openingNewGUI = true;
                mainShop.open(whoClicked);
                SaltyGames.openingNewGUI = false;

                if(closed){
                    pl.getNMS().updateInventoryTitle(whoClicked, pl.lang.SHOP_IS_CLOSED);
                } else {
                    pl.getNMS().updateInventoryTitle(whoClicked, pl.lang.SHOP_TITLE_MAIN_SHOP.replace("%player%", whoClicked.getDisplayName()));
                }
                return true;
            } else if(categories.containsKey(args[0])) {
                int page;
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException exception){
                    Bukkit.getLogger().log(Level.SEVERE, "failed to open shop page due to corrupted args!");
                    return false;
                }
                SaltyGames.openingNewGUI = true;
                boolean open = categories.get(args[0]).openPage(whoClicked, page);
                SaltyGames.openingNewGUI = false;
                if(open) {
                    pl.getNMS().updateInventoryTitle(whoClicked, pl.lang.SHOP_TITLE_PAGE_SHOP.replace("%page%",String.valueOf(page+1)));
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if (saved) pl.getPluginManager().restoreInventory(whoClicked);
            whoClicked.sendMessage(lang.PREFIX + lang.CMD_NO_PERM);

            if(guiManager.isInMainGUI(whoClicked.getUniqueId())) {
                String currentTitle = pl.lang.TITLE_MAIN_GUI.replace("%player%", whoClicked.getName());
                pl.getPluginManager().startTitleTimer(whoClicked, currentTitle, titleMessageSeconds);
                pl.getNMS().updateInventoryTitle(whoClicked, pl.lang.TITLE_NO_PERM);
            }

            return false;
        }
        if (saved) pl.getPluginManager().restoreInventory(whoClicked);
        Bukkit.getLogger().log(Level.SEVERE, "trying to open a shop page failed");
        Bukkit.getLogger().log(Level.SEVERE, "args: " + Arrays.asList(args));
        whoClicked.sendMessage("Error");
        return false;
    }

	public FileConfiguration getShop() {
		return shop;
	}
	
	public void loadCategory(String cat) {
		categories.put(cat, new Category(pl, this, guiManager, cat));
	}
	
	public boolean inShop(UUID uuid) {
		if (mainShop.isInGui(uuid))
			return true;
		for (Category cat : categories.values()) {
			if (cat.isCategory(uuid))
				return true;
		}
		return false;
	}
	
	
	public void onClick(InventoryClickEvent e) {
		boolean topInv = e.getSlot() == e.getRawSlot();
		
		if (mainShop.isInGui(e.getWhoClicked().getUniqueId())) {
			if (topInv) {
				mainShop.onInvClick(e);
			} else {
				mainShop.onBottomInvClick(e);
			}
			return;
		}
		
		for (Category cat : categories.values()) {
			if (cat.isCategory(e.getWhoClicked().getUniqueId())) {
				if (topInv) {
					cat.onInvClick(e);
				} else {
					cat.onButtomInvClick(e);
				}
			}
		}
	}
	
	public void onInvClose(InventoryCloseEvent e) {
        if(mainShop.isInGui(e.getPlayer().getUniqueId())){
            mainShop.onInvClose(e);
            return;
        }
        for(Category cat : categories.values()){
            if(cat.isCategory(e.getPlayer().getUniqueId())){
                cat.onInvClose(e);
            }
        }
    }
	
	public void updateTokens(GBPlayer p) {
		mainShop.updateTokens(p);
		for (Category cat : categories.values()) {
			cat.updateTokens(p);
		}
	}
	
	public ItemStack getShopItemStack(String category, String counter) {
		if (categories.get(category) != null) {
			return categories.get(category).getShopItemStack(counter);
		}
		return null;
	}
	
	public ShopItem getShopItem(String category, String counter) {
		if (categories.get(category) != null) {
			return categories.get(category).getShopItem(counter);
		}
		return null;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public AGui getShopGui(UUID uuid) {
		if (mainShop.isInGui(uuid))
			return mainShop;
		for (Category cat: categories.values()) {
			if (cat.isCategory(uuid)) {
				return cat.getShopGui(uuid);
			}
		}
		return null;
	}
	
}
