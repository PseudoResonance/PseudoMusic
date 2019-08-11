package io.github.pseudoresonance.pseudomusic.completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class PseudoMusicTC implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> possible = new ArrayList<String>();
		if (args.length == 1) {
			possible.add("help");
			if (sender.hasPermission("pseudomusic.reload")) {
				possible.add("reload");
			}
			if (sender.hasPermission("pseudomusic.reset")) {
				possible.add("reset");
			}
			if (sender.hasPermission("pseudomusic.browse")) {
				possible.add("browse");
			}
			if (sender.hasPermission("pseudomusic.play")) {
				possible.add("play");
			}
			if (sender.hasPermission("pseudomusic.stop")) {
				possible.add("stop");
			}
			if (sender.hasPermission("pseudomusic.shuffle")) {
				possible.add("shuffle");
			}
			if (sender.hasPermission("pseudomusic.repeat")) {
				possible.add("repeat");
			}
			if (args[0].equalsIgnoreCase("")) {
				return possible;
			} else {
				List<String> checked = new ArrayList<String>();
				for (String check : possible) {
					if (check.startsWith(args[0])) {
						checked.add(check);
					}
				}
				return checked;
			}
		}
		return null;
	}

}
