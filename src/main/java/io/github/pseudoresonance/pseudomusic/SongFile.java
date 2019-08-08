package io.github.pseudoresonance.pseudomusic;

import java.io.File;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;

import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.model.Song;

public class SongFile {
	
	private String name = "";
	private Material disk;
	private ChatColor color;
	private BarColor barColor;
	private Song song;
	
	SongFile(String file) {
		this.name = file.replace(".nbs", "");
		Random rand = new Random(seed(name));
		int diskNumber = rand.nextInt(11) + 1;
		switch(diskNumber) {
		case 1:
			this.disk = Material.MUSIC_DISC_13;
			this.color = ChatColor.GOLD;
			this.barColor = BarColor.YELLOW;
			break;
		case 2:
			this.disk = Material.MUSIC_DISC_CAT;
			this.color = ChatColor.GREEN;
			this.barColor = BarColor.GREEN;
			break;
		case 3:
			this.disk = Material.MUSIC_DISC_BLOCKS;
			this.color = ChatColor.RED;
			this.barColor = BarColor.RED;
			break;
		case 4:
			this.disk = Material.MUSIC_DISC_CHIRP;
			this.color = ChatColor.DARK_RED;
			this.barColor = BarColor.RED;
			break;
		case 5:
			this.disk = Material.MUSIC_DISC_FAR;
			this.color = ChatColor.AQUA;
			this.barColor = BarColor.GREEN;
			break;
		case 6:
			this.disk = Material.MUSIC_DISC_MALL;
			this.color = ChatColor.DARK_PURPLE;
			this.barColor = BarColor.PURPLE;
			break;
		case 7:
			this.disk = Material.MUSIC_DISC_MELLOHI;
			this.color = ChatColor.LIGHT_PURPLE;
			this.barColor = BarColor.PINK;
			break;
		case 8:
			this.disk = Material.MUSIC_DISC_STAL;
			this.color = ChatColor.DARK_GRAY;
			this.barColor = BarColor.BLUE;
			break;
		case 9:
			this.disk = Material.MUSIC_DISC_STRAD;
			this.color = ChatColor.WHITE;
			this.barColor = BarColor.WHITE;
			break;
		case 10:
			this.disk = Material.MUSIC_DISC_WARD;
			this.color = ChatColor.DARK_GREEN;
			this.barColor = BarColor.GREEN;
			break;
		case 11:
			this.disk = Material.MUSIC_DISC_WAIT;
			this.color = ChatColor.DARK_AQUA;
			this.barColor = BarColor.BLUE;
			break;
		default:
			this.disk = Material.MUSIC_DISC_11;
			this.color = ChatColor.GRAY;
			this.barColor = BarColor.BLUE;
			break;
		}
		this.song = NBSDecoder.parse(new File(Config.songPath, file));
	}
	
	public String getName() {
		return this.name;
	}
	
	public Material getDisk() {
		return this.disk;
	}
	
	public ChatColor getColor() {
		return this.color;
	}
	
	public BarColor getBarColor() {
		return this.barColor;
	}
	
	public Song getSong() {
		return this.song;
	}
	
	public String getLength() {
		int total = (int) Math.ceil((double) song.getLength() / song.getSpeed());
		return Jukebox.format(total);
	}
	
	private static int seed(String s) {
	    if (s == null) {
	        return 0;
	    }
	    int hash = s.hashCode();
	    return hash;
	}

}
