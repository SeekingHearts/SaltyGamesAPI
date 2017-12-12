package me.aaron.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.main.Module;
import me.aaron.main.SaltyGames;

public class FileUtils {
	
	public static void copyDefaultLanguageFiles() {
		URL main = SaltyGames.class.getResource("SaltyGames.class");
		
		try {
			
			JarURLConnection con = (JarURLConnection) main.openConnection();
			
			JarFile jar = new JarFile(con.getJarFileURL().getFile());
			Plugin sg = Bukkit.getPluginManager().getPlugin("SaltyGames");
			for (Enumeration list = jar.entries(); list.hasMoreElements();) {
				JarEntry entry = (JarEntry) list.nextElement();
				if (entry.getName().split(File.separator)[0].equals("language")) {
					
					String[] pathParts = entry.getName().split(File.separator);
					
					if (pathParts.length < 2 || !entry.getName().endsWith(".yml")) {
						continue;
					}
					
					File file = new File(sg.getDataFolder().toString() + File.separatorChar + entry.getName());
					if (!file.exists()) {
						file.getParentFile().mkdirs();
						sg.saveResource(entry.getName(), false);
					}
				}
			}
			
			jar.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static boolean copyExternalResources(SaltyGames sg, Module module){
        JavaPlugin external = module.getExternalPlugin();
        if(external == null) return false;
        URL main = external.getClass().getResource(module.getClassPath()
                .split("\\.")[module.getClassPath().split("\\.").length - 1] + ".class");

        try {
            JarURLConnection connection = (JarURLConnection) main.openConnection();

            JarFile jar = new JarFile(connection.getJarFileURL().getFile());

            boolean foundDefaultLang = false;
            boolean foundConfig = false;

            for (Enumeration list = jar.entries(); list.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) list.nextElement();

                String[] pathParts = entry.getName().split(File.separator);
                String folderName = pathParts[0];

                if(folderName.equals("language")) {

                    if (pathParts.length < 3 || !entry.getName().endsWith(".yml")){
                        continue;
                    }

                    // subfolder in language folder
                    if(!pathParts[1].equalsIgnoreCase(module.getModuleID())) continue;

                    String fileName = pathParts[pathParts.length - 1];

                    if(fileName.equals("lang_en.yml")){
                        foundDefaultLang = true;
                    }

                    String gbPath = sg.getDataFolder().toString() + File.separatorChar
                            + "language" + File.separatorChar
                            + module.getModuleID() + File.separatorChar + fileName;
                    File file = new File(gbPath);

                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        saveResourceToGBFolder(entry.getName(), gbPath, external);
                    }

                } else if(folderName.equalsIgnoreCase("games")){
                    if(entry.isDirectory()) continue;

                    // only take resources from module folders
                    if(pathParts.length < 3){
                        continue;
                    }

                    // check module folder
                    if(!pathParts[1].equalsIgnoreCase(module.getModuleID())){
                        // resource of other module
                        continue;
                    }

                    StringBuilder builder = new StringBuilder();
                    for(int i = 2; i < pathParts.length; i++){
                        builder.append(pathParts[i]);
                        if(i+1 < pathParts.length){
                            builder.append(File.separatorChar);
                        }
                    }
                    String fileName = builder.toString();

                    if(fileName.equals("config.yml")){
                        foundConfig = true;
                    }

                    String gbPath = sg.getDataFolder().toString() + File.separatorChar
                            + "games" + File.separatorChar
                            + module.getModuleID() + File.separatorChar + fileName;
                    File file = new File(gbPath);

                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        saveResourceToGBFolder(entry.getName(), gbPath, external);
                    }
                }
            }
            jar.close();
            if(!foundDefaultLang){
                sg.info(" Failed to locate default language file for the module '" + module.getModuleID() +"'");
                sg.info(" " + jar.getName() + " is missing the file 'language/" + module.getModuleID() +"/lang_en.yml'");
            }
            if(!foundConfig){
                sg.info(" Failed to locate the configuration of the module '" + module.getModuleID() +"'");
                sg.info(" " + jar.getName() + " is missing the file 'games/" + module.getModuleID() +"/config.yml'");
            }
            if(!foundConfig || !foundDefaultLang){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    
    static private void saveResourceToGBFolder(String resourcePath, String gbPath, JavaPlugin plugin) {
        if(resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        Plugin sg = Bukkit.getPluginManager().getPlugin("sg");
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = plugin.getResource(resourcePath);
        if(in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + plugin.getName());
        }

        File outFile = new File(gbPath);
        int lastIndex = resourcePath.lastIndexOf(47);
        File outDir = new File(sg.getDataFolder(), resourcePath.substring(0, lastIndex >= 0?lastIndex:0));
        if(!outDir.exists()) {
            outDir.mkdirs();
        }

        if(outFile.exists()) return;

        try {
            OutputStream out = new FileOutputStream(outFile);
            byte[] buf = new byte[1024];

            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (IOException var10) {
            sg.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
        }
    }
}