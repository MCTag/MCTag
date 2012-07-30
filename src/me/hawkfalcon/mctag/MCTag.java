package me.hawkfalcon.mctag;
//Made by: hawkfalcon. Feel free to use the code


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MCTag extends JavaPlugin implements Listener, CommandExecutor {
	Set<String> frozenPlayers = new HashSet<String>();
	String playerIt = null;
	Player playerIsit = null;
	String previouslyIt = null;
	boolean gameOn = false;
	boolean startBool = true;
	String commands = "Commands: \n /tag <start|stop> - start and stop game \n /tag it - view tagged player \n /tag tagback <allow|forbid> - allow and forbid tagback \n /tag freezetag <on|off> - turn freeze tag on and off \n /tag reload - reloads the config";

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

	//commands
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("tag")||cmd.getName().equalsIgnoreCase("mctag")) {
			if (args.length == 0){
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + commands);
				return true;
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("start")||args[0].equalsIgnoreCase("on")){
					if (sender.hasPermission("MCTag.start")) {
						if (player.getName().equals(playerIt)) {
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You are already it!");
						}
						else {
							boolean freeze = getConfig().getBoolean("freeze_tag");
							if (!freeze){
								if ((!gameOn) || (startBool)){
									int playersonline = Arrays.asList(this.getServer().getOnlinePlayers()).size();
									if (playersonline > 1){
										gameOn = true;
										startBool = false;
										getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of tag has begun!");
										selectPlayer();
									}
									else{
										player.sendMessage(ChatColor.RED + "There must be at least 2 people online to play tag");
									}
								}
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is already a game of tag started!");
								}
							}

							else if (freeze) {
								if ((!gameOn) || (startBool)){
									int playersonline = Arrays.asList(this.getServer().getOnlinePlayers()).size();
									if (playersonline > 2){
										gameOn = true;
										startBool = false;
										getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of freeze tag has begun!");
										selectPlayer();
									}
									else {
										player.sendMessage(ChatColor.RED + "There must be at least 3 people online to play freeze tag");
									}
								}
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is already a game of tag started!");

								}
							}
							else {
								getServer().broadcastMessage("Error #102");

							}
						}
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permission!");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("stop")||args[0].equalsIgnoreCase("off")){
					if (sender.hasPermission("MCTag.stop")) {
						if (gameOn == true){
							gameOn = false;
							playerIt = null;
							previouslyIt = null;
							getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "The game of tag has ended!");
							frozenPlayers.clear();
						}
						else {
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is no game to stop!");

						}
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permission!");
					}
					return true;

				}
				if (args[0].equalsIgnoreCase("it")){
					if (sender.hasPermission("MCTag.it")) {
						if (playerIt != null){
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + playerIt + " is currently it!");
						}
						else {
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD +  "Nobody is currently it!");

						}
					}
					else {
						player.sendMessage(ChatColor.RED + "You don't have permission!");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("reload")){
					if (sender.hasPermission("MCTag.reload")) {
						this.reloadConfig();
						player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "Config reloaded");
					}

					else {
						player.sendMessage(ChatColor.RED + "You don't have permission!");
					}
					return true;
				}

				else {
					player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + commands);
				}
				return true;

			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("tagback")){
					if (args[1].equalsIgnoreCase("allow")||args[1].equalsIgnoreCase("on")){
						if (sender.hasPermission("MCTag.tagbackallow")) {
							boolean tagback = getConfig().getBoolean("allow_tagbacks");
							if (tagback == false){
								this.getConfig().set("allow_tagbacks", true);
								this.saveConfig();								
								getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Tagbacks are now allowed!");
							}
							else {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Tagbacks are already allowed!");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;

					}
					if (args[1].equalsIgnoreCase("forbid")||args[1].equalsIgnoreCase("off")){
						if (sender.hasPermission("MCTag.tagbackforbid")) {
							boolean tagback = getConfig().getBoolean("allow_tagbacks");
							if (tagback == true){
								this.getConfig().set("allow_tagbacks", false);
								this.saveConfig();								
								getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Tagbacks are now forbidden!");
							}
							else {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Tagbacks are already forbidden!");

							}
						} else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;

					}
				}

				if (args[0].equalsIgnoreCase("freezetag")){
					if (args[1].equalsIgnoreCase("on")){
						if (sender.hasPermission("MCTag.freezetagon")) {
							boolean freeze = getConfig().getBoolean("freeze_tag");
							if (freeze == false){
								this.getConfig().set("freeze_tag", true);
								this.saveConfig();								
								getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Freeze tag is now enabled!");
							}
							else{
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Freeze tag is already on!");

							}
						} else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;

					}
					if (args[1].equalsIgnoreCase("off")){
						if (sender.hasPermission("MCTag.freezetagoff")) {
							boolean freeze = getConfig().getBoolean("freeze_tag");
							if (freeze == true){
								this.getConfig().set("freeze_tag", false);
								this.saveConfig();								
								getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Freeze tag is now disabled!");
							}
							else{
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Freeze tag is already off!");

							}
						} else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					else {
						player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + commands);
					}
				}
				else {
					player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + commands);
				}
				return true;
			}
			else {
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + commands);
			}
		}

		return true;

	}

	//Private Methods

	private void selectPlayer(){
		List<Player> players = Arrays.asList(this.getServer().getOnlinePlayers());
		int theSize = players.size();
		Random random = new Random();
		Player theNextPlayer = players.get(random.nextInt(theSize));
		if (theNextPlayer == playerIsit) {
			selectPlayer();
		}
		else {
			tagPlayer(theNextPlayer);

		}
	}

	private void rewardPlayer(Player player) {
		int amount = getConfig().getInt("diamond_amount");
		player.getInventory().addItem(new ItemStack(Material.DIAMOND, amount));
		player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "You have recieved " + amount +" diamonds as a reward for winning freeze tag!");
	}

	private void tagPlayer(Player player) {
		playerIt = player.getName();
		playerIsit = player;
		getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player.getName() + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
		for (int i = 0; i <= 8; i++)
			player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
	}

	private void freezePlayer(Player player) {
		String frozen = player.getName();
		if (!frozenPlayers.contains(player.getName())){
			getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player.getName() + " is now frozen!");
			frozenPlayers.add(frozen);
			for (int i = 0; i <= 8; i++)
				player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
		}
		else {
			getServer().broadcastMessage("Error #104");
		}
	}

	//event listeners

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onTag(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
			Player damager = (Player) event.getDamager();
			Player player = (Player) event.getEntity();
			boolean tagback = getConfig().getBoolean("allow_tagbacks");
			boolean freeze = getConfig().getBoolean("freeze_tag");
			boolean airInHand = getConfig().getBoolean("air_in_hand_to_tag");
			boolean tag_damage = getConfig().getBoolean("damage_from_tagger");
			if (damager.getName().equals(playerIt)) {
				//check if player holds air or air mode is off
				if ((damager.getItemInHand().getType() == Material.AIR) || (airInHand == false)){
					//normal tag
					if (freeze == false) {
						//tagbacks on
						if (tagback == true) {
							tagPlayer(player);
							if (tag_damage == false) {
								event.setCancelled(true);
							}						}

						//tagback off
						else {
							//previouslyit
							if (player.getName().equals(previouslyIt)) {
								damager.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "No tagbacks!");
								if (tag_damage == false) {
									event.setCancelled(true);
								}
							}
							//normal
							else {
								tagPlayer(player);
								previouslyIt = damager.getName();
								if (tag_damage == false) {
									event.setCancelled(true);
								}

							}
						}
					}

					//freezetag
					else if (freeze == true){
						//player is not already frozen
						if (!frozenPlayers.contains(player.getName())){
							freezePlayer(player);
							int theAmount = Arrays.asList(this.getServer().getOnlinePlayers()).size();
							int playersFrozen = frozenPlayers.size();
							//everyone's frozen
							if (playersFrozen == theAmount-1){
								getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + playerIt + " has won the game of freeze tag!");
								getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Randomly selecting next player to be it!");
								frozenPlayers.clear();
								rewardPlayer(damager);
								selectPlayer();
							}
							if (tag_damage == false) {
								event.setCancelled(true);
							}
						}
					}
					//if anything goes wrong
					else {
						if (airInHand == true){
							damager.sendMessage(ChatColor.RED + "You must have air in your hand to tag somebody");
						}

					}
				}
			}
			//unfreeze
			else if (!damager.getName().equals(playerIt)){
				//is person hit frozen?
				if (frozenPlayers.contains(player.getName())){
					getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player.getName() + " is unfrozen!");
					frozenPlayers.remove(player.getName());
					if (tag_damage == false) {
						event.setCancelled(true);
					}
				}
			}
			//if anything goes wrong
			else {
				if (airInHand == true){
					damager.sendMessage(ChatColor.RED + "You must have air in your hand to tag somebody");
				}
				if (tag_damage == false) {
					event.setCancelled(true);
				}

			}
		}
	}


	//stop teleporting
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (gameOn == true){
			//check config
			boolean notele = getConfig().getBoolean("tagger_can_teleport");
			if  (notele == false){
				if (event.getPlayer().getName().equals(playerIt)) {
					event.setCancelled(true);
					Player tele = (Player) event.getPlayer();
					tele.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't teleport while you are it!");
				}
			}
		}
	}
	//if player quits
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		//if its the player who is it
		if (event.getPlayer().getName().equals(playerIt)) {
			getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + playerIt + " has left, the game of tag has ended!");
			playerIt = null;
			previouslyIt = null;
			gameOn = false;
		}
	}
	//freeze player
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) 
	public void freezePlayers(PlayerMoveEvent event){
		boolean freeze = getConfig().getBoolean("freeze_tag");
		//is it freeze tag?
		if (freeze == true){
			if (frozenPlayers.contains(event.getPlayer().getName())){

				Block fromBlock = event.getFrom().getBlock();
				Block toBlock = event.getTo().getBlock();
				if (fromBlock.equals(toBlock)) {
					return;
				}
				if (!(fromBlock.getX() == toBlock.getX() && fromBlock.getZ() == toBlock.getZ())) {
					event.getPlayer().teleport(fromBlock.getLocation());
					event.setCancelled(true);
				}
			}
		}
	}
	//stop placing while frozen
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		String player = event.getPlayer().getName();
		if (frozenPlayers.contains(player)) {
			event.setCancelled(true);
		}
	}
	//stop breaking while frozen
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		String player = event.getPlayer().getName();
		if (frozenPlayers.contains(player)) {
			event.setCancelled(true);
		}
	}

}