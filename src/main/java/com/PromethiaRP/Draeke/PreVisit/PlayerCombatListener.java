package com.PromethiaRP.Draeke.PreVisit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.PromethiaRP.Draeke.PreVisit.DataManagers.CombatManager;

public class PlayerCombatListener implements Listener {

	CombatManager combatManager;
	
	public PlayerCombatListener(CombatManager combat) {
		combatManager = combat;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent e){
		if(e.isCancelled()){
			return;
		}
		if(!(e.getEntity() instanceof Player)){
			return;
		}
		
		Player play = (Player) e.getEntity();
		
		combatManager.track(play.getUniqueId());
		
	}
}
