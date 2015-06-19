package com.PromethiaRP.Draeke.PreVisit.Requirements;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.PlayerManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class VisitedCheck implements Requirement {

	PlayerManager playerManager;
	
	public VisitedCheck(PlayerManager manager) {
		playerManager = manager;
	}

	@Override
	public boolean willAllow(Player player, Zone zone) {
		return playerManager.hasVisited(player, zone.getName());
	}

	@Override
	public void sendDenyMessage(Player player, Zone zone) {
		MessageDispatcher.teleportFailNotVisited(player, zone.getName());
	}
	
	@Override
	public void sendAcceptMessage(Player player, Zone zone) {
		MessageDispatcher.teleportVisitSuccess(player, zone.getName());
	}

	@Override
	public boolean isInterested(Player play, Zone zone) {
		return ! zone.isPublic();
	}

}
