package io.github.pseudoresonance.pseudomusic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class ShuffleSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("pseudomusic.shuffle")) {
				Player p = (Player) sender;
				if (args.length == 0) {
					if (JukeboxController.isShuffling(p)) {
						JukeboxController.setShuffle(p, false);
						PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.shuffle_disabled"));
					} else {
						JukeboxController.setShuffle(p, true);
						PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.shuffle_enabled"));
					}
				} else {
					if (args[0].equalsIgnoreCase("true") && args[0].equalsIgnoreCase("on") && args[0].equalsIgnoreCase("t") && args[0].equalsIgnoreCase("shuffle")) {
						JukeboxController.setShuffle(p, true);
						PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.shuffle_enabled"));
					} else if (args[0].equalsIgnoreCase("false") && args[0].equalsIgnoreCase("off") && args[0].equalsIgnoreCase("f") && args[0].equalsIgnoreCase("noshuffle")) {
						JukeboxController.setShuffle(p, false);
						PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.shuffle_disabled"));
					} else {
						if (JukeboxController.isShuffling(p)) {
							JukeboxController.setShuffle(p, false);
							PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.shuffle_disabled"));
						} else {
							JukeboxController.setShuffle(p, true);
							PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.shuffle_enabled"));
						}
					}
				}
			} else {
				PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudomusic.permission_shuffle"));
				return false;
			}
		} else {
			PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudomusic.error_players_only"));
			return false;
		}
		return true;
	}

}
