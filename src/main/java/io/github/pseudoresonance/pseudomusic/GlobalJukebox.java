package io.github.pseudoresonance.pseudomusic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.SoundCategory;

public class GlobalJukebox {
	
	protected List<Player> listening = new ArrayList<Player>();
	protected SongFile songFile;
	protected SongPlayer songPlayer;
	protected boolean playing = false;
	protected BossBar bossBar;
	protected boolean bossBarVisibility = false;
	protected GlobalBarUpdate barUpdate;
	protected boolean repeat = false;
	protected boolean shuffle = false;
	protected boolean stopped = true;
	private Random random = new Random();
	
	GlobalJukebox() {
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
		startSong();
		bossBar();
		title();
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
		stopped = false;
		if (PseudoMusic.songs.size() >= 1) {
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
				i = getSongID();
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
	
	public void nextSong() {
		stopped = false;
		if (PseudoMusic.songs.size() >= 1) {
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
			if (ConfigOptions.bossBar) {
				if (barUpdate != null) {
					barUpdate.cancel();
				}
			}
			if (bossBar != null) {
				bossBar.setVisible(false);
				bossBarVisibility = false;
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
		songPlayer = new RadioSongPlayer(songFile.getSong(), SoundCategory.RECORDS);
		for (Player p : listening) {
			songPlayer.addPlayer(p);
		}
		songPlayer.setPlaying(true);
		playing = true;
	}
	
	private void bossBar() {
		if (ConfigOptions.bossBar) {
			String barMessage = ConfigOptions.barMessage;
			barMessage = barMessage.replace("{name}", songFile.getName());
			barMessage = barMessage.replace("{cname}", songFile.getColor() + songFile.getName());
			int now = (int) Math.ceil(((double) songPlayer.getTick()) / songPlayer.getSong().getSpeed());
			int total = (int) Math.ceil((double) songPlayer.getSong().getLength() / songPlayer.getSong().getSpeed());
			String nowS = Jukebox.format(now);
			String totalS = Jukebox.format(total);
			barMessage = barMessage.replace("{time}", nowS);
			barMessage = barMessage.replace("{total}", totalS);
			bossBar.setTitle(barMessage);
			bossBar.setColor(songFile.getBarColor());
			bossBar.setProgress(0.0);
			for (Player p : listening) {
				bossBar.addPlayer(p);
			}
			bossBar.setVisible(true);
			bossBarVisibility = true;
			if (ConfigOptions.barVisibility != 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(PseudoMusic.plugin, new Runnable() {
					public void run() {
						bossBar.setVisible(false);
					}
				}, ConfigOptions.barVisibility);
			} else {
				barUpdate = new GlobalBarUpdate(this);
				barUpdate.runTaskTimer(PseudoMusic.plugin, ConfigOptions.barUpdate, ConfigOptions.barUpdate);
			}
		}
	}
	
	private void title() {
		if (ConfigOptions.title) {
			String titleMessage = ConfigOptions.titleMessage;
			titleMessage = titleMessage.replace("{name}", songFile.getName());
			titleMessage = titleMessage.replace("{cname}", songFile.getColor() + songFile.getName());
			for (Player p : listening) {
				p.sendTitle("", titleMessage, ConfigOptions.titleFade, ConfigOptions.titleVisibility, ConfigOptions.titleFade);
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
				Bukkit.getScheduler().scheduleSyncDelayedTask(PseudoMusic.plugin, new Runnable() {
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
	
	public void setRepeat(boolean value) {
		repeat = value;
	}

	public void setShuffle(boolean value) {
		shuffle = value;
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