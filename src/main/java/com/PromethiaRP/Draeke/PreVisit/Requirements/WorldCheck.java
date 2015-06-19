package com.PromethiaRP.Draeke.PreVisit.Requirements;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisitPermissions;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class WorldCheck implements Requirement {

	public WorldCheck() {
	}

	@Override
	public boolean willAllow(Player player, Zone zone) {
		
		return player.getLocation().getWorld().getName().equalsIgnoreCase(zone.getPosition().getWorld());
	}

	@Override
	public void sendDenyMessage(Player player, Zone zone) {
		MessageDispatcher.teleportFailWorldChange(player, zone.getName());
	}

	@Override
	public void sendAcceptMessage(Player player, Zone zone) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInterested(Player play, Zone zone) {
		return ! play.hasPermission(PreVisitPermissions.WorldChange);
	}

}
