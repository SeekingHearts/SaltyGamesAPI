package me.aaron.gui.guis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.aaron.events.EnterSaltyGamesEvent;
import me.aaron.games.GameGui;
import me.aaron.games.GameManager;
import me.aaron.gui.GUIManager;
import me.aaron.gui.buttons.AButton;
import me.aaron.gui.guis.game.StartMultiplayerGamePage;
import me.aaron.gui.shop.Shop;
import me.aaron.gui.shop.ShopItem;
import me.aaron.main.PluginManager;
import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesSettings;
import me.aaron.utils.ClickAction;
import me.aaron.utils.InvUtil;
import me.aaron.utils.Permission;

public abstract class AGui {

	protected Inventory inventory;
	protected Map<UUID, Inventory> openInventories = new HashMap<>();
	protected Set<UUID> inGui;

	protected GUIManager guiManager;
	protected SaltyGames plugin;
	protected PluginManager pluginManager;

	protected float volume = 0.5f, pitch = 10f;

	protected AButton[] grid;
	protected AButton[] lowerGrid = new AButton[36];

	protected String[] args;

	protected String title;
	private Sound successfulClick, unsuccessfulClick;

	
	public AGui(SaltyGames plugin, GUIManager guiManager, int slots, String[] args, String title){
		this.plugin = plugin;
		this.args = args;
		this.guiManager = guiManager;
		this.pluginManager = plugin.getPluginManager();
		this.grid = new AButton[slots];
		inGui = new HashSet<>();

		this.successfulClick = SaltyGamesSettings.successfulClick;
		this.unsuccessfulClick = SaltyGamesSettings.unsuccessfulClick;

		this.title = title;

		if(this instanceof GameGui){
			title = title.replace("%game%", pluginManager.getGame(args[0]).getGameLang().PLAIN_NAME);
		}

		this.inventory = InvUtil.createInv(null, slots, title);
	}
	
	public boolean open(Player player){
		SaltyGames.debug("opening gui (method open in AGui)");
		// permissions are checked in the GUIManager
		if(openInventories.keySet().contains(player.getUniqueId())){
			SaltyGames.debug("found and now opening own inventory");
			player.openInventory(openInventories.get(player.getUniqueId()));
		} else {
			player.openInventory(inventory);
		}

		for(int slot = 0; slot < lowerGrid.length; slot++){
			player.getOpenInventory().getBottomInventory().setItem(slot, lowerGrid[slot]);
		}
		pluginManager.setItemsToKeep(player);

		inGui.add(player.getUniqueId());
		return true;
	}
	
