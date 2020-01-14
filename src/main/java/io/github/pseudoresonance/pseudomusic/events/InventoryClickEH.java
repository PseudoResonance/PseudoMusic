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
						switch (name.substring(0, 4)) {
						case "§1§f":
							JukeboxController.lastSong(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
							return;
						case "§2§f":
							JukeboxController.nextSong(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
							return;
						case "§3§f":
							JukeboxController.stopSong(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
							return;
						case "§4§f":
							JukeboxController.startPlayer(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
							return;
						case "§5§f":
							JukeboxController.setRepeat(p, false);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
							return;
						case "§6§f":
							JukeboxController.setRepeat(p, true);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
							return;
						case "§7§f":
							JukeboxController.setShuffle(p, false);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
							return;
						case "§8§f":
							JukeboxController.setShuffle(p, true);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
							return;
						case "§9§f":
							GUISetPage.setPage(p, page - 1, e.getInventory());
							e.setCancelled(true);
							return;
						case "§0§f":
							GUISetPage.setPage(p, page + 1, e.getInventory());
							e.setCancelled(true);
							return;
						}
						if (is.getType().isRecord()) {
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
