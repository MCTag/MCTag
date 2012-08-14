package me.hawkfalcon.mctag;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Commands implements CommandExecutor{
	private MCTag plugin;
	private TheMethods method;
	public Commands(MCTag m, TheMethods me) {
		this.plugin = m;
		this.method = me;
	}

	//commands
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//tag
		if (cmd.getName().equalsIgnoreCase("tag")||cmd.getName().equalsIgnoreCase("mctag")) {
			if (sender instanceof Player){
				Player player = (Player) sender;
				boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
				//nothing
				if (args.length == 0){
					player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
					return true;
				}
				if (args.length == 1) {
					//start game
					if (args[0].equalsIgnoreCase("start")||args[0].equalsIgnoreCase("on")){
						if (sender.hasPermission("MCTag.start")) {
							//player is already it
							if (player.getName().equals(plugin.playerIt)) {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You are already it!");
							}
							//start game
							else {
								boolean freeze = plugin.getConfig().getBoolean("freeze_tag");
								//normal tag
								if (!freeze){
									//no games are on
									if ((!plugin.gameOn) || (plugin.startBool)){
										//not arena mode
										if (!arena_mode){
											int playersonline = Arrays.asList(plugin.getServer().getOnlinePlayers()).size();
											//more than 1 player on
											if (playersonline > 1){
												plugin.gameOn = true;
												plugin.startBool = false;
												plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of tag has begun!");
												method.selectPlayer();
											}
											//1 player
											else {
												player.sendMessage(ChatColor.RED + "There must be at least 2 people online to play tag");

											}
										}
										//arena mode
										else{
											plugin.gameOn = true;
											plugin.startBool = false;
											for (Player p : plugin.playersInGame) {
												p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of tag has begun! Type /tag join to join the game");
											}
											method.tagPlayer(player);
											method.joinPlayer(player);
										}
									}
									//game on already
									else {
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is already a game of tag started!");
									}
								}
								//freeze tag
								else if (freeze) {
									//no games are on
									if ((!plugin.gameOn) || (plugin.startBool)){
										if (!arena_mode){
											int playersonline = Arrays.asList(plugin.getServer().getOnlinePlayers()).size();
											//more then 2 people on
											if (playersonline > 2){
												plugin.gameOn = true;
												plugin.startBool = false;
												plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of freeze tag has begun!");
												method.selectPlayer();
											}
											//2- players
											else {
												player.sendMessage(ChatColor.RED + "There must be at least 3 people online to play freeze tag");
											}
										}
										//arena mode
										else {
											plugin.gameOn = true;
											plugin.startBool = false;
											for (Player p : plugin.playersInGame) {
												p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of freeze tag has begun!");
											}
											method.tagPlayer(player);
											method.joinPlayer(player);
										}
									}
									//game already on
									else {
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is already a game of tag started!");

									}
								}
								//this shouldn't happen, but just incase
								else {
									player.sendMessage("Error #102");

								}
							}
						} 
						//no permission
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//stop game
					if (args[0].equalsIgnoreCase("stop")||args[0].equalsIgnoreCase("off")){
						if (sender.hasPermission("MCTag.stop")) {
							//game is on
							if (plugin.gameOn){
								plugin.gameOn = false;
								plugin.playerIt = null;
								plugin.previouslyIt = null;
								plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "The game of tag has ended!");
								plugin.frozenPlayers.clear();
								plugin.playersInGame.clear();
							}
							//game is off
							else {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is no game to stop!");

							}
						} 
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;

					}
					//who is it
					if (args[0].equalsIgnoreCase("it")){
						if (sender.hasPermission("MCTag.it")) {
							//someone is it
							if (plugin.playerIt != null){
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + plugin.playerIt + " is currently it!");
							}
							//noone is it
							else {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD +  "Nobody is currently it!");

							}
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//who is it
					if (args[0].equalsIgnoreCase("setspawn")){
						if (sender.hasPermission("MCTag.setspawn")) {
							Location loc = player.getLocation();
							String location = (loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY() + "|" + loc.getZ());
							this.plugin.getConfig().set("spawn_location", location);
							this.plugin.saveConfig();				
							player.sendMessage(ChatColor.GOLD + "Spawn point set!");					}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//join game
					if (args[0].equalsIgnoreCase("join")){
						if (sender.hasPermission("MCTag.join")) {
							method.joinPlayer(player);
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//join game
					if (args[0].equalsIgnoreCase("leave")){
						if (sender.hasPermission("MCTag.leave")) {
							//arena mode on
							if (arena_mode){
								if (plugin.playersInGame.contains(player)){
									plugin.playersInGame.remove(player);
									for (Player p : plugin.playersInGame) {
										p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left the game!");
									}
									player.teleport(player.getWorld().getSpawnLocation());
								}
								//You are already in the game
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED +  "You are not in the game!");
								}
							}
							else {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED +  "Arena mode is off!");
							}
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//reload config
					if (args[0].equalsIgnoreCase("reload")){
						if (sender.hasPermission("MCTag.reload")) {
							this.plugin.reloadConfig();
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "Config reloaded");
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//Misspellings
					else {
						player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
					}
					return true;


				}
				if (args.length == 2) {
					//tagback toggle
					if (args[0].equalsIgnoreCase("tagback")){
						//tagback on
						if (args[1].equalsIgnoreCase("allow")||args[1].equalsIgnoreCase("on")){
							if (sender.hasPermission("MCTag.tagbackallow")) {
								boolean tagback = plugin.getConfig().getBoolean("allow_tagbacks");
								//tagbacks are off
								if (!tagback){
									this.plugin.getConfig().set("allow_tagbacks", true);
									this.plugin.saveConfig();								
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Tagbacks are now allowed!");
								}
								//tagbacks are on
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Tagbacks are already allowed!");
								}

							} 
							//no perms
							else {
								player.sendMessage(ChatColor.RED + "You don't have permission!");
							}
							return true;

						}
						//tagbacks off
						if (args[1].equalsIgnoreCase("forbid")||args[1].equalsIgnoreCase("off")){
							if (sender.hasPermission("MCTag.tagbackforbid")) {
								boolean tagback = plugin.getConfig().getBoolean("allow_tagbacks");
								//tagbacks are on
								if (tagback){
									this.plugin.getConfig().set("allow_tagbacks", false);
									this.plugin.saveConfig();								
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Tagbacks are now forbidden!");
								}
								//tagbacks are off
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Tagbacks are already forbidden!");

								}
							} 
							//no perms
							else {
								player.sendMessage(ChatColor.RED + "You don't have permission!");
							}
							return true;

						}
					}
					//freezetag toggle
					if (args[0].equalsIgnoreCase("freezetag")){
						//tagback on
						if (args[1].equalsIgnoreCase("on")){
							if (sender.hasPermission("MCTag.freezetagon")) {
								boolean freeze = plugin.getConfig().getBoolean("freeze_tag");
								//freezetag is off
								if (!freeze){
									this.plugin.getConfig().set("freeze_tag", true);
									this.plugin.saveConfig();								
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Freeze tag is now enabled!");
								}
								//freezetag is on
								else{
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Freeze tag is already on!");

								}
							} 
							//no perms
							else {
								player.sendMessage(ChatColor.RED + "You don't have permission!");
							}
							return true;

						}
						//freezetag off
						if (args[1].equalsIgnoreCase("off")){
							if (sender.hasPermission("MCTag.freezetagoff")) {
								boolean freeze = plugin.getConfig().getBoolean("freeze_tag");
								//freezetag is on
								if (freeze){
									this.plugin.getConfig().set("freeze_tag", false);
									this.plugin.saveConfig();								
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Freeze tag is now disabled!");
								}
								//freezetag is off
								else{
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Freeze tag is already off!");

								}
							} 
							//no perms
							else {
								player.sendMessage(ChatColor.RED + "You don't have permission!");
							}
							return true;
						}
						//misspelled
						else {
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
						}
					}
					//misspelled
					else {
						player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
					}
					return true;
				}
				//misspelled
				else {
					player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
				}
			}
			sender.sendMessage("Not from console. It would crash.");

			return true;

		}
		return true;

	}
}
