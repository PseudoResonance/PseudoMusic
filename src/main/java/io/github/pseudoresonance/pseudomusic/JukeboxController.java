package io.github.pseudoresonance.pseudomusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;

public class JukeboxController {
	
	private static Map<String, Jukebox> jukeboxes = new HashMap<String, Jukebox>();
	private static GlobalJukebox global = new GlobalJukebox();
	
	public static void connect(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			Jukebox j = new Jukebox(p);
			Object shuffle = PlayerDataController.getPlayerSetting(p.getUniqueId().toString(), "musicShuffle", true).join();
			if (shuffle instanceof Boolean) {
				boolean b = (Boolean) shuffle;
				if (b) {
					j.setShuffle(true);
				}
			}
			Object repeat = PlayerDataController.getPlayerSetting(p.getUniqueId().toString(), "musicRepeat", true).join();
			if (repeat instanceof Boolean) {
				boolean b = (Boolean) repeat;
				if (b) {
					j.setRepeat(true);
				}
			}
			jukeboxes.put(p.getName(), j);
		} else {
			global.addPlayer(p);
		}
	}
	
	public static void disconnect(Player p) {
		Jukebox jb = jukeboxes.remove(p.getName());
		if (jb != null)
			jb.kill(true);
		global.removePlayer(p);
	}
	
	public static void kill() {
		for (Jukebox j : jukeboxes.values()) {
			j.kill(true);
		}
		global.kill();
	}
	
	public static void start() {
		if (Config.playerType == PlayerType.PRIVATE) {
			for (Jukebox j : jukeboxes.values()) {
				j.start();
			}
		} else {
			global.start();
		}
	}
	
	public static void startPlayer(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p.getName()).start();
		} else {
			global.start();
		}
	}
	
	public static void nextSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p.getName()).nextSong();
		} else {
			global.nextSong();
		}
	}
	
	public static void setSong(Player p, SongFile sf) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p.getName()).setSong(sf);
		} else {
			global.setSong(sf);
		}
	}
	
	public static void lastSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p.getName()).lastSong();
		} else {
			global.lastSong();
		}
	}
	
	public static void setRepeat(Player p, boolean value) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p.getName()).setRepeat(value);
		} else {
			global.setRepeat(value);
		}
	}
	
	public static void setShuffle(Player p, boolean value) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p.getName()).setShuffle(value);
		} else {
			global.setShuffle(value);
		}
	}
	
	public static SongFile getSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			SongFile sf = jukeboxes.get(p.getName()).getSong();
			if (sf != null) {
				return sf;
			} else {
				return null;
			}
		} else {
			SongFile sf = global.getSong();
			if (sf != null) {
				return sf;
			} else {
				return null;
			}
		}
	}
	
	public static int getSongID(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			if (jukeboxes.get(p.getName()) != null)
				return jukeboxes.get(p.getName()).getSongID();
		} else {
			if (global != null)
				return global.getSongID();
		}
		return 0;
	}
	
	public static short getSongPosition(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			if (jukeboxes.get(p.getName()) != null)
				return jukeboxes.get(p.getName()).getSongPosition();
		} else {
			if (global != null)
				return global.getSongPosition();
		}
		return 0;
	}
	
	public static SongFile getNextSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p.getName()).getNextSong();
		} else {
			return global.getNextSong();
		}
	}
	
	public static SongFile getLastSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p.getName()).getLastSong();
		} else {
			return global.getLastSong();
		}
	}
	
	public static void stopSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p.getName()).kill(false);
		} else {
			global.kill();
		}
	}
	
	public static boolean isPlaying(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p.getName()).isPlaying();
		} else {
			return global.isPlaying();
		}
	}
	
	public static boolean isRepeating(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p.getName()).isRepeating();
		} else {
			return global.isRepeating();
		}
	}
	
	public static boolean isShuffling(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p.getName()).isShuffling();
		} else {
			return global.isShuffling();
		}
	}
	
	public static GlobalJukebox getJukebox() {
		if (Config.playerType == PlayerType.GLOBAL) {
			return global;
		} else {
			return null;
		}
	}
	
	public static Jukebox[] getJukeboxes() {
		if (Config.playerType == PlayerType.PRIVATE) {
			List<Jukebox> jbs = new ArrayList<Jukebox>();
			for (Jukebox j : jukeboxes.values()) {
				jbs.add(j);
			}
			return jbs.toArray(new Jukebox[jbs.size()]);
		} else {
			return null;
		}
	}
	
	public static enum PlayerType {
		PRIVATE,
		GLOBAL;
	}


}
