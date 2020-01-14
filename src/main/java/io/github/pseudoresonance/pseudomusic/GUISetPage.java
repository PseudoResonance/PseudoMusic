package io.github.pseudoresonance.pseudomusic;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import net.md_5.bungee.api.ChatColor;

public class GUISetPage {

	public static void setPage(Player p, int page) {
		setPage(p, page, null);
	}

	public static void setPage(Player p, int page, Inventory inv) {
		SongFile[] songs = PseudoMusic.getSongs();
		boolean newInv = false;
		if (songs.length >= 1) {
			SongFile lastSong = JukeboxController.getLastSong(p);
			SongFile nextSong = JukeboxController.getNextSong(p);
			SongFile currentSong = JukeboxController.getSong(p);
			String currentSongName = "";
			if (currentSong != null) {
				currentSongName = currentSong.getColor() + currentSong.getName();
			} else {
				currentSongName = LanguageManager.getLanguage(p).getMessage("pseudomusic.none");
			}
			if (inv == null) {
				newInv = true;
				inv = Bukkit.createInventory(new PseudoMusicHolder(), 54, LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_name"));
			} else {
				inv.clear();
			}
			int total = (songs.length - 1) / 45 + 1;
			if (page > total || page <= 0) {
				page = 1;
			}
			PseudoMusic.setPage(p.getName(), page);
			if (PseudoMusic.getSongs().length > 1) {
				if (p.hasPermission("pseudomusic.play")) {
					if (lastSong != null) {
						inv.setItem(Config.lastSongLocation, newStack(Config.lastSongMaterial, 1, "§1§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_last_song_name", lastSong.getColor() + lastSong.getName()), lastSong.getLength()));
					}
					if (nextSong != null) {
						inv.setItem(Config.nextSongLocation, newStack(Config.nextSongMaterial, 1, "§2§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_next_song_name", nextSong.getColor() + nextSong.getName()), nextSong.getLength()));
					}
				}
			}
			if (JukeboxController.isPlaying(p)) {
				if (p.hasPermission("pseudomusic.stop")) {
					inv.setItem(Config.stopLocation, newStack(Config.stopMaterial, 1, "§3§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_stop_name", currentSongName)));
				}
			} else {
				if (p.hasPermission("pseudomusic.play")) {
					inv.setItem(Config.stopLocation, newStack(Config.playMaterial, 1, "§4§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_play_name", currentSongName)));
				}
			}
			if (p.hasPermission("pseudomusic.repeat")) {
				if (JukeboxController.isRepeating(p)) {
					inv.setItem(Config.repeatLocation, newStack(Config.stopRepeatMaterial, 1, "§5§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_stop_repeat_name", currentSongName)));
				} else {
					inv.setItem(Config.repeatLocation, newStack(Config.repeatMaterial, 1, "§6§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_repeat_name", currentSongName)));
				}
			}
			if (p.hasPermission("pseudomusic.shuffle")) {
				if (JukeboxController.isShuffling(p)) {
					inv.setItem(Config.shuffleLocation, newStack(Config.stopShuffleMaterial, 1, "§7§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_stop_shuffle_name", currentSongName)));
				} else {
					inv.setItem(Config.shuffleLocation, newStack(Config.shuffleMaterial, 1, "§8§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_shuffle_name", currentSongName)));
				}
			}
			if (total > 1) {
				if (page < total)
					inv.setItem(Config.nextPageLocation, newStack(Config.nextPageMaterial, 1, "§0§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_next_page_name", page + 1)));
				if (page > 1)
					inv.setItem(Config.lastPageLocation, newStack(Config.lastPageMaterial, 1, "§9§f" + LanguageManager.getLanguage(p).getMessage("pseudomusic.interface_last_page_name", page - 1)));
			}
			int invIndex = 8;
			for (int i = (page - 1) * 45; i < page * 45; i++) {
				invIndex++;
				if (i < songs.length) {
					SongFile sf = songs[i];
					inv.setItem(invIndex, newStack(sf.getDisk(), 1, sf.getColor() + sf.getName(), sf.getLength()));
				} else
					break;
			}
			if (newInv)
				p.openInventory(inv);
			else
				p.updateInventory();
		} else {
			PseudoMusic.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudomusic.error_no_songs"));
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
	
	public static class PseudoMusicHolder implements InventoryHolder {  
	    @Override
	    public Inventory getInventory() {
	        return null;
	    }
	}

}
