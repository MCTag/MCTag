package me.hawkfalcon.mctag;
//Made by: hawkfalcon. Feel free to use the code


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MCTag extends JavaPlugin implements Listener {
    Logger log;
	ArrayList<String> frozenPlayers = new ArrayList<String>();
	ArrayList<String> playersInGame = new ArrayList<String>();
	String playerIt = null;
	String previouslyIt = null;
	boolean gameOn = false;
	boolean startBool = true;
	String commands = "Commands: \n /tag <join|leave> - join or leave the game \n /tag <start|stop> - start and stop game \n /tag it - view tagged player \n /tag players - view joined playrs \n /tag tagback <allow|forbid> - allow and forbid tagback \n /tag freezetag <on|off> - turn freeze tag on and off \n /tag reload - reloads the config \n /tag setspawn - set arena spawnpoint";
	public FileConfiguration config;
	public CommandExecutor Commands = new Commands(this, new TheMethods(this));
	public Listener Tag = new Tag(this, new TheMethods(this));
	
	public void onEnable() {
		log = this.getLogger();
		log.info("MCTag has been enabled");
		getServer().getPluginManager().registerEvents(new Events(this, new TheMethods(this)), this);
		getServer().getPluginManager().registerEvents(Tag, this);
		getCommand("Tag").setExecutor(Commands);
		final File f = new File(getDataFolder(), "config.yml");
		if (!f.exists()){
			saveDefaultConfig();
		}
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
		gameOn = false;
		playerIt = null;
		previouslyIt = null;
		startBool = true;
	}

	public void onDisable() {
		log.info("MCTag has been disabled");
		gameOn = false;
		playerIt = null;
		previouslyIt = null;
	}
}