package io.github.wolfleader116.wolfmusic.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.SongPlayer;

public class Jukebox {
	
	protected Player player;
	protected SongFile songFile;
	protected SongPlayer songPlayer;
	protected boolean playing = false;
	protected BossBar bossBar;
	protected BarUpdate barUpdate;
	protected boolean repeat = false;
	protected boolean stopped = true;
	
	Jukebox(Player player) {
		this.player = player;
		String barMessage = ConfigOptions.barMessage;
		barMessage = barMessage.replace("{name}", "None");
		barMessage = barMessage.replace("{cname}", "None");
		barMessage = barMessage.replace("{time}", "0:00");
		barMessage = barMessage.replace("{total}", "0:00");
		bossBar = Bukkit.getServer().createBossBar(barMessage, BarColor.WHITE, BarStyle.SOLID);
		nextSong();
	}

	public void setSong(SongFile sf) {
		kill();
		stopped = false;
		songFile = sf;
		songPlayer = new RadioSongPlayer(songFile.getSong());
		songPlayer.setAutoDestroy(true);
		songPlayer.addPlayer(player);
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
			bossBar.setTitle(barMessage);
			bossBar.setColor(songFile.getBarColor());
			bossBar.setProgress(0.0);
			bossBar.addPlayer(player);
			bossBar.setVisible(true);
			if (ConfigOptions.barVisibility != 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
					public void run() {
						bossBar.setVisible(false);
					}
				}, ConfigOptions.barVisibility);
			} else {
				barUpdate = new BarUpdate(this);
				barUpdate.runTaskTimer(WolfMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
			}
		}
		if (ConfigOptions.title) {
			String titleMessage = ConfigOptions.titleMessage;
			titleMessage = titleMessage.replace("{name}", songFile.getName());
			titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
			player.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
		}
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public SongFile getSong() {
		return songFile;
	}
	
	public boolean isRepeating() {
		return repeat;
	}
	
	public void kill() {
		if (ConfigOptions.bossBar) {
			if (barUpdate != null) {
				barUpdate.cancel();
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
			}
		}
		if (songPlayer != null) {
			songPlayer.destroy();
		}
		songPlayer = null;
		playing = false;
		stopped = true;
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
	
	public void nextAutoSong() {
		if (!stopped) {
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
				}
				int i = 0;
				if (repeat) {
					if (songFile != null) {
						i = WolfMusic.songs.indexOf(songFile);
						if (i >= WolfMusic.songs.size()) {
							i = 0;
						}
					}
					if (WolfMusic.songs.size() == 1) {
						i = 0;
					}
				} else {
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
				}
				songFile = WolfMusic.getSongs()[i];
				songPlayer = new RadioSongPlayer(songFile.getSong());
				songPlayer.setAutoDestroy(true);
				songPlayer.addPlayer(player);
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
					bossBar.setTitle(barMessage);
					bossBar.setColor(songFile.getBarColor());
					bossBar.setProgress(0.0);
					bossBar.addPlayer(player);
					bossBar.setVisible(true);
					if (ConfigOptions.barVisibility != 0) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
							public void run() {
								bossBar.setVisible(false);
							}
						}, ConfigOptions.barVisibility);
					} else {
						barUpdate = new BarUpdate(this);
						barUpdate.runTaskTimer(WolfMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
					}
				}
				if (ConfigOptions.title) {
					String titleMessage = ConfigOptions.titleMessage;
					titleMessage = titleMessage.replace("{name}", songFile.getName());
					titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
					player.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
				}
			}
		}
	}
	
	public void start() {
		stopped = false;
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
			}
			int i = 0;
			if (songFile != null) {
				i = WolfMusic.songs.indexOf(songFile);
				if (i >= WolfMusic.songs.size()) {
					i = 0;
				}
			}
			if (WolfMusic.songs.size() == 1) {
				i = 0;
			}
			songFile = WolfMusic.getSongs()[i];
			songPlayer = new RadioSongPlayer(songFile.getSong());
			songPlayer.setAutoDestroy(true);
			songPlayer.addPlayer(player);
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
				bossBar.setTitle(barMessage);
				bossBar.setColor(songFile.getBarColor());
				bossBar.setProgress(0.0);
				bossBar.addPlayer(player);
				bossBar.setVisible(true);
				if (ConfigOptions.barVisibility != 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
						public void run() {
							bossBar.setVisible(false);
						}
					}, ConfigOptions.barVisibility);
				} else {
					barUpdate = new BarUpdate(this);
					barUpdate.runTaskTimer(WolfMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
				}
			}
			if (ConfigOptions.title) {
				String titleMessage = ConfigOptions.titleMessage;
				titleMessage = titleMessage.replace("{name}", songFile.getName());
				titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
				player.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
			}
		}
	}
	
	public void nextSong() {
		stopped = false;
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
			songPlayer.setAutoDestroy(true);
			songPlayer.addPlayer(player);
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
				bossBar.setTitle(barMessage);
				bossBar.setColor(songFile.getBarColor());
				bossBar.setProgress(0.0);
				bossBar.addPlayer(player);
				bossBar.setVisible(true);
				if (ConfigOptions.barVisibility != 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
						public void run() {
							bossBar.setVisible(false);
						}
					}, ConfigOptions.barVisibility);
				} else {
					barUpdate = new BarUpdate(this);
					barUpdate.runTaskTimer(WolfMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
				}
			}
			if (ConfigOptions.title) {
				String titleMessage = ConfigOptions.titleMessage;
				titleMessage = titleMessage.replace("{name}", songFile.getName());
				titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
				player.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
			}
		}
	}
	
	public void lastSong() {
		stopped = false;
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
			songPlayer.setAutoDestroy(true);
			songPlayer.addPlayer(player);
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
				bossBar.setTitle(barMessage);
				bossBar.setColor(songFile.getBarColor());
				bossBar.setProgress(0.0);
				bossBar.addPlayer(player);
				bossBar.setVisible(true);
				if (ConfigOptions.barVisibility != 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
						public void run() {
							bossBar.setVisible(false);
						}
					}, ConfigOptions.barVisibility);
				} else {
					barUpdate = new BarUpdate(this);
					barUpdate.runTaskTimer(WolfMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
				}
			}
			if (ConfigOptions.title) {
				String titleMessage = ConfigOptions.titleMessage;
				titleMessage = titleMessage.replace("{name}", songFile.getName());
				titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
				player.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
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
			}
		}
		if (ConfigOptions.playlist) {
			long d = new Long((int) Math.round(ConfigOptions.playlistDelay * 20));
			if (ConfigOptions.autoStart) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(WolfMusic.plugin, new Runnable() {
					public void run() {
						nextAutoSong();
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
			return "0:00";
		} else {
			int minutes = (int) Math.floor(((double) i) / 60);
			int secondsInMinutes = minutes * 60;
			int seconds = i - secondsInMinutes;
			int hours = 0;
			if (minutes >= 60) {
				hours = (int) Math.floor(((double) minutes) / 60);
				int minutesInHours = hours * 60;
				minutes -= minutesInHours;
			}
			String ret = "0:00";
			String sec = "00";
			if (seconds < 10) {
				sec = "0" + String.valueOf(seconds);
			} else {
				sec = String.valueOf(seconds);
			}
			if (hours > 0) {
				String min = "0";
				if (minutes < 10) {
					min = "0" + String.valueOf(minutes);
				} else {
					min = String.valueOf(minutes);
				}
				String hr = String.valueOf(hours);
				ret = hr + ":" +  min + ":" + sec;
			} else {
				String min = String.valueOf(minutes);
				ret = min + ":" + sec;
			}
			return ret;
		}
	}
	
	public void setRepeat(boolean value) {
		repeat = value;
	}

}

class BarUpdate extends BukkitRunnable {
	
	private Jukebox j;
	private String barMessage = "";
	
	BarUpdate(Jukebox j) {
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