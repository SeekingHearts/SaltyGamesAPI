package me.aaron.main;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

public class Module {

	private String moduleID, classPath;
	
	private boolean isGame = false;
	
	private JavaPlugin externalPlugin;
	
	public Module(SaltyGames sg, String modID, String classPath, JavaPlugin pl) {
		Validate.isTrue(modID != null && !modID.isEmpty(), " moduleID cannot be null or empty!");
		if (classPath != null && !classPath.isEmpty()) {
			this.isGame = true;
		}
		this.classPath = classPath;
		this.moduleID = moduleID.toLowerCase();
		this.externalPlugin = pl;
		
		sg.getGameReg().regMod(this);
	}
	
	public Module(SaltyGames pl, String modID, String classPath) {
		this(pl, modID, classPath, null);
	}
	
	public Module(SaltyGames pl, String modID) {
		this(pl, modID, null, null);
	}
	
	public String getModuleID() {
		return moduleID;
	}
	
	public String getClassPath() {
		return classPath;
	}
	
	public boolean isGame() {
		return isGame;
	}
	
	public JavaPlugin getExternalPlugin() {
		return externalPlugin;
	}
	
	@Override
	public boolean equals(Object mod) {
		
		if (!(mod instanceof Module)) {
			return false;
		}
		return moduleID.equalsIgnoreCase(((Module) mod).moduleID);
	}
	
}
