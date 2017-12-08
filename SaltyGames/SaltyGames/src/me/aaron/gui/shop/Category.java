package me.aaron.gui.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aaron.data.GBPlayer;
import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.gui.guis.AGui;
import me.aaron.main.SaltyGames;
import me.aaron.utils.ClickAction;
import me.aaron.utils.ItemStackUtil;
import me.aaron.utils.StringUtil;

public class Category {

	private Map<Integer, Page> pages;
	private String key;
	private ShopManager shopManager;
	private GUIManager guiManager;
	private SaltyGames pl;

	private ItemStack back, forward;

	private FileConfiguration shop;

	private Map<String, ShopItem> shopItems = new HashMap<>();

	private int slots = 27, itemsPerPage = 18, backSlot = 21, forSlot = 23;

	public Category(SaltyGames pl, ShopManager shopManager, GUIManager guiManager, String key) {
		this.key = key;
		this.shopManager = shopManager;
		this.guiManager = guiManager;
		this.pl = pl;
		this.shop = shopManager.getShop();

		pages = new HashMap<>();

		back = new ItemStack(Material.ARROW, 1);
		{
			ItemMeta meta = back.getItemMeta();
			meta.setDisplayName(pl.lang.BUTTON_BACK);
			back.setItemMeta(meta);
		}

		forward = new ItemStack(Material.ARROW, 1);
		{
			ItemMeta meta = forward.getItemMeta();
			meta.setDisplayName(pl.lang.BUTTON_FORWARD);
			forward.setItemMeta(meta);
		}

	}

	private void loadShopItems() {
		ConfigurationSection pageSection = shop.getConfigurationSection("shop.categories." + key + ".items");
		if (pageSection == null)
			return;

		Map<Integer, AButton> allItems = new HashMap<>();
		ItemStack itemStack;
		ItemMeta meta;
		List<String> lore = new ArrayList<>();
		int counter = 0, token = 0, money = 0, amount = 0;

		for (String itmKey : pageSection.getKeys(false)) {

			itemStack = ItemStackUtil.getItemStack(pageSection.getString(itmKey + ".materialData"));

			if (pageSection.isSet(itmKey + ".tokens") && pageSection.isInt(itmKey + ".tokens")) {
				token = pageSection.getInt(itmKey + ".tokens");
			} else if (pageSection.isBoolean(itmKey + ".token") && pageSection.isInt(itmKey + ".token")) {
				token = pageSection.getInt(itmKey + ".token");
			} else {
				token = 0;
			}
			if (pageSection.isSet(itmKey + ".money") || pageSection.isInt(itmKey + ".money")) {
				money = pageSection.getInt(itmKey + ".money");
			} else {
				money = 0;
			}

			if (itemStack != null) {
				if (pageSection.isSet(itmKey + ".count") && pageSection.isInt(itmKey + ".count")) {
					amount = pageSection.getInt(itmKey + ".count");

					if (amount > itemStack.getMaxStackSize()) {
						itemStack.setAmount(itemStack.getMaxStackSize());
					} else {
						itemStack.setAmount(amount);
					}
				}
				if (pageSection.getBoolean(itmKey + ".glow", false)) {
					itemStack = pl.getNMS().addGlow(itemStack);
				}

				meta = itemStack.getItemMeta();

				if (pageSection.isString(itmKey + ".displayName")) {
					meta.setDisplayName(StringUtil.color(pageSection.getString(itmKey + ".displayName")));
				}

				if (pageSection.isList(itmKey + ".lore")) {
					lore = new ArrayList<>(pageSection.getStringList(itmKey + ".lore"));

					for (int i = 0; i < lore.size(); i++) {
						lore.set(i, StringUtil.color(lore.get(i)));
					}
					meta.setLore(lore);
				}
				itemStack.setItemMeta(meta);
			}
			ItemStack buttonItem = getButtonItem(itemStack, pageSection, itmKey);
			if (buttonItem == null) {
				Bukkit.getConsoleSender().sendMessage(pl.lang.PREFIX + ChatColor.RED + " Problem im Shop: "
						+ ChatColor.GREEN + "tokenShop.yml " + pageSection.getCurrentPath() + "." + itmKey);
				Bukkit.getConsoleSender().sendMessage(
						pl.lang.PREFIX + ChatColor.RED + " Item UND PresentItem sind nicht definiert oder fehlerhaft!");
				Bukkit.getConsoleSender().sendMessage(pl.lang.PREFIX + ChatColor.RED + "  Überspringe...");
				continue;
			}

			ShopItem shopItem = new ShopItem();

			if (itemStack != null)
				shopItem.setItemStack(new ItemStack(itemStack));

			if (pageSection.isList(itmKey + ".requirements" + ".permissions"))
				shopItem.setPermissions(pageSection.getStringList(itmKey + ".requirements" + ".permissions"));

			if (pageSection.isList(itmKey + ".requirements" + ".noPermissions"))
				shopItem.setNoPermissions(pageSection.getStringList(itmKey + ".requirements" + ".noPermissions"));

			if (pageSection.isList(itmKey + ".commands"))
				shopItem.setCommands(pageSection.getStringList(itmKey + ".commands"));

			if (pageSection.getBoolean(itmKey + ".manipulateInventory", false))
				shopItem.setManipulatesInventory(true);

			AButton button = new AButton(buttonItem);
			button.setAction(ClickAction.BUY);

			meta = button.getItemMeta();
			lore.clear();
			lore.add("");
			if (money == 0 && token == 0) {
				lore.add(pl.lang.SHOP_FREE);
			} else if (token != 0) {
				lore.add(pl.lang.SHOP_TOKEN.replace("%token%", String.valueOf(token)));
			} else if (money != 0) {
				lore.add(pl.lang.SHOP_MONEY.replace("%money%", String.valueOf(money)));
			}

			if (meta.hasLore())
				lore.addAll(meta.getLore());

			meta.setLore(lore);
			button.setItemMeta(meta);

			button.setArgs(key, String.valueOf(counter), String.valueOf(token), String.valueOf(money));
			allItems.put(counter, button);
			shopItems.put(String.valueOf(counter), shopItem);

			counter++;
		}
		SaltyGames.debug("Alle Items von Seite " + key + "geladen:");
		for (int number : allItems.keySet()) {
			SaltyGames.debug("      " + allItems.get(number).toString());
		}
		counter++;
		int pageNr = counter / itemsPerPage + (counter % itemsPerPage == 0 ? 0 : 1);
		counter = 0;
		Page page = null;

		while (!allItems.isEmpty()) {
			SaltyGames.debug(allItems.keySet().size() + " verbleibende Items zum sortieren");
			if ((counter) % itemsPerPage == 0) {
				pages.put(counter / itemsPerPage, (page = new Page(pl, guiManager, slots, counter / itemsPerPage,
						shopManager, new String[] { key, String.valueOf(counter / itemsPerPage) })));

				if (counter / itemsPerPage == 0) {
					page.setButton(
							new AButton(back).setActionAndArgs(ClickAction.OPEN_SHOP_PAGE, ShopManager.MAIN, "0"),
							backSlot);
				} else {
					page.setButton(new AButton(back).setActionAndArgs(ClickAction.OPEN_SHOP_PAGE, key,
							String.valueOf(counter / itemsPerPage - 1)), backSlot);
				}

				if (pageNr - 1 > counter / itemsPerPage) {
					page.setButton(new AButton(forward).setActionAndArgs(ClickAction.OPEN_SHOP_PAGE, key,
							String.valueOf(counter / itemsPerPage + 1)), forSlot);
				}
			}
			if (page == null) {
				Bukkit.getConsoleSender().sendMessage(pl.lang.PREFIX + ChatColor.RED + " Error: Page == null");
			}

			page.setButton(allItems.get(counter));
			allItems.remove(counter);
			counter++;
		}
	}