	@SuppressWarnings("unused")
	public boolean action(InventoryClickEvent event, ClickAction action, String[] args){
		
		if(SaltyGames.debug) Bukkit.getConsoleSender().sendMessage("action called: " + action.toString() + " with the args: " + (args == null?"": Arrays.asList(args)));
		switch (action){
			case CHANGE_GAME_GUI:
			case OPEN_GAME_GUI:
				if(args.length != 2){
					Bukkit.getConsoleSender().sendMessage("wrong number of arguments to open a game gui: " + args.length);
				}

				if(guiManager.openGameGui((Player)event.getWhoClicked(), args[0], args[1])){
					inGui.remove(event.getWhoClicked().getUniqueId());
					return true;
				}
				return false;
			
			case START_GAME:
				if(args == null || args.length < 1){
					SaltyGames.debug("missing gameID to start a game");
					return false;
				}
				String gameID = args[0];
				GameManager manager;
				if((manager = pluginManager.getGameManager(gameID)) != null){
					// set flag
					SaltyGames.openingNewGUI = true;
					Player[] player = args.length == 3?new Player[2]:new Player[1];
					player[0] = (Player) event.getWhoClicked();

					checkPerms:
					if(!event.getWhoClicked().hasPermission(Permission.PLAY_ALL_GAMES.getPermissions()) && !event.getWhoClicked().hasPermission(Permission.PLAY_SPECIFIC_GAME.getPermission(gameID))){

						// special case for multiplayer games and option 'ExceptInvitesWithoutPlayPermission'
						if(player.length > 1 && SaltyGamesSettings.exceptInvitesWithoutPlayPermission){
							break checkPerms;
						}

						guiManager.sentInventoryTitleMessage(player[0], plugin.lang.TITLE_NO_PERM, gameID);

						// remove flag
						SaltyGames.openingNewGUI = false;
						return false;
					}

					if(args.length == 3){
						// last entry should be a UUID
						try{
							UUID uuid = UUID.fromString(args[2]);
							Player player2 = Bukkit.getPlayer(uuid);
							if(player == null)
								return false;
							player[1] = player2;
						} catch (IllegalArgumentException exception){
							exception.printStackTrace();
							SaltyGames.debug("tried inviting with a not valid UUID");
							return false;
						}

						if(pluginManager.isInGame(player[1].getUniqueId())){
							guiManager.sentInventoryTitleMessage(player[0], plugin.lang.TITLE_ALREADY_IN_ANOTHER_GAME, gameID);
							return false;
						}

						if(!guiManager.isInGUI(player[1].getUniqueId()) && !guiManager.getShopManager().inShop(player[1].getUniqueId())){

							// call enter SaltyGames event for the second player
							EnterSaltyGamesEvent enterEvent = new EnterSaltyGamesEvent(player[1], args[0], args[1]);
							if(!enterEvent.isCancelled()){
								pluginManager.saveInventory(player[1]);
							} else {
								for(Player playerObj: player) {
									if(guiManager.isInGUI(playerObj.getUniqueId()) || guiManager.getShopManager().inShop(playerObj.getUniqueId())) {
										guiManager.sentInventoryTitleMessage(playerObj, enterEvent.getCancelMessage(), gameID);
									} else {
										playerObj.sendMessage("A game was canceled with the reason: " + enterEvent.getCancelMessage());
									}
								}
							}
						}
					}


					int returnedCode = manager.startGame(player, (SaltyGamesSettings.playSounds && pluginManager.getPlayer(player[0].getUniqueId()).isPlaySounds()), args[1]);
					switch (returnedCode){
						case SaltyGames.GAME_STARTED:
							SaltyGames.debug("started game "+ args[0]+" for player " + player[0].getName() + (player.length==2?" and " + player[1].getName():"") + " with the arguments: " + Arrays.asList(args));
							AGui gui;
							for(Player playerObj : player) {
								gui = guiManager.getCurrentGui(playerObj.getUniqueId());
								if(gui != null){
									gui.removePlayer(playerObj.getUniqueId());
								}
								for (int slot : pluginManager.getHotBarButtons().keySet()) {
									playerObj.getInventory().setItem(slot, pluginManager.getHotBarButtons().get(slot));
								}
							}
							// remove flag
							SaltyGames.openingNewGUI = false;
							return true;

						case  SaltyGames.GAME_NOT_ENOUGH_MONEY:
							guiManager.sentInventoryTitleMessage(player[0], plugin.lang.TITLE_NOT_ENOUGH_MONEY, gameID);
							break;

						case SaltyGames.GAME_NOT_ENOUGH_MONEY_1:
							guiManager.sentInventoryTitleMessage(player[0], plugin.lang.TITLE_NOT_ENOUGH_MONEY, gameID);

							if(args.length == 3){
								if(guiManager.isInGUI(player[1].getUniqueId())) {
									guiManager.sentInventoryTitleMessage(player[1], plugin.lang.TITLE_OTHER_PLAYER_NOT_ENOUGH_MONEY, gameID);
								} else {
									//ToDo
									player[1].sendMessage(player[0].getName()+" Does not have enough money to start the game!");
								}
							}
							break;

						case SaltyGames.GAME_NOT_ENOUGH_MONEY_2:
							if(args.length == 3){
								if(guiManager.isInGUI(player[1].getUniqueId())) {
									guiManager.sentInventoryTitleMessage(player[1], plugin.lang.TITLE_NOT_ENOUGH_MONEY, gameID);
								} else {
									//ToDo
									player[1].sendMessage(player[0].getName()+" tried starting a game with you. But you do not have enough money!");
								}
							}
							guiManager.sentInventoryTitleMessage(player[0], plugin.lang.TITLE_OTHER_PLAYER_NOT_ENOUGH_MONEY, gameID);
							break;

						case SaltyGames.GAME_NOT_STARTED_ERROR:
							for(Player playerObj: player) {
								if(guiManager.isInGUI(playerObj.getUniqueId()) || guiManager.getShopManager().inShop(playerObj.getUniqueId())) {
									guiManager.sentInventoryTitleMessage(playerObj, plugin.lang.TITLE_ERROR, gameID);
								} else {
									playerObj.sendMessage("A game failed to start");
								}
							}
							break;

						default:
							plugin.getLogger().info(" unknown return code with args: " + Arrays.asList(args));
					}

					// remove flag
					SaltyGames.openingNewGUI = false;

					for(Player playerObj: player) {
						if (!guiManager.isInGUI(playerObj.getUniqueId()) && !pluginManager.isInGame(playerObj.getUniqueId())) {
							pluginManager.restoreInventory(playerObj);
							playerObj.updateInventory();
						}
					}
					SaltyGames.debug("did not start a game");
					return false;


				}
				SaltyGames.debug("Game with id: " + args[0] + " was not found");
				return false;
			
			case OPEN_MAIN_GUI:
				if(this instanceof MainGui) return false;

				if(guiManager.openMainGui((Player)event.getWhoClicked())){
					inGui.remove(event.getWhoClicked().getUniqueId());
					return true;
				}
				return false;


			case CLOSE:
				// do i need to do more here?
				event.getWhoClicked().closeInventory();
				((Player)event.getWhoClicked()).updateInventory();
				return true;


			case NOTHING:
				return true;

			case START_PLAYER_INPUT:
				if(this instanceof StartMultiplayerGamePage){
					// if this gets called from a StartMultiplayerGamePage it is the beginning of an invite!
					// check for perm in this case and stop invite if necessary
					if(!event.getWhoClicked().hasPermission(Permission.PLAY_ALL_GAMES.getPermissions())
							&& !event.getWhoClicked().hasPermission(Permission.PLAY_SPECIFIC_GAME.getPermission(args[0]))){
						guiManager.sentInventoryTitleMessage((Player)event.getWhoClicked(), plugin.lang.TITLE_NO_PERM, args[0]);
						return false;
					}
				}
				long timeStamp = System.currentTimeMillis();
				boolean worked = pluginManager.getHandleInviteInput().addWaiting(event.getWhoClicked().getUniqueId(), timeStamp + SaltyGamesSettings.timeForPlayerInput*1000, args);
				if(worked){
					event.getWhoClicked().closeInventory();
					((Player)event.getWhoClicked()).updateInventory();
					event.getWhoClicked().sendMessage(plugin.lang.PREFIX + plugin.lang.INPUT_START_MESSAGE);
					for(String message : plugin.lang.INPUT_HELP_MESSAGE){
						event.getWhoClicked().sendMessage(message.replace("%seconds%", String.valueOf(SaltyGamesSettings.timeForPlayerInput)));
					}
					return true;
				}
				return false;

			case TOGGLE:
				if(args != null && args.length == 1){
					if(args[0].equals("sound")){
						pluginManager.getPlayer(event.getWhoClicked().getUniqueId()).toggleSound();
					}
				}
				event.getInventory().setItem(event.getSlot(), ((MainGui)this).getSoundToggleButton(event.getWhoClicked().getUniqueId()).toggle());
				((Player)event.getWhoClicked()).updateInventory();
				return true;

			case SHOW_TOP_LIST:
				if(args.length != 2){
					Bukkit.getLogger().log(Level.WARNING, "show top list click has the wrong number of arguments: " + args.length);
					return false;
				}

				if(guiManager.openGameGui((Player) event.getWhoClicked(),args)){
					inGui.remove(event.getWhoClicked().getUniqueId());
					return true;
				}
				return false;

			case OPEN_SHOP_PAGE:
				if(args.length != 2){
					Bukkit.getLogger().log(Level.WARNING, "OPEN_SHOP_PAGE has the wrong number of arguments: " + args.length);
					return false;
				}
				if(guiManager.openShopPage((Player)event.getWhoClicked(), args)){
					inGui.remove(event.getWhoClicked().getUniqueId());
					return true;
				}

				return false;

			case BUY:

				// check for closed shop
				if(guiManager.getShopManager().isClosed()){
					guiManager.sentInventoryTitleMessage((Player)event.getWhoClicked(), plugin.lang.SHOP_IS_CLOSED, null);
					return false;
				}

				// check whether player can pay
				int tokens, money;
				try{
					tokens = Integer.parseInt(args[2]);
					money = Integer.parseInt(args[3]);
				} catch (NumberFormatException exception){
					exception.printStackTrace();
					Bukkit.getLogger().log(Level.WARNING, "a shop item had wrong cost-info args: " + Arrays.asList(args));
					return false;
				}

				int hasToken = 0;
				if(tokens > 0 ){
					if((hasToken = pluginManager.getPlayer(event.getWhoClicked().getUniqueId()).getTokens()) < tokens){
						guiManager.sentInventoryTitleMessage((Player)event.getWhoClicked(), plugin.lang.SHOP_TITLE_NOT_ENOUGH_TOKEN, null);
						return false;
					}
				}
				if(money > 0){
					String world = event.getWhoClicked().getWorld().getName().toString();
					if(!SaltyGamesSettings.econEnabled || (SaltyGames.econ.getAccountManager().getAccount(event.getWhoClicked().toString()).getBalance(world, "LegendKingdomDollar")) < money){
						guiManager.sentInventoryTitleMessage((Player)event.getWhoClicked(), plugin.lang.SHOP_TITLE_NOT_ENOUGH_MONEY, null);
						return false;
					}
				}

				// clone the item if it exists
				//   otherwise the number beeing given to the player is wrong after the first buy
				ItemStack item = guiManager.getShopManager().getShopItemStack(args[0], args[1]) == null?
						null: guiManager.getShopManager().getShopItemStack(args[0], args[1]).clone();

				ShopItem shopItem = guiManager.getShopManager().getShopItem(args[0], args[1]);

				// test for perms
				if(!shopItem.getPermissions().isEmpty()){
					for(String permission : shopItem.getPermissions()){
						if(!event.getWhoClicked().hasPermission(permission)){
							Bukkit.getConsoleSender().sendMessage("does not have  " + permission);
							guiManager.sentInventoryTitleMessage((Player)event.getWhoClicked(), plugin.lang.SHOP_TITLE_REQUIREMENT_NOT_FULFILLED, null);
							return false;
						}
					}
				}

				if(!shopItem.getNoPermissions().isEmpty()){
					for(String noPermission : shopItem.getNoPermissions()){
						if(event.getWhoClicked().hasPermission(noPermission)){
							Bukkit.getConsoleSender().sendMessage("has " + noPermission);
							guiManager.sentInventoryTitleMessage((Player)event.getWhoClicked(), plugin.lang.SHOP_TITLE_REQUIREMENT_NOT_FULFILLED, null);
							return false;
						}
					}
				}

				if(item != null) {
					if (!pluginManager.addItem(event.getWhoClicked().getUniqueId(), item)) {
						guiManager.sentInventoryTitleMessage((Player) event.getWhoClicked(), plugin.lang.SHOP_TITLE_INVENTORY_FULL, null);
						return false;
					} else {
						guiManager.sentInventoryTitleMessage((Player) event.getWhoClicked(), plugin.lang.SHOP_TITLE_BOUGHT_SUCCESSFULLY, null);
					}
				}

				if(shopItem.ManipulatesInventory() && this instanceof Shop){
					SaltyGames.debug("   closed due to shop item manipulating the inventory");
					event.getWhoClicked().closeInventory();
				}

				// item was given now check for commands and sent them
				if(!shopItem.getCommands().isEmpty()){
					for(String command : shopItem.getCommands()){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", event.getWhoClicked().getName()));
					}
				}

				// reopen shop page
				if(shopItem.ManipulatesInventory() && this instanceof Shop){
					SaltyGames.debug("   reopening the gui");
					guiManager.openShopPage((Player) event.getWhoClicked(), this.args);
				}

				if(tokens > 0 ){
					pluginManager.getPlayer(event.getWhoClicked().getUniqueId()).setTokens(hasToken - tokens);
				}
				if(money > 0){
					SaltyGames.econ.getAccountManager().getAccount(event.getWhoClicked().getName()).withdraw(money, event.getWhoClicked().getWorld().getName(), "LegendKingdomDollars");
				}

				return true;

			default:
				Bukkit.getConsoleSender().sendMessage("Missing case: "+action);
				return false;
		}
	}

