package com.PromethiaRP.Draeke.PreVisit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.EnergyManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.PlayerManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.ZoneManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class PlayerMovementListener implements Listener {

	
	private PlayerManager playerManager;
	private EnergyManager energyManager;
	private ZoneManager zoneManager;
	
	public PlayerMovementListener(PlayerManager player, EnergyManager energy, ZoneManager zone) {
		this.playerManager = player;
		this.energyManager = energy;
		this.zoneManager = zone;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player play = event.getPlayer();
		
		if ( ! playerManager.isTrackingPlayer(play)) {
			playerManager.trackPlayer(play.getUniqueId());
			energyManager.trackPlayer(play.getUniqueId());
			
		}
		
		Zone zone = zoneManager.getContainingZone(play.getLocation());
		if (zone == null) {
			return;
		}
		
		if(playerManager.addZoneToPlayer(play, zone.getName())){
			if (zone.isPublic()) {
				MessageDispatcher.discoverPublicWarp(play, zone.getName());
			} else {
				MessageDispatcher.discoverRegularWarp(play, zone.getName());
			}
		}
		
	}
}
