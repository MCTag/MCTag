package me.hawkfalcon.mctag;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Events{
	MCTag mctag = new MCTag();
	TheMethods method = new TheMethods();

	//stop teleporting
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (mctag.gameOn){
			//check config
			boolean notele = mctag.getConfig().getBoolean("tagger_can_teleport");
			if  (!notele){
				if (event.getPlayer().getName().equals(mctag.playerIt)) {
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
		if (event.getPlayer().getName().equals(mctag.playerIt)) {
			boolean arena_mode = mctag.getConfig().getBoolean("arena_mode");
			//arena
			if (arena_mode) {
				for (Player p : mctag.playersInGame) {
					p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + mctag.playerIt + " has left, randomly selecting next person to be it!");
				}
				method.selectPlayerFromArena();

			}
			//not arena
			else {
				mctag.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + mctag.playerIt + " has left, randomly selecting next person to be it!");
				method.selectPlayer();
			}
		}
		else if (mctag.playersInGame.contains(event.getPlayer())){
			mctag.playersInGame.remove(event.getPlayer());
		}
	}
	//freeze player
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) 
	public void freezePlayers(PlayerMoveEvent event){
		boolean freeze = mctag.getConfig().getBoolean("freeze_tag");
		//is it freeze tag?
		if (freeze){
			//get frozen players
			if (mctag.frozenPlayers.contains(event.getPlayer().getName())){
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
	//no commands
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) 
	public void nocommands(PlayerCommandPreprocessEvent event){
		boolean commands = mctag.getConfig().getBoolean("commands_in_arena");
		boolean arena_mode = mctag.getConfig().getBoolean("arena_mode");
		//check config?
		if (!commands){
			if (arena_mode) {
				//no commands
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't use commands in the arena!");

			}
		}
	}
	//stop placing while frozen
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		String player = event.getPlayer().getName();
		if (mctag.frozenPlayers.contains(player)) {
			event.setCancelled(true);
		}
	}
	//stop breaking while frozen
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		String player = event.getPlayer().getName();
		if (mctag.frozenPlayers.contains(player)) {
			event.setCancelled(true);
		}
	}

}
