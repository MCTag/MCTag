package me.hawkfalcon.mctag;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Events implements Listener{
	private MCTag plugin;
	private TheMethods method;
	public Events(MCTag m, TheMethods me) {
    this.plugin = m;
    this.method = me;
    }

	//stop teleporting
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (plugin.gameOn){
			//check config
			boolean notele = plugin.getConfig().getBoolean("tagger_can_teleport");
			if  (!notele){
				if (event.getPlayer().getName().equals(plugin.playerIt)) {
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
		if (event.getPlayer().getName().equals(plugin.playerIt)) {
			boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
			//arena
			if (arena_mode) {
				for (Player p : plugin.playersInGame) {
					p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left, randomly selecting next person to be it!");
				}
				method.selectPlayerFromArena();

			}
			//not arena
			else {
				plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left, randomly selecting next person to be it!");
				method.selectPlayer();
			}
		}
		else if (plugin.playersInGame.contains(event.getPlayer())){
			plugin.playersInGame.remove(event.getPlayer());
		}
	}
	//freeze player
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) 
	public void freezePlayers(PlayerMoveEvent event){
		boolean freeze = plugin.getConfig().getBoolean("freeze_tag");
		//is it freeze tag?
		if (freeze){
			//get frozen players
			if (plugin.frozenPlayers.contains(event.getPlayer().getName())){
				Block fromBlock = event.getFrom().getBlock();
				Block toBlock = event.getTo().getBlock();
				if (!(fromBlock.getX() == toBlock.getX() && fromBlock.getZ() == toBlock.getZ())) {
					event.getPlayer().teleport(fromBlock.getLocation());
					event.setCancelled(true);
				}
			}
		}
	}
	//no commands
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) 
	public void nocommands(PlayerCommandPreprocessEvent event){
		boolean commands = plugin.getConfig().getBoolean("commands_in_arena");
		boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
		//check config?
		if (!commands){
			if (arena_mode) {
				if (plugin.playersInGame.contains(event.getPlayer())){
				//no commands
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't use commands in the arena!");
				}
			}
		}
	}
	//stop placing while frozen
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		String player = event.getPlayer().getName();
		if (plugin.frozenPlayers.contains(player)) {
			event.setCancelled(true);
		}
	}
	//stop breaking while frozen
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		String player = event.getPlayer().getName();
		if (plugin.frozenPlayers.contains(player)) {
			event.setCancelled(true);
		}
	}

}
