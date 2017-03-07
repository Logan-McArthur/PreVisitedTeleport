package com.PromethiaRP.Draeke.PreVisit.Requirements;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisitPermissions;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.CombatManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class CombatCheck implements Requirement {

	private CombatManager combatManager;
	
	public CombatCheck(CombatManager combat) {
		combatManager = combat;
	}
	
	@Override
	public boolean willAllow(Player player, Zone zone) {
		return combatManager.isTracking(player.getUniqueId());
	}

	@Override
	public void sendAcceptMessage(Player player, Zone zone) {
		// TODO: CombatCheck Accept Message
	}

	@Override
	public void sendDenyMessage(Player player, Zone zone) {
		MessageDispatcher.teleportFailInCombat(player, zone.getName(), combatManager.getRemainingTime(player.getUniqueId()));
	}

	@Override
	public boolean isInterested(Player play, Zone zone) {
		return play.hasPermission(PreVisitPermissions.CombatWait);
	}

}
