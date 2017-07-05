package io.github.wolfleader116.wolfmusic.bukkit;

import java.io.File;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.Song;

public class SongFile {
	
	private String name = "";
	private Material disk;
	private ChatColor color;
	private BarColor barColor;
	private Song song;
	
	SongFile(String file) {
		this.name = file.replace(".nbs", "");
		Random rand = new Random(seed(name));
		int diskNumber = rand.nextInt((11 - 1) + 1) + 1;
		switch(diskNumber) {
		case 1:
			this.disk = Material.GOLD_RECORD;
			this.color = ChatColor.GOLD;
			this.barColor = BarColor.YELLOW;
			break;
		case 2:
			this.disk = Material.GREEN_RECORD;
			this.color = ChatColor.GREEN;
			this.barColor = BarColor.GREEN;
			break;
		case 3:
			this.disk = Material.RECORD_3;
			this.color = ChatColor.RED;
			this.barColor = BarColor.RED;
			break;
		case 4:
			this.disk = Material.RECORD_4;
			this.color = ChatColor.DARK_RED;
			this.barColor = BarColor.RED;
			break;
		case 5:
			this.disk = Material.RECORD_5;
			this.color = ChatColor.AQUA;
			this.barColor = BarColor.GREEN;
			break;
		case 6:
			this.disk = Material.RECORD_6;
			this.color = ChatColor.DARK_PURPLE;
			this.barColor = BarColor.PURPLE;
			break;
		case 7:
			this.disk = Material.RECORD_7;
			this.color = ChatColor.LIGHT_PURPLE;
			this.barColor = BarColor.PINK;
			break;
		case 8:
			this.disk = Material.RECORD_8;
			this.color = ChatColor.DARK_GRAY;
			this.barColor = BarColor.BLUE;
			break;
		case 9:
			this.disk = Material.RECORD_9;
			this.color = ChatColor.WHITE;
			this.barColor = BarColor.WHITE;
			break;
		case 10:
			this.disk = Material.RECORD_10;
			this.color = ChatColor.DARK_GREEN;
			this.barColor = BarColor.GREEN;
			break;
		case 11:
			this.disk = Material.RECORD_12;
			this.color = ChatColor.DARK_AQUA;
			this.barColor = BarColor.BLUE;
			break;
		default:
			this.disk = Material.RECORD_11;
			this.color = ChatColor.GRAY;
			this.barColor = BarColor.BLUE;
			break;
		}
		this.song = NBSDecoder.parse(new File(ConfigOptions.songPath, file));
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
	
	private static long seed(String s) {
	    if (s == null) {
	        return 0;
	    }
	    long hash = 0;
	    for (char c : s.toCharArray()) {
	        hash = 31L*hash + c;
	    }
	    return hash;
	}

}
