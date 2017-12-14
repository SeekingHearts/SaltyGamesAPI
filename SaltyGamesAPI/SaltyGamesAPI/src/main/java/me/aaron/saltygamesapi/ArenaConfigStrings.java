package main.java.me.aaron.saltygamesapi;

public interface ArenaConfigStrings {

	String BOUNDS_LOW = "bounds.low";
	String BOUNDS_HIGH = "bounds.high";
	String LOBBY_BOUNDS_LOW = "lobbybounds.bounds.low";
	String LOBBY_BOUNDS_HIGH = "lobbybounds.bounds.high";
	String SPEC_BOUNDS_LOW = "specbounds.bounds.low";
	String SPEC_BOUNDS_HIGH = "specbounds.bounds.high";
	String SPEC_SPAWN = "specspawn";
	
	String ARENAS_PREFIX = "arenas.";
	String DISPLAYNAME_SUFFIX = ".displayname";
	String AUTHOR_SUFFIX = ".author";
	String DESCRIPTION_SUFFIX = ".description";
	
	String CONFIG_CLASS_SELECTION_ITEM = "config.selection_items.classes_selection_item"; 
    String CONFIG_EXIT_ITEM = "config.selection_items.exit_item"; 
    String CONFIG_ACHIEVEMENT_ITEMS = "config.selection_items.achievement_item"; 
    String CONFIG_SPECTATOR_ITEM = "config.selection_items.spectator_item"; 
    String CONFIG_SHOP_SELECTION_ITEM = "config.selection_items.shop_selection_item"; 
    String CONFIG_CLASSES_GUI_ROWS = "config.GUI.classes_gui_rows"; 
    String CONFIG_SHOP_GUI_ROWS = "config.GUI.shop_gui_rows"; 
    String CONFIG_SPECTATOR_AFTER_FALL_OR_DEATH = "config.spectator.spectator_after_fall_or_death"; 
    String CONFIG_SPECTATOR_MOVE_Y_LOCK = "config.spectator.spectator_move_y_lock"; 
    String CONFIG_DEFAULT_MAX_PLAYERS = "config.defaults.default_max_players"; 
    String CONFIG_DEFAULT_MIN_PLAYERS = "config.defaults.default_min_players"; 
    String CONFIG_DEFAULT_MAX_GAME_TIME_IN_MINUTES = "config.defaults.default_max_game_time_in_minutes"; 
    String CONFIG_LOBBY_COUNTDOWN = "config.countdowns.lobby_countdown"; 
    String CONFIG_INGAME_COUNTDOWN = "config.countdowns.ingame_countdown"; 
    String CONFIG_INGAME_COUNTDOWN_ENABLED = "config.countdowns.ingame_countdown_enabled"; 
	
    String CONFIG_SKIP_LOBBY = "config.countdowns.skip_lobby";
    String CONFIG_CLEANINV_WHILE_INGAMECOUNTDOWN = "config.countdowns.clearinv_while_ingamecountdown";
    
    String CONFIG_CLASSES_ENABLED = "config.classes_enabled"; 
    String CONFIG_SHOP_ENABLED = "config.shop_enabled"; 
    String CONFIG_USE_CREDITS_INSTEAD_MONEY_FOR_KITS = "config.use_credits_instead_of_money_for_kits"; 
    String CONFIG_RESET_INV_WHEN_LEAVING_SERVER = "config.reset_inventory_when_players_leave_server"; 
    String CONFIG_COLOR_BACKGROUND_WOOL = "config.color_background_wool_of_signs"; 
    String CONFIG_SHOW_CLASSES_WITHOUT_PERM = "config.show_classes_without_usage_permission";
    
