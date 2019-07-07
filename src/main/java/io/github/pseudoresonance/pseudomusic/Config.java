package io.github.pseudoresonance.pseudomusic;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.data.PluginConfig;
import net.md_5.bungee.api.ChatColor;

public class Config extends PluginConfig {

	public static PlayerType playerType = PlayerType.PRIVATE;
	public static boolean playlist = true;
	public static double playlistDelay = 5.0;
	
	public static File songPath = new File(PseudoMusic.plugin.getDataFolder() + "/songs");
	public static String interfaceName = "&c&lPseudoMusic";
	public static String lastPageName = "Page {page}";
	public static Material lastPageMaterial = Material.PAPER;
	public static int lastPageLocation = 0;
	public static String nextPageName = "Page {page}";
	public static Material nextPageMaterial = Material.PAPER;
	public static int nextPageLocation = 8;
	public static String lastSongName = "Play {name}";
	public static Material lastSongMaterial = Material.FEATHER;
	public static int lastSongLocation = 2;
	public static String nextSongName = "Play {name}";
	public static Material nextSongMaterial = Material.FEATHER;
	public static int nextSongLocation = 6;
	public static String stopName = "Stop Playing";
	public static Material stopMaterial = Material.BARRIER;
	public static int stopLocation = 4;
	public static String playName = "Play {name}";
	public static Material playMaterial = Material.JUKEBOX;
	public static String repeatName = "Repeat";
	public static Material repeatMaterial = Material.ARROW;
	public static int repeatLocation = 1;
	public static String stopRepeatName = "Disable Repeat";
	public static Material stopRepeatMaterial = Material.STRUCTURE_VOID;
	public static String shuffleName = "Shuffle";
	public static Material shuffleMaterial = Material.MAGENTA_GLAZED_TERRACOTTA;
	public static int shuffleLocation = 7;
	public static String stopShuffleName = "Disable Shuffle";
	public static Material stopShuffleMaterial = Material.STRUCTURE_VOID;
	
	public static boolean bossBar = true;
	public static int barVisibility = 0;
	public static String barMessage = "&c&lPlaying {cname} &3&l - &f{time} &c&l- &f{total}";
	public static int barUpdate = 10;
	
	public static boolean title = true;
	public static int titleVisibility = 5;
	public static int titleFade = 10;
	public static String titleMessage = "&c&lPlaying {cname}";
	
	public void reloadConfig() {
		FileConfiguration fc = PseudoMusic.plugin.getConfig();
		boolean locationError = false;
		String playerType = PluginConfig.getString(fc, "PlayerType", Config.playerType.toString());
		if (playerType.equalsIgnoreCase("private"))
			Config.playerType = PlayerType.PRIVATE;
		else if (playerType.equalsIgnoreCase("global"))
			Config.playerType = PlayerType.GLOBAL;
		else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for PlayerType!");
		}
		playlist = PluginConfig.getBoolean(fc, "Playlist", playlist);
		playlistDelay = PluginConfig.getDouble(fc, "PlaylistDelay", playlistDelay);

