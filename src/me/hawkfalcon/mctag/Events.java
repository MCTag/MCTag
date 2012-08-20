package me.hawkfalcon.mctag;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
//import org.bukkit.event.player.PlayerTeleportEvent;

public class Events implements Listener{
	private MCTag plugin;
	private TheMethods method;
	public Events(MCTag m, TheMethods me) {
		this.plugin = m;
		this.method = me;
	}

	/*//stop teleporting
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (plugin.gameOn){
			//check config
			boolean notele = plugin.getConfig().getBoolean("tagger_can_teleport");
			boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
			boolean noarenatele = plugin.getConfig().getBoolean("teleport_in_arena");

			if  (!notele || event.getPlayer().hasPermission("MCTag.notele.bypass")){
				if (!arena_mode){
					if (event.getPlayer().getName().equals(plugin.playerIt)) {
						event.setCancelled(true);
						Player tele = (Player) event.getPlayer();
						tele.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't teleport while you are it!");
					}
				}
				else {
						if (event.getPlayer().getName().equals(plugin.playerIt)){
								if (!plugin.frozenPlayers.contains(event.getPlayer())) {
							event.setCancelled(true);
							Player tele = (Player) event.getPlayer();
							tele.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't teleport while you are it!");
						}
					
					}
				}
			}
			if  (!noarenatele || event.getPlayer().hasPermission("MCTag.notele.bypass")){
				if (arena_mode){
					if (!event.getPlayer().getName().equals(plugin.playerIt)) {
						event.setCancelled(true);
						Player tele = (Player) event.getPlayer();
						tele.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't teleport in the arena!");
					
					}
				}

			}

		}
	}*/
	
	//if player quits
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		//if its the player who is it
		if (event.getPlayer().getName().equals(plugin.playerIt)) {
			boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
			String player = event.getPlayer().getName();
			//arena
			if (arena_mode) {
				if (plugin.playersInGame.size() < 1){
				for (String p : plugin.playersInGame) {
					Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left, randomly selecting next person to be it!");
				}
				method.selectPlayerFromArena();
			}
				else {
					method.gameOff();
				}
				if (plugin.playersInGame.contains(player)){
					plugin.playersInGame.remove(player);
					for (String p : plugin.playersInGame) {
						Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left the game!");
					}
				}
			}
			//not arena
			else {
				if (Arrays.asList(plugin.getServer().getOnlinePlayers()).size() < 1){

				plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left, randomly selecting next person to be it!");
				method.selectPlayer();
			}
				else {
                    method.gameOff();
				}
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
		if (!commands || event.getPlayer().hasPermission("MCTag.nocommands.bypass")){
			if (arena_mode) {
				if (!event.getMessage().toLowerCase().startsWith("/tag")) {
				if (plugin.playersInGame.contains(event.getPlayer().getName())){
					//no commands
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't use commands in the arena!");
				}
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
	public void onPlayerDeath(PlayerRespawnEvent event) {
		String player = event.getPlayer().getName();
		if (plugin.playersInGame.contains(player)){
        method.teleportPlayer(player);
		}
	}

}
