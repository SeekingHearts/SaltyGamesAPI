package main.java.me.aaron.saltygamesapi.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PartyMessagesConfig {
	
	private FileConfiguration msgConfig = null;
	private File msgFile = null;
	private JavaPlugin pl = null;
	
	public PartyMessagesConfig(final JavaPlugin pl) {
		this.pl = pl;
		this.init();
	}
	
	public void init() {
		this.getConfig().addDefault("messages.cannot_invite_yourself", this.cannot_invite_yourself);
		this.getConfig().addDefault("messages.player_not_online", this.player_not_online);
		this.getConfig().addDefault("messages.you_invited", this.you_invited);
		this.getConfig().addDefault("messages.you_were_invited", this.you_were_invited);
		this.getConfig().addDefault("messages.not_invited_to_any_party", this.not_invited_to_any_party);
		this.getConfig().addDefault("messages.not_invited_to_players_party", this.not_invited_to_players_party);
		this.getConfig().addDefault("messages.player_not_in_party", this.player_not_in_party);
		this.getConfig().addDefault("messages.you_joined_party", this.you_joined_party);
		this.getConfig().addDefault("messages.player_joined_party", this.player_joined_party);
		this.getConfig().addDefault("messages.you_left_party", this.you_left_party);
		this.getConfig().addDefault("messages.player_left_party", this.player_left_party);
		this.getConfig().addDefault("messages.party_disbanded", this.party_disbanded);
		this.getConfig().addDefault("messages.party_too_big_to_join", this.party_too_big_to_join);
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		this.cannot_invite_yourself = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.cannot_invite_yourself"));
		this.player_not_online = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.player_not_online"));
		this.you_invited = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.you_invited"));
		this.you_were_invited = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.you_were_invited"));
		this.not_invited_to_any_party = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.not_invited_to_any_party"));
		this.not_invited_to_players_party = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.not_invited_to_players_party"));
		this.player_not_in_party = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.player_not_in_party"));
		this.you_joined_party = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.you_joined_party"));
		this.player_joined_party = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.player_joined_party"));
		this.you_left_party = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.you_left_party"));
		this.player_left_party = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.player_left_party"));
		this.party_disbanded = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.party_disbanded"));
		this.party_too_big_to_join = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.party_too_big_to_join"));
	}
	
	public String cannot_invite_yourself       = "&cDu kannst dich nicht selbst einladen!";
	public String player_not_online            = "&4<player> &cist nicht online!";
	public String you_invited                  = "&a Du hast &2<player> &aeingeladen!";
	public String you_were_invited             = "&2<player> &ahat dich in seine Party eingeladen! Benutze &2/party accept <player> &a um beizutreten.";
	public String not_invited_to_any_party     = "&cDu wurdest zu keiner Party eingeladen.";
	public String not_invited_to_players_party = "&cDu wurdest nicht zu der Party von &4<player> &ceingeladen.";
	public String player_not_in_party          = "&4<player> &cist nicht in deiner Party.";
	public String you_joined_party             = "&7 Du bist der Party von &8<player> &7beigetreten.";
	public String player_joined_party          = "&2<player> &aist der Party beigetreten.";
	public String you_left_party               = "&7Du hast die Party von &8<player> &7verlassen..";
	public String player_left_party            = "&4<player> &chat die Party verlassen.";
	public String party_disbanded              = "&c Die Party wurde aufgelöst.";
	public String party_too_big_to_join        = "&cDeine Party ist zu groß um dieser Arena beizutreten.";

	
	public FileConfiguration getConfig() {
		if (this.msgConfig == null) {
			this.reloadConfig();
		}
		return this.msgConfig;
	}

	public void reloadConfig() {
		if (this.msgFile == null) {
			this.msgFile = new File(this.pl.getDataFolder(), "partymessages.yml");
		}
		this.msgConfig = YamlConfiguration.loadConfiguration(this.msgFile);
		
		final InputStream is = this.pl.getResource("partymessages.yml");
		if (is != null) {
			final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(is);
			this.msgConfig.setDefaults(cfg);
		}
	}
	
	public void saveConfig() {
		if (this.msgConfig == null || this.msgFile == null) {
			return;
		}
		try {
			this.getConfig().save(this.msgFile);
		} catch (final IOException e) {
			;
		}
	}
	
}
