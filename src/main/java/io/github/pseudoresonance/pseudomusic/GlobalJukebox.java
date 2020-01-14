package io.github.pseudoresonance.pseudomusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;

import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;

import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;

public class GlobalJukebox {

	protected List<Player> listening = new ArrayList<Player>();
	protected SongFile songFile;
	protected SongPlayer songPlayer;
	protected volatile boolean playing = false;
	protected ConcurrentHashMap<String, BossBar> bossBars = new ConcurrentHashMap<String, BossBar>();
	protected boolean bossBarVisibility = false;
	protected GlobalBarUpdate barUpdate;
	protected boolean repeat = false;
	protected boolean shuffle = false;
	protected volatile boolean stopped = true;
	protected volatile boolean cancelAutoRun = false;
	private Random random = new Random();

	static Pattern patternNow = Pattern.compile("\\{\\$3\\$\\}");
	static Pattern patternTotal = Pattern.compile("\\{\\$4\\$\\}");

	GlobalJukebox() {
		nextSong();
	}

	public void setSong(SongFile sf) {
		setSong(sf, (short) -1);
	}

	public void setSong(SongFile sf, short position) {
		kill();
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
		kill();
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

	private BossBar addBossBar(String lang) {
		BossBar bb = bossBars.get(lang);
		if (bb == null) {
			String none = LanguageManager.getLanguage(lang).getMessage("pseudomusic.none");
			String barMessage = LanguageManager.getLanguage(lang).getMessage("pseudomusic.bossbar_name", none, none, "0:00", "0:00");
			bb = Bukkit.getServer().createBossBar(barMessage, BarColor.WHITE, BarStyle.SOLID);
			this.bossBars.put(lang, bb);
		}
		return bb;
	}

	public void addPlayer(Player p) {
		listening.add(p);
		if (songPlayer != null) {
			songPlayer.addPlayer(p);
			if (bossBarVisibility) {
				String lang = LanguageManager.getLanguage(p).getName();
				BossBar bb = addBossBar(lang);
				bb.addPlayer(p);
			}
			if (Config.title) {
				String titleMessage = LanguageManager.getLanguage(p).getMessage("pseudomusic.title_name", songFile.getName(), songFile.getColor() + songFile.getName());
				p.sendTitle("", titleMessage, Config.titleFade, Config.titleVisibility * 20, Config.titleFade);
			}
		}
	}

	public void removePlayer(Player p) {
		listening.remove(p);
		if (songPlayer != null) {
			songPlayer.removePlayer(p);
			if (bossBarVisibility) {
				BossBar bb = bossBars.get(LanguageManager.getLanguage(p).getName());
				if (bb != null) {
					bb.removePlayer(p);
				}
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
		if (Config.bossBar) {
			if (barUpdate != null) {
				barUpdate.cancel();
			}
			for (BossBar b : bossBars.values()) {
				b.setVisible(false);
				b.removeAll();
			}
			bossBarVisibility = false;
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
				for (BossBar b : bossBars.values()) {
					b.setVisible(false);
					b.removeAll();
				}
				bossBarVisibility = false;
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
			for (BossBar b : bossBars.values()) {
				b.setVisible(false);
				b.removeAll();
			}
			bossBarVisibility = false;
			int i = 0;
			if (songFile != null) {
				i = getSongID();
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
			for (BossBar b : bossBars.values()) {
				b.setVisible(false);
				b.removeAll();
			}
			bossBarVisibility = false;
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
			for (BossBar b : bossBars.values()) {
				b.setVisible(false);
				b.removeAll();
			}
			bossBarVisibility = false;
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
		if (playing)
			songPlayer.getTick();
		if (playing)
			return 0;
		return -1;
	}

	private void startSong() {
		startSong((short) -1);
	}

	private void startSong(short position) {
		songPlayer = new RadioSongPlayer(songFile.getSong(), SoundCategory.RECORDS);
		for (Player p : listening) {
			songPlayer.addPlayer(p);
		}
		songPlayer.setAutoDestroy(true);
		songPlayer.setTick(position);
		songPlayer.setPlaying(true);
		playing = true;
	}

	private void bossBar() {
		if (Config.bossBar) {
			for (Player p : listening) {
				addBossBar(LanguageManager.getLanguage(p).getName()).addPlayer(p);
			}
			int now = (int) Math.ceil(((double) songPlayer.getTick()) / songPlayer.getSong().getSpeed());
			int total = (int) Math.ceil((double) songPlayer.getSong().getLength() / songPlayer.getSong().getSpeed());
			String nowS = Jukebox.format(now);
			String totalS = Jukebox.format(total);
			for (Entry<String, BossBar> ent : bossBars.entrySet()) {
				BossBar bb = ent.getValue();
				String barMessage = LanguageManager.getLanguage(ent.getKey()).getMessage("pseudomusic.bossbar_name", songFile.getName(), songFile.getColor() + songFile.getName(), nowS, totalS);
				bb.setTitle(barMessage);
				bb.setColor(songFile.getBarColor());
				bb.setProgress(0.0);
				bb.setVisible(true);
			}
			bossBarVisibility = true;
			if (Config.barVisibility != 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(PseudoMusic.plugin, new Runnable() {
					public void run() {
						for (BossBar bb : bossBars.values())
							bb.setVisible(false);
					}
				}, Config.barVisibility * 20);
			} else {
				barUpdate = new GlobalBarUpdate(this);
				barUpdate.runTaskTimer(PseudoMusic.plugin, Config.barUpdate, Config.barUpdate);
			}
		}
	}

	private void title() {
		if (Config.title) {
			for (Player p : listening) {
				String titleMessage = LanguageManager.getLanguage(p).getMessage("pseudomusic.title_name", songFile.getName(), songFile.getColor() + songFile.getName());
				p.sendTitle("", titleMessage, Config.titleFade, Config.titleVisibility * 20, Config.titleFade);
			}
		}
	}

	public void done() {
		if (Config.bossBar) {
			if (barUpdate != null) {
				barUpdate.cancel();
			}
			for (BossBar b : bossBars.values()) {
				b.setVisible(false);
				b.removeAll();
			}
			bossBarVisibility = false;
		}
		if (Config.playlist) {
			long d = new Long((int) Math.round(Config.playlistDelay * 20));
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

	public void setRepeat(boolean value) {
		repeat = value;
	}

	public void setShuffle(boolean value) {
		shuffle = value;
	}

}

class GlobalBarUpdate extends BukkitRunnable {

	private GlobalJukebox j;
	private HashMap<String, String> barMessages = new HashMap<String, String>();

	GlobalBarUpdate(GlobalJukebox j) {
		this.j = j;
		for (String lang : j.bossBars.keySet()) {
			barMessages.put(lang, LanguageManager.getLanguage(lang).getMessage("pseudomusic.bossbar_name", j.songFile.getName(), j.songFile.getColor() + j.songFile.getName()));
		}
	}

	public void run() {
		if (j.songFile != null && j.bossBars != null) {
			int now = (int) Math.ceil(((double) j.songPlayer.getTick()) / j.songPlayer.getSong().getSpeed());
			int total = (int) Math.ceil((double) j.songPlayer.getSong().getLength() / j.songPlayer.getSong().getSpeed());
			String nowS = Jukebox.format(now);
			String totalS = Jukebox.format(total);
			double progress = (double) j.songPlayer.getTick() / j.songPlayer.getSong().getLength();
			if (progress > 1.0) {
				progress = 1.0;
			} else if (progress < 0.0) {
				progress = 0.0;
			}
			for (Entry<String, BossBar> ent : j.bossBars.entrySet()) {
				BossBar bb = ent.getValue();
				String output = barMessages.get(ent.getKey());
				if (output == null) {
					output = LanguageManager.getLanguage(ent.getKey()).getMessage("pseudomusic.bossbar_name", j.songFile.getName(), j.songFile.getColor() + j.songFile.getName());
					barMessages.put(ent.getKey(), output);
				}
				output = Jukebox.patternNow.matcher(output).replaceFirst(nowS);
				output = Jukebox.patternTotal.matcher(output).replaceFirst(totalS);
				bb.setTitle(output);
				bb.setProgress(progress);
			}
		} else {
			this.cancel();
		}
	}
}