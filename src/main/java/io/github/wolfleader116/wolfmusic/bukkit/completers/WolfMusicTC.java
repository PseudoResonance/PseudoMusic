package io.github.wolfleader116.wolfmusic.bukkit.completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class WolfMusicTC implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> possible = new ArrayList<String>();
		if (args.length == 1) {
			possible.add("help");
			if (sender.hasPermission("wolfmusic.reload")) {
				possible.add("reload");
			}
			if (sender.hasPermission("wolfmusic.reset")) {
				possible.add("reset");
			}
			if (sender.hasPermission("wolfmusic.browse")) {
				possible.add("browse");
			}
			if (sender.hasPermission("wolfmusic.play")) {
				possible.add("play");
			}
			if (sender.hasPermission("wolfmusic.stop")) {
				possible.add("stop");
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
