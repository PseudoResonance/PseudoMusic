package io.github.wolfleader116.wolfmusic.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class JukeboxController {
	
	private static Map<Player, Jukebox> jukeboxes = new HashMap<Player, Jukebox>();
	private static GlobalJukebox global = new GlobalJukebox();
	
	public static void connect(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			Jukebox j = new Jukebox(p);
			jukeboxes.put(p, j);
		} else {
			global.addPlayer(p);
		}
	}
	
	public static void disconnect(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).kill();
			jukeboxes.remove(p);
		} else {
			global.removePlayer(p);
		}
	}
	
	public static void kill() {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			for (Jukebox j : jukeboxes.values()) {
				j.kill();
			}
		} else {
			global.kill();
		}
	}
	
	public static void start() {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			for (Jukebox j : jukeboxes.values()) {
				j.start();
			}
		} else {
			global.start();
		}
	}
	
	public static void startPlayer(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).start();
		} else {
			global.start();
		}
	}
	
	public static void nextSong(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).nextSong();
		} else {
			global.nextSong();
		}
	}
	
	public static void setSong(Player p, SongFile sf) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).setSong(sf);
		} else {
			global.setSong(sf);
		}
	}
	
	public static void lastSong(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).lastSong();
		} else {
			global.lastSong();
		}
	}
	
	public static void setRepeat(Player p, boolean value) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			jukeboxes.get(p).setRepeat(value);
		} else {
			global.setRepeat(value);
		}
	}
	
	public static String getSong(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			SongFile sf = jukeboxes.get(p).getSong();
			if (sf != null) {
				return sf.getName();
			} else {
				return "";
			}
		} else {
			SongFile sf = global.getSong();
			if (sf != null) {
				return sf.getName();
			} else {
				return "";
			}
		}
	}
	
	public static String getNextSong(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).nextSongName();
		} else {
			return global.nextSongName();
		}
	}
	
	public static String getLastSong(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).lastSongName();
		} else {
			return global.lastSongName();
		}
	}
	
	public static void stopSong(Player p) {
		if (p.hasPermission("wolfmusic.stop")) {
			if (ConfigOptions.playerType == PlayerType.PRIVATE) {
				jukeboxes.get(p).kill();
			} else {
				global.kill();
			}
		} else {
			
		}
	}
	
	public static boolean isPlaying(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).isPlaying();
		} else {
			return global.isPlaying();
		}
	}
	
	public static boolean isRepeating(Player p) {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
			return jukeboxes.get(p).isRepeating();
		} else {
			return global.isRepeating();
		}
	}
	
	public static GlobalJukebox getJukebox() {
		if (ConfigOptions.playerType == PlayerType.GLOBAL) {
			return global;
		} else {
			return null;
		}
	}
	
	public static Jukebox[] getJukeboxes() {
		if (ConfigOptions.playerType == PlayerType.PRIVATE) {
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
