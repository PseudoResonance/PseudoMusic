package io.github.pseudoresonance.pseudomusic.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class PlayerJoinLeaveEH implements Listener {
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		JukeboxController.connect(p);
		Object shuffle = PlayerDataController.getPlayerSetting(p.getUniqueId().toString(), "musicShuffle");
		if (shuffle instanceof Boolean) {
			boolean b = (Boolean) shuffle;
			if (b) {
				JukeboxController.setShuffle(p, true);
			}
		}
		Object repeat = PlayerDataController.getPlayerSetting(p.getUniqueId().toString(), "musicRepeat");
		if (repeat instanceof Boolean) {
			boolean b = (Boolean) repeat;
			if (b) {
				JukeboxController.setRepeat(p, true);
			}
		}
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		int id = JukeboxController.getSongID(p);
		if (id == -1)
			id = 0;
		PlayerDataController.setPlayerSetting(p.getUniqueId().toString(), "musicSong", id);
		JukeboxController.disconnect(p);
		PseudoMusic.removePage(p.getName());
	}
}
