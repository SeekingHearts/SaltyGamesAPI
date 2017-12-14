package main.java.me.aaron.saltygamesapi.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.aaron.saltygamesapi.ArenaState;

public class MessagesConfig {

	private FileConfiguration classesConfig = null;
	private File classesFile = null;
	private JavaPlugin pl = null;

	public MessagesConfig(final JavaPlugin pl) {
		this.pl = pl;
		
	}
	
	public void init() {
		
		getConfig().options().header("Enthält eine Nachrichen für eine einfache Übersetzung. Du kannst Nachrichten entfernen, indem du sie auf: '' (nichts) einstellst.");
		final Map<String, String> namecol = ArenaState.getAllStateNameColors();
		for (final String state : namecol.keySet()) {
			final String color = namecol.get(state);
			getConfig().addDefault("signs." + state.toLowerCase() + ".0", color + "[]");
			getConfig().addDefault("signs." + state.toLowerCase() + ".1", color + "<arena>");
			getConfig().addDefault("signs." + state.toLowerCase() + ".2", color + "<count>/<maxcount>");
			getConfig().addDefault("signs." + state.toLowerCase() + ".3", color + "[]");
		}
		
		getConfig().addDefault("signs.arcade.0", "[]");
		getConfig().addDefault("signs.arcade.1", "&cArcade");
		getConfig().addDefault("signs.arcade.2", "<count>/<maxcount>");
		getConfig().addDefault("signs.arcade.3", "[]");
		
		getConfig().addDefault("signs.leave.0", "");
		getConfig().addDefault("signs.leave.1", "&4Leave");
		getConfig().addDefault("signs.leave.2", "");
		getConfig().addDefault("signs.leave.3", "");

		getConfig().addDefault("signs.random.0", "&a[]");
		getConfig().addDefault("signs.random.1", "&2Random");
		getConfig().addDefault("signs.random.2", "");
		getConfig().addDefault("signs.random.3", "&a[]");
	
		getConfig().addDefault("messages.no_perm", no_perm);
        getConfig().addDefault("messages.successfully_reloaded", successfully_reloaded);
        getConfig().addDefault("messages.successfully_set", successfully_set);
        getConfig().addDefault("messages.successfully_saved_arena", successfully_saved_arena);
        getConfig().addDefault("messages.arena_invalid", arena_invalid);
        getConfig().addDefault("messages.failed_saving_arena", failed_saving_arena);
        getConfig().addDefault("messages.broadcast_players_left", broadcast_players_left);
        getConfig().addDefault("messages.broadcast_player_joined", broadcast_player_joined);
        getConfig().addDefault("messages.player_died", player_died);
        getConfig().addDefault("messages.arena_action", arena_action);
        getConfig().addDefault("messages.you_already_are_in_arena", you_already_are_in_arena);
        getConfig().addDefault("messages.you_joined_arena", you_joined_arena);
        getConfig().addDefault("messages.not_in_arena", not_in_arena);
        getConfig().addDefault("messages.teleporting_to_arena_in", teleporting_to_arena_in);
        getConfig().addDefault("messages.starting_in", starting_in);
        getConfig().addDefault("messages.failed_removing_arena", failed_removing_arena);
        getConfig().addDefault("messages.successfully_removed", successfully_removed);
        getConfig().addDefault("messages.failed_removing_component", failed_removing_component);
        getConfig().addDefault("messages.joined_arena", joined_arena);
        getConfig().addDefault("messages.you_won", you_won);
        getConfig().addDefault("messages.you_lost", you_lost);
        getConfig().addDefault("messages.you_got_a_kill", you_got_a_kill);
        getConfig().addDefault("messages.player_was_killed_by", player_was_killed_by);
        getConfig().addDefault("messages.arena_not_initialized", arena_not_initialized);
        getConfig().addDefault("messages.guns.attributelevel_increased", attributelevel_increased);
        getConfig().addDefault("messages.guns.not_enough_credits", not_enough_credits);
        getConfig().addDefault("messages.guns.too_many_main_guns", too_many_main_guns);
        getConfig().addDefault("messages.guns.successfully_set_main_gun", successfully_set_main_gun);
        getConfig().addDefault("messages.guns.all_guns", all_guns);
        getConfig().addDefault("messages.arcade_next_minigame", arcade_next_minigame);
        getConfig().addDefault("messages.arcade_joined_waiting", arcade_joined_waiting);
        getConfig().addDefault("messages.arcade_joined_spectator", arcade_joined_spectator);
        getConfig().addDefault("messages.arcade_new_round", arcade_new_round);
        getConfig().addDefault("messages.arena_disabled", arena_disabled);
        getConfig().addDefault("messages.you_can_leave_with", you_can_leave_with);
        getConfig().addDefault("messages.no_perm_to_join_arena", no_perm_to_join_arena);
        getConfig().addDefault("messages.set_kit", set_kit);
        getConfig().addDefault("messages.classes_item", classes_item);
        getConfig().addDefault("messages.achievement_item", achievement_item);
        getConfig().addDefault("messages.shop_item", shop_item);
        getConfig().addDefault("messages.spectator_item", spectator_item);
        getConfig().addDefault("messages.server_broadcast_winner", server_broadcast_winner);
        getConfig().addDefault("messages.exit_item", exit_item);
        getConfig().addDefault("messages.successfully_bought_kit", successfully_bought_kit);
        getConfig().addDefault("messages.scoreboard.title", scoreboard_title);
        getConfig().addDefault("messages.scoreboard.lobby_title", scoreboard_lobby_title);
        getConfig().addDefault("messages.you_got_kicked_because_vip_joined", you_got_kicked_because_vip_joined);
        getConfig().addDefault("messages.powerup_spawned", powerup_spawned);
	
        if (!getConfig().isSet("config.generatedv182")) {
        	getConfig().addDefault("messages.custom_scoreboard.line0", "Spieler:<playercount>");
            getConfig().addDefault("messages.custom_scoreboard.line1", "Spectators:<lostplayercount>");
            getConfig().addDefault("messages.custom_scoreboard.line2", "Verbleibend:<playeralivecount>");
            getConfig().addDefault("messages.custom_scoreboard.line3", "Dein Geld:<points>");
            getConfig().addDefault("messages.custom_scoreboard.line4", "Gewonnen:<wins>");
            getConfig().addDefault("messages.custom_lobby_scoreboard.line0", "Spieler:<playercount>");
            getConfig().addDefault("messages.custom_lobby_scoreboard.line1", "Maximale Spieler:<maxplayercount>");
            getConfig().addDefault("messages.custom_lobby_scoreboard.line2", "Dein Geld:<points>");
            getConfig().addDefault("messages.custom_lobby_scoreboard.line3", "Gewonnen:<wins>");
        }
        
        getConfig().addDefault("messages.you_got_the_achievement", you_got_the_achievement);
        getConfig().addDefault("messages.game_started", game_started);
        getConfig().addDefault("messages.no_game_started", no_game_started);
        getConfig().addDefault("messages.author_of_the_map", author_of_the_map);
        getConfig().addDefault("messages.description_of_the_map", description_of_the_map);
        getConfig().addDefault("messages.not_enough_money", not_enough_money);
        getConfig().addDefault("messages.possible_kits", possible_kits);
        getConfig().addDefault("messages.possible_shopitems", possible_shopitems);
        getConfig().addDefault("messages.cancelled_starting", cancelled_starting);
        getConfig().addDefault("messages.minigame_description", minigame_description);
        getConfig().addDefault("messages.successfully_bought_shopitem", successfully_bought_shopitem);
        getConfig().addDefault("messages.already_bought_shopitem", already_bought_shopitem);
        getConfig().addDefault("messages.you_received_rewards", you_received_rewards);
        getConfig().addDefault("messages.you_received_rewards_2", you_received_rewards_2);
        getConfig().addDefault("messages.you_received_rewards_3", you_received_rewards_3);
        getConfig().addDefault("messages.already_in_arena", already_in_arena);
        getConfig().addDefault("messages.stop_cause_maximum_game_time", stop_cause_maximum_game_time);
        getConfig().addDefault("messages.compass.no_player_found", compass_no_player_found);
        getConfig().addDefault("messages.compass.found_player", compass_player_found);
        getConfig().addDefault("messages.you_got_a_participation_reward", you_got_a_participation_reward);
        getConfig().addDefault("messages.kit_warning", kit_warning);
        
        if (!getConfig().isSet("config.generatedv1102")) {
        	getConfig().addDefault("messages.stats.line0", "&7----- &a&lStatistiken &7-----; ");
            getConfig().addDefault("messages.stats.line1", "&7Gewonnen: &a<wins>");
            getConfig().addDefault("messages.stats.line2", "&7Verloren: &c<loses>");
            getConfig().addDefault("messages.stats.line3", "&7Insgesamte Kills: &a<alltime_kills>");
            getConfig().addDefault("messages.stats.line4", "&7Insgesamte Tode: &c<alltime_deaths>");
            getConfig().addDefault("messages.stats.line5", "&7KDR: &e<kdr>");
            getConfig().addDefault("messages.stats.line6", "&7Punkte: &e<points>");
            getConfig().addDefault("messages.stats.line7", " ;&7-----------------");
        }
        
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        no_perm = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.no_perm"));
        successfully_reloaded = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successfully_reloaded"));
        successfully_set = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successfully_set"));
        successfully_saved_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successfully_saved_arena"));
        failed_saving_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failed_saving_arena"));
        arena_invalid = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.arena_invalid"));
        player_died = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.player_died"));
        broadcast_players_left = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.broadcast_players_left"));
        broadcast_player_joined = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.broadcast_player_joined"));
        arena_action = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.arena_action"));
        you_already_are_in_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_already_are_in_arena"));
        you_joined_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_joined_arena"));
        not_in_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.not_in_arena"));
        teleporting_to_arena_in = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.teleporting_to_arena_in"));
        starting_in = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.starting_in"));
        failed_removing_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failed_removing_arena"));
        successfully_removed = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successfully_removed"));
        failed_removing_component = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failed_removing_component"));
        joined_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.joined_arena"));
        you_won = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_won"));
        you_lost = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_lost"));
        you_got_a_kill = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_got_a_kill"));
        player_was_killed_by = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.player_was_killed_by"));
        arena_not_initialized = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.arena_not_initialized"));
        arcade_next_minigame = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.arcade_next_minigame"));
        arcade_new_round = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.arcade_new_round"));
        arena_disabled = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.arena_disabled"));
        you_can_leave_with = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_can_leave_with"));
        arcade_joined_waiting = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.arcade_joined_waiting"));
        arcade_joined_spectator = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.arcade_joined_spectator"));
        no_perm_to_join_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.no_perm_to_join_arena"));
        set_kit = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.set_kit"));
        classes_item = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.classes_item"));
        achievement_item = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.achievement_item"));
        shop_item = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.shop_item"));
        spectator_item = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.spectator_item"));
        server_broadcast_winner = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.server_broadcast_winner"));
        exit_item = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.exit_item"));
        successfully_bought_kit = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successfully_bought_kit"));
        scoreboard_title = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.scoreboard.title"));
        scoreboard_lobby_title = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.scoreboard.lobby_title"));
        you_got_kicked_because_vip_joined = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_got_kicked_because_vip_joined"));
        powerup_spawned = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.powerup_spawned"));
        you_got_the_achievement = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_got_the_achievement"));
        game_started = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.game_started"));
        no_game_started = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.no_game_started"));
        
        author_of_the_map = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.author_of_the_map"));
        description_of_the_map = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.description_of_the_map"));
        not_enough_money = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.not_enough_money"));
        possible_kits = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.possible_kits"));
        possible_shopitems = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.possible_shopitems"));
        cancelled_starting = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.cancelled_starting"));
        minigame_description = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.minigame_description"));
        successfully_bought_shopitem = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successfully_bought_shopitem"));
        already_bought_shopitem = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.already_bought_shopitem"));
        you_received_rewards = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_received_rewards"));
        you_received_rewards_2 = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_received_rewards_2"));
        you_received_rewards_3 = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_received_rewards_3"));
        already_in_arena = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.already_in_arena"));
        stop_cause_maximum_game_time = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.stop_cause_maximum_game_time"));
        compass_no_player_found = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.compass.no_player_found"));
        compass_player_found = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.compass.found_player"));
        you_got_a_participation_reward = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.you_got_a_participation_reward"));
        kit_warning = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.kit_warning"));
        
        attributelevel_increased = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.guns.attributelevel_increased"));
        not_enough_credits = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.guns.not_enough_credits"));
        too_many_main_guns = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.guns.too_many_main_guns"));
        successfully_set_main_gun = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.guns.successfully_set_main_gun"));
        all_guns = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.guns.all_guns"));
        
        getConfig().set("config.generatedv182", true);
        getConfig().set("config.generatedv1102", true);
        saveConfig();
	}
	
