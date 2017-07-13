package io.github.wolfleader116.wolfmusic.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.Errors;
import io.github.wolfleader116.wolfapi.bukkit.SubCommandExecutor;
import io.github.wolfleader116.wolfmusic.bukkit.JukeboxController;
import io.github.wolfleader116.wolfmusic.bukkit.WolfMusic;

public class ShuffleSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("wolfmusic.shuffle")) {
				Player p = (Player) sender;
				if (args.length == 0) {
					if (JukeboxController.isShuffling(p)) {
						JukeboxController.setShuffle(p, false);
						WolfMusic.message.sendPluginMessage(sender, "Disabled shuffle!");
					} else {
						JukeboxController.setShuffle(p, true);
						WolfMusic.message.sendPluginMessage(sender, "Enabled shuffle!");
					}
				} else {
					if (args[0].equalsIgnoreCase("true") && args[0].equalsIgnoreCase("on") && args[0].equalsIgnoreCase("t") && args[0].equalsIgnoreCase("shuffle")) {
						JukeboxController.setShuffle(p, true);
						WolfMusic.message.sendPluginMessage(sender, "Enabled shuffle!");
					} else if (args[0].equalsIgnoreCase("false") && args[0].equalsIgnoreCase("off") && args[0].equalsIgnoreCase("f") && args[0].equalsIgnoreCase("noshuffle")) {
						JukeboxController.setShuffle(p, false);
						WolfMusic.message.sendPluginMessage(sender, "Disabled shuffle!");
					} else {
						if (JukeboxController.isShuffling(p)) {
							JukeboxController.setShuffle(p, false);
							WolfMusic.message.sendPluginMessage(sender, "Disabled shuffle!");
						} else {
							JukeboxController.setShuffle(p, true);
							WolfMusic.message.sendPluginMessage(sender, "Enabled shuffle!");
						}
					}
				}
			} else {
				WolfMusic.message.sendPluginError(sender, Errors.NO_PERMISSION, "set shuffle!");
				return false;
			}
		} else {
			WolfMusic.message.sendPluginError(sender, Errors.CUSTOM, "This command is for players only!");
			return false;
		}
		return true;
	}

}