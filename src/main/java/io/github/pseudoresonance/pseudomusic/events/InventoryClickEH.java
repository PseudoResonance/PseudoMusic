package io.github.pseudoresonance.pseudomusic.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudomusic.Config;
import io.github.pseudoresonance.pseudomusic.GUISetPage;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.SongFile;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class InventoryClickEH implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getView().getTopInventory().getHolder() instanceof GUISetPage.PseudoMusicHolder) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			ItemStack is = e.getCurrentItem();
			if (is != null) {
				ItemMeta im = is.getItemMeta();
				if (im != null) {
					if (im.hasDisplayName()) {
						String name = im.getDisplayName();
						int page = PseudoMusic.getPage(p.getName());
						int slot = e.getSlot();
						if (slot == Config.lastSongLocation) {
							JukeboxController.lastSong(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (slot == Config.nextSongLocation) {
							JukeboxController.nextSong(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (slot == Config.stopLocation) {
							if (JukeboxController.isPlaying(p)) {
								JukeboxController.stopSong(p);
								GUISetPage.setPage(p, page, e.getInventory());
								e.setCancelled(true);
							} else {
								JukeboxController.startPlayer(p);
								GUISetPage.setPage(p, page, e.getInventory());
								e.setCancelled(true);
							}
						} else if (slot == Config.repeatLocation) {
							JukeboxController.setRepeat(p, !JukeboxController.isRepeating(p));
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (slot == Config.shuffleLocation) {
							JukeboxController.setShuffle(p, !JukeboxController.isShuffling(p));
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (slot == Config.lastPageLocation) {
							GUISetPage.setPage(p, page - 1, e.getInventory());
							e.setCancelled(true);
						} else if (slot == Config.nextPageLocation) {
							GUISetPage.setPage(p, page + 1, e.getInventory());
							e.setCancelled(true);
						} else if (is.getType().isRecord()) {
							String songName = ChatColor.stripColor(name);
							for (SongFile sf : PseudoMusic.getSongs()) {
								if (sf.getName().equalsIgnoreCase(songName)) {
									if (p.hasPermission("pseudomusic.play")) {
										JukeboxController.setSong(p, sf);
										GUISetPage.setPage(p, page, e.getInventory());
									} else {
										PseudoMusic.plugin.getChat().sendPluginError(p, Errors.NO_PERMISSION, LanguageManager.getLanguage(p).getMessage("pseudomusic.permission_select_song"));
									}
									e.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}

}
