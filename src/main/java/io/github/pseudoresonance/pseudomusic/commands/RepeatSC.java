package io.github.pseudoresonance.pseudomusic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
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
						PseudoMusic.message.sendPluginMessage(sender, "Disabled repeat!");
					} else {
						JukeboxController.setRepeat(p, true);
						PseudoMusic.message.sendPluginMessage(sender, "Enabled repeat!");
					}
				} else {
					if (args[0].equalsIgnoreCase("true") && args[0].equalsIgnoreCase("on") && args[0].equalsIgnoreCase("t") && args[0].equalsIgnoreCase("repeat")) {
						JukeboxController.setRepeat(p, true);
						PseudoMusic.message.sendPluginMessage(sender, "Enabled repeat!");
					} else if (args[0].equalsIgnoreCase("false") && args[0].equalsIgnoreCase("off") && args[0].equalsIgnoreCase("f") && args[0].equalsIgnoreCase("norepeat")) {
						JukeboxController.setRepeat(p, false);
						PseudoMusic.message.sendPluginMessage(sender, "Disabled repeat!");
					} else {
						if (JukeboxController.isRepeating(p)) {
							JukeboxController.setRepeat(p, false);
							PseudoMusic.message.sendPluginMessage(sender, "Disabled repeat!");
						} else {
							JukeboxController.setRepeat(p, true);
							PseudoMusic.message.sendPluginMessage(sender, "Enabled repeat!");
						}
					}
				}
			} else {
				PseudoMusic.message.sendPluginError(sender, Errors.NO_PERMISSION, "set repeat!");
				return false;
			}
		} else {
			PseudoMusic.message.sendPluginError(sender, Errors.CUSTOM, "This command is for players only!");
			return false;
		}
		return true;
	}

}