	public void onInvClick(InventoryClickEvent event){
		if(event.getCurrentItem() == null) return;
		AButton button = grid[event.getRawSlot()];
		boolean perInvitation = false;
		StartMultiplayerGamePage mpGui = null;
		if(button == null){
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR){
				if(guiManager.getCurrentGui(event.getWhoClicked().getUniqueId()) instanceof StartMultiplayerGamePage){
					mpGui = (StartMultiplayerGamePage)guiManager.getCurrentGui(event.getWhoClicked().getUniqueId());
					button = mpGui.getButton(event.getWhoClicked().getUniqueId(), event.getSlot());
					if(button == null) return;
					perInvitation = true;
				} else {
					return;
				}
			} else {
				return;
			}
		}

		if(action(event, button.getAction(), button.getArgs())){
			if(SaltyGamesSettings.playSounds && pluginManager.getPlayer(event.getWhoClicked().getUniqueId()).isPlaySounds() && button.getAction() != ClickAction.NOTHING) {
				((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), successfulClick, volume, pitch);
			}
			if(perInvitation){
				mpGui.removeInvite( UUID.fromString(button.getArgs()[2]), event.getWhoClicked().getUniqueId());
			}
		} else {
			if(SaltyGamesSettings.playSounds && pluginManager.getPlayer(event.getWhoClicked().getUniqueId()).isPlaySounds() && button.getAction() != ClickAction.NOTHING) {
				((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), unsuccessfulClick, volume, pitch);
			}
		}
	}
	public void onInvClose(InventoryCloseEvent event){
		inGui.remove(event.getPlayer().getUniqueId());
		SaltyGames.debug("GUI was closed");
	}

	public boolean isInGui(UUID uuid){
		return inGui.contains(uuid);
	}

	public boolean isInGui(Player uuid){
		return inGui.contains(uuid.getUniqueId());
	}

	public void setButton(AButton button, int slot){
		grid[slot] = button;
		this.inventory.setItem(slot, button);
	}

	public void setButton(AButton button){
		int i = 0;
		while(grid[i] != null){
			i++;
		}
		setButton(button, i);
	}

	public void setLowerButton(AButton button, int slot){
		lowerGrid[slot] = button;
	}

	public void setLowerButton(AButton button){
		int i = 0;
		while(lowerGrid[i] != null){
			i++;
		}
		setLowerButton(button, i);
	}

	public void onBottomInvClick(InventoryClickEvent event) {
		if(lowerGrid != null && lowerGrid[event.getSlot()] != null){
			if(action(event, lowerGrid[event.getSlot()].getAction(), lowerGrid[event.getSlot()].getArgs())){
				if(SaltyGamesSettings.playSounds && pluginManager.getPlayer(event.getWhoClicked().getUniqueId()).isPlaySounds()) {
					((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), successfulClick, volume, pitch);
				}
			} else {
				((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), unsuccessfulClick, volume, pitch);
			}
		}
	}

	
	public void removePlayer(UUID uuid){
		inGui.remove(uuid);
		openInventories.remove(uuid);
	}

	public String[] getArgs(){
		return this.args;
	}

	public String getTitle(){
		return title;
	}
	
}
