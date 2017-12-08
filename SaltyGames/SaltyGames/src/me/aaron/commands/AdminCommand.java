package me.aaron.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.aaron.data.DataBase;
import me.aaron.data.SGPlayer;
import me.aaron.main.Language;
import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesLang;
import me.aaron.utils.Permission;

public class AdminCommand implements CommandExecutor {
	
	private SaltyGames plugin;
    private SaltyGamesLang lang;

    private HashMap<String, HashMap<String, List<String>>> missingLanguageKeys;


    public AdminCommand(SaltyGames plugin){
        this.plugin = plugin;
        this.lang = plugin.lang;

        missingLanguageKeys = new HashMap<>();

        new BukkitRunnable(){
            @Override
            public void run() {
                SaltyGames.debug(" running late check in Admin command");
                checkLanguageFiles();
            }
        }.runTask(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission(Permission.ADMIN.getPermissions())){
            sender.sendMessage(lang.PREFIX + lang.CMD_NO_PERM);
            return true;
        }

        if(args.length == 0) {
            sendHelpMessages(sender);
            return true;
        }

        if(args[0].equalsIgnoreCase("givetoken") || args[0].equalsIgnoreCase("taketoken") || args[0].equalsIgnoreCase("settoken")){
            if(args.length != 3){
                sender.sendMessage(lang.PREFIX + " /gba [givetoken:taketoken:settoken] [player name] [count (integer)]");
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if(player == null || !player.hasPlayedBefore()){
                sender.sendMessage(lang.PREFIX + ChatColor.RED + " can't find player " + args[1]);
                return true;
            }
            int count;
            try{
                count = Integer.parseInt(args[2]);
            } catch (NumberFormatException exception){
                sender.sendMessage(lang.PREFIX + ChatColor.RED + " last argument has to be an integer!");
                return true;
            }
            if(count < 0){
                sender.sendMessage(lang.PREFIX + ChatColor.RED + " count can't be negative!");
                return true;
            }

            // all arguments are valid

            // handle cached online players
            cachedPlayer:
            if(player.isOnline()){
                SGPlayer gbPlayer = plugin.getPluginManager().getPlayer(player.getUniqueId());
                if(gbPlayer == null){
                    break cachedPlayer;
                }
                switch (args[0].toLowerCase()){
                    case "givetoken":
                        gbPlayer.setTokens(gbPlayer.getTokens() + count);
                        break;

                    case "taketoken":
                        if(gbPlayer.getTokens() >= count){
                            gbPlayer.setTokens(gbPlayer.getTokens() - count);
                        } else {
                            sender.sendMessage(lang.PREFIX + ChatColor.RED + " " + player.getName() + " only has " + gbPlayer.getTokens() + " token!");
                            return true;
                        }
                        break;

                    case "settoken":
                        gbPlayer.setTokens(count);
                        break;

                    default: // can't happen due to the check at the beginning of the command
                        return false;

                }
                sender.sendMessage(lang.PREFIX + lang.CMD_TOKEN.replace("%player%", player.getName()).replace("%token%", String.valueOf(gbPlayer.getTokens())));
                return true;
            }

            // handle offline or not cached players
            if(!plugin.getDataBase().isSet(player.getUniqueId().toString())){
                switch (args[0].toLowerCase()){
                    case "givetoken":
                        plugin.getDataBase().set(player.getUniqueId().toString(), DataBase.TOKEN_PATH, count);
                        break;

                    case "taketoken":
                        sender.sendMessage(lang.PREFIX + ChatColor.RED+ " " + player.getName() + " only has " + 0 + " token!");
                        return true;

                    case "settoken":
                        plugin.getDataBase().set(player.getUniqueId().toString(), DataBase.TOKEN_PATH, count);
                        break;

                    default: // can't happen due to the check at the beginning of the command
                        return false;

                }
                sender.sendMessage(lang.PREFIX + lang.CMD_TOKEN.replace("%player%", player.getName()).replace("%token%", String.valueOf(count)));
                return true;
            } else {
                int oldCount = plugin.getDataBase().getInt(player.getUniqueId(), DataBase.TOKEN_PATH, 0);
                switch (args[0].toLowerCase()){
                    case "givetoken":
                        plugin.getDataBase().set(player.getUniqueId().toString(), DataBase.TOKEN_PATH, count + oldCount);
                        break;

                    case "taketoken":
                        if(oldCount >= count){
                            plugin.getDataBase().set(player.getUniqueId().toString(), DataBase.TOKEN_PATH, oldCount - count);
                        } else {
                            sender.sendMessage(lang.PREFIX + ChatColor.RED + " " + player.getName() + " only has " + oldCount + " token!");
                            return true;
                        }

                    case "settoken":
                        plugin.getDataBase().set(player.getUniqueId().toString(), DataBase.TOKEN_PATH, count);
                        break;

                    default: // can't happen due to the check at the beginning of the command
                        return false;

                }
                sender.sendMessage(lang.PREFIX + lang.CMD_TOKEN.replace("%player%", player.getName()).replace("%token%", String.valueOf(plugin.getDataBase().getInt(player.getUniqueId(), DataBase.TOKEN_PATH, 0))));
                return true;
            }
        } // end of give/take/set token cmd
        else if(args[0].equalsIgnoreCase("token")){
            if(args.length != 2){
                sender.sendMessage(lang.PREFIX + " /gba [token] [player name]");
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if(player == null || !player.hasPlayedBefore()){
                sender.sendMessage(lang.PREFIX + ChatColor.RED + " can't find player " + args[1]);
                return true;
            }

            // handle cached players
            cachedPlayer:
            if(player.isOnline()){
                SGPlayer gbPlayer = plugin.getPluginManager().getPlayer(player.getUniqueId());
                if(gbPlayer == null){
                    break cachedPlayer;
                }
                sender.sendMessage(lang.PREFIX + lang.CMD_TOKEN.replace("%player%", player.getName()).replace("%token%", String.valueOf(gbPlayer.getTokens())));
                return true;
            }

            sender.sendMessage(lang.PREFIX + lang.CMD_TOKEN.replace("%player%", player.getName()).replace("%token%"
                    , String.valueOf(plugin.getDataBase().getInt(player.getUniqueId(), DataBase.TOKEN_PATH, 0))));
            return true;
        } else if(args[0].equalsIgnoreCase("reload")){
            if(plugin.reload()){
                sender.sendMessage(lang.PREFIX + lang.RELOAD_SUCCESS);
                return true;
            } else {
                sender.sendMessage(lang.PREFIX + lang.RELOAD_FAIL);
                Bukkit.getPluginManager().disablePlugin(plugin);
                return true;
            }
        } else if(args[0].equalsIgnoreCase("language")){
            // args length is min. 1
            if(args.length == 1){
                if(missingLanguageKeys.isEmpty()){
                    plugin.info(ChatColor.GREEN + " You have no missing messages in your language files :)");
                    return true;
                } else {
                    // ToDo: allow for cmd from op? (atm only printed to console)
                    printIncompleteLangFilesInfo();
                    return true;
                }
            } else if(args.length == 2){
                switch (args[1].toLowerCase()){
                    case "all":
                        printMissingKeys(sender);
                        return true;
                    default:
                        // ToDo print keys of only one specific file
                        sender.sendMessage(" Todo...");
                        return true;
                }
            }

            return true;
        } else if(args[0].equalsIgnoreCase("debug")){
            if(sender instanceof Player){
                return true;
            }
            SaltyGames.debug = !SaltyGames.debug;
            sender.sendMessage(lang.PREFIX + " Set debug mode to: " + SaltyGames.debug);
            return true;
        }
        sendHelpMessages(sender);
        return true;
    }

    public void printIncompleteLangFilesInfo() {
        // return if no keys are missing. This is only for calls from outside this class (e.g. from main class)
        if (missingLanguageKeys.isEmpty()) return;

        // print info about the incomplete files
        plugin.info(ChatColor.RED + "+ - + - + - + - + - + - + - + - + - + - + - + - + - + - +");
        plugin.info(ChatColor.BOLD + " There are missing keys in the following file(s):");
        plugin.info("");
        Iterator<String> iterator = missingLanguageKeys.keySet().iterator();
        String moduleID;
        while (iterator.hasNext()){
            moduleID = iterator.next();
            plugin.info(ChatColor.RED + "   -> " + ChatColor.RESET + plugin.getLanguage(moduleID).DEFAULT_PLAIN_NAME);
        }
        plugin.info("");
        plugin.info(" To get the specific missing keys of one file run ");
        plugin.info("      " + ChatColor.BLUE + "/gba language <>");
        plugin.info(" To get the specific missing keys of all files run ");
        plugin.info("      " + ChatColor.BLUE + "/gba language all");
        plugin.info(ChatColor.RED + "+ - + - + - + - + - + - + - + - + - + - + - + - + - + - +");
    }

    private void sendHelpMessages(CommandSender sender) {
        plugin.info(ChatColor.BOLD.toString() + ChatColor.GOLD + " Change the number of tokens for online/offline players");
        plugin.info(ChatColor.DARK_GREEN + " /gba [givetoken:taketoken:settoken] [player name] [count (integer)]");
        plugin.info(ChatColor.BOLD.toString() + ChatColor.GOLD + " Get the number of tokens an online/offline player has");
        plugin.info(ChatColor.DARK_GREEN + " /gba [token] [player name]");
        plugin.info(ChatColor.BOLD.toString() + ChatColor.GOLD + " Reload SaltyGames and all registered games");
        plugin.info(ChatColor.DARK_GREEN + " /gba reload");
        plugin.info(ChatColor.BOLD.toString() + ChatColor.GOLD + " Display information about your used language file");
        plugin.info(ChatColor.DARK_GREEN + " /gba language");
    }

    private void printMissingKeys(CommandSender sender){
        if(missingLanguageKeys.isEmpty()) return;

        HashMap<String, List<String>> currentKeys;
        List<String> keys;
        plugin.info(ChatColor.RED + "+ - + - + - + - + - + - + - + - + - + - + - + - + - + - +");
        Iterator<String> iterator = missingLanguageKeys.keySet().iterator();
        String moduleID;
        while(iterator.hasNext()){
            moduleID = iterator.next();
            currentKeys = missingLanguageKeys.get(moduleID);
            plugin.info(" Missing from " + ChatColor.BLUE + plugin.getLanguage(moduleID).DEFAULT_PLAIN_NAME
                    + ChatColor.RESET + " language file:");

            if(currentKeys.keySet().contains("string")){
                plugin.info(" ");
                plugin.info(ChatColor.BOLD + "   Strings:");
                keys = currentKeys.get("string");
                for(String key : keys){
                    plugin.info(ChatColor.RED + "   -> " + ChatColor.RESET + key);
                }
            }

            if(currentKeys.keySet().contains("list")){
                plugin.info(" ");
                plugin.info(ChatColor.BOLD + "   Lists:");
                keys = currentKeys.get("list");
                for(String key : keys){
                    plugin.info(ChatColor.RED + "   -> " + ChatColor.RESET + key);
                }
            }
            if(iterator.hasNext()) {
                plugin.info(" ");
                plugin.info(" ");
            } else {
                plugin.info(ChatColor.RED + "+ - + - + - + - + - + - + - + - + - + - + - + - + - + - +");
            }
        }
    }



    private void checkLanguageFiles() {
		/*
		ToDo: check for missing keys in all used language files and add some support for creating
		Files that contain all customized messages and the default ones for previously unset keys
		*/
        HashMap<String, List<String>> currentKeys;
        for(String moduleID : plugin.getGameReg().getModuleIDs()){
            currentKeys = collectMissingKeys(moduleID);
            if(!currentKeys.isEmpty()){
                missingLanguageKeys.put(moduleID, currentKeys);
            }
        }
    }

    private HashMap<String, List<String>> collectMissingKeys(String moduleID){
        Language language = plugin.getLanguage(moduleID);

        List<String> missingStringKeys = language.findMissingStringMessages();
        List<String> missingListKeys = language.findMissingListMessages();

        HashMap<String, List<String>> toReturn = new HashMap<>();

        if(!missingListKeys.isEmpty()){
            toReturn.put("list", missingListKeys);
        }

        if(!missingStringKeys.isEmpty()){
            toReturn.put("string", missingStringKeys);
        }

        return toReturn;
    }

    public HashMap<String, HashMap<String, List<String>>> getMissingLanguageKeys(){
        return this.missingLanguageKeys;
    }

    // Todo (method in language)
    private void createDiffFile(String moduleID){

    }

}
