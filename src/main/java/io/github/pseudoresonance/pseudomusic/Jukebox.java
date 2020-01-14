package io.github.pseudoresonance.pseudomusic;

import java.util.Random;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;

import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;

public class Jukebox {
	
	protected Player player;
	protected SongFile songFile;
	protected SongPlayer songPlayer;
	protected volatile boolean playing = false;
	protected BossBar bossBar;
	protected BarUpdate barUpdate;
	protected boolean repeat = false;
	protected boolean shuffle = false;
	protected volatile boolean stopped = true;
	protected volatile boolean cancelAutoRun = false;
	private Random random = new Random();

	static Pattern patternNow = Pattern.compile("\\{\\$3\\$\\}");
	static Pattern patternTotal = Pattern.compile("\\{\\$4\\$\\}");
	
	Jukebox(Player player) {
		this.player = player;
		String none = LanguageManager.getLanguage(player).getMessage("pseudomusic.none");
		String barMessage = LanguageManager.getLanguage(player).getMessage("pseudomusic.bossbar_name", none, none, "0:00", "0:00");
		bossBar = Bukkit.getServer().createBossBar(barMessage, BarColor.WHITE, BarStyle.SOLID);
		Bukkit.getScheduler().scheduleSyncDelayedTask(PseudoMusic.plugin, () -> {
			Object o = PlayerDataController.getPlayerSetting(player.getUniqueId().toString(), "musicPlaying").join();
			if (o instanceof Boolean) {
				boolean b = (Boolean) o;
				if (b) {
					Object obj = PlayerDataController.getPlayerSetting(player.getUniqueId().toString(), "musicSong").join();
					if (obj instanceof Integer) {
						int song = (Integer) obj;
						if (song < PseudoMusic.songs.size() && song >= 0) {
							Object continueO = PlayerDataController.getPlayerSetting(player.getUniqueId().toString(), "musicContinue", true).join();
							if (continueO != null && continueO instanceof Boolean) {
								boolean cont = (Boolean) continueO;
								if (cont) {
									Object positionO = PlayerDataController.getPlayerSetting(player.getUniqueId().toString(), "musicPosition", true).join();
									if (positionO != null && positionO instanceof Integer) {
										int position = (Integer) positionO;
										setSong(song, (short) position);
										return;
									}
								}
							}
							setSong(song);
						}
					}
				}
			} else {
				nextSong();
			}
		}, 10);
	}

	public void setSong(SongFile sf) {
		setSong(sf, (short) -1);
	}

	public void setSong(SongFile sf, short position) {
		kill(true);
		stopped = false;
		songFile = sf;
		if (position < -1)
			position = -1;
		startSong(position);
		bossBar();
		title();
		cancelAutoRun = true;
	}
	
	public void setSong(int id) {
		setSong(id, (short) -1);
	}
	
	public void setSong(int id, short position) {
		kill(true);
		stopped = false;
		SongFile sf = PseudoMusic.songs.get(id);
		if (sf == null) {
			if (PseudoMusic.songs.size() >= 1) {
				sf = PseudoMusic.songs.get(0);
			}
		}
		if (sf != null) {
			if (position < -1)
				position = -1;
			songFile = sf;
			startSong(position);
			bossBar();
			title();
			cancelAutoRun = true;
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
	
	public void kill(boolean temporaryStop) {
		if (!temporaryStop)
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
		cancelAutoRun = true;
	}
	
	public SongFile getNextSong() {
		if (PseudoMusic.songs.size() >= 1) {
			int i = 0;
			if (songFile != null) {
				i = getSongID();
				i++;
				if (i >= PseudoMusic.songs.size() || i < 0) {
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
						if (i >= PseudoMusic.songs.size() || i < 0) {
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
							if (i >= PseudoMusic.songs.size() || i < 0) {
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
		Object obj = PlayerDataController.getPlayerSetting(player.getUniqueId().toString(), "musicSong").join();
		if (obj instanceof Integer) {
			int song = (Integer) obj;
			setSong(song);
			cancelAutoRun = true;
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
				if (i >= PseudoMusic.songs.size() || i < 0) {
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
			cancelAutoRun = true;
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
			cancelAutoRun = true;
		}
	}
	
	public short getSongPosition() {
		if (songPlayer != null)
			return songPlayer.getTick();
		if (playing)
			return 0;
		return -1;
	}
	
	private void startSong() {
		startSong((short) -1);
	}
	
	private void startSong(short position) {
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
			songPlayer.addPlayer(player);
			songPlayer.setTick(position);
			songPlayer.setPlaying(true);
			playing = true;
		}
	}
	
	private void bossBar() {
		if (Config.bossBar) {
			int now = (int) Math.ceil(((double) songPlayer.getTick()) / songPlayer.getSong().getSpeed());
			int total = (int) Math.ceil((double) songPlayer.getSong().getLength() / songPlayer.getSong().getSpeed());
			String nowS = format(now);
			String totalS = format(total);
			String barMessage = LanguageManager.getLanguage(player).getMessage("pseudomusic.bossbar_name", songFile.getName(), songFile.getColor() + songFile.getName(), nowS, totalS);
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
			String titleMessage = LanguageManager.getLanguage(player).getMessage("pseudomusic.title_name", songFile.getName(), songFile.getColor() + songFile.getName());
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
			cancelAutoRun = false;
			Bukkit.getScheduler().scheduleSyncDelayedTask(PseudoMusic.plugin, new Runnable() {
				public void run() {
					if (!cancelAutoRun)
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
		barMessage = LanguageManager.getLanguage(j.player).getMessage("pseudomusic.bossbar_name", j.songFile.getName(), j.songFile.getColor() + j.songFile.getName());
	}
	
	public void run() {
		if (j.songFile != null && j.bossBar != null) {
			int now = (int) Math.ceil(((double) j.songPlayer.getTick()) / j.songPlayer.getSong().getSpeed());
			int total = (int) Math.ceil((double) j.songPlayer.getSong().getLength() / j.songPlayer.getSong().getSpeed());
			String nowS = Jukebox.format(now);
			String totalS = Jukebox.format(total);
			String output = barMessage;
			output = Jukebox.patternNow.matcher(output).replaceFirst(nowS);
			output = Jukebox.patternTotal.matcher(output).replaceFirst(totalS);
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