package io.github.pseudoresonance.pseudomusic;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import io.github.pseudoresonance.pseudoapi.bukkit.CommandDescription;
import io.github.pseudoresonance.pseudoapi.bukkit.HelpSC;
import io.github.pseudoresonance.pseudoapi.bukkit.MainCommand;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater;
import io.github.pseudoresonance.pseudoapi.bukkit.data.PluginConfig;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.Column;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudomusic.commands.BrowseSC;
import io.github.pseudoresonance.pseudomusic.commands.PlaySC;
import io.github.pseudoresonance.pseudomusic.commands.ReloadSC;
import io.github.pseudoresonance.pseudomusic.commands.RepeatSC;
import io.github.pseudoresonance.pseudomusic.commands.ResetSC;
import io.github.pseudoresonance.pseudomusic.commands.ShuffleSC;
import io.github.pseudoresonance.pseudomusic.commands.StopSC;
import io.github.pseudoresonance.pseudomusic.completers.PseudoMusicTC;
import io.github.pseudoresonance.pseudomusic.events.InventoryClickEH;
import io.github.pseudoresonance.pseudomusic.events.PlayerJoinLeaveEH;
import io.github.pseudoresonance.pseudomusic.events.SongEndEH;

public class PseudoMusic extends PseudoPlugin implements Listener {

	public static PseudoMusic plugin;
	public static Message message;
	
	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	
	private static Config config;
	
	protected static List<SongFile> songs = new ArrayList<SongFile>();
	private static Map<String, Integer> page = new HashMap<String, Integer>();
	
	public void onLoad() {
		PseudoUpdater.registerPlugin(this);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		this.saveDefaultConfig();
		plugin = this;
		PlayerDataController.addColumn(new Column("musicPlaying", "BIT", "1"));
		PlayerDataController.addColumn(new Column("musicShuffle", "BIT", "0"));
		PlayerDataController.addColumn(new Column("musicRepeat", "BIT", "0"));
		PlayerDataController.addColumn(new Column("musicSong", "SMALLINT(5) UNSIGNED", "0"));
		PlayerDataController.addColumn(new Column("musicPosition", "SMALLINT(5)", "0"));
		PlayerDataController.addColumn(new Column("musicContinue", "BIT", "0"));
		config = new Config(this);
		config.updateConfig();
		message = new Message(this);
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		config.reloadConfig();
		Config.songPath.mkdir();
		PseudoAPI.registerConfig(config);
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		doAsync(() -> {
			updateSongs();
			doSync(() -> {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					JukeboxController.connect(p);
				}
				PlayerJoinLeaveEH.initComplete();
			});
		});
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
        Bukkit.getScheduler().cancelTasks(this);
	}
	
	public static PluginConfig getConfigOptions() {
		return PseudoMusic.config;
	}

	private void initializeCommands() {
		this.getCommand("pseudomusic").setExecutor(mainCommand);
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("browse", new BrowseSC());
		subCommands.put("play", new PlaySC());
		subCommands.put("stop", new StopSC());
		subCommands.put("repeat", new RepeatSC());
		subCommands.put("shuffle", new ShuffleSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudomusic").setTabCompleter(new PseudoMusicTC());
	}
	
	private void initializeListeners() {
		getServer().getPluginManager().registerEvents(new InventoryClickEH(), this);
		getServer().getPluginManager().registerEvents(new SongEndEH(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinLeaveEH(), this);
	}

	private void setCommandDescriptions() {
		commandDescriptions.add(new CommandDescription("pseudomusic", "Shows PseudoMusic information", ""));
		commandDescriptions.add(new CommandDescription("pseudomusic help", "Shows PseudoMusic commands", ""));
		commandDescriptions.add(new CommandDescription("pseudomusic reload", "Reloads PseudoMusic config", "pseudomusic.reload"));
		commandDescriptions.add(new CommandDescription("pseudomusic reset", "Resets PseudoMusic config", "pseudomusic.reset"));
		commandDescriptions.add(new CommandDescription("pseudomusic browse", "Opens a GUI of all songs", "pseudomusic.browse"));
		commandDescriptions.add(new CommandDescription("pseudomusic play", "Starts the player", "pseudomusic.play"));
		commandDescriptions.add(new CommandDescription("pseudomusic stop", "Stops the player", "pseudomusic.stop"));
		commandDescriptions.add(new CommandDescription("pseudomusic repeat", "Enables/disables repeat", "pseudomusic.repeat"));
		commandDescriptions.add(new CommandDescription("pseudomusic shuffle", "Enables/disables shuffle", "pseudomusic.shuffle"));
	}
	
	public void doSync(Runnable run) {
		Bukkit.getScheduler().runTask(this, run);
	}
	
	public void doAsync(Runnable run) {
		Bukkit.getScheduler().runTaskAsynchronously(this, run);
	}
	
	public static SongFile[] getSongs() {
		return songs.toArray(new SongFile[songs.size()]);
	}
	
	public static void updateSongs() {
		long start = System.nanoTime();
		message.sendConsolePluginMessage("Loading music...");
		List<SongFile> songs = new ArrayList<SongFile>();
		File file = Config.songPath;
		String[] files = file.list();
		Collections.sort(Arrays.asList(files), Collator.getInstance());
		for (String string : files) {
			if (string.endsWith(".nbs")) {
				songs.add(new SongFile(string));
			}
		}
		PseudoMusic.songs = songs;
		long diff = System.nanoTime() - start;
		diff /= 1000000;
		message.sendConsolePluginMessage("Done loading music! Operation took " + diff + "ms to load " + songs.size() + " songs!");
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