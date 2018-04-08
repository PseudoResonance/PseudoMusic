package io.github.pseudoresonance.pseudomusic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
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
					PseudoMusic.message.sendPluginError(sender, Errors.CUSTOM, "There are no songs on the server!");
				}
			} else {
				PseudoMusic.message.sendPluginError(sender, Errors.NO_PERMISSION, "browse the music!");
				return false;
			}
		} else {
			PseudoMusic.message.sendPluginError(sender, Errors.CUSTOM, "This command is for players only!");
			return false;
		}
		return true;
	}

}