    String CONFIG_REWARDS_ECONOMY = "config.rewards.economy"; 
    String CONFIG_REWARDS_ECONOMY_REWARD = "config.rewards.economy_reward"; 
    String CONFIG_REWARDS_ITEM_REWARD = "config.rewards.item_reward"; 
    String CONFIG_REWARDS_ITEM_REWARD_IDS = "config.rewards.item_reward_ids"; 
    String CONFIG_REWARDS_COMMAND_REWARD = "config.rewards.command_reward"; 
    String CONFIG_REWARDS_COMMAND = "config.rewards.command"; 
    String CONFIG_REWARDS_ECONOMY_FOR_KILLS = "config.rewards.economy_for_kills"; 
    String CONFIG_REWARDS_ECONOMY_REWARD_FOR_KILLS = "config.rewards.economy_reward_for_kills"; 
    String CONFIG_REWARDS_COMMAND_REWARD_FOR_KILLS = "config.rewards.command_reward_for_kills"; 
    String CONFIG_REWARDS_COMMAND_FOR_KILLS = "config.rewards.command_for_kills"; 
    String CONFIG_REWARDS_ECONOMY_FOR_PARTICIPATION = "config.rewards.economy_for_participation"; 
    String CONFIG_REWARDS_ECONOMY_REWARD_FOR_PARTICIPATION = "config.rewards.economy_reward_for_participation"; 
    String CONFIG_REWARDS_COMMAND_REWARD_FOR_PARTICIPATION = "config.rewards.command_reward_for_participation"; 
    String CONFIG_REWARDS_COMMAND_FOR_PARTICIPATION = "config.rewards.command_for_participation"; 
    
    
    String CONFIG_STATS_POINTS_FOR_KILL = "config.stats.points_for_kill"; 
    
    String CONFIG_STATS_POINTS_FOR_WIN = "config.stats.points_for_win"; 
    
    
    String CONFIG_ARCADE_ENABLED = "config.arcade.enabled"; 
    
    String CONFIG_ARCADE_MIN_PLAYERS = "config.arcade.min_players"; 
    
    String CONFIG_ARCADE_MAX_PLAYERS = "config.arcade.max_players"; 
    
    String CONFIG_ARCADE_ARENA_TO_PREFER_ENABLED = "config.arcade.arena_to_prefer.enabled"; 
    
    String CONFIG_ARCADE_ARENA_TO_PREFER_ARENA = "config.arcade.arena_to_prefer.arena"; 
    
    String CONFIG_ARCADE_LOBBY_COUNTDOWN = "config.arcade.lobby_countdown"; 
    
    String CONFIG_ARCADE_SHOW_EACH_LOBBY_COUNTDOWN = "config.arcade.show_each_lobby_countdown"; 
    
    String CONFIG_ARCADE_INFINITE_ENABLED = "config.arcade.infinite_mode.enabled"; 
    
    String CONFIG_ARCADE_INFINITE_SECONDS_TO_NEW_ROUND = "config.arcade.infinite_mode.seconds_to_new_round"; 
    
    String CONFIG_BUNGEE_GAME_ON_JOIN = "config.bungee.game_on_join"; 
    
    String CONFIG_BUNGEE_TELEPORT_ALL_TO_SERVER_ON_STOP_TP = "config.bungee.teleport_all_to_server_on_stop.tp"; 
    
    String CONFIG_BUNGEE_TELEPORT_ALL_TO_SERVER_ON_STOP_SERVER = "config.bungee.teleport_all_to_server_on_stop.server"; 
    
    String CONFIG_BUNGEE_WHITELIST_WHILE_GAME_RUNNING = "config.bungee.whitelist_while_game_running"; 
    
    String CONFIG_EXECUTE_CMDS_ON_STOP = "config.execute_cmds_on_stop"; 
    
    String CONFIG_CMDS = "config.cmds"; 
    
    String CONFIG_CMDS_AFTER = "config.cmds_after"; 
    
    String CONFIG_MAP_ROTATION = "config.map_rotation"; 
    
    String CONFIG_BROADCAST_WIN = "config.broadcast_win"; 
    
    String CONFIG_BUY_CLASSES_FOREVER = "config.buy_classes_forever"; 
    
    String CONFIG_DISABLE_COMMANDS_IN_ARENA = "config.disable_commands_in_arena"; 
    
    String CONFIG_COMMAND_WHITELIST = "config.command_whitelist"; 
    
