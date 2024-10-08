package com.andrewyunt.townygui;

import com.andrewyunt.townygui.utilities.Utils;
import com.palmergames.bukkit.towny.TownyMessaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

public class IconMenu {
	
	private final FileConfiguration commandConfig, menuConfig;
	private final Set<String> icons;
	private final String title;
	private final int size;
	
	public IconMenu(Player player, String menu) {
		
		String locale = player.getLocale();
		
		commandConfig = TownyGUI.getInstance().commandConfig.getConfig(locale);
		menuConfig = TownyGUI.getInstance().menuConfig.getConfig(locale);
		
		icons = menuConfig.getConfigurationSection("menus." + menu + ".icons").getKeys(false); 
		
		title = ChatColor.translateAlternateColorCodes('&', menuConfig.getString("menus." + menu + ".title"));
		size = Utils.getInventorySize(menuConfig.getInt("menus." + menu + ".size"));
		
		openMenu(player, menu);
	}
	
	private void openMenu(Player player, String menu) {
		
		Inventory inv = Bukkit.createInventory(null, size, title);
		for (String icon : icons) {
			ItemStack is = null;
			String type, permission;
			FileConfiguration config;
			
			if (icon.startsWith("/")) {
				config = commandConfig;
				type = "commands";
			} else {
				config = menuConfig;
				type = "menus";
			}
			
			
			permission = config.getString(type + "." + icon + ".permission");
			
			if (!(permission == null))
				if(!player.hasPermission(permission))
					continue;
			
			try {
				is = new ItemStack(Material.matchMaterial(config.getString(type + "." + icon + ".material")), 1);
			} catch(NullPointerException e) {
				TownyMessaging.sendErrorMsg(player, "The material for the icon " + icon + " is invalid. OR there is an error in the configuration.");
				TownyMessaging.sendErrorMsg(player, e.getMessage());
				TownyMessaging.sendErrorMsg(player, "Please report this error to your server administrator.");
			}
			
			
			ItemMeta meta = is.getItemMeta();
			
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(type + "." + icon + ".title")));
			meta.setLore(Utils.hideStringInLore(Utils.colorizeStringList(config.getStringList(type + "." + icon + ".lore")), icon));
			is.setItemMeta(meta);
			
			if (config.getBoolean(type + "." + icon + ".enchant_glow"))
				Utils.addGlow(is);
			
			try {
				inv.setItem(menuConfig.getInt("menus." + menu + ".icons." + icon + ".slot") - 1, is);
			} catch(ArrayIndexOutOfBoundsException e) {
				TownyMessaging.sendErrorMsg(player, "Icon " + icon + " is configured in a slot outside of the menu. " + e.getMessage());
				TownyMessaging.sendErrorMsg(player, "Please report this error to your server administrator.");
			}
		}
	
		player.openInventory(inv);
	}
}