package io.github.pseudoresonance.pseudomusic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class RepeatSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("pseudomusic.repeat")) {
				Player p = (Player) sender;
				if (args.length == 0) {
					if (JukeboxController.isRepeating(p)) {
						JukeboxController.setRepeat(p, false);
						PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.repeat_disabled"));
					} else {
						JukeboxController.setRepeat(p, true);
						PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.repeat_enabled"));
					}
				} else {
					if (args[0].equalsIgnoreCase("true") && args[0].equalsIgnoreCase("on") && args[0].equalsIgnoreCase("t") && args[0].equalsIgnoreCase("repeat")) {
						JukeboxController.setRepeat(p, true);
						PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.repeat_enabled"));
					} else if (args[0].equalsIgnoreCase("false") && args[0].equalsIgnoreCase("off") && args[0].equalsIgnoreCase("f") && args[0].equalsIgnoreCase("norepeat")) {
						JukeboxController.setRepeat(p, false);
						PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.repeat_disabled"));
					} else {
						if (JukeboxController.isRepeating(p)) {
							JukeboxController.setRepeat(p, false);
							PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.repeat_disabled"));
						} else {
							JukeboxController.setRepeat(p, true);
							PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudomusic.repeat_enabled"));
						}
					}
				}
			} else {
				PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudomusic.permission_repeat"));
				return false;
			}
		} else {
			PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudomusic.error_players_only"));
			return false;
		}
		return true;
	}

}