    String CONFIG_LEAVE_COMMAND = "config.leave_command"; 
    
    String CONFIG_SPAWN_FIREWORKS_FOR_WINNERS = "config.spawn_fireworks_for_winners"; 
    
    String CONFIG_POWERUP_BROADCAST = "config.powerup_spawning.broadcast"; 
    
    String CONFIG_POWERUP_FIREWORKS = "config.powerup_spawning.spawn_firework"; 
    
    String CONFIG_USE_CUSTOM_SCOREBOARD = "config.use_custom_scoreboard"; 
    
    String CONFIG_DELAY_ENABLED = "config.delay.enabled"; 
    
    String CONFIG_DELAY_AMOUNT_SECONDS = "config.delay.amount_seconds"; 
    
    String CONFIG_SEND_GAME_STARTED_MSG = "config.send_game_started_msg"; 
    
    String CONFIG_AUTO_ADD_DEFAULT_KIT = "config.auto_add_default_kit"; 
    
    String CONFIG_LAST_MAN_STANDING_WINS = "config.last_man_standing_wins"; 
    
    String CONFIG_EFFECTS_BLOOD = "config.effects.blood"; 
    
    String CONFIG_EFFECTS_DMG_IDENTIFIER_HOLO = "config.effects.damage_identifier_holograms"; 
    
    String CONFIG_EFFECTS_DEAD_IN_FAKE_BED = "config.effects.dead_in_fake_bed"; 
    
    String CONFIG_EFFECTS_1_8_TITLES = "config.effects.1_8_titles"; 
    
    String CONFIG_EFFECTS_1_8_SPECTATOR_MODE = "config.effects.1_8_spectator_mode"; 
    
    String CONFIG_SOUNDS_LOBBY_COUNTDOWN = "config.sounds.lobby_countdown"; 
    
    String CONFIG_SOUNDS_INGAME_COUNTDOWN = "config.sounds.ingame_countdown"; 
    
    String CONFIG_CHAT_PER_ARENA_ONLY = "config.chat_per_arena_only"; 
    
    String CONFIG_CHAT_SHOW_SCORE_IN_ARENA = "config.chat_show_score_in_arena"; 
    
    String CONFIG_COMPASS_TRACKING_ENABLED = "config.compass_tracking_enabled"; 
    
    String CONFIG_ALLOW_CLASSES_SELECTION_OUT_OF_ARENAS = "config.allow_classes_selection_out_of_arenas"; 
    
    String CONFIG_SEND_STATS_ON_STOP = "config.send_stats_on_stop"; 
    
    String CONFIG_USE_XP_BAR_LEVEL = "config.use_xp_bar_level"; 
    
    String CONFIG_USE_OLD_RESET_METHOD = "config.use_old_reset_method"; 
    
    String CONFIG_CHAT_ENABLED = "config.chat_enabled"; 
    
    
    String CONFIG_EXTRA_LOBBY_ITEM_PREFIX = "config.extra_lobby_item."; 
    
    String CONFIG_EXTRA_LOBBY_ITEM_ENABLED_SUFFIX = ".enabled"; 
    
    String CONFIG_EXTRA_LOBBY_ITEM_ITEM_SUFFIX = ".item"; 
    
    String CONFIG_EXTRA_LOBBY_ITEM_NAME_SUFFIX = ".name"; 
    
    String CONFIG_EXTRA_LOBBY_ITEM_COMMAND_SUFFIX = ".command"; 
    
    
    String CONFIG_MYSQL_ENABLED = "mysql.enabled"; 
    String CONFIG_MYSQL_HOST = "mysql.host"; 
    String CONFIG_MYSQL_USER = "mysql.user"; 
    String CONFIG_MYSQL_PW = "mysql.pw"; 
    String CONFIG_MYSQL_DATABASE = "mysql.database"; 
    
    String RESET_INVENTORY = "config.reset_on_leave.inventory"; 

    String RESET_XP = "config.reset_on_leave.xp"; 

    String RESET_GAMEMMODE = "config.reset_on_leave.gamemode"; 
    
}
