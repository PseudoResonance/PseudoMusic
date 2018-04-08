package io.github.pseudoresonance.pseudomusic.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class ResetSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("pseudomusic.reset")) {
				try {
					File conf = new File(PseudoMusic.plugin.getDataFolder(), "config.yml");
					conf.delete();
					PseudoMusic.plugin.saveDefaultConfig();
					PseudoMusic.plugin.reloadConfig();
				} catch (Exception e) {
					PseudoMusic.message.sendPluginError(sender, Errors.GENERIC);
					return false;
				}
				JukeboxController.kill();
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					JukeboxController.disconnect(p);
				}
				PseudoMusic.getConfigOptions().reloadConfig();
				PseudoMusic.updateSongs();
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					JukeboxController.connect(p);
				}
				JukeboxController.start();
				PseudoMusic.message.sendPluginMessage(sender, "Plugin config reset!");
				return true;
			} else {
				PseudoMusic.message.sendPluginError(sender, Errors.NO_PERMISSION, "reset the config!");
				return false;
			}
		} else {
			try {
				File conf = new File(PseudoMusic.plugin.getDataFolder(), "config.yml");
				conf.delete();
				PseudoMusic.plugin.saveDefaultConfig();
				PseudoMusic.plugin.reloadConfig();
			} catch (Exception e) {
				PseudoMusic.message.sendPluginError(sender, Errors.GENERIC);
				return false;
			}
			JukeboxController.kill();
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				JukeboxController.disconnect(p);
			}
			PseudoMusic.getConfigOptions().reloadConfig();
			PseudoMusic.updateSongs();
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				JukeboxController.connect(p);
			}
			JukeboxController.start();
			PseudoMusic.message.sendPluginMessage(sender, "Plugin config reset!");
			return true;
		}
	}

}
