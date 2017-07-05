package io.github.wolfleader116.wolfmusic.bukkit.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.xxmicloxx.NoteBlockAPI.SongEndEvent;

import io.github.wolfleader116.wolfmusic.bukkit.ConfigOptions;
import io.github.wolfleader116.wolfmusic.bukkit.Jukebox;
import io.github.wolfleader116.wolfmusic.bukkit.JukeboxController;
import io.github.wolfleader116.wolfmusic.bukkit.PlayerType;

public class SongEndEH implements Listener {
	
	@EventHandler
	public void songEnd(SongEndEvent e) {
		if (ConfigOptions.playerType == PlayerType.GLOBAL) {
			e.getSongPlayer().destroy();
			JukeboxController.getJukebox().done();
		} else {
			for (Jukebox j : JukeboxController.getJukeboxes()) {
				if (e.getSongPlayer() == j.getSongPlayer()) {
					e.getSongPlayer().destroy();
					j.done();
					break;
				}
			}
		}
	}
}
