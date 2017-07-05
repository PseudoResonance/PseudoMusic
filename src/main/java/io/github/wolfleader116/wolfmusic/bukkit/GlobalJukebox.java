package io.github.wolfleader116.wolfmusic.bukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.SongPlayer;

public class GlobalJukebox {
	
	protected List<Player> listening = new ArrayList<Player>();
	protected SongFile songFile;
	protected SongPlayer songPlayer;
	protected boolean playing = false;
	protected BossBar bossBar;
	protected boolean bossBarVisibility = false;
	protected GlobalBarUpdate barUpdate;
	
	GlobalJukebox() {
		nextSong();
	}

	public void setSong(SongFile sf) {
		kill();
		songFile = sf;
		songPlayer = new RadioSongPlayer(songFile.getSong());
		for (Player p : listening) {
			songPlayer.addPlayer(p);
		}
		songPlayer.setPlaying(true);
		playing = true;
		if (ConfigOptions.bossBar) {
			String barMessage = ConfigOptions.barMessage;
			barMessage = barMessage.replace("{name}", songFile.getName());
			barMessage = barMessage.replace("{cname}", songFile.getColor() + songFile.getName());
			int now = (int) Math.ceil(((double) songPlayer.getTick()) / songPlayer.getSong().getSpeed());
			int total = (int) Math.ceil((double) songPlayer.getSong().getLength() / songPlayer.getSong().getSpeed());
			String nowS = format(now);
			String totalS = format(total);
			barMessage = barMessage.replace("{time}", nowS);
			barMessage = barMessage.replace("{total}", totalS);
			bossBar = Bukkit.getServer().createBossBar(barMessage, songFile.getBarColor(), BarStyle.SOLID);
			bossBar.setProgress(0.0);
			for (Player p : listening) {
				bossBar.addPlayer(p);
			}
			bossBar.setVisible(true);
			bossBarVisibility = true;
			if (ConfigOptions.barVisibility != 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
					public void run() {
						bossBar.setVisible(false);
					}
				}, ConfigOptions.barVisibility);
			} else {
				barUpdate = new GlobalBarUpdate(this);
				barUpdate.runTaskTimer(WolfMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
			}
		}
		if (ConfigOptions.title) {
			String titleMessage = ConfigOptions.titleMessage;
			titleMessage = titleMessage.replace("{name}", songFile.getName());
			titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
			for (Player p : listening) {
				p.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
			}
		}
	}
	
	public void addPlayer(Player p) {
		listening.add(p);
		if (songPlayer != null) {
			songPlayer.addPlayer(p);
			if (bossBarVisibility) {
				bossBar.addPlayer(p);
			}
			if (ConfigOptions.title) {
				String titleMessage = ConfigOptions.titleMessage;
				titleMessage = titleMessage.replace("{name}", songFile.getName());
				titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
				p.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
			}
		}
	}
	
	public void removePlayer(Player p) {
		listening.remove(p);
		if (songPlayer != null) {
			songPlayer.removePlayer(p);
			if (bossBarVisibility) {
				bossBar.removePlayer(p);
			}
		}
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public List<Player> getPlayers() {
		return listening;
	}
	
	public SongFile getSong() {
		return songFile;
	}
	
	public void kill() {
		if (ConfigOptions.bossBar) {
			if (barUpdate != null) {
				barUpdate.cancel();
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
				bossBarVisibility = false;
			}
		}
		if (songPlayer != null) {
			songPlayer.destroy();
		}
		songPlayer = null;
		songFile = null;
		playing = false;
	}
	
	public String nextSongName() {
		if (WolfMusic.songs.size() >= 1) {
			int i = 0;
			if (songFile != null) {
				i = WolfMusic.songs.indexOf(songFile);
				i++;
				if (i >= WolfMusic.songs.size()) {
					i = 0;
				}
			}
			if (WolfMusic.songs.size() == 1) {
				i = 0;
			}
			SongFile sf = WolfMusic.getSongs()[i];
			return sf.getName();
		}
		return "None";
	}
	
	public String lastSongName() {
		if (WolfMusic.songs.size() >= 1) {
			int i = WolfMusic.songs.size() - 1;
			if (songFile != null) {
				i = WolfMusic.songs.indexOf(songFile);
				i--;
				if (i < 0) {
					i = WolfMusic.songs.size() - 1;
				}
			}
			if (WolfMusic.songs.size() == 1) {
				i = 0;
			}
			SongFile sf = WolfMusic.getSongs()[i];
			return sf.getName();
		}
		return "None";
	}
	
	public void nextSong() {
		if (WolfMusic.songs.size() >= 1) {
			if (songPlayer != null) {
				songPlayer.destroy();
			}
			if (ConfigOptions.bossBar) {
				if (barUpdate != null) {
					barUpdate.cancel();
				}
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
				bossBarVisibility = false;
			}
			int i = 0;
			if (songFile != null) {
				i = WolfMusic.songs.indexOf(songFile);
				i++;
				if (i >= WolfMusic.songs.size()) {
					i = 0;
				}
			}
			if (WolfMusic.songs.size() == 1) {
				i = 0;
			}
			songFile = WolfMusic.getSongs()[i];
			songPlayer = new RadioSongPlayer(songFile.getSong());
			for (Player p : listening) {
				songPlayer.addPlayer(p);
			}
			songPlayer.setPlaying(true);
			playing = true;
			if (ConfigOptions.bossBar) {
				String barMessage = ConfigOptions.barMessage;
				barMessage = barMessage.replace("{name}", songFile.getName());
				barMessage = barMessage.replace("{cname}", songFile.getColor() + songFile.getName());
				int now = (int) Math.ceil(((double) songPlayer.getTick()) / songPlayer.getSong().getSpeed());
				int total = (int) Math.ceil((double) songPlayer.getSong().getLength() / songPlayer.getSong().getSpeed());
				String nowS = format(now);
				String totalS = format(total);
				barMessage = barMessage.replace("{time}", nowS);
				barMessage = barMessage.replace("{total}", totalS);
				bossBar = Bukkit.getServer().createBossBar(barMessage, songFile.getBarColor(), BarStyle.SOLID);
				bossBar.setProgress(0.0);
				for (Player p : listening) {
					bossBar.addPlayer(p);
				}
				bossBar.setVisible(true);
				bossBarVisibility = true;
				if (ConfigOptions.barVisibility != 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
						public void run() {
							bossBar.setVisible(false);
						}
					}, ConfigOptions.barVisibility);
				} else {
					barUpdate = new GlobalBarUpdate(this);
					barUpdate.runTaskTimer(WolfMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
				}
			}
			if (ConfigOptions.title) {
				String titleMessage = ConfigOptions.titleMessage;
				titleMessage = titleMessage.replace("{name}", songFile.getName());
				titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
				for (Player p : listening) {
					p.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
				}
			}
		}
	}
	
	public void lastSong() {
		if (WolfMusic.songs.size() >= 1) {
			if (songPlayer != null) {
				songPlayer.destroy();
			}
			if (ConfigOptions.bossBar) {
				if (barUpdate != null) {
					barUpdate.cancel();
				}
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
				bossBarVisibility = false;
			}
			int i = WolfMusic.songs.size() - 1;
			if (songFile != null) {
				i = WolfMusic.songs.indexOf(songFile);
				i--;
				if (i < 0) {
					i = WolfMusic.songs.size() - 1;
				}
			}
			if (WolfMusic.songs.size() == 1) {
				i = 0;
			}
			songFile = WolfMusic.getSongs()[i];
			songPlayer = new RadioSongPlayer(songFile.getSong());
			for (Player p : listening) {
				songPlayer.addPlayer(p);
			}
			songPlayer.setPlaying(true);
			playing = true;
			if (ConfigOptions.bossBar) {
				String barMessage = ConfigOptions.barMessage;
				barMessage = barMessage.replace("{name}", songFile.getName());
				barMessage = barMessage.replace("{cname}", songFile.getColor() + songFile.getName());
				int now = (int) Math.ceil(((double) songPlayer.getTick()) / songPlayer.getSong().getSpeed());
				int total = (int) Math.ceil((double) songPlayer.getSong().getLength() / songPlayer.getSong().getSpeed());
				String nowS = format(now);
				String totalS = format(total);
				barMessage = barMessage.replace("{time}", nowS);
				barMessage = barMessage.replace("{total}", totalS);
				bossBar = Bukkit.getServer().createBossBar(barMessage, songFile.getBarColor(), BarStyle.SOLID);
				bossBar.setProgress(0.0);
				for (Player p : listening) {
					bossBar.addPlayer(p);
				}
				bossBar.setVisible(true);
				bossBarVisibility = true;
				if (ConfigOptions.barVisibility != 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
						public void run() {
							bossBar.setVisible(false);
						}
					}, ConfigOptions.barVisibility);
				} else {
					barUpdate = new GlobalBarUpdate(this);
					barUpdate.runTaskTimer(WolfMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
				}
			}
			if (ConfigOptions.title) {
				String titleMessage = ConfigOptions.titleMessage;
				titleMessage = titleMessage.replace("{name}", songFile.getName());
				titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
				for (Player p : listening) {
					p.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
				}
			}
		}
	}
	
	public void done() {
		if (ConfigOptions.bossBar) {
			if (barUpdate != null) {
				barUpdate.cancel();
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
				bossBarVisibility = false;
			}
		}
		if (ConfigOptions.playlist) {
			long d = new Long((int) Math.round(ConfigOptions.playlistDelay * 20));
			if (ConfigOptions.autoStart) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
					public void run() {
						nextSong();
					}
				}, d);
			}
		}
	}
	
	public SongPlayer getSongPlayer() {
		return this.songPlayer;
	}
	
	protected String format(int i) {
		if (i < 0) {
			return "0";
		} else {
			int minutes = (int) Math.floor(((double) i) / 60);
			int secondsInMinutes = minutes * 60;
			int seconds = i - secondsInMinutes;
			return String.valueOf(minutes) + ":" + String.valueOf(seconds);
		}
	}

}

