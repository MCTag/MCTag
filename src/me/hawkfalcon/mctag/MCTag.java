package me.hawkfalcon.mctag;
//Made by: hawkfalcon. Feel free to use the code


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
		getCommand("Tag").setExecutor(this);
	}

	public void onDisable() {
	}

	String playerIt = null;

	private void tagPlayer(Player player) {
		playerIt = player.getName();
		getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + player.getName() + ChatColor.MAGIC + "+" + ChatColor.DARK_GREEN + " is now it!");
		for (int i = 0; i <= 8; i++)
			player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("Tag")) {
			if (sender.hasPermission("MCTag.Tag")) {
				if (player.getName().equals(playerIt)) {
					player.sendMessage(ChatColor.RED + "You are already it!");
				}
				else {
					getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of tag has begun!");
					tagPlayer(player);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission!");
			}

		}
		if (cmd.getName().equalsIgnoreCase("Tagend")) {
			if (sender.hasPermission("MCTag.Tagend")) {
				playerIt = null;
				getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "The game of tag has ended!");
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission!");
			}
		}
		return true;
	}



	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
			Player damager = (Player) event.getDamager();
			Player player = (Player) event.getEntity();
			if (damager.getName().equals(playerIt))
				if (damager.getItemInHand().getType() == Material.AIR) {
					tagPlayer(player);
				}
		}
	}
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getPlayer().getName().equals(playerIt)) {
			event.setCancelled(true);
			Player tele = (Player) event.getPlayer();
			tele.sendMessage(ChatColor.RED + "You can't teleport while you are it!");
		}
	}
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		if (event.getPlayer().getName().equals(playerIt)) {
			playerIt = null;
			getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + playerIt + "has left, the game of tag has ended!");
		}
	}
	

}
