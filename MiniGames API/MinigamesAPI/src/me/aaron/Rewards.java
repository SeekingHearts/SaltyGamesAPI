package me.aaron;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.utils.Util;
import me.aaron.utils.Validator;

public class Rewards {
	
	private JavaPlugin pl = null;
	
	private boolean econrewards;
	private boolean itemrewards;
	private boolean cmdsrewards;
	private boolean kill_econrewards;
	private boolean kill_cmdsrewards;
	private boolean participation_econrewards;
	private boolean participation_cmdsrewards;
	
	private int econ_reward = 0;
	private int kill_econ_reward = 0;
	private int participation_econ_reward = 0;
	private String command = "";
	private String kill_command = "";
	private String participation_command = "";
	
	private ItemStack[] items = null;
	
	public Rewards(final JavaPlugin pl)
    {
        this.pl = pl;
        this.reloadVariables();
        
        if (!MinigamesAPI.getAPI().economyAvailable())
        {
            this.econrewards = false;
            this.kill_econrewards = false;
            this.participation_econrewards = false;
        }
    }
	
	public void reloadVariables()
    {
        econrewards = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_REWARDS_ECONOMY);
        itemrewards = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_REWARDS_ITEM_REWARD);
        cmdsrewards = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_REWARDS_COMMAND_REWARD);
        kill_econrewards = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_REWARDS_ECONOMY_FOR_KILLS);
        kill_cmdsrewards = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_REWARDS_COMMAND_REWARD_FOR_KILLS);
        participation_econrewards = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_REWARDS_ECONOMY_FOR_PARTICIPATION);
        participation_cmdsrewards = pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_REWARDS_COMMAND_REWARD_FOR_PARTICIPATION);
        
        econ_reward = pl.getConfig().getInt(ArenaConfigStrings.CONFIG_REWARDS_ECONOMY_REWARD);
        command = pl.getConfig().getString(ArenaConfigStrings.CONFIG_REWARDS_COMMAND);
        items = Util.parseItems(pl.getConfig().getString(ArenaConfigStrings.CONFIG_REWARDS_ITEM_REWARD_IDS)).toArray(new ItemStack[0]);
        kill_econ_reward = pl.getConfig().getInt(ArenaConfigStrings.CONFIG_REWARDS_ECONOMY_REWARD_FOR_KILLS);
        kill_command = pl.getConfig().getString(ArenaConfigStrings.CONFIG_REWARDS_COMMAND_FOR_KILLS);
        participation_econ_reward = pl.getConfig().getInt(ArenaConfigStrings.CONFIG_REWARDS_ECONOMY_REWARD_FOR_PARTICIPATION);
        participation_command = pl.getConfig().getString(ArenaConfigStrings.CONFIG_REWARDS_COMMAND_FOR_PARTICIPATION);
    }
	
	public void giveRewardsToWinners(final Arena arena) {
		for (final String p_ : arena.getAllPlayers()) {
//			
		}
	}
	
	public void giveReward(final String p_) {
		if (Validator.isPlayerOnline(p_)) {
			final Player p = Bukkit.getPlayer(p_);
			
			if (econrewards) {
				MinigamesAPI.getAPI();
				MinigamesAPI.econ.depositPlayer(p.getName(), econ_reward);
			}
			MinigamesAPI.getAPI().getPluginInstance(pl).getStatsInstance().win(p_, 10);
		}
	}
	
	public void giveKillReward(final String p_) {
		if (Validator.isPlayerOnline(p_)) {
			final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(pl);
			final Player p = Bukkit.getPlayer(p_);
			
			if (kill_econrewards && MinigamesAPI.getAPI().economyAvailable()) {
				MinigamesAPI.getAPI();
				MinigamesAPI.econ.depositPlayer(p.getName(), kill_econ_reward);
			}
			
			if (kill_cmdsrewards)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), kill_command.replaceAll("<player>", p_));
			
			pli.getStatsInstance().addPoints(p_, pli.getStatsInstance().stats_kill_points);
			pli.getStatsInstance().addKill(p_);
			pli.getSQLInstance().updateWinnerStats(p, pli.getStatsInstance().stats_kill_points, false);
		}
	}
	
	public void giveKillReward(final String p_, final int reward) {
		giveKillReward(p_);
	}
	
	public void giveAchievementReward(final String p_, final boolean econ, final boolean isCommand, final int money_reward, final String cmd) {
		if (Validator.isPlayerOnline(p_)) {
			final Player p = Bukkit.getPlayer(p_);
			
			if (econ && MinigamesAPI.getAPI().economyAvailable()) {
				MinigamesAPI.getAPI();
				MinigamesAPI.econ.depositPlayer(p.getName(), money_reward);
			}
			
			if (isCommand)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("<player>", p_));
		}
	}
	
//	KILLING
	
