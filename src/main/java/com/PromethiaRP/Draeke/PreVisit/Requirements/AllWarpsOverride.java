package com.PromethiaRP.Draeke.PreVisit.Requirements;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisitPermissions;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class AllWarpsOverride implements Requirement {

	public AllWarpsOverride() {
		
	}
	
	public void sendAcceptMessage(Player player, Zone zone) {
		MessageDispatcher.teleportAdminSuccess(player, zone.getName());
	}

	@Override
	public boolean willAllow(Player player, Zone zone) {
		return true;
	}

	@Override
	public void sendDenyMessage(Player player, Zone zone) {
		
	}

	@Override
	public boolean isInterested(Player play, Zone zone) {
		return play.hasPermission(PreVisitPermissions.AllWarps);
	}

}
