package com.andrewyunt.townygui.configuration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.andrewyunt.townygui.TownyGUI;

public class CommandConfiguration {
	
	private HashMap<String, FileConfiguration> langConfig = new HashMap<>();
	
	public void reloadConfig() {
		langConfig.clear();
		
		addLangIfExist("default");
	}
	
	public void addLangIfExist(String locale) {
		String fileName;
		if(locale.equalsIgnoreCase("default")) {
			fileName = "commands.yml";
		} else {
			fileName = "commands_"+locale+".yml";
		}
			
		File configFile = new File(TownyGUI.getInstance().getDataFolder(), fileName);
		if(configFile.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
			
			Reader defConfigStream = null;
			
			defConfigStream = new InputStreamReader(TownyGUI.getInstance().getResource(fileName), StandardCharsets.UTF_8);
			
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				config.setDefaults(defConfig);
			}
			langConfig.put(locale, config);
		}
	}
	
	public FileConfiguration getConfig() {
		return getConfig("default");
	}
	
	public FileConfiguration getConfig(String locale) {
		
		if (langConfig.isEmpty())
			reloadConfig();
		if(!langConfig.containsKey(locale))
			addLangIfExist(locale);
		
		return langConfig.getOrDefault(locale, langConfig.get("default"));
	}
	
	public void saveDefaultConfig() {
		
		File configFile = new File(TownyGUI.getInstance().getDataFolder(), "commands.yml");
		
		if (!configFile.exists()) {
			TownyGUI.getInstance().saveResource("commands.yml", false);
			TownyGUI.getInstance().saveResource("commands_fr_fr.yml", false);
		}	
	}
}