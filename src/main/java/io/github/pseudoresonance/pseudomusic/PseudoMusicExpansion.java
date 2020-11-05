package io.github.pseudoresonance.pseudomusic;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudomusic.JukeboxController.PlayerType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PseudoMusicExpansion extends PlaceholderExpansion {

	private Plugin plugin;

	public PseudoMusicExpansion(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return plugin.getName().toLowerCase();
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		switch (identifier) {
		case "now_playing":
			if (Config.playerType == PlayerType.PRIVATE) {
				if (player != null) {
					if (JukeboxController.isPlaying(player))
						return JukeboxController.getSong(player).getName();
					else
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_none");
				} else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_not_player");
			} else {
				if (JukeboxController.getJukebox().isPlaying())
					return JukeboxController.getJukebox().getSong().getName();
				else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_none");
			}
		case "next_song":
			if (Config.playerType == PlayerType.PRIVATE) {
				if (player != null) {
					if (JukeboxController.isPlaying(player))
						return JukeboxController.getNextSong(player).getName();
					else
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_none");
				} else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_not_player");
			} else {
				if (JukeboxController.getJukebox().isPlaying())
					return JukeboxController.getJukebox().getNextSong().getName();
				else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_none");
			}
		case "last_song":
			if (Config.playerType == PlayerType.PRIVATE) {
				if (player != null) {
					if (JukeboxController.isPlaying(player))
						return JukeboxController.getLastSong(player).getName();
					else
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_none");
				} else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_not_player");
			} else {
				if (JukeboxController.getJukebox().isPlaying())
					return JukeboxController.getJukebox().getLastSong().getName();
				else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_none");
			}
		case "playing":
			if (Config.playerType == PlayerType.PRIVATE) {
				if (player != null) {
					if (JukeboxController.isPlaying(player))
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_true");
					else
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_false");
				} else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_not_player");
			} else {
				if (JukeboxController.getJukebox().isPlaying())
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_true");
				else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_false");
			}
		case "repeating":
			if (Config.playerType == PlayerType.PRIVATE) {
				if (player != null) {
					if (JukeboxController.isRepeating(player))
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_true");
					else
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_false");
				} else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_not_player");
			} else {
				if (JukeboxController.getJukebox().isRepeating())
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_true");
				else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_false");
			}
		case "shuffling":
			if (Config.playerType == PlayerType.PRIVATE) {
				if (player != null) {
					if (JukeboxController.isShuffling(player))
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_true");
					else
						return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_false");
				} else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_not_player");
			} else {
				if (JukeboxController.getJukebox().isShuffling())
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_true");
				else
					return LanguageManager.getLanguage(player).getMessage("pseudomusic.placeholder_false");
			}
		default:
			return "";
		}
	}

}
