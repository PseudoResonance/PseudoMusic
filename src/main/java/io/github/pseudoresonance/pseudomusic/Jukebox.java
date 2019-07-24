package io.github.pseudoresonance.pseudomusic;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;

import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;

public class Jukebox {
	
	protected Player player;
	protected SongFile songFile;
	protected SongPlayer songPlayer;
	protected boolean playing = false;
	protected BossBar bossBar;
	protected BarUpdate barUpdate;
	protected boolean repeat = false;
	protected boolean shuffle = false;
	protected boolean stopped = true;
	private Random random = new Random();
	
	Jukebox(Player player) {
		this.player = player;
		String barMessage = Config.barMessage;
		barMessage = barMessage.replace("{name}", "None");
		barMessage = barMessage.replace("{cname}", "None");
		barMessage = barMessage.replace("{time}", "0:00");
		barMessage = barMessage.replace("{total}", "0:00");
		bossBar = Bukkit.getServer().createBossBar(barMessage, BarColor.WHITE, BarStyle.SOLID);
		Object o = PlayerDataController.getPlayerSetting(player.getUniqueId().toString(), "musicPlaying");
		if (o instanceof Boolean) {
			boolean b = (Boolean) o;
			if (b) {
				Object obj = PlayerDataController.getPlayerSetting(player.getUniqueId().toString(), "musicSong");
				if (obj instanceof Integer) {
					int song = (Integer) obj;
					if (song < PseudoMusic.songs.size() && song >= 0) {
						setSong(song);
					}
				}
			}
		} else {
			nextSong();
		}
	}

	public void setSong(SongFile sf) {
		kill(false);
		stopped = false;
		songFile = sf;
		startSong();
		bossBar();
		title();
	}
	
