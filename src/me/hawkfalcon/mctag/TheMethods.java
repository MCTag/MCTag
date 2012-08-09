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
	MCTag mctag = new MCTag();

	//public Methods
	//selects player randomly
	public void selectPlayer(){
		List<Player> players = Arrays.asList(mctag.getServer().getOnlinePlayers());
		int theSize = players.size();
		Random random = new Random();
		Player theNextPlayer = players.get(random.nextInt(theSize));
		//try again
		if (theNextPlayer == mctag.playerIsit) {
			selectPlayer();
		}
		//tag player
		else {
			tagPlayer(theNextPlayer);

		}
	}
	public void selectPlayerFromArena(){
		int theSize = mctag.playersInGame.size();
		Random random = new Random();
		Player theNextPlayer = mctag.playersInGame.get(random.nextInt(theSize));
		//try again
		if (theNextPlayer == mctag.playerIsit) {
			selectPlayerFromArena();
		}
		//tag player
		else {
			tagPlayerFromArena(theNextPlayer);

		}
	}
	//rewards diamonds
	public void rewardPlayer(Player player) {
		boolean arena_mode = mctag.getConfig().getBoolean("arena_mode");
		int amount = mctag.getConfig().getInt("diamond_amount");
		if (!arena_mode) {
			player.getInventory().addItem(new ItemStack(Material.DIAMOND, amount));
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "You have recieved " + amount +" diamonds as a reward for winning freeze tag!");
		}
		else {
			if (mctag.playersInGame.size() > 2) {
				player.getInventory().addItem(new ItemStack(Material.DIAMOND, amount));
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "You have recieved " + amount +" diamonds as a reward for winning freeze tag!");
			}
			else {
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There were not enough players in the game to recieve an award!");
			}
		}
	}

	//tags player
	public void tagPlayer(Player player) {
		mctag.playerIt = player.getName();
		mctag.playerIsit = player;
		boolean arena_mode = mctag.getConfig().getBoolean("arena_mode");
		//arena mode
		if (arena_mode){
			for (Player p : mctag.playersInGame) {
				p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player.getName() + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
			}
		}
		//not arena mode
		else {
			mctag.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player.getName() + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
		}
		//smoke!
		for (int i = 0; i <= 8; i++)
			player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
	}
	public void tagPlayerFromArena(Player player) {
		if (mctag.playersInGame.contains(player)){
			mctag.playerIt = player.getName();
			mctag.playerIsit = player;
			for (Player p : mctag.playersInGame) {
				p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player.getName() + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
			}
			//smoke!
			for (int i = 0; i <= 8; i++)
				player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
		}
	}

	//freezes player
	public void freezePlayer(Player player) {
		boolean arena_mode = mctag.getConfig().getBoolean("arena_mode");
		String frozen = player.getName();
		//player is not already frozen
		if (!mctag.frozenPlayers.contains(player.getName())){
			if (!arena_mode){
				mctag.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player.getName() + " is now frozen!");
			}
			else {
				for (Player p : mctag.playersInGame) {
					p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player.getName() + " is now frozen!");
				}
			}
			mctag.frozenPlayers.add(frozen);
			for (int i = 0; i <= 8; i++)
				player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
		}
	}
	public void joinPlayer(Player player) {
		//arena mode on
		boolean arena_mode = mctag.getConfig().getBoolean("arena_mode");
		if (arena_mode){
			if (!mctag.playersInGame.contains(player)){
				mctag.playersInGame.add(player);
				String location = mctag.getConfig().getString("spawn_location");
				String[] loc = location.split("\\|");

			    World world = Bukkit.getWorld(loc[0]);
				Double x = Double.parseDouble(loc[1]);
				Double y = Double.parseDouble(loc[2]);
				Double z = Double.parseDouble(loc[3]);
				 
				final Location spawnpoint = new Location(world, x, y, z);
				for (Player p : mctag.getServer().getOnlinePlayers()) {
					if (p.getLocation().getWorld().getName().equalsIgnoreCase(world.getName())) {
					p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + mctag.playerIt + " is now in the game!");
				}
					player.teleport(spawnpoint);
			}
			}
			//You are already in the game
			else {
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED +  "You are already in the game!");
			}
		}
		else {
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED +  "Arena mode is off!");
		}		
	}

}
