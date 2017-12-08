package me.aaron.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import me.aaron.games.Game;
import me.aaron.utils.FileUtils;

public class GameReg {

	private SaltyGames sg;
	
	private HashMap<String, Module> mods = new HashMap<>();
	
	public GameReg(SaltyGames pl) {
		this.sg = pl;
	}
	
	private final Set<String> forbiddenModIDs = new HashSet<>(Arrays.asList("all, game, games"));
	
	public boolean regMod(Module mod) {
		if (isRegistered(mod.getModuleID())) {
			sg.getLogger().log(Level.WARNING, "Dieses Modul ist bereits registriert!");
			return false;
		}
		if (forbiddenModIDs.contains(mod.getModuleID())) {
			sg.getLogger().log(Level.WARNING, "Dieses Modul ist verboten. ID: '" + mod.getModuleID() + "'");
			return false;
		}
		
		mods.put(mod.getModuleID(), mod);
		
		if (mod.getExternalPlugin() != null) {
			if (!FileUtils.copyExternalResources(sg, mod)) {
				sg.info(" Fehler beim laden des externen Modules: '" + mod.getModuleID() + "'");
				return false;
			}
		}
		
		if (mod.isGame()) {
			loadGame(mod);
		}
		
		return true;
		
	}
	
	
	public boolean isRegistered(Module mod) {
		return isRegistered(mod.getModuleID());
	}
	
	public boolean isRegistered(String modID) {
		return mods.containsKey(modID.toLowerCase());
	}
	
	public Module getModule(String modID) {
		return mods.get(modID);
	}
	
	
	public void loadGames() {
		for (Module mod : mods.values()) {
			
		}
	}
	
	private void loadGame(Module mod) {
		Class clss = null;
		
		try {
			
			clss = Class.forName(mod.getClassPath());
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if (clss == null)
			return;
		
		try {
			
			Constructor<Game> ctor = ((Class<Game>) clss).getConstructor(SaltyGames.class);

			Game game = ctor.newInstance(sg);
			
			sg.getPluginManager().addGame(game);
			game.onEnable();
			
			
			
		} catch (NoSuchMethodException | IllegalAccessException 
				| InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
			sg.info(" Die Game Class braucht ein public constructor, welcher nur ein SaltyGames Objekt besitzt!");
		}
	}


	public Set<String> getModuleIDs() {
		return Collections.unmodifiableSet(mods.keySet());
	}
	
}