	public void setSong(int id) {
		kill(false);
		stopped = false;
		SongFile sf = PseudoMusic.songs.get(id);
		if (sf == null) {
			if (PseudoMusic.songs.size() >= 1) {
				sf = PseudoMusic.songs.get(0);
			}
		}
		if (sf != null) {
			songFile = sf;
			startSong();
			bossBar();
			title();
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
	
	public int getSongID() {
		if (songFile != null) {
			int i = PseudoMusic.songs.indexOf(songFile);
			if (i >= 0)
				return i;
		}
		return -1;
	}
	
	public boolean isRepeating() {
		return repeat;
	}
	
	public boolean isShuffling() {
		return shuffle;
	}
	
	public void kill(boolean logoff) {
		if (!logoff)
			PlayerDataController.setPlayerSetting(player.getUniqueId().toString(), "musicPlaying", false);
		if (Config.bossBar) {
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
	
	public SongFile getNextSong() {
		if (PseudoMusic.songs.size() >= 1) {
			int i = 0;
			if (songFile != null) {
				i = getSongID();
				i++;
				if (i >= PseudoMusic.songs.size()) {
					i = 0;
				}
			}
			if (PseudoMusic.songs.size() == 1) {
				i = 0;
			}
			SongFile sf = PseudoMusic.getSongs()[i];
			return sf;
		}
		return null;
	}
	
	public SongFile getLastSong() {
		if (PseudoMusic.songs.size() >= 1) {
			int i = PseudoMusic.songs.size() - 1;
			if (songFile != null) {
				i = getSongID();
				i--;
				if (i < 0) {
					i = PseudoMusic.songs.size() - 1;
				}
			}
			if (PseudoMusic.songs.size() == 1) {
				i = 0;
			}
			SongFile sf = PseudoMusic.getSongs()[i];
			return sf;
		}
		return null;
	}
	
	public void nextAutoSong() {
		if (!stopped) {
			if (PseudoMusic.songs.size() >= 1) {
				if (songPlayer != null) {
					songPlayer.destroy();
				}
				if (Config.bossBar) {
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
						i = getSongID();
						if (i >= PseudoMusic.songs.size()) {
							i = 0;
						}
					}
					if (PseudoMusic.songs.size() == 1) {
						i = 0;
					}
				} else {
					if (shuffle) {
						int count = PseudoMusic.songs.size();
						i = random.nextInt(count);
					} else {
						if (songFile != null) {
							i = getSongID();
							i++;
							if (i >= PseudoMusic.songs.size()) {
								i = 0;
							}
						}
						if (PseudoMusic.songs.size() == 1) {
							i = 0;
						}
					}
				}
				songFile = PseudoMusic.getSongs()[i];
				startSong();
				bossBar();
				title();
			}
		}
	}
	
	public void start() {
		Object obj = PlayerDataController.getPlayerSetting(player.getUniqueId().toString(), "musicSong");
		if (obj instanceof Integer) {
			int song = (Integer) obj;
			setSong(song);
		}
	}
	
	public void nextSong() {
		stopped = false;
		if (PseudoMusic.songs.size() >= 1) {
			if (songPlayer != null) {
				songPlayer.destroy();
			}
			if (Config.bossBar) {
				if (barUpdate != null) {
					barUpdate.cancel();
				}
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
			}
			int i = 0;
			if (songFile != null) {
				i = getSongID();
				i++;
				if (i >= PseudoMusic.songs.size()) {
					i = 0;
				}
			}
			if (PseudoMusic.songs.size() == 1) {
				i = 0;
			}
			songFile = PseudoMusic.getSongs()[i];
			startSong();
			bossBar();
			title();
		}
	}
	
	public void lastSong() {
		stopped = false;
		if (PseudoMusic.songs.size() >= 1) {
			if (songPlayer != null) {
				songPlayer.destroy();
			}
			if (Config.bossBar) {
				if (barUpdate != null) {
					barUpdate.cancel();
				}
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
			}
			int i = PseudoMusic.songs.size() - 1;
			if (songFile != null) {
				i = getSongID();
				i--;
				if (i < 0) {
					i = PseudoMusic.songs.size() - 1;
				}
			}
			if (PseudoMusic.songs.size() == 1) {
				i = 0;
			}
			songFile = PseudoMusic.getSongs()[i];
			startSong();
			bossBar();
			title();
		}
	}
	
	private void startSong() {
		PlayerDataController.setPlayerSetting(player.getUniqueId().toString(), "musicPlaying", true);
		PlayerDataController.setPlayerSetting(player.getUniqueId().toString(), "musicSong", getSongID());
		if (PseudoMusic.songs.size() >= 1) {
			if (songFile != null) {
				int songNumber = getSongID();
				if (songNumber == -1)
					songNumber = 0;
				PlayerDataController.setPlayerSetting(player.getUniqueId().toString(), "musicSong", songNumber);
			}
		}
		if (songFile != null) {
			songPlayer = new RadioSongPlayer(songFile.getSong(), SoundCategory.RECORDS);
			songPlayer.setAutoDestroy(true);
			songPlayer.addPlayer(player);
			songPlayer.setPlaying(true);
			playing = true;
		}
	}
	
	private void bossBar() {
		if (Config.bossBar) {
			String barMessage = Config.barMessage;
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
			if (Config.barVisibility != 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(PseudoMusic.plugin, new Runnable() {
					public void run() {
						bossBar.setVisible(false);
					}
				}, Config.barVisibility * 20);
			} else {
				barUpdate = new BarUpdate(this);
				barUpdate.runTaskTimer(PseudoMusic.plugin, Config.barUpdate, Config.barUpdate);
			}
		}
	}
	
	private void title() {
		if (Config.title) {
			String titleMessage = Config.titleMessage;
			titleMessage = titleMessage.replace("{name}", songFile.getName());
			titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
			player.sendTitle("", titleMessage, Config.titleFade, Config.titleVisibility * 20, Config.titleFade);
		}
	}
	
	public void done() {
		if (Config.bossBar) {
			if (barUpdate != null) {
				barUpdate.cancel();
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
			}
		}
		if (Config.playlist) {
			long d = new Long((int) Math.round(Config.playlistDelay * 20));
			Bukkit.getScheduler().scheduleSyncDelayedTask(PseudoMusic.plugin, new Runnable() {
				public void run() {
					nextAutoSong();
				}
			}, d);
		}
	}
	
	public SongPlayer getSongPlayer() {
		return this.songPlayer;
	}
	
	protected static String format(int i) {
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
		PlayerDataController.setPlayerSetting(player.getUniqueId().toString(), "musicRepeat", value);
		repeat = value;
	}
	
	public void setShuffle(boolean value) {
		PlayerDataController.setPlayerSetting(player.getUniqueId().toString(), "musicShuffle", value);
		shuffle = value;
	}

}

class BarUpdate extends BukkitRunnable {
	
	private Jukebox j;
	private String barMessage = "";
	
	BarUpdate(Jukebox j) {
		this.j = j;
		barMessage = Config.barMessage;
		barMessage = barMessage.replace("{name}", j.songFile.getName());
		barMessage = barMessage.replace("{cname}", j.songFile.getColor() + j.songFile.getName());
	}
	
	public void run() {
		if (j.songFile != null && j.bossBar != null) {
			int now = (int) Math.ceil(((double) j.songPlayer.getTick()) / j.songPlayer.getSong().getSpeed());
			int total = (int) Math.ceil((double) j.songPlayer.getSong().getLength() / j.songPlayer.getSong().getSpeed());
			String nowS = Jukebox.format(now);
			String totalS = Jukebox.format(total);
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