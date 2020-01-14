package io.github.pseudoresonance.pseudomusic.events;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudomusic.Config;
import io.github.pseudoresonance.pseudomusic.JukeboxController;
import io.github.pseudoresonance.pseudomusic.JukeboxController.PlayerType;
import io.github.pseudoresonance.pseudomusic.PseudoMusic;

public class PlayerJoinLeaveEH implements Listener {
	
	private static boolean firstInit = false;
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		if (firstInit) {
			Player p = e.getPlayer();
			JukeboxController.connect(p);
		}
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		if (firstInit) {
			HashMap<String, Object> settings = new HashMap<String, Object>();
			Player p = e.getPlayer();
			boolean playing = JukeboxController.isPlaying(p);
			if (playing && Config.playerType == PlayerType.PRIVATE) {
				String uuid = p.getUniqueId().toString();
				int id = JukeboxController.getSongID(p);
				if (id == -1)
					id = 0;
				short tick = JukeboxController.getSongPosition(p);
				settings.put("musicPosition", tick);
				settings.put("musicContinue", true);
				settings.put("musicSong", id);
				PlayerDataController.setPlayerSettings(uuid, settings);
				JukeboxController.disconnect(p);
				PseudoMusic.removePage(p.getName());
				if (Config.restartSongIfOfflineFor >= 0) {
					Bukkit.getScheduler().runTaskLaterAsynchronously(PseudoMusic.plugin, () -> {
						Object onlineO = PlayerDataController.getPlayerSetting(uuid, "online", true).join();
						if (onlineO instanceof Boolean) {
							boolean online = (Boolean) onlineO;
							if (!online) {
								PlayerDataController.setPlayerSetting(uuid, "musicContinue", false);
							}
						}
					}, Config.restartSongIfOfflineFor * 20);
				}
			}
		}
	}
	
	public static void initComplete() {
		firstInit = true;
	}
}
