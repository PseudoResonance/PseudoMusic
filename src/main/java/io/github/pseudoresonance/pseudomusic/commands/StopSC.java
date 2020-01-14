package io.github.pseudoresonance.pseudomusic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.SongFile;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class StopSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("pseudomusic.stop")) {
				SongFile[] songs = PseudoMusic.getSongs();
				if (songs.length == 0) {
					PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudomusic.error_no_songs"));
				} else {
					Player p = (Player) sender;
					if (JukeboxController.isPlaying(p)) {
						JukeboxController.stopSong(p);
					} else {
						PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudomusic.error_nothing_playing"));
					}
				}
			} else {
				PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudomusic.permission_stop"));
				return false;
			}
		} else {
			PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudomusic.error_players_only"));
			return false;
		}
		return true;
	}

}