	private ItemStack getButtonItem(ItemStack itemStack, ConfigurationSection pageSection, String itmKey) {
		String path = itmKey + ".buttonItem";
		ItemStack presentItem = ItemStackUtil.getItemStack(pageSection.getString(path + ".materialData"));
		if (presentItem == null && itemStack == null)
			return null;

		if (presentItem == null)
			presentItem = new ItemStack(itemStack);

		if (pageSection.getBoolean(path + ".glow", false))
			presentItem = pl.getNMS().addGlow(presentItem);

		if (pageSection.isInt(path + ".count"))
			presentItem.setAmount(pageSection.getInt(path + ".count"));

		ItemMeta meta = presentItem.getItemMeta();

		if (pageSection.isString(path + ".displayName"))
			meta.setDisplayName(StringUtil.color(pageSection.getString(path + ".displayName")));

		if (pageSection.isList(path + ".additionalLore")) {
			List<String> lore = new ArrayList<>();
			lore.add(" ");
			lore.add(ChatColor.GOLD + "- - - - - - - - - - - - - - - - - - -");
			lore.add(" ");

			List<String> addLore = new ArrayList<>(pageSection.getStringList(path + ".additionalLore"));
			for (int i = 0; i < addLore.size(); i++) {
				addLore.set(i, StringUtil.color(addLore.get(i)));
			}
			lore.addAll(addLore);
			meta.setLore(lore);
		}
		presentItem.setItemMeta(meta);

		return presentItem;
	}

	protected boolean isCategory(UUID uuid) {
		for (Page page : pages.values()) {
			if (page.isInGui(uuid))
				return true;
		}
		return false;
	}

	public void onInvClick(InventoryClickEvent e) {
		for (Page page : pages.values()) {
			if (page.isInGui(e.getWhoClicked().getUniqueId())) {
				page.onInvClick(e);
				return;
			}
		}
	}

	public void onButtomInvClick(InventoryClickEvent e) {
		for (Page page : pages.values()) {
			if (page.isInGui(e.getWhoClicked().getUniqueId())) {
				page.onBottomInvClick(e);
				return;
			}
		}
	}

	public void onInvClose(InventoryCloseEvent e) {
		for (Page page : pages.values()) {
			if (page.isInGui(e.getPlayer().getUniqueId())) {
				page.onInvClose(e);
				return;
			}
		}
	}

	public boolean openPage(Player p, int page) {
		if (!pages.containsKey(page)) {
			pages.get(page).open(p);
			return true;
		} else {
			return false;
		}
	}

	public void updateTokens(GBPlayer gbPlayer) {
		for (Page page : pages.values()) {
			page.updateTokens(gbPlayer);
		}
	}

	public ItemStack getShopItemStack(String counter) {
		return shopItems.get(counter).getItemStack();
	}

	public ShopItem getShopItem(String counter) {
		return shopItems.get(counter);
	}

	public AGui getShopGui(UUID uuid) {
		for (Page page : pages.values()) {
			if (page.isInGui(uuid)) {
				return page;
			}
		}
		return null;
	}
}
