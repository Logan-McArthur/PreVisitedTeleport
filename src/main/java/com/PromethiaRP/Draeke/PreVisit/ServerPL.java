package com.PromethiaRP.Draeke.PreVisit;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * The listener class meant to handle all of the events and manage the Zones
 * @author Draeke_Forther
 *
 */
@Deprecated
public class ServerPL implements Listener {

	public PreVisit plugin;
	
	private static ArrayList<String> coolDown = new ArrayList<String>();

	public static final boolean COMBAT_WAIT = false;
	
	public ServerPL(PreVisit pv) {
		plugin = pv;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent e){
		if ( ! COMBAT_WAIT) {
			return;
		}
		if(e.isCancelled()){
			return;
		}
		if(!(e.getEntity() instanceof Player)){
			return;
		}
		if(!((Player)e.getEntity()).hasPermission("previsit.combatwait")){
			return;
		}
		if(coolDown.contains(((Player)e.getEntity()).getName())){
			coolDown.remove(((Player)e.getEntity()).getName());
			
		}
		coolDown.add(((Player)e.getEntity()).getName());	
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

			@Override
			public void run() {
				if(!ServerPL.coolDown.isEmpty()){
					ServerPL.coolDown.remove(0);
				}
			}
			
		}, 100L);
	}
	

}
