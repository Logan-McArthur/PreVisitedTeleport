package com.PromethiaRP.Draeke.PreVisit.Requirements;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisit;
import com.PromethiaRP.Draeke.PreVisit.PreVisitPermissions;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.EnergyManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class EnergyCheck implements Requirement {

	private EnergyManager energyManager;
	
	public EnergyCheck(EnergyManager manager) {
		energyManager = manager;
	}

	@Override
	public boolean willAllow(Player player, Zone zone) {
		return energyManager.getEnergy(player) >= energyManager.getRequiredEnergy(player.getLocation(), zone);
	}

	@Override
	public void sendDenyMessage(Player player, Zone zone) {
		MessageDispatcher.teleportFailEnergy(player, zone.getName(), energyManager.getRequiredEnergy(player.getLocation(), zone), energyManager.getEnergy(player));
	}
	
	@Override
	public void sendAcceptMessage(Player player, Zone zone) {
		PreVisit.log("EnergyCheck called to sendAcceptMessage when it should not be.");
	}

	@Override
	public boolean isInterested(Player play, Zone zone) {
		return play.hasPermission(PreVisitPermissions.UseEnergy);
	}

}
