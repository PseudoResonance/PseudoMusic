package io.github.wolfleader116.wolfmusic.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.Errors;
import io.github.wolfleader116.wolfapi.bukkit.SubCommandExecutor;
import io.github.wolfleader116.wolfmusic.bukkit.JukeboxController;
import io.github.wolfleader116.wolfmusic.bukkit.WolfMusic;

public class ReloadSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("wolfmusic.reload")) {
				try {
					WolfMusic.plugin.reloadConfig();
				} catch (Exception e) {
					WolfMusic.message.sendPluginError(sender, Errors.GENERIC);
					return false;
				}
				JukeboxController.kill();
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					JukeboxController.disconnect(p);
				}
				WolfMusic.getConfigOptions().reloadConfig();
				WolfMusic.updateSongs();
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					JukeboxController.connect(p);
				}
				JukeboxController.start();
				WolfMusic.message.sendPluginMessage(sender, "Plugin config reloaded!");
				return true;
			} else {
				WolfMusic.message.sendPluginError(sender, Errors.NO_PERMISSION, "reload the config!");
				return false;
			}
		} else {
			try {
				WolfMusic.plugin.reloadConfig();
			} catch (Exception e) {
				WolfMusic.message.sendPluginError(sender, Errors.GENERIC);
				return false;
			}
			JukeboxController.kill();
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				JukeboxController.disconnect(p);
			}
			WolfMusic.getConfigOptions().reloadConfig();
			WolfMusic.updateSongs();
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				JukeboxController.connect(p);
			}
			JukeboxController.start();
			WolfMusic.message.sendPluginMessage(sender, "Plugin config reloaded!");
			return true;
		}
	}

}
