package io.github.wolfleader116.wolfmusic.bukkit;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.wolfleader116.wolfapi.bukkit.Errors;
import io.github.wolfleader116.wolfapi.bukkit.Message;
import net.md_5.bungee.api.ChatColor;

public class GUISetPage {
	
	public static void setPage(Player p, int page) {
		SongFile[] songs = WolfMusic.getSongs();
		if (songs.length >= 1) {
			SongFile lastSong = JukeboxController.getLastSong(p);
			SongFile nextSong = JukeboxController.getNextSong(p);
			SongFile currentSong = JukeboxController.getSong(p);
			String currentSongName = "None";
			if (currentSong != null) {
				currentSongName = currentSong.getColor() + currentSong.getName();
			}
			Inventory inv = Bukkit.createInventory(null, 54, ConfigOptions.interfaceName);
			int total = (int) Math.ceil((double) songs.length / 45);
			if (page > total) {
				Message.sendConsoleMessage(ChatColor.RED + "Programming Error! That page number is too high!");
				return;
			} else if (page <= 0) {
				Message.sendConsoleMessage(ChatColor.RED + "Programming Error! Negative page number!");
				return;
			} else if (page == 1) {
				WolfMusic.setPage(p.getName(), 1);
				if (WolfMusic.getSongs().length > 1) {
					if (p.hasPermission("wolfmusic.play")) {
						if (lastSong != null) {
							inv.setItem(ConfigOptions.lastSongInt, newStack(ConfigOptions.lastSongMaterial, 1, "§1§f" + ConfigOptions.lastSongName.replace("{name}", lastSong.getColor() + lastSong.getName()), lastSong.getLength()));
						}
						if (nextSong != null) {
							inv.setItem(ConfigOptions.nextSongInt, newStack(ConfigOptions.nextSongMaterial, 1, "§2§f" + ConfigOptions.nextSongName.replace("{name}", nextSong.getColor() + nextSong.getName()), nextSong.getLength()));
						}
					}
				}
				if (JukeboxController.isPlaying(p)) {
					if (p.hasPermission("wolfmusic.stop")) {
						inv.setItem(ConfigOptions.stopInt, newStack(ConfigOptions.stopMaterial, 1, "§3§f" + ConfigOptions.stopName.replace("{name}", currentSongName)));
					}
				} else {
					if (p.hasPermission("wolfmusic.play")) {
						inv.setItem(ConfigOptions.stopInt, newStack(ConfigOptions.playMaterial, 1, "§4§f" + ConfigOptions.playName.replace("{name}", currentSongName)));
					}
				}
				if (p.hasPermission("wolfmusic.repeat")) {
					if (JukeboxController.isRepeating(p)) {
						inv.setItem(ConfigOptions.repeatInt, newStack(ConfigOptions.stopRepeatMaterial, 1, "§5§f" + ConfigOptions.stopRepeatName.replace("{name}", currentSongName)));
					} else {
						inv.setItem(ConfigOptions.repeatInt, newStack(ConfigOptions.repeatMaterial, 1, "§6§f" + ConfigOptions.repeatName.replace("{name}", currentSongName)));
					}
				}
				if (p.hasPermission("wolfmusic.shuffle")) {
					if (JukeboxController.isShuffling(p)) {
						inv.setItem(ConfigOptions.shuffleInt, newStack(ConfigOptions.stopShuffleMaterial, 1, "§7§f" + ConfigOptions.stopShuffleName.replace("{name}", currentSongName)));
					} else {
						inv.setItem(ConfigOptions.shuffleInt, newStack(ConfigOptions.shuffleMaterial, 1, "§8§f" + ConfigOptions.shuffleName.replace("{name}", currentSongName)));
					}
				}
				if (songs.length >= 45) {
					inv.setItem(ConfigOptions.nextPageInt, newStack(ConfigOptions.nextPageMaterial, 1, "§0§f" + ConfigOptions.nextPageName.replace("{page}", "2")));
				}
				for (int i = 0; i <= 44; i++) {
					if (i < songs.length) {
						SongFile sf = songs[i];
						inv.setItem(i + 9, newStack(sf.getDisk(), 1, sf.getColor() + sf.getName(), sf.getLength()));
					}
				}
			} else if (page == total) {
				WolfMusic.setPage(p.getName(), total);
				if (WolfMusic.getSongs().length > 1) {
					if (p.hasPermission("wolfmusic.play")) {
						if (lastSong != null) {
							inv.setItem(ConfigOptions.lastSongInt, newStack(ConfigOptions.lastSongMaterial, 1, "§1§f" + ConfigOptions.lastSongName.replace("{name}", lastSong.getColor() + lastSong.getName()), lastSong.getLength()));
						}
						if (nextSong != null) {
							inv.setItem(ConfigOptions.nextSongInt, newStack(ConfigOptions.nextSongMaterial, 1, "§2§f" + ConfigOptions.nextSongName.replace("{name}", nextSong.getColor() + nextSong.getName()), nextSong.getLength()));
						}
					}
				}
				if (JukeboxController.isPlaying(p)) {
					if (p.hasPermission("wolfmusic.stop")) {
						inv.setItem(ConfigOptions.stopInt, newStack(ConfigOptions.stopMaterial, 1, "§3§f" + ConfigOptions.stopName.replace("{name}", currentSongName)));
					}
				} else {
					if (p.hasPermission("wolfmusic.play")) {
						inv.setItem(ConfigOptions.stopInt, newStack(ConfigOptions.playMaterial, 1, "§4§f" + ConfigOptions.playName.replace("{name}", currentSongName)));
					}
				}
				if (p.hasPermission("wolfmusic.repeat")) {
					if (JukeboxController.isRepeating(p)) {
						inv.setItem(ConfigOptions.repeatInt, newStack(ConfigOptions.stopRepeatMaterial, 1, "§5§f" + ConfigOptions.stopRepeatName.replace("{name}", currentSongName)));
					} else {
						inv.setItem(ConfigOptions.repeatInt, newStack(ConfigOptions.repeatMaterial, 1, "§6§f" + ConfigOptions.repeatName.replace("{name}", currentSongName)));
					}
				}
				if (p.hasPermission("wolfmusic.shuffle")) {
					if (JukeboxController.isShuffling(p)) {
						inv.setItem(ConfigOptions.shuffleInt, newStack(ConfigOptions.stopShuffleMaterial, 1, "§7§f" + ConfigOptions.stopShuffleName.replace("{name}", currentSongName)));
					} else {
						inv.setItem(ConfigOptions.shuffleInt, newStack(ConfigOptions.shuffleMaterial, 1, "§8§f" + ConfigOptions.shuffleName.replace("{name}", currentSongName)));
					}
				}
				if (songs.length >= 45) {
					inv.setItem(ConfigOptions.lastPageInt, newStack(ConfigOptions.lastPageMaterial, 1, "§9§f" + ConfigOptions.lastPageName.replace("{page}", Integer.toString(page - 1))));
				}
				int loc = 8;
				for (int i = (page - 1) * 45; i <= ((page - 1) * 45) + 44; i++) {
					loc++;
					if (i < songs.length) {
						SongFile sf = songs[i];
						inv.setItem(loc, newStack(sf.getDisk(), 1, sf.getColor() + sf.getName(), sf.getLength()));
					}
				}
			} else {
				WolfMusic.setPage(p.getName(), page);
				if (WolfMusic.getSongs().length > 1) {
					if (p.hasPermission("wolfmusic.play")) {
						if (lastSong != null) {
							inv.setItem(ConfigOptions.lastSongInt, newStack(ConfigOptions.lastSongMaterial, 1, "§1§f" + ConfigOptions.lastSongName.replace("{name}", lastSong.getColor() + lastSong.getName()), lastSong.getLength()));
						}
						if (nextSong != null) {
							inv.setItem(ConfigOptions.nextSongInt, newStack(ConfigOptions.nextSongMaterial, 1, "§2§f" + ConfigOptions.nextSongName.replace("{name}", nextSong.getColor() + nextSong.getName()), nextSong.getLength()));
						}
					}
				}
				if (JukeboxController.isPlaying(p)) {
					if (p.hasPermission("wolfmusic.stop")) {
						inv.setItem(ConfigOptions.stopInt, newStack(ConfigOptions.stopMaterial, 1, "§3§f" + ConfigOptions.stopName.replace("{name}", currentSongName)));
					}
				} else {
					if (p.hasPermission("wolfmusic.play")) {
						inv.setItem(ConfigOptions.stopInt, newStack(ConfigOptions.playMaterial, 1, "§4§f" + ConfigOptions.playName.replace("{name}", currentSongName)));
					}
				}
				if (p.hasPermission("wolfmusic.repeat")) {
					if (JukeboxController.isRepeating(p)) {
						inv.setItem(ConfigOptions.repeatInt, newStack(ConfigOptions.stopRepeatMaterial, 1, "§5§f" + ConfigOptions.stopRepeatName.replace("{name}", currentSongName)));
					} else {
						inv.setItem(ConfigOptions.repeatInt, newStack(ConfigOptions.repeatMaterial, 1, "§6§f" + ConfigOptions.repeatName.replace("{name}", currentSongName)));
					}
				}
				if (p.hasPermission("wolfmusic.shuffle")) {
					if (JukeboxController.isShuffling(p)) {
						inv.setItem(ConfigOptions.shuffleInt, newStack(ConfigOptions.stopShuffleMaterial, 1, "§7§f" + ConfigOptions.stopShuffleName.replace("{name}", currentSongName)));
					} else {
						inv.setItem(ConfigOptions.shuffleInt, newStack(ConfigOptions.shuffleMaterial, 1, "§8§f" + ConfigOptions.shuffleName.replace("{name}", currentSongName)));
					}
				}
				if (songs.length >= 45) {
					inv.setItem(ConfigOptions.lastPageInt, newStack(ConfigOptions.lastPageMaterial, 1, "§9§f" + ConfigOptions.lastPageName.replace("{page}", Integer.toString(page - 1))));
					inv.setItem(ConfigOptions.nextPageInt, newStack(ConfigOptions.nextPageMaterial, 1, "§0§f" + ConfigOptions.nextPageName.replace("{page}", Integer.toString(page + 1))));
				}
				int loc = 8;
				for (int i = (page - 1) * 45; i <= ((page - 1) * 45) + 44; i++) {
					loc++;
					if (i < songs.length) {
						SongFile sf = songs[i];
						inv.setItem(loc, newStack(sf.getDisk(), 1, sf.getColor() + sf.getName(), sf.getLength()));
					}
				}
			}
			p.openInventory(inv);
		} else {
			WolfMusic.message.sendPluginError(p, Errors.CUSTOM, "There are no songs on the server!");
		}
	}
	
	protected static ItemStack newStack(Material material, int quantity, String name) {
		ItemStack is = new ItemStack(material, quantity);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.addItemFlags(ItemFlag.values());
		is.setItemMeta(im);
		return is;
	}
	
	protected static ItemStack newStack(Material material, int quantity, String name, String length) {
		ItemStack is = new ItemStack(material, quantity);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.addItemFlags(ItemFlag.values());
		im.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY + length)));
		is.setItemMeta(im);
		return is;
	}

}
