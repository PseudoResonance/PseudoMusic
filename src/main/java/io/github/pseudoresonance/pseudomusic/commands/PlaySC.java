package io.github.pseudoresonance.pseudomusic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.SongFile;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class PlaySC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("pseudomusic.play")) {
				SongFile[] songs = PseudoMusic.getSongs();
				if (songs.length == 0) {
					PseudoMusic.message.sendPluginError(sender, Errors.CUSTOM, "There are no songs on the server!");
				} else {
					Player p = (Player) sender;
					if (JukeboxController.isPlaying(p)) {
						PseudoMusic.message.sendPluginMessage(sender, "Music is already playing!");
					} else {
						JukeboxController.startPlayer(p);
					}
				}
			} else {
				PseudoMusic.message.sendPluginError(sender, Errors.NO_PERMISSION, "play music!");
				return false;
			}
		} else {
			PseudoMusic.message.sendPluginError(sender, Errors.CUSTOM, "This command is for players only!");
			return false;
		}
		return true;
	}

}
