package me.aaron.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aaron.main.LobbyPlugin;

public class FriendListTest implements CommandExecutor {

	private LobbyPlugin pl;

	public FriendListTest(LobbyPlugin pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (args[0].equalsIgnoreCase("list")) {
				p.sendMessage("Friend List Command Run");
				return true;
			}
		}
		return true;
	}
}