	public String no_perm                           = "&cYou don't have permission.";
    public String successfully_reloaded             = "&aSuccessfully reloaded all configs.";
    public String successfully_set                  = "&aSuccessfully set &3<component>&a.";
    public String successfully_saved_arena          = "&aSuccessfully saved &3<arena>&a.";
    public String failed_saving_arena               = "&cFailed to save &3<arena>&c.";
    public String failed_removing_arena             = "&cFailed to remove &3<arena>&c.";
    public String arena_invalid                     = "&3<arena> &cappears to be invalid.";
    public String broadcast_players_left            = "&eThere are &4<count> &eplayers left!";
    public String broadcast_player_joined           = "&2<player> &ajoined the arena! (<count>/<maxcount>)";
    public String player_died                       = "&c<player> died.";
    public String arena_action                      = "&aYou <action> arena &3<arena>&a!";
    public String you_joined_arena                  = "&aYou joined arena &3<arena>&a!";
    public String you_already_are_in_arena          = "&aYou already seem to be in arena &3<arena>&a!";
    public String arena_not_initialized             = "&cThe arena appears to be not initialized, did you save the arena?";
    public String not_in_arena                      = "&cYou don't seem to be in an arena right now.";
    public String teleporting_to_arena_in           = "&7Teleporting to arena in <count>.";
    public String starting_in                       = "&aStarting in <count>!";
    public String successfully_removed              = "&cSuccessfully removed &3<component>&c!";
    public String failed_removing_component         = "&cFailed removing &3<component>&c. <cause>.";
    public String joined_arena                      = "&aYou joined &3<arena>&a.";
    public String you_won                           = "&aYou &2won &athe game!";
    public String you_lost                          = "&cYou &4lost &cthe game.";
    public String you_got_a_kill                    = "&aYou killed &2<player>!";
    public String player_was_killed_by              = "&4<player> &cwas killed by &4<killer>&c!";
    public String attributelevel_increased          = "&aThe <attribute> level was increased successfully!";
    public String not_enough_credits                = "&cThe max level of 3 was reached or you don't have enough credits. Needed: <credits>";
    public String too_many_main_guns                = "&cYou already have 2 main guns, remove one first.";
    public String successfully_set_main_gun         = "&aSuccessfully set a main gun (of a maximum of two).";
    public String arcade_next_minigame              = "&6Next Arcade game: &4<minigame>&6!";
    public String arena_disabled                    = "&cThe arena is disabled thus you can't join.";
    public String all_guns                          = "&aYour current main guns: &2<guns>";
    public String you_can_leave_with                = "&cYou can leave with <cmd> or /l!";
    public String arcade_joined_spectator           = "&6You joined Arcade as a spectator! You'll be able to play in the next minigame.";
    public String arcade_joined_waiting             = "&6You joined Arcade! Waiting for <count> more players to start.";
    public String arcade_new_round                  = "&6Next Arcade round in <count>!";
    public String no_perm_to_join_arena             = "&cYou don't have permission (arenas.<arena>) to join this arena as it's vip!";
    public String set_kit                           = "&aSuccessfully set &2<kit>&a!";
    public String classes_item                      = "&4Classes";
    public String achievement_item                  = "&4Achievements";
    public String shop_item                         = "&4Shop";
    public String spectator_item                    = "&4Players";
    public String server_broadcast_winner           = "&2<player> &awon the game on &2<arena>&a!";
    public String exit_item                         = "&4Leave the game";
    public String successfully_bought_kit           = "&aSuccessfully bought &2<kit> &afor &2<money>&a.";
    public String scoreboard_title                  = "&4<arena>";
    public String scoreboard_lobby_title            = "&4[<arena>]";
    public String you_got_kicked_because_vip_joined = "&cYou got kicked out of the game because a vip joined!";
    public String powerup_spawned                   = "&2A Powerup spawned!";
    public String you_got_the_achievement           = "&3You got the achievement &b<achievement>&3!";
    public String game_started                      = "&2The game has started!";
    public String no_game_started                   = "&2There is no started game!";
    public String author_of_the_map                 = "&3You are playing on the map &b<arena> &3by &b<author>&3!";
    public String description_of_the_map            = "<description>";
    public String not_enough_money                  = "&cYou don't have enough money.";
    public String possible_kits                     = "&aPossible kits: &2";
    public String possible_shopitems                = "&aPossible shop items: &2";
    public String cancelled_starting                = "&cThe starting countdown was cancelled because there's only one player left in the arena.";
    public String minigame_description              = "";
    public String successfully_bought_shopitem      = "&aSuccessfully bought &2<shopitem> &afor &2<money>&a.";
    public String already_bought_shopitem           = "&aYou already had &2<shopitem>&a.";
    public String you_received_rewards              = "&aYou received a reward of &2<economyreward>";
    public String you_received_rewards_2            = " &aand ";
    public String you_received_rewards_3            = "&2<itemreward>&a!";
    public String already_in_arena                  = "&cYou are already in an arena.";
    public String stop_cause_maximum_game_time      = "&cThe game is stopping in 5 seconds because the maximum game time was reached.";
    public String compass_no_player_found           = "&cNo near players found!";
    public String compass_player_found              = "&aThe compass is tracking &3<player> &anow. Distance: <distance>";
    public String you_got_a_participation_reward    = "&aYou received &2<economyreward> &afor participating!";
    public String kit_warning                       = "&7Be aware that you'll only get the &8last &7kit you bought even if you buy all of them.";

	public FileConfiguration getConfig() {
		if (classesConfig == null)
			reloadConfig();
		return classesConfig;
	}
	
	public void saveConfig()
    {
        if (classesConfig == null || classesFile == null)
        	return;
        try
        {
            this.getConfig().save(classesFile);
        }
        catch (final IOException ex)
        {
            ;
        }
    }

	public void reloadConfig() {

		if (this.classesFile == null) {
			this.classesFile = new File(this.pl.getDataFolder(), "messages.yml");
		}
		this.classesConfig = YamlConfiguration.loadConfiguration(this.classesFile);

		final InputStream defConfigStream = this.pl.getResource("messages.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.classesConfig.setDefaults(defConfig);
		}
	}
}
