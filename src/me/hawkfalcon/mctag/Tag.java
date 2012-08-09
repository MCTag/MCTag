package me.hawkfalcon.mctag;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Tag implements Listener{
	public MCTag plugin;
	public Tag(MCTag m) {
    this.plugin = m;
    }	
	TheMethods method = new TheMethods();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onTag(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
			Player damager = (Player) event.getDamager();
			Player player = (Player) event.getEntity();
			boolean tagback = plugin.getConfig().getBoolean("allow_tagbacks");
			boolean freeze = plugin.getConfig().getBoolean("freeze_tag");
			boolean airInHand = plugin.getConfig().getBoolean("air_in_hand_to_tag");
			boolean tag_damage = plugin.getConfig().getBoolean("damage_from_tagger");
			if (damager.getName().equals(plugin.playerIt)) {
				//check if player holds air or air mode is off
				if ((damager.getItemInHand().getType() == Material.AIR) || (airInHand == false)){
					//normal tag
					if (freeze == false) {
						//tagbacks on
						if (tagback == true) {
							method.tagPlayer(player);
							if (tag_damage == false) {
								event.setCancelled(true);
							}						}

						//tagback off
						else {
							//previouslyit
							if (player.getName().equals(plugin.previouslyIt)) {
								damager.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "No tagbacks!");
								if (tag_damage == false) {
									event.setCancelled(true);
								}
							}
							//normal
							else {
								method.tagPlayer(player);
								plugin.previouslyIt = damager.getName();
								if (tag_damage == false) {
									event.setCancelled(true);
								}

							}
						}
					}

					//freezetag
					else if (freeze == true){
						//player is not already frozen
						if (!plugin.frozenPlayers.contains(player.getName())){
							method.freezePlayer(player);
							int theAmount = Arrays.asList(plugin.getServer().getOnlinePlayers()).size();
							int playersingame = plugin.playersInGame.size();
							int playersFrozen = plugin.frozenPlayers.size();
							boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
							//everyone's frozen
							if (!arena_mode){
								//everyones frozen
								if (playersFrozen == theAmount-1){
									plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + plugin.playerIt + " has won the game of freeze tag!");
									plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Randomly selecting next player to be it!");
									plugin.frozenPlayers.clear();
									method.rewardPlayer(damager);
									method.selectPlayer();
								}
								else {
									//everyones frozen
									if (playersFrozen == playersingame-1){
										for (Player p : plugin.playersInGame) {
											p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + plugin.playerIt + " has won the game of freeze tag!");
											p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Randomly selecting next player to be it!");
											plugin.frozenPlayers.clear();
										    method.rewardPlayer(damager);
											method.selectPlayerFromArena();
										}
									}

								}
								if (tag_damage == false) {
									event.setCancelled(true);
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
					else if (!damager.getName().equals(plugin.playerIt)){
						//is person hit frozen?
						boolean arena_mode = plugin.getConfig().getBoolean("arena_mode");
						//frozen?
						if (plugin.frozenPlayers.contains(player.getName())){
							//not arena mode
							if (!arena_mode) {
								plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player.getName() + " is unfrozen!");
								plugin.frozenPlayers.remove(player.getName());
							}
							//arena mode
							else {
								for (Player p : plugin.playersInGame) {
									if (plugin.playersInGame.contains(damager)){
									p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player.getName() + " is unfrozen!");
									plugin.frozenPlayers.remove(player.getName());
									}
								}
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
						if (tag_damage == false) {
							event.setCancelled(true);
						}

					}
				}
			}
		}
	}
}
