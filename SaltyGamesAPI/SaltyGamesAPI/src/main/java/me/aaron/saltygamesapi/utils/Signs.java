package main.java.me.aaron.saltygamesapi.utils;

import java.util.Arrays;

public class Signs {
	
	private static final String squares;
	
	private static final char[] squares_mid = new char[10];
	private static final char[] squares_full = new char[10];
	private static final char[] squares_medium = new char[10];
	private static final char[] squares_light = new char[10];

	private static final String ssquares_mid;
	private static final String ssquares_full;
	private static final String ssquares_medium;
	private static final String ssquares_light;
	
	static {
		Arrays.fill(squares_mid, (char) 0x25A0);
		Arrays.fill(squares_full, (char) 0x2588);
		Arrays.fill(squares_medium, (char) 0x2592);
		Arrays.fill(squares_light, (char) 0x2591);
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < 11; i++) {
			sb.append((char) 0x25A0);
		}
		squares = sb.toString();
		ssquares_mid = new String(squares_mid);
		ssquares_full = new String(squares_full);
		ssquares_medium = new String(squares_medium);
		ssquares_light = new String(squares_light);
	}
	
	public static final String format(String src) {
		return replaceSquares(replaceColorCodes(src));
	}
	
	public static final String replaceColorCodes(String src) {
		return src.replaceAll("&", "§");
	}
	
	public static final String replaceSquares(String src) {
		return src.replace("[]", new String(ssquares_mid)).replace("[1]", new String(ssquares_full))
				.replace("[2]", new String(ssquares_medium)).replace("[3]", new String(ssquares_light));
	}
	
}