//	WINNING
	private void giveWinReward(final String p_, final Arena ar, final int global_multiplier) {
		giveWinReward(p_, ar, 1);
	}
	
	public void giveWinReward(final String p_, final Arena ar, final ArrayList<String> players, final int global_multiplier) {
		if (Validator.isPlayerOnline(p_))
        {
            final PluginInstance pli = MinigamesAPI.getAPI().getPluginInstance(this.pl);
            final Player p = Bukkit.getPlayer(p_);
            if (!pli.global_lost.containsKey(p_))
            {
                String received_rewards_msg = pli.getMessagesConfig().you_received_rewards;
                if (this.econrewards && MinigamesAPI.getAPI().economyAvailable())
                {
                    int multiplier = global_multiplier;
                    if (pli.getShopHandler().hasItemBought(p_, "coin_boost2_solo"))
                    {
                        multiplier = 2;
                    }
                    if (pli.getShopHandler().hasItemBought(p_, "coin_boost3_solo"))
                    {
                        multiplier = 3;
                    }
                    MinigamesAPI.getAPI();
                    MinigamesAPI.econ.depositPlayer(p.getName(), this.econ_reward * multiplier);
                    received_rewards_msg = received_rewards_msg.replaceAll("<economyreward>", Integer.toString(this.econ_reward * multiplier) + " " + MinigamesAPI.econ.currencyNamePlural());
                }
                else
                {
                    received_rewards_msg = received_rewards_msg.replaceAll("<economyreward>", "");
                }
                if (this.itemrewards)
                {
                    p.getInventory().addItem(this.items);
                    p.updateInventory();
                    String items_str = "";
                    for (final ItemStack i : this.items)
                    {
                        items_str += Integer.toString(i.getAmount()) + " " + Character.toUpperCase(i.getType().toString().charAt(0)) + i.getType().toString().toLowerCase().substring(1) + ", ";
                    }
                    if (items_str.length() > 2)
                    {
                        items_str = items_str.substring(0, items_str.length() - 2);
                    }
                    if (this.econrewards && MinigamesAPI.getAPI().economyAvailable())
                    {
                        received_rewards_msg += pli.getMessagesConfig().you_received_rewards_2;
                        received_rewards_msg += pli.getMessagesConfig().you_received_rewards_3.replaceAll("<itemreward>", items_str);
                    }
                    else
                    {
                        received_rewards_msg += pli.getMessagesConfig().you_received_rewards_3.replaceAll("<itemreward>", items_str);
                    }
                }
                else
                {
                    received_rewards_msg += pli.getMessagesConfig().you_received_rewards_3.replaceAll("<itemreward>", "");
                }
                if (cmdsrewards)
                {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.command.replaceAll("<player>", p_));
                }
                
                pli.getStatsInstance().win(p_, pli.getStatsInstance().stats_win_points);
                
                try
                {
                    if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_BROADCAST_WIN))
                    {
                        final String msgs[] = pli.getMessagesConfig().server_broadcast_winner.replaceAll("<player>", p_).replaceAll("<arena>", ar.getInternalName()).split(";");
                        for (final String msg : msgs)
                        {
                            Bukkit.getServer().broadcastMessage(msg);
                        }
                    }
                    else
                    {
                        final String msgs[] = pli.getMessagesConfig().server_broadcast_winner.replaceAll("<player>", p_).replaceAll("<arena>", ar.getInternalName()).split(";");
                        for (final String playername : players)
                        {
                            if (Validator.isPlayerOnline(playername))
                            {
                                Bukkit.getPlayer(playername).sendMessage(msgs);
                            }
                        }
                    }
                }
                catch (final Exception e)
                {
                    this.pl.getLogger().log(Level.WARNING, "Could not find arena for broadcast. ", e);
                }
                
                Util.sendMessage(this.pl, p, pli.getMessagesConfig().you_won);
                Util.sendMessage(this.pl, p, received_rewards_msg);
                if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_EFFECTS_1_8_TITLES))
                {
                    Effects.playTitle(p, pli.getMessagesConfig().you_won, 0);
                }
                
                // Participation Rewards
                if (this.participation_econrewards)
                {
                    MinigamesAPI.getAPI();
                    MinigamesAPI.econ.depositPlayer(p.getName(), this.participation_econ_reward);
                    Util.sendMessage(this.pl, p, pli.getMessagesConfig().you_got_a_participation_reward.replaceAll("<economyreward>",
                            Integer.toString(this.participation_econ_reward) + " " + MinigamesAPI.econ.currencyNamePlural()));
                }
                if (this.participation_cmdsrewards)
                {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.participation_command.replaceAll("<player>", p_));
                }
                
                if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_SPAWN_FIREWORKS_FOR_WINNERS))
                {
                    Bukkit.getScheduler().runTaskLater(this.pl, () -> Util.spawnFirework(p), 20L);
                }
            }
            else
            {
                Util.sendMessage(this.pl, p, pli.getMessagesConfig().you_lost);
                if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_EFFECTS_1_8_TITLES))
                {
                    Effects.playTitle(p, pli.getMessagesConfig().you_lost, 0);
                }
                MinigamesAPI.getAPI().getPluginInstance(this.pl).getStatsInstance().lose(p_);
            }
        }
	}

}
