package io.github.wolfleader116.wolfmusic.bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.wolfleader116.wolfapi.bukkit.ConfigOption;
import io.github.wolfleader116.wolfapi.bukkit.Message;
import net.md_5.bungee.api.ChatColor;

public class ConfigOptions implements ConfigOption {

	public static PlayerType playerType = PlayerType.PRIVATE;
	public static boolean autoStart = true;
	public static double autoStartDelay = 5.0;
	public static boolean playlist = true;
	public static double playlistDelay = 5.0;
	public static File songPath = new File(WolfMusic.plugin.getDataFolder() + "/songs");
	public static String interfaceName = "&c&lWolfMusic";
	public static Material lastPageMaterial = Material.PAPER;
	public static String lastPageName = "Page {page}";
	public static int lastPageInt = 0;
	public static Material nextPageMaterial = Material.PAPER;
	public static String nextPageName = "Page {page}";
	public static int nextPageInt = 8;
	public static Material lastSongMaterial = Material.FEATHER;
	public static String lastSongName = "Play {name}";
	public static int lastSongInt = 2;
	public static Material nextSongMaterial = Material.FEATHER;
	public static String nextSongName = "Play {name}";
	public static int nextSongInt = 6;
	public static Material stopMaterial = Material.BARRIER;
	public static String stopName = "Stop Playing";
	public static int stopInt = 4;
	public static boolean bossBar = true;
	public static long barVisibility = 0;
	public static String barMessage = "&c&lPlaying {cname} &3&l - &f{time} &c&l- &f{total}";
	public static long barUpdate = 10;
	public static boolean title = true;
	public static int titleVisibility = 100;
	public static int titleFade = 10;
	public static String titleMessage = "&c&lPlaying {cname}";
	
