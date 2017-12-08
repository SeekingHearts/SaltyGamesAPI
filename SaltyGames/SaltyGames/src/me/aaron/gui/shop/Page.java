package me.aaron.gui.shop;

import me.aaron.gui.GUIManager;
import me.aaron.main.SaltyGames;

public class Page extends Shop {

	private int page;
	
	Page(SaltyGames plugin, GUIManager guiManager, int slots, int page, ShopManager shopManager, String[] args) {
        super(plugin, guiManager, slots, shopManager, args, plugin.lang.SHOP_TITLE_PAGE_SHOP);
        this.page = page;
    }
	
	public int getPage() {
		return this.page;
	}
}
