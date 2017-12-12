package me.aaron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ArenaState {

	JOIN("&a"),

	STARTING("&a"),

	INGAME("&4"),

	RESTARTING("&e");

	private String colorCode;

	private ArenaState(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getColorCode() {
		return this.colorCode;
	}

	public static Iterable<String> getAllStateNames() {
		final List<String> result = new ArrayList<>();
		for (final ArenaState state : ArenaState.values()) {
			result.add(state.name());
		}
		return result;
	}

	public static Map<String, String> getAllStateNameColors() {
		final Map<String, String> result = new HashMap<>();
		for (final ArenaState state : ArenaState.values()) {
			result.put(state.name(), state.getColorCode());
		}
		return result;
	}

}