class GlobalBarUpdate extends BukkitRunnable {
	
	private GlobalJukebox j;
	private String barMessage = "";
	
	GlobalBarUpdate(GlobalJukebox j) {
		this.j = j;
		barMessage = ConfigOptions.barMessage;
		barMessage = barMessage.replace("{name}", j.songFile.getName());
		barMessage = barMessage.replace("{cname}", j.songFile.getColor() + j.songFile.getName());
	}
	
	public void run() {
		if (j.songFile != null && j.bossBar != null) {
			int now = (int) Math.ceil(((double) j.songPlayer.getTick()) / j.songPlayer.getSong().getSpeed());
			int total = (int) Math.ceil((double) j.songPlayer.getSong().getLength() / j.songPlayer.getSong().getSpeed());
			String nowS = j.format(now);
			String totalS = j.format(total);
			String output = barMessage;
			output = output.replace("{time}", nowS);
			output = output.replace("{total}", totalS);
			j.bossBar.setTitle(output);
			double progress = (double) j.songPlayer.getTick() / j.songPlayer.getSong().getLength();
			if (progress > 1.0) {
				progress = 1.0;
			} else if (progress < 0.0) {
				progress = 0.0;
			}
			j.bossBar.setProgress(progress);
		} else {
			this.cancel();
		}
	}
}