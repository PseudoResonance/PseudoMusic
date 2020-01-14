package io.github.pseudoresonance.pseudomusic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudomusic.GUISetPage;
import io.github.pseudoresonance.pseudomusic.SongFile;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class BrowseSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("pseudomusic.browse")) {
				SongFile[] songs = PseudoMusic.getSongs();
				if (songs.length >= 1) {
					if (args.length >= 1) {
						try {
							int page = Integer.valueOf(args[0]);
							GUISetPage.setPage((Player) sender, page);
						} catch (NumberFormatException e) {
							GUISetPage.setPage((Player) sender, 1);
						}
					} else {
						GUISetPage.setPage((Player) sender, 1);
					}
				} else {
					PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudomusic.error_no_songs"));
				}
			} else {
				PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudomusic.permission_browse"));
				return false;
			}
		} else {
			PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudomusic.error_players_only"));
			return false;
		}
		return true;
	}

}
