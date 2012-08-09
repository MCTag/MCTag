package me.hawkfalcon.mctag;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Tag{
	MCTag mctag = new MCTag();
	TheMethods method = new TheMethods();
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onTag(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
			Player damager = (Player) event.getDamager();
			Player player = (Player) event.getEntity();
			boolean tagback = mctag.getConfig().getBoolean("allow_tagbacks");
			boolean freeze = mctag.getConfig().getBoolean("freeze_tag");
			boolean airInHand = mctag.getConfig().getBoolean("air_in_hand_to_tag");
			boolean tag_damage = mctag.getConfig().getBoolean("damage_from_tagger");
			if (damager.getName().equals(mctag.playerIt)) {
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
							if (player.getName().equals(mctag.previouslyIt)) {
								damager.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "No tagbacks!");
								if (tag_damage == false) {
									event.setCancelled(true);
								}
							}
							//normal
							else {
								method.tagPlayer(player);
								mctag.previouslyIt = damager.getName();
								if (tag_damage == false) {
									event.setCancelled(true);
								}

							}
						}
					}

					//freezetag
					else if (freeze == true){
						//player is not already frozen
						if (!mctag.frozenPlayers.contains(player.getName())){
							method.freezePlayer(player);
							int theAmount = Arrays.asList(mctag.getServer().getOnlinePlayers()).size();
							int playersingame = mctag.playersInGame.size();
							int playersFrozen = mctag.frozenPlayers.size();
							boolean arena_mode = mctag.getConfig().getBoolean("arena_mode");
							//everyone's frozen
							if (!arena_mode){
								//everyones frozen
								if (playersFrozen == theAmount-1){
									mctag.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + mctag.playerIt + " has won the game of freeze tag!");
									mctag.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Randomly selecting next player to be it!");
									mctag.frozenPlayers.clear();
									method.rewardPlayer(damager);
									method.selectPlayer();
								}
								else {
									//everyones frozen
									if (playersFrozen == playersingame-1){
										for (Player p : mctag.playersInGame) {
											p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + mctag.playerIt + " has won the game of freeze tag!");
											p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Randomly selecting next player to be it!");
											mctag.frozenPlayers.clear();
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
					else if (!damager.getName().equals(mctag.playerIt)){
						//is person hit frozen?
						boolean arena_mode = mctag.getConfig().getBoolean("arena_mode");
						//frozen?
						if (mctag.frozenPlayers.contains(player.getName())){
							//not arena mode
							if (!arena_mode) {
								mctag.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player.getName() + " is unfrozen!");
								mctag.frozenPlayers.remove(player.getName());
							}
							//arena mode
							else {
								for (Player p : mctag.playersInGame) {
									if (mctag.playersInGame.contains(damager)){
									p.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player.getName() + " is unfrozen!");
									mctag.frozenPlayers.remove(player.getName());
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
