package io.github.pseudoresonance.pseudomusic;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import io.github.pseudoresonance.pseudoapi.bukkit.CommandDescription;
import io.github.pseudoresonance.pseudoapi.bukkit.HelpSC;
import io.github.pseudoresonance.pseudoapi.bukkit.MainCommand;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater;
import io.github.pseudoresonance.pseudoapi.bukkit.data.PluginConfig;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
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

	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;

	private static Config config;

	protected static List<SongFile> songs = new ArrayList<SongFile>();
	private static Map<String, Integer> page = new HashMap<String, Integer>();

	private static Metrics metrics = null;

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
				initializeMetrics();
			});
		});
	}

	@Override
	public void onDisable() {
		super.onDisable();
		Bukkit.getScheduler().cancelTasks(this);
	}

	private void initializeMetrics() {
		metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("num_songs", () -> {
			int i = songs.size() / 20 * 20;
			return i + "-" + (i + 20);
		}));
		metrics.addCustomChart(new Metrics.SimplePie("player_type", () -> {
			switch (Config.playerType) {
			case GLOBAL:
				return "Global";
			case PRIVATE:
			default:
				return "Private";
			}
		}));
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
		commandDescriptions.add(new CommandDescription("pseudomusic", "pseudomusic.pseudomusic_help", ""));
		commandDescriptions.add(new CommandDescription("pseudomusic help", "pseudomusic.pseudomusic_help_help", ""));
		commandDescriptions.add(new CommandDescription("pseudomusic reload", "pseudomusic.pseudomusic_reload_help", "pseudomusic.reload"));
		commandDescriptions.add(new CommandDescription("pseudomusic reloadlocalization", "pseudomusic.pseudomusic_reloadlocalization_help", "pseudomusic.reloadlocalization"));
		commandDescriptions.add(new CommandDescription("pseudomusic reset", "pseudomusic.pseudomusic_reset_help", "pseudomusic.reset"));
		commandDescriptions.add(new CommandDescription("pseudomusic resetlocalization", "pseudomusic.pseudomusic_resetlocalization_help", "pseudomusic.resetlocalization"));
		commandDescriptions.add(new CommandDescription("pseudomusic browse", "pseudomusic.pseudomusic_browse_help", "pseudomusic.browse"));
		commandDescriptions.add(new CommandDescription("pseudomusic play", "pseudomusic.pseudomusic_play_help", "pseudomusic.play"));
		commandDescriptions.add(new CommandDescription("pseudomusic stop", "pseudomusic.pseudomusic_stop_help", "pseudomusic.stop"));
		commandDescriptions.add(new CommandDescription("pseudomusic repeat", "pseudomusic.pseudomusic_repeat_help", "pseudomusic.repeat"));
		commandDescriptions.add(new CommandDescription("pseudomusic shuffle", "pseudomusic.pseudomusic_shuffle_help", "pseudomusic.shuffle"));
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
		plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudomusic.loading_music"));
		List<SongFile> songs = new ArrayList<SongFile>();
		File file = Config.songPath;
		String[] files = file.list();
		Collections.sort(Arrays.asList(files), Collator.getInstance());
		int failedCounter = 0;
		for (String string : files) {
			if (string.endsWith(".nbs")) {
				try {
					SongFile sf = new SongFile(string);
					songs.add(sf);
				} catch (RuntimeException e) {
					failedCounter++;
				}
			}
		}
		PseudoMusic.songs = songs;
		long diff = System.nanoTime() - start;
		diff /= 1000000;
		plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudomusic.done_loading_music", diff, songs.size()));
		if (failedCounter > 0)
			plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudomusic.failed_to_load", io.github.pseudoresonance.pseudoapi.bukkit.Config.errorTextColor + failedCounter));
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