	public static boolean updateConfig() {
		boolean error = false;
		InputStream configin = WolfMusic.plugin.getClass().getResourceAsStream("/config.yml"); 
		BufferedReader configreader = new BufferedReader(new InputStreamReader(configin));
		YamlConfiguration configc = YamlConfiguration.loadConfiguration(configreader);
		int configcj = configc.getInt("Version");
		try {
			configreader.close();
			configin.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (WolfMusic.plugin.getConfig().getInt("Version") != configcj) {
			try {
				String oldFile = "";
				File conf = new File(WolfMusic.plugin.getDataFolder(), "config.yml");
				if (new File(WolfMusic.plugin.getDataFolder(), "config.yml.old").exists()) {
					for (int i = 1; i > 0; i++) {
						if (!(new File(WolfMusic.plugin.getDataFolder(), "config.yml.old" + i).exists())) {
							conf.renameTo(new File(WolfMusic.plugin.getDataFolder(), "config.yml.old" + i));
							oldFile = "config.yml.old" + i;
							break;
						}
					}
				} else {
					conf.renameTo(new File(WolfMusic.plugin.getDataFolder(), "config.yml.old"));
					oldFile = "config.yml.old";
				}
				WolfMusic.plugin.saveDefaultConfig();
				WolfMusic.plugin.reloadConfig();
				Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date! Old config file renamed to " + oldFile + ".");
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Error while updating config!");
				error = true;
			}
		}
		if (!error) {
			Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date!");
		} else {
			return false;
		}
		return true;
	}
	
	public void reloadConfig() {
		try {
			String s = WolfMusic.plugin.getConfig().getString("PlayerType");
			if (s.equalsIgnoreCase("global")) {
				playerType = PlayerType.GLOBAL;
			} else if (s.equalsIgnoreCase("private")) {
				playerType = PlayerType.PRIVATE;
			} else {
				playerType = PlayerType.PRIVATE;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for PlayerType!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for PlayerType!");
		}
		try {
			String s = WolfMusic.plugin.getConfig().getString("AutoStart");
			if (s.equalsIgnoreCase("true")) {
				autoStart = true;
			} else if (s.equalsIgnoreCase("false")) {
				autoStart = false;
			} else {
				autoStart = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for AutoStart!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for AutoStart!");
		}
		String as = WolfMusic.plugin.getConfig().getString("AutoStartDelay");
		if (isInteger(as)) {
			autoStartDelay = (double) Integer.valueOf(as);
		} else if (isDouble(as)) {
			autoStartDelay = Double.valueOf(as);
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for AutoStartDelay!");
		}
		try {
			String s = WolfMusic.plugin.getConfig().getString("Playlist");
			if (s.equalsIgnoreCase("true")) {
				playlist = true;
			} else if (s.equalsIgnoreCase("false")) {
				playlist = false;
			} else {
				playlist = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for Playlist!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for Playlist!");
		}
		String playlist = WolfMusic.plugin.getConfig().getString("PlaylistDelay");
		if (isInteger(playlist)) {
			playlistDelay = (double) Integer.valueOf(playlist);
		} else if (isDouble(playlist)) {
			playlistDelay = Double.valueOf(playlist);
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for PlaylistDelay!");
		}
		songPath = new File(WolfMusic.plugin.getDataFolder() + "/" + WolfMusic.plugin.getConfig().getString("SongPath"));
		interfaceName = ChatColor.translateAlternateColorCodes('&', WolfMusic.plugin.getConfig().getString("InterfaceName"));
		try {
			lastPageMaterial = Enum.valueOf(Material.class, WolfMusic.plugin.getConfig().getString("LastPageItem").toUpperCase());
		} catch (IllegalArgumentException e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid item in config for LastPage!");
		}
		lastPageName = ChatColor.translateAlternateColorCodes('&', WolfMusic.plugin.getConfig().getString("LastPageName"));
		int lastPageLocation = WolfMusic.plugin.getConfig().getInt("LastPageLocation");
		if (lastPageLocation >= 1 && lastPageLocation <= 9) {
			lastPageInt = lastPageLocation - 1;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid location in config for LastPage!");
		}
		try {
			nextPageMaterial = Enum.valueOf(Material.class, WolfMusic.plugin.getConfig().getString("NextPageItem").toUpperCase());
		} catch (IllegalArgumentException e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid item in config for NextPage!");
		}
		nextPageName = ChatColor.translateAlternateColorCodes('&', WolfMusic.plugin.getConfig().getString("NextPageName"));
		int nextPageLocation = WolfMusic.plugin.getConfig().getInt("NextPageLocation");
		if (nextPageLocation >= 1 && nextPageLocation <= 9 && nextPageLocation != lastPageLocation) {
			nextPageInt = nextPageLocation - 1;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid location in config for NextPage!");
		}
		try {
			lastSongMaterial = Enum.valueOf(Material.class, WolfMusic.plugin.getConfig().getString("LastSongItem").toUpperCase());
		} catch (IllegalArgumentException e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid item in config for LastSong!");
		}
		lastSongName = ChatColor.translateAlternateColorCodes('&', WolfMusic.plugin.getConfig().getString("LastSongName"));
		int lastSongLocation = WolfMusic.plugin.getConfig().getInt("LastSongLocation");
		if (lastSongLocation >= 1 && lastSongLocation <= 9 && lastSongLocation != lastPageLocation && lastSongLocation != nextPageLocation) {
			lastSongInt = lastSongLocation - 1;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid location in config for LastSong!");
		}
		try {
			nextSongMaterial = Enum.valueOf(Material.class, WolfMusic.plugin.getConfig().getString("NextSongItem").toUpperCase());
		} catch (IllegalArgumentException e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid item in config for NextSong!");
		}
		nextSongName = ChatColor.translateAlternateColorCodes('&', WolfMusic.plugin.getConfig().getString("NextSongName"));
		int nextSongLocation = WolfMusic.plugin.getConfig().getInt("NextSongLocation");
		if (nextSongLocation >= 1 && nextSongLocation <= 9 && nextSongLocation != lastPageLocation && nextSongLocation != nextPageLocation && nextSongLocation != lastSongLocation) {
			nextSongInt = nextSongLocation - 1;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid location in config for NextSong!");
		}
		try {
			stopMaterial = Enum.valueOf(Material.class, WolfMusic.plugin.getConfig().getString("StopItem").toUpperCase());
		} catch (IllegalArgumentException e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid item in config for Stop!");
		}
		stopName = ChatColor.translateAlternateColorCodes('&', WolfMusic.plugin.getConfig().getString("StopName"));
		int stopLocation = WolfMusic.plugin.getConfig().getInt("StopLocation");
		if (stopLocation >= 1 && stopLocation <= 9 && stopLocation != lastPageLocation && stopLocation != nextPageLocation && stopLocation != lastSongLocation && stopLocation != nextSongLocation) {
			stopInt = stopLocation - 1;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid location in config for Stop!");
		}
		try {
			String s = WolfMusic.plugin.getConfig().getString("BossBar");
			if (s.equalsIgnoreCase("true")) {
				bossBar = true;
			} else if (s.equalsIgnoreCase("false")) {
				bossBar = false;
			} else {
				bossBar = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for BossBar!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for BossBar!");
		}
		int barTime = WolfMusic.plugin.getConfig().getInt("BarVisibility");
		if (barTime >= 0) {
			barVisibility = barTime * 20;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for BarVisibility!");
		}
		barMessage = ChatColor.translateAlternateColorCodes('&', WolfMusic.plugin.getConfig().getString("BarMessage"));
		int barUpdate = WolfMusic.plugin.getConfig().getInt("BarUpdate");
		if (barUpdate > 0) {
			ConfigOptions.barUpdate = barUpdate;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for BarUpdate!");
		}
		try {
			String s = WolfMusic.plugin.getConfig().getString("Title");
			if (s.equalsIgnoreCase("true")) {
				title = true;
			} else if (s.equalsIgnoreCase("false")) {
				title = false;
			} else {
				title = false;
				Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for Title!");
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for Title!");
		}
		int titleTime = WolfMusic.plugin.getConfig().getInt("TitleVisibility");
		if (titleTime > 0) {
			titleVisibility = titleTime * 20;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for TitleVisibility!");
		}
		int titleFade = WolfMusic.plugin.getConfig().getInt("TitleFade");
		if (titleFade >= 0) {
			ConfigOptions.titleFade = titleFade;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for TitleFade!");
		}
		titleMessage = ChatColor.translateAlternateColorCodes('&', WolfMusic.plugin.getConfig().getString("TitleMessage"));
	}
	
	private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	
	private static boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}

}