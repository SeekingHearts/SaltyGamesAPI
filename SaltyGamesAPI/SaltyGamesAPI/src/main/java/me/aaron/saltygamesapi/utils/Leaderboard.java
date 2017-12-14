package main.java.me.aaron.saltygamesapi.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;

public class Leaderboard {

	protected Object convertUUID(final String str) {
		if (str.matches("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
			return UUID.fromString(str);
		} else {
			return str;
		}
	}

	protected static Map<Integer, String> sortByComparator(final Map<String, Integer> unsortMap, final boolean order) {
		final List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

		Collections.sort(list, (o1, o2) -> {
			if (order) {
				return o1.getValue().compareTo(o2.getValue());
			} else {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		final Map<Integer, String> sortedMap = new LinkedHashMap<>();
		for (final Entry<String, Integer> entry : list) {
			for (int i = 1; i < 4; i++) {
				if (!sortedMap.containsKey(i)) {
					sortedMap.put(i, entry.getKey());
					break;
				}
			}
		}
		return sortedMap;
	}

	public Leaderboard(final Location firstPlace, final Location secondPlace, final Location thirdPlace,
			final Material firstBlockType, final Material secondBlockType, final Material thirdBlockType,
			final BlockFace direction, final FileConfiguration configurationFile, final String configurationFormat) {

		final String[] a = configurationFormat.split("\\.");

		int playerConfigLoc = -1;

		for (int i = 0; i < a.length; i++) {
			if (a[i].equals("player")) {
				playerConfigLoc = i;
			}
		}
		if (playerConfigLoc == -1)
			return;

		final ArrayList<String> b = new ArrayList<>();
		for (final String ba : b) {
			if (!ba.equals("player")) {
				b.add(ba);
			} else {
				break;
			}
		}

		final ArrayList<String> c = new ArrayList<>();
		for (final String ca : a) {
			if (!ca.equals("integer")) {
				c.add(ca);
			} else {
				break;
			}
		}

		final HashMap<String, Integer> playersEdit = new HashMap<>();
		final HashMap<String, Integer> playersKeep = new HashMap<>();

		for (final String p : configurationFile
				.getConfigurationSection(b.toString().replace("[", "").replace("]", "").replace(", ", "."))
				.getKeys(false)) {
			playersEdit.put(p, configurationFile
					.getInt(c.toString().replace("[", "").replace("]", "").replace(", ", ".").replace("player", p)));
			playersKeep.put(p, configurationFile
					.getInt(c.toString().replace("[", "").replace("]", "").replace(", ", ".").replace("player", p)));
		}

		final Map<Integer, String> leaderboard = Leaderboard.sortByComparator(playersEdit, false);

		firstPlace.getBlock().setType(firstBlockType);
		secondPlace.getBlock().setType(secondBlockType);
		thirdPlace.getBlock().setType(thirdBlockType);

		final ArrayList<BlockState> signBlockState = new ArrayList<>();
		final Block firstSign = firstPlace.getBlock().getRelative(direction);
		final Block secondSign = secondPlace.getBlock().getRelative(direction);
		final Block thirdSign = thirdPlace.getBlock().getRelative(direction);

		switch (direction) {

		case EAST:
			firstSign.setTypeIdAndData(68, (byte) 0x5, true);
			secondSign.setTypeIdAndData(68, (byte) 0x5, true);
			thirdSign.setTypeIdAndData(68, (byte) 0x5, true);
			break;
		case NORTH:
			firstSign.setTypeIdAndData(68, (byte) 0x2, true);
			secondSign.setTypeIdAndData(68, (byte) 0x2, true);
			thirdSign.setTypeIdAndData(68, (byte) 0x2, true);
			break;
		case SOUTH:
			firstSign.setTypeIdAndData(68, (byte) 0x3, true);
			secondSign.setTypeIdAndData(68, (byte) 0x3, true);
			thirdSign.setTypeIdAndData(68, (byte) 0x3, true);
			break;
		case WEST:
			firstSign.setTypeIdAndData(68, (byte) 0x4, true);
			secondSign.setTypeIdAndData(68, (byte) 0x4, true);
			thirdSign.setTypeIdAndData(68, (byte) 0x4, true);
			break;
		default:
			break;
		}
		signBlockState.add(firstSign.getState());
		signBlockState.add(secondSign.getState());
		signBlockState.add(thirdSign.getState());

		for (final BlockState bs : signBlockState) {
			if (bs instanceof Sign) {
				final Sign s = (Sign) bs;

				if (leaderboard.containsKey(signBlockState.indexOf(bs) + 1)) {
					if (this.convertUUID(leaderboard.get(signBlockState.indexOf(bs) + 1)) instanceof UUID) {
						s.setLine(1,
								Bukkit.getPlayer(
										(UUID) this.convertUUID(leaderboard.get(signBlockState.indexOf(bs) + 1)))
										.getName());
					} else {
						s.setLine(1, leaderboard.get(signBlockState.indexOf(bs) + 1));
					}

					switch (signBlockState.indexOf(bs) + 1) {
					case 1:
						s.setLine(0, ChatColor.AQUA + "" + ChatColor.BOLD + "1st");
						break;
					case 2:
						s.setLine(0, ChatColor.YELLOW + "" + ChatColor.BOLD + "2st");
						break;
					case 3:
						s.setLine(0, ChatColor.DARK_RED + "" + ChatColor.BOLD + "3st");
						break;
					}
				}
				s.setLine(2, playersKeep.get(leaderboard.get(signBlockState.indexOf(bs) + 1)) + " Points");
				bs.getBlock().getChunk().load();
				s.update(true);
			}
		}

		final Location firstSkull = firstPlace.getBlock().getLocation().add(0, 1, 0);
		final Location secondSkull = secondPlace.getBlock().getLocation().add(0, 1, 0);
		final Location thirdSkull = thirdPlace.getBlock().getLocation().add(0, 1, 0);

		firstSkull.getBlock().setType(Material.SKULL);
		secondSkull.getBlock().setType(Material.SKULL);
		thirdSkull.getBlock().setType(Material.SKULL);

		final ArrayList<BlockState> skullBlockState = new ArrayList<>();

		skullBlockState.add(firstSkull.getBlock().getState());
		skullBlockState.add(secondSkull.getBlock().getState());
		skullBlockState.add(thirdSkull.getBlock().getState());

		for (final BlockState bs : skullBlockState) {
			if (bs instanceof Skull) {
				final Skull s = (Skull) bs;
				s.setSkullType(SkullType.PLAYER);
				s.setRawData((byte) 1);
				s.setRotation(direction);

				if (leaderboard.containsKey(skullBlockState.indexOf(bs) + 1)) {
					if (this.convertUUID(leaderboard.get(skullBlockState.indexOf(bs) + 1)) instanceof UUID) {
						s.setOwner(Bukkit
								.getPlayer((UUID) this.convertUUID(leaderboard.get(skullBlockState.indexOf(bs) + 1)))
								.getName());
					} else {
						s.setOwner(leaderboard.get(skullBlockState.indexOf(bs) + 1));
					}
				}

				bs.getBlock().getChunk().load();
				s.update(true);
			}
		}
	}

}
