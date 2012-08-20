package me.hawkfalcon.mctag;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class TheMethods{
	private MCTag plugin;
	public TheMethods(MCTag m) {
    this.plugin = m;
    }
	//selects player randomly
	public void selectPlayer(){
		List<Player> players = Arrays.asList(plugin.getServer().getOnlinePlayers());
		int theSize = players.size();
		Random random = new Random();
		String theNextString = players.get(random.nextInt(theSize)).getName();
		//try again
		if (theNextString == plugin.playerIt) {
			selectPlayer();
		}
		//tag player
		else {
			tagPlayer(theNextString);

		}
	}
	public void startGameWith(String player){
		joinPlayer(player);
		tagPlayer(player);
	}
	public void selectPlayerFromArena(){
		List<Player> players = Arrays.asList(plugin.getServer().getOnlinePlayers());
		int theSize = plugin.playersInGame.size();
		Random random = new Random();
		String theNextString = players.get(random.nextInt(theSize)).getName();
		//try again
		if (theNextString == plugin.playerIt) {
			selectPlayerFromArena();
		}
		//tag player
		else {
			tagPlayerFromArena(theNextString);

		}
	}
	//rewards diamonds
	public void rewardPlayer(String playerstring) {
		Player player = Bukkit.getPlayer(playerstring);
		boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
		int amount = this.plugin.getConfig().getInt("diamond_amount");
		if (!arena_mode) {
			player.getInventory().addItem(new ItemStack(Material.DIAMOND, amount));
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "You have recieved " + amount +" diamonds as a reward for winning freeze tag!");
		}
		else {
			if (plugin.playersInGame.size() > 2) {
				player.getInventory().addItem(new ItemStack(Material.DIAMOND, amount));
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "You have recieved " + amount +" diamonds as a reward for winning freeze tag!");
			}
			else {
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There were not enough players in the game to recieve an award!");
			}
		}
	}

	//tags player
	public void tagPlayer(String player) {
		plugin.playerIt = player;
		plugin.playerIt = player;
		boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
		//arena mode
		if (arena_mode){
			for (String p : plugin.playersInGame) {
				Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
			}
		}
		//not arena mode
		else {
			plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
		}
		//smoke!
		for (int i = 0; i <= 8; i++)
			Bukkit.getPlayer(player).getWorld().playEffect(Bukkit.getPlayer(player).getLocation(), Effect.SMOKE, i);
	}
	public void tagPlayerFromArena(String player) {
		if (plugin.playersInGame.contains(player)){
			plugin.playerIt = player;
			plugin.playerIt = player;
			for (String p : plugin.playersInGame) {
				Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
			}
			//smoke!
			for (int i = 0; i <= 8; i++)
				Bukkit.getPlayer(player).getWorld().playEffect(Bukkit.getPlayer(player).getLocation(), Effect.SMOKE, i);
		}
	}

	//freezes player
	public void freezePlayer(String player) {
		boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
		//player is not already frozen
		if (!plugin.frozenPlayers.contains(player)){
			plugin.frozenPlayers.add(player);
			for (int i = 0; i <= 8; i++) {
				Bukkit.getPlayer(player).getWorld().playEffect(Bukkit.getPlayer(player).getLocation(), Effect.SMOKE, i);
			}
			if (!arena_mode){
				plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player + " is now frozen!");
			}
			else {
				for (String p : plugin.playersInGame) {
					Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player + " is now frozen!");
				}
			}
			
		}
	}
	public void teleportPlayer(String player) {
		String location = plugin.getConfig().getString("spawn_location");
		String[] loc = location.split("\\|");

	    World world = Bukkit.getWorld(loc[0]);
		Double x = Double.parseDouble(loc[1]);
		Double y = Double.parseDouble(loc[2]);
		Double z = Double.parseDouble(loc[3]);
		 
		final Location spawnpoint = new Location(world, x, y, z);
		Bukkit.getPlayer(player).teleport(spawnpoint);

	}
	public void joinPlayer(String player) {
		//arena mode on
		boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
		if (arena_mode){
			if (!plugin.playersInGame.contains(player)){
				
				for (String p : plugin.playersInGame) {
					Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + player + " is now in the game!");
				}
				teleportPlayer(player);
				plugin.playersInGame.add(player);
			
			}
			//You are already in the game
			else {
				Bukkit.getPlayer(player).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED +  "You are already in the game!");
			}
		}
		else {
			Bukkit.getPlayer(player).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED +  "Arena mode is off!");
		}		
	}
	public void gameOff() {
	plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "The game of tag has ended!");
    cleanUp();
	}
	public void cleanUp() {
		plugin.gameOn = false;
		plugin.playerIt = null;
		plugin.previouslyIt = null;
		plugin.frozenPlayers.clear();
		plugin.playersInGame.clear();	
	}
}
