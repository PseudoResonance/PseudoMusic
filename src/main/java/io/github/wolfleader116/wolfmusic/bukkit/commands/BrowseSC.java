package io.github.wolfleader116.wolfmusic.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.wolfleader116.wolfapi.bukkit.Errors;
import io.github.wolfleader116.wolfapi.bukkit.SubCommandExecutor;
import io.github.wolfleader116.wolfmusic.bukkit.GUISetPage;
import io.github.wolfleader116.wolfmusic.bukkit.SongFile;
import io.github.wolfleader116.wolfmusic.bukkit.WolfMusic;

public class BrowseSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("wolfmusic.browse")) {
				SongFile[] songs = WolfMusic.getSongs();
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
					WolfMusic.message.sendPluginError(sender, Errors.CUSTOM, "There are no songs on the server!");
				}
			} else {
				WolfMusic.message.sendPluginError(sender, Errors.NO_PERMISSION, "browse the music!");
				return false;
			}
		} else {
			WolfMusic.message.sendPluginError(sender, Errors.CUSTOM, "This command is for players only!");
			return false;
		}
		return true;
	}

}
