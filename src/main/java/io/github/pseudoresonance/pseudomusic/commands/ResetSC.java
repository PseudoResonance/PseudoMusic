package io.github.pseudoresonance.pseudomusic.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class ResetSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudomusic.reset")) {
			PseudoMusic.plugin.doAsync(() -> {
				try {
					File conf = new File(PseudoMusic.plugin.getDataFolder(), "config.yml");
					conf.delete();
					PseudoMusic.plugin.saveDefaultConfig();
					PseudoMusic.plugin.reloadConfig();
				} catch (Exception e) {
					PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.GENERIC);
					return;
				}
				PseudoMusic.getConfigOptions().reloadConfig();
				PseudoMusic.updateSongs();
				PseudoMusic.plugin.doSync(() -> {
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						JukeboxController.disconnect(p);
					}
					JukeboxController.kill();
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						JukeboxController.connect(p);
					}
					JukeboxController.start();
					PseudoMusic.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.config_reset"));
				});
				return;
			});
			return true;
		} else {
			PseudoMusic.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_reset_config"));
			return false;
		}
	}

}
