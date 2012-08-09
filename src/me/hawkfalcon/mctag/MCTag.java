package me.hawkfalcon.mctag;
//Made by: hawkfalcon. Feel free to use the code


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MCTag extends JavaPlugin implements Listener {

	Set<String> frozenPlayers = new HashSet<String>();
	List<Player> playersInGame = new ArrayList<Player>();
	String playerIt = null;
	Player playerIsit = null;
	String previouslyIt = null;
	boolean gameOn = false;
	boolean startBool = true;
	String commands = "Commands: \n /tag <join|leave> - join or leave the game \n /tag <start|stop> - start and stop game \n /tag it - view tagged player \n /tag tagback <allow|forbid> - allow and forbid tagback \n /tag freezetag <on|off> - turn freeze tag on and off \n /tag reload - reloads the config \n /tag setspawn - set arena spawnpoint";

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
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
		playerIsit = null;
		startBool = true;
	}

	public void onDisable() {
		gameOn = false;
		playerIt = null;
		previouslyIt = null;
		playerIsit = null;
	}
}