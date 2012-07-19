package me.hawkfalcon.mctag;
//Made by: hawkfalcon. Feel free to use the code


import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MCTag extends JavaPlugin implements Listener, CommandExecutor {
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		final File f = new File(getDataFolder(), "config.yml");
		if (!f.exists()){
			saveDefaultConfig();
		}
	}

	public void onDisable() {
	}
	String playerIt = null;
	String previouslyIt = null;

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("Tag")) {
			if (args.length == 0){
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GREEN + "usage: /tag [start|stop|tagback (allow|forbid)]");
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("start")){
					if (sender.hasPermission("MCTag.start")) {
						if (player.getName().equals(playerIt)) {
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You are already it!");
						}
						else {
							getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of tag has begun!");
							tagPlayer(player);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You don't have permission!");
					}
				}
				if (args[0].equalsIgnoreCase("stop")){
					if (sender.hasPermission("MCTag.stop")) {
						playerIt = null;
						previouslyIt = null;
						getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "The game of tag has ended!");
					} else {
						sender.sendMessage(ChatColor.RED + "You don't have permission!");
					}
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("tagback")){
					if (args[1].equalsIgnoreCase("allow")){
						if (sender.hasPermission("MCTag.tagbackallow")) {
							this.getConfig().set("allow_tagbacks", true);
							this.saveConfig();								
							getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Tagbacks are now allowed!");
						} else {
							sender.sendMessage(ChatColor.RED + "You don't have permission!");
						}
					}
					if (args[1].equalsIgnoreCase("forbid")){
						if (sender.hasPermission("MCTag.tagbackforbid")) {
							this.getConfig().set("allow_tagbacks", false);
							this.saveConfig();								
							getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Tagbacks are now forbidden!");
						} else {
							sender.sendMessage(ChatColor.RED + "You don't have permission!");
						}
					}
				}
			}
		}
		else {
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GREEN + "usage: /tag [start|stop|tagback (allow|forbid)]");

		}
		return true;
	}

	private void tagPlayer(Player player) {
		playerIt = player.getName();
		getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player.getName() + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
		for (int i = 0; i <= 8; i++)
			player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
	}


	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
			Player damager = (Player) event.getDamager();
			Player player = (Player) event.getEntity();
			boolean tagback = getConfig().getBoolean("allow_tagbacks");

			if (damager.getName().equals(playerIt) && damager.getItemInHand().getType() == Material.AIR) {

				if (tagback == true) {
					tagPlayer(player);
				}
				else {
					if (player.getName().equals(previouslyIt)) {
						damager.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "No tagbacks!");
					}

					else {
						tagPlayer(player);
						previouslyIt = damager.getName();

					}
				}
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getPlayer().getName().equals(playerIt)) {
			event.setCancelled(true);
			Player tele = (Player) event.getPlayer();
			tele.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't teleport while you are it!");
		}

	}
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		if (event.getPlayer().getName().equals(playerIt)) {
			getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + playerIt + " has left, the game of tag has ended!");
			playerIt = null;
			previouslyIt = null;

		}
	}



}
