package io.github.pseudoresonance.pseudomusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class JukeboxController {
	
	private static Map<Player, Jukebox> jukeboxes = new HashMap<Player, Jukebox>();
	private static GlobalJukebox global = new GlobalJukebox();
	
	public static void connect(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			Jukebox j = new Jukebox(p);
			jukeboxes.put(p, j);
		} else {
			global.addPlayer(p);
		}
	}
	
	public static void disconnect(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).kill(true);
			jukeboxes.remove(p);
		} else {
			global.removePlayer(p);
		}
	}
	
	public static void kill() {
		if (Config.playerType == PlayerType.PRIVATE) {
			for (Jukebox j : jukeboxes.values()) {
				j.kill(false);
			}
		} else {
			global.kill();
		}
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
			jukeboxes.get(p).start();
		} else {
			global.start();
		}
	}
	
	public static void nextSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).nextSong();
		} else {
			global.nextSong();
		}
	}
	
	public static void setSong(Player p, SongFile sf) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).setSong(sf);
		} else {
			global.setSong(sf);
		}
	}
	
	public static void lastSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).lastSong();
		} else {
			global.lastSong();
		}
	}
	
	public static void setRepeat(Player p, boolean value) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).setRepeat(value);
		} else {
			global.setRepeat(value);
		}
	}
	
	public static void setShuffle(Player p, boolean value) {
		if (Config.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).setShuffle(value);
		} else {
			global.setShuffle(value);
		}
	}
	
	public static SongFile getSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			SongFile sf = jukeboxes.get(p).getSong();
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
			if (jukeboxes.get(p) != null)
				return jukeboxes.get(p).getSongID();
		} else {
			if (global != null)
				return global.getSongID();
		}
		return 0;
	}
	
	public static SongFile getNextSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).getNextSong();
		} else {
			return global.getNextSong();
		}
	}
	
	public static SongFile getLastSong(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).getLastSong();
		} else {
			return global.getLastSong();
		}
	}
	
	public static void stopSong(Player p) {
		if (p.hasPermission("pseudomusic.stop")) {
			if (Config.playerType == PlayerType.PRIVATE) {
				jukeboxes.get(p).kill(false);
			} else {
				global.kill();
			}
		} else {
			
		}
	}
	
	public static boolean isPlaying(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).isPlaying();
		} else {
			return global.isPlaying();
		}
	}
	
	public static boolean isRepeating(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).isRepeating();
		} else {
			return global.isRepeating();
		}
	}
	
	public static boolean isShuffling(Player p) {
		if (Config.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).isShuffling();
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

}
