package io.github.wolfleader116.wolfmusic.bukkit;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import io.github.wolfleader116.wolfapi.bukkit.CommandDescription;
import io.github.wolfleader116.wolfapi.bukkit.HelpSC;
import io.github.wolfleader116.wolfapi.bukkit.MainCommand;
import io.github.wolfleader116.wolfapi.bukkit.Message;
import io.github.wolfleader116.wolfapi.bukkit.WolfAPI;
import io.github.wolfleader116.wolfapi.bukkit.WolfPlugin;
import io.github.wolfleader116.wolfmusic.bukkit.commands.BrowseSC;
import io.github.wolfleader116.wolfmusic.bukkit.commands.PlaySC;
import io.github.wolfleader116.wolfmusic.bukkit.commands.ReloadSC;
import io.github.wolfleader116.wolfmusic.bukkit.commands.RepeatSC;
import io.github.wolfleader116.wolfmusic.bukkit.commands.ResetSC;
import io.github.wolfleader116.wolfmusic.bukkit.commands.StopSC;
import io.github.wolfleader116.wolfmusic.bukkit.completers.WolfMusicTC;
import io.github.wolfleader116.wolfmusic.bukkit.events.InventoryClickEH;
import io.github.wolfleader116.wolfmusic.bukkit.events.PlayerJoinLeaveEH;
import io.github.wolfleader116.wolfmusic.bukkit.events.SongEndEH;

public class WolfMusic extends WolfPlugin implements Listener {

	public static WolfPlugin plugin;
	public static Message message;
	
	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	
	private static ConfigOptions configOptions;
	
	protected static List<SongFile> songs = new ArrayList<SongFile>();
	private static Map<String, Integer> page = new HashMap<String, Integer>();
	
	@Override
	public void onEnable() {
		super.onEnable();
		this.saveDefaultConfig();
		plugin = this;
		configOptions = new ConfigOptions();
		ConfigOptions.updateConfig();
		message = new Message(this);
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		configOptions.reloadConfig();
		ConfigOptions.songPath.mkdir();
		WolfAPI.registerConfig(configOptions);
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		updateSongs();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
        Bukkit.getScheduler().cancelTasks(this);
	}
	
	public static ConfigOptions getConfigOptions() {
		return WolfMusic.configOptions;
	}

	private void initializeCommands() {
		this.getCommand("wolfmusic").setExecutor(mainCommand);
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("browse", new BrowseSC());
		subCommands.put("play", new PlaySC());
		subCommands.put("stop", new StopSC());
		subCommands.put("repeat", new RepeatSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("wolfmusic").setTabCompleter(new WolfMusicTC());
	}
	
	private void initializeListeners() {
		getServer().getPluginManager().registerEvents(new InventoryClickEH(), this);
		getServer().getPluginManager().registerEvents(new SongEndEH(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinLeaveEH(), this);
	}

	private void setCommandDescriptions() {
		commandDescriptions.add(new CommandDescription("wolfmusic", "Shows WolfMusic information", ""));
		commandDescriptions.add(new CommandDescription("wolfmusic help", "Shows WolfMusic commands", ""));
		commandDescriptions.add(new CommandDescription("wolfmusic reload", "Reloads WolfMusic config", "wolfmusic.reload"));
		commandDescriptions.add(new CommandDescription("wolfmusic reset", "Resets WolfMusic config", "wolfmusic.reset"));
		commandDescriptions.add(new CommandDescription("wolfmusic browse", "Opens a GUI of all songs", "wolfmusic.browse"));
		commandDescriptions.add(new CommandDescription("wolfmusic play", "Starts the player", "wolfmusic.play"));
		commandDescriptions.add(new CommandDescription("wolfmusic stop", "Stops the player", "wolfmusic.stop"));
		commandDescriptions.add(new CommandDescription("wolfmusic repeat", "Enables/disables repeat", "wolfmusic.repeat"));
	}
	
	public static SongFile[] getSongs() {
		return songs.toArray(new SongFile[songs.size()]);
	}
	
	public static void updateSongs() {
		List<SongFile> songs = new ArrayList<SongFile>();
		File file = ConfigOptions.songPath;
		String[] files = file.list();
		Collections.sort(Arrays.asList(files), Collator.getInstance());
		for (String string : files) {
			if (string.endsWith(".nbs")) {
				songs.add(new SongFile(string));
			}
		}
		WolfMusic.songs = songs;
	}
	
	public static Map<String, Integer> getPages() {
		return page;
	}
	
	public static int getPage(String p) {
		return page.get(p);
	}
	
	public static void removePage(String p) {
		page.remove(p);
	}
	
	public static void setPage(String p, int i) {
		page.put(p, i);
	}

}