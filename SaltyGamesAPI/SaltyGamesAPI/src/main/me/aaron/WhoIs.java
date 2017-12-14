package main.me.aaron;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import main.me.aaron.utils.Validator;

public class WhoIs extends BukkitCommand {

	protected WhoIs(String name) {
		super(name);
		this.setPermission("saltygamesapi.wfi");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {

		if (!sender.hasPermission(this.getPermission())) {
			return true;
		}
		if (args.length != 1) {
			return true;
		}

		if (!Validator.isPlayerOnline(args[1])) {
			return true;
		}

		if (sender instanceof Player) {
			Player p = (Player) sender;
			Player target = Bukkit.getPlayer(args[1]);
			try {

//				WebServiceClient client = new WebServiceClient.Builder(42, "license_key").build();
//
//				InetSocketAddress playerIP = target.getAddress();
//				String sFullIP = playerIP.toString();
//				String[] fullIP;
//				String[] IPandPort;
//				fullIP = sFullIP.split("/");
//				String sIPandPort = fullIP[1];
//				IPandPort = sIPandPort.split(":");
//				String sIP = IPandPort[0];
//
//				InetAddress IP = InetAddress.getByName(sIP);
//
//				
//				
//				CityResponse resp = client.city(IP);
//
//				Country country = resp.getCountry();
//				City city = resp.getCity();
//				Postal postal = resp.getPostal();
//				com.maxmind.geoip2.record.Location geoLocation = resp.getLocation();

				p.sendMessage("Nick: " + target.getDisplayName());
				p.sendMessage("Health: " + target.getHealth());
				p.sendMessage("Hunger / Saturation: " + target.getFoodLevel() + "/" + target.getSaturation());
				p.sendMessage("Exp: " + target.getLevel());
				p.sendMessage("Location: " + target.getLocation().getWorld().getName() + " "
						+ target.getLocation().getBlockX() + " " + target.getLocation().getBlockY() + " "
						+ target.getLocation().getBlockZ());
				if (MinigamesAPI.econEnabled) {
					p.sendMessage("Money " + MinigamesAPI.econ.getBalance(target));
				}
				p.sendMessage("IPAddress: " + target.getAddress().getAddress().toString());
//				final String location = country.getIsoCode() + "/" + city.getName() + "/" + postal.getCode() + "//"
//						+ geoLocation.getLatitude() + ":" + geoLocation.getLongitude();
//				p.sendMessage("GeoLocation: " + location);

				p.sendMessage("Gamemode: " + target.getGameMode().toString());
				p.sendMessage("OP: " + target.isOp());

				return true;
			} catch (Exception e) {
				MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "MaxMind ist scheisse", e);
			}
		}
		return false;
	}
}
