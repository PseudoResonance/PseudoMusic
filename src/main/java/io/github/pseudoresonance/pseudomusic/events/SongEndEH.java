package io.github.pseudoresonance.pseudomusic.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;

import io.github.pseudoresonance.pseudomusic.ConfigOptions;
import io.github.pseudoresonance.pseudomusic.Jukebox;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.PlayerType;

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
