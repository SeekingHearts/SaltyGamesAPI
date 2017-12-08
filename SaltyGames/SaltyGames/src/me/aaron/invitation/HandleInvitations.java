package me.aaron.invitation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.aaron.commands.MainCommand;
import me.aaron.gui.guis.game.StartMultiplayerGamePage;
import me.aaron.main.PluginManager;
import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesLang;
import me.aaron.main.SaltyGamesSettings;

public class HandleInvitations extends BukkitRunnable {
	
	private Set<Invitation> invitations = new HashSet<>();
    private PluginManager pluginManager;
    private SaltyGames plugin;

    private SaltyGamesLang lang;

    public HandleInvitations(SaltyGames plugin){
        pluginManager = plugin.getPluginManager();
        this.plugin = plugin;
        this.lang = plugin.lang;
        this.runTaskTimerAsynchronously(plugin, 20, 10);
    }

    @Override
    public void run() {
        Iterator<Invitation> it = invitations.iterator();
        ArrayList<UUID> ranOut = new ArrayList<>();
        while (it.hasNext()){
            Invitation inv = it.next();
            long currentTimeMillis = System.currentTimeMillis();
            if(currentTimeMillis > inv.timeStamp){
                ranOut.add(inv.player1);
                it.remove();
                ((StartMultiplayerGamePage)pluginManager.getGuiManager().getGameGui(inv.args[0], inv.args[1])).removeInvite(inv.player1, inv.player2);
                SaltyGames.debug("removing old invitation");
            }
        }
    }


    public boolean addInvite(UUID player1, UUID player2, long timeStamp, String... args){
        for(Invitation inv : invitations){
            if(inv.player1.equals(player1) && inv.player2.equals(player2)){
                Player player = Bukkit.getPlayer(player1);
                if(player != null) player.sendMessage(plugin.lang.INVITATION_ALREADY_THERE);
                return false;
            }
            if(inv.player1.equals(player2) && inv.player2.equals(player1)){
                //ToDO: accept invite automatically
                // for now just continue and allow it
            }
        }

        Player first = Bukkit.getPlayer(player1);
        Player second = Bukkit.getPlayer(player2);

        if(first != null && second != null){
            for(String message : plugin.lang.INVITE_MESSAGE) {
                second.sendMessage(plugin.lang.PREFIX + message.replace("%player%", first.getName()).replace("%game%", pluginManager.getGame(args[0]).getGameLang().PLAIN_NAME));
            }

            boolean boldClick = false;

            if(SaltyGamesSettings.sendInviteClickMessage) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw "
                        + second.getName()
                        + " [{\"text\":\"" + lang.JSON_PREFIX_PRE_TEXT + "\",\"color\":\"" + lang.JSON_PREFIX_PRE_COLOR + "\"},{\"text\":\"" + lang.JSON_PREFIX_TEXT + "\",\"color\":\""
                        + lang.JSON_PREFIX_COLOR + "\"},{\"text\":\"" + lang.JSON_PREFIX_AFTER_TEXT + "\",\"color\":\"" + lang.JSON_PREFIX_AFTER_COLOR + "\"}" +
                        ",{\"text\":\"" + lang.INVITATION_PRE_TEXT + "\",\"color\":\""
                        + lang.INVITATION_PRE_COLOR + "\"},{\"text\":\"" + lang.INVITATION_CLICK_TEXT + "\",\"color\":\""
                        + lang.INVITATION_CLICK_COLOR + "\",\"bold\":" + boldClick + ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/gb "
                        + MainCommand.inviteClickCommand + " " + args[0] + " " + args[1]
                        + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + lang.INVITATION_HOVER_TEXT + "\",\"color\":\""
                        + lang.INVITATION_HOVER_COLOR + "\"}}}, {\"text\":\"" + lang.INVITATION_AFTER_TEXT + "\",\"color\":\"" + lang.INVITATION_AFTER_COLOR + "\"}]");
            }
        } else {
            return false;
        }

        new Invitation(player1, player2, timeStamp, args);
        ((StartMultiplayerGamePage)pluginManager.getGuiManager().getGameGui(args[0], args[1])).addInvite(player1, player2);
        return true;
    }

    public class Invitation{
        protected long timeStamp;
        protected UUID player1, player2;
        protected String[] args;

        public Invitation(UUID player1, UUID player2, long timeStamp, String... args){
            this.player1 = player1;
            this.player2 = player2;
            this.timeStamp = timeStamp;
            this.args = args;

            SaltyGames.debug("adding new invitation");
            invitations.add(this);
        }
    }

}