		songPath = new File(PseudoMusic.plugin.getDataFolder() + "/" + PluginConfig.getString(fc, "SongPath", songPath.getAbsolutePath()));
		interfaceName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "InterfaceName", interfaceName));
		lastPageName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "LastPageName", lastPageName));
		lastPageMaterial = PluginConfig.getMaterial(fc, "LastPageMaterial", lastPageMaterial);
		int lastPageLocation = PluginConfig.getInt(fc, "LastPageLocation", Config.lastPageLocation);
		if (lastPageLocation >= 0 && lastPageLocation <= 8) {
			Config.lastPageLocation = lastPageLocation;
		} else {
			locationError = true;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for LastPageLocation!");
		}
		nextPageName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "NextPageName", nextPageName));
		nextPageMaterial = PluginConfig.getMaterial(fc, "NextPageMaterial", nextPageMaterial);
		int nextPageLocation = PluginConfig.getInt(fc, "NextPageLocation", Config.nextPageLocation);
		if (nextPageLocation >= 0 && nextPageLocation <= 8 && nextPageLocation != lastPageLocation) {
			Config.nextPageLocation = nextPageLocation;
		} else {
			locationError = true;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for NextPageLocation!");
		}
		lastSongName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "LastSongName", lastSongName));
		lastSongMaterial = PluginConfig.getMaterial(fc, "LastSongMaterial", lastSongMaterial);
		int lastSongLocation = PluginConfig.getInt(fc, "LastSongLocation", Config.lastSongLocation);
		if (lastSongLocation >= 0 && lastSongLocation <= 8 && lastSongLocation != lastPageLocation && lastSongLocation != nextPageLocation) {
			Config.lastSongLocation = lastSongLocation;
		} else {
			locationError = true;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for LastSongLocation!");
		}
		nextSongName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "NextSongName", nextSongName));
		nextSongMaterial = PluginConfig.getMaterial(fc, "NextSongMaterial", nextSongMaterial);
		int nextSongLocation = PluginConfig.getInt(fc, "NextSongLocation", Config.nextSongLocation);
		if (nextSongLocation >= 0 && nextSongLocation <= 8 && nextSongLocation != lastPageLocation && nextSongLocation != nextPageLocation && nextSongLocation != lastSongLocation) {
			Config.nextSongLocation = nextSongLocation;
		} else {
			locationError = true;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for NextSongLocation!");
		}
		stopName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "StopName", stopName));
		stopMaterial = PluginConfig.getMaterial(fc, "StopMaterial", stopMaterial);
		int stopLocation = PluginConfig.getInt(fc, "StopLocation", Config.stopLocation);
		if (stopLocation >= 0 && stopLocation <= 8 && stopLocation != lastPageLocation && stopLocation != nextPageLocation && stopLocation != lastSongLocation && stopLocation != nextSongLocation) {
			Config.stopLocation = stopLocation;
		} else {
			locationError = true;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for StopLocation!");
		}
		playName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "PlayName", playName));
		playMaterial = PluginConfig.getMaterial(fc, "PlayMaterial", playMaterial);
		repeatName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "RepeatName", repeatName));
		repeatMaterial = PluginConfig.getMaterial(fc, "RepeatMaterial", repeatMaterial);
		int repeatLocation = PluginConfig.getInt(fc, "RepeatLocation", Config.repeatLocation);
		if (repeatLocation >= 0 && repeatLocation <= 8 && repeatLocation != lastPageLocation && repeatLocation != nextPageLocation && repeatLocation != lastSongLocation && repeatLocation != nextSongLocation && repeatLocation != stopLocation) {
			Config.repeatLocation = repeatLocation;
		} else {
			locationError = true;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for RepeatLocation!");
		}
		stopRepeatName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "StopRepeatName", stopRepeatName));
		stopShuffleMaterial = PluginConfig.getMaterial(fc, "StopShuffleMaterial", stopShuffleMaterial);
		shuffleName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "ShuffleName", shuffleName));
		shuffleMaterial = PluginConfig.getMaterial(fc, "ShuffleMaterial", shuffleMaterial);
		int shuffleLocation = PluginConfig.getInt(fc, "ShuffleLocation", Config.shuffleLocation);
		if (shuffleLocation >= 0 && shuffleLocation <= 8 && shuffleLocation != lastPageLocation && shuffleLocation != nextPageLocation && shuffleLocation != lastSongLocation && shuffleLocation != nextSongLocation && shuffleLocation != stopLocation && shuffleLocation != repeatLocation) {
			Config.shuffleLocation = shuffleLocation;
		} else {
			locationError = true;
			Message.sendConsoleMessage(ChatColor.RED + "Invalid config option for ShuffleLocation!");
		}
		stopShuffleName = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "StopShuffleName", stopShuffleName));
		stopShuffleMaterial = PluginConfig.getMaterial(fc, "StopShuffleMaterial", stopShuffleMaterial);
		if (locationError) {
			lastPageLocation = 0;
			nextPageLocation = 8;
			lastSongLocation = 2;
			nextSongLocation = 6;
			stopLocation = 4;
			repeatLocation = 1;
			shuffleLocation = 7;
			Message.sendConsoleMessage(ChatColor.RED + "Default item locations have been used!");
		}

		bossBar = PluginConfig.getBoolean(fc, "BossBar", bossBar);
		barVisibility = PluginConfig.getInt(fc, "BarVisibility", barVisibility);
		barMessage = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "BarMessage", barMessage));
		barUpdate = PluginConfig.getInt(fc, "BarUpdate", barUpdate);

		title = PluginConfig.getBoolean(fc, "Title", title);
		titleVisibility = PluginConfig.getInt(fc, "TitleVisibility", titleVisibility);
		titleFade = PluginConfig.getInt(fc, "TitleFade", titleFade);
		titleMessage = ChatColor.translateAlternateColorCodes('&', PluginConfig.getString(fc, "TitleMessage", titleMessage));
	}
	
	public Config(PseudoPlugin plugin) {
		super(plugin);
	}

}