package io.github.pseudoresonance.pseudomusic.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudomusic.Config;
import io.github.pseudoresonance.pseudomusic.GUISetPage;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.SongFile;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class InventoryClickEH implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		String t = e.getView().getTitle();
		if (t.equalsIgnoreCase(Config.interfaceName)) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			ItemStack is = e.getCurrentItem();
			if (is != null) {
				ItemMeta im = is.getItemMeta();
				if (im != null) {
					if (im.hasDisplayName()) {
						String name = im.getDisplayName();
						int page = PseudoMusic.getPage(p.getName());
						if (name.startsWith("§1§f")) {
							JukeboxController.lastSong(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§2§f")) {
							JukeboxController.nextSong(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§3§f")) {
							JukeboxController.stopSong(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§4§f")) {
							JukeboxController.startPlayer(p);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§5§f")) {
							JukeboxController.setRepeat(p, false);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§6§f")) {
							JukeboxController.setRepeat(p, true);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§7§f")) {
							JukeboxController.setShuffle(p, false);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§8§f")) {
							JukeboxController.setShuffle(p, true);
							GUISetPage.setPage(p, page, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§9§f")) {
							int o = page - 1;
							GUISetPage.setPage(p, o, e.getInventory());
							e.setCancelled(true);
						} else if (name.startsWith("§0§f")) {
							int o = page + 1;
							GUISetPage.setPage(p, o, e.getInventory());
							e.setCancelled(true);
						} else if (is.getType().isRecord()) {
							String songName = ChatColor.stripColor(name);
							for (SongFile sf : PseudoMusic.getSongs()) {
								if (sf.getName().equalsIgnoreCase(songName)) {
									if (p.hasPermission("pseudomusic.play")) {
										JukeboxController.setSong(p, sf);
										GUISetPage.setPage(p, page, e.getInventory());
									} else {
										PseudoMusic.message.sendPluginError(p, Errors.NO_PERMISSION, "select a song!");
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
