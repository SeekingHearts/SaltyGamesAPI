package me.aaron.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class StringUtil {
	
	public static ArrayList<String> color(List<String> list) {
		ArrayList<String> toReturn = new ArrayList(list);
		for (int i = 0; i < list.size(); i++) {
			toReturn.set(i, color(toReturn.get(i)));
		}
		return toReturn;
	}
	
	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static String shorten(String str, int lenght) {
		if (str != null && str.length() > lenght) {
			return str.substring(0, lenght - 3) + "...";
		} else {
			return str;
		}
	}
	
	public static String formatTime(int secs) {
		int mins = secs / 60;
		int sec = secs % 60;
		return (mins < 10 ? "0" + String.valueOf(mins)
			: String.valueOf(mins)) + ":" + (sec < 10 ? "0"
					+ String.valueOf(sec) : String.valueOf(sec));
	}

}
