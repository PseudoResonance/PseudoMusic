package io.github.pseudoresonance.pseudomusic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class ResetLocalizationSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudomusic.resetlocalization")) {
			try {
				LanguageManager.copyDefaultPluginLanguageFiles(PseudoMusic.plugin, true);
			} catch (Exception e) {
				PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.GENERIC);
				return false;
			}
			PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.localization_reset"));
			return true;
		} else {
			PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_reset_localization"));
			return false;
		}
	}

}
