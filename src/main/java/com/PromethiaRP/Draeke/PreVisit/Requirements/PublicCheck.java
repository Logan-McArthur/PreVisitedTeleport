package com.PromethiaRP.Draeke.PreVisit.Requirements;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisit;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class PublicCheck implements Requirement {

	public PublicCheck() {
	}

	@Override
	public boolean willAllow(Player player, Zone zone) {
		return true;
	}

	@Override
	public void sendDenyMessage(Player player, Zone zone) {
		PreVisit.log("PublicCheck had sendDenyMessage called when it should not happen.");
	}

	@Override
	public void sendAcceptMessage(Player player, Zone zone) {
		MessageDispatcher.teleportPublicSuccess(player, zone.getName());
	}

	@Override
	public boolean isInterested(Player play, Zone zone) {
		return zone.isPublic();
	}
	
	
}
