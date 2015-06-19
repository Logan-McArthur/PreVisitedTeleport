package com.PromethiaRP.Draeke.PreVisit.Utilities;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.*;
import com.PromethiaRP.Draeke.PreVisit.Requirements.*;

public class RequirementManager {

	private PlayerManager playerManager;
	private ZoneManager zoneManager;
	private EnergyManager energyManager;
	
	private Set<Requirement> overrides;
	private Set<Requirement> needs;
	private Set<Requirement> checks;
	
	public RequirementManager(PlayerManager playerManager, ZoneManager zoneManager, EnergyManager energyManager) {
		this.playerManager = playerManager;
		this.zoneManager = zoneManager;
		this.energyManager = energyManager;
		
		overrides = new HashSet<Requirement>();
		needs = new HashSet<Requirement>();
		checks = new HashSet<Requirement>();
		
		constructRequirements();
	}
	
	private void constructRequirements() {
		overrides.add(new AllWarpsOverride());
		
		needs.add(new EnergyCheck(energyManager));
		needs.add(new WorldCheck());

		checks.add(new PublicCheck());
		checks.add(new VisitedCheck(playerManager));
		
	}
	
	private Requirement getAllowingRequirement(Set<Requirement> set, Player player, Zone zone) {
		return getEffectiveRequirement(set, true, player, zone);
	}
	
	private Requirement getDenyingRequirement(Set<Requirement> set, Player player, Zone zone) { 
		return getEffectiveRequirement(set, false, player, zone);
	}
	
	private Requirement getEffectiveRequirement(Set<Requirement> set, boolean shouldAccept, Player player, Zone zone) {
		Requirement result = null;
		for (Requirement rqmt : set) {
			if (rqmt.isInterested(player, zone)) {
				if ( rqmt.willAllow(player, zone) == shouldAccept ) {
					result = rqmt;
				}
			}
		}
		
		return result;
	}
	
	// Should be be (Any Override) OR ( (All needs) AND (Any check) )
	//				AllWarps, etc		Energy, World		Public, Visited
	
	public boolean canFastTravel(Player player, Zone zone) {
		
		Requirement effective = this.getEffectiveRequirement(player, zone);
		if (effective != null) {
			boolean result = effective.willAllow(player, zone);
			
			if (result) {
				effective.sendAcceptMessage(player, zone);
			} else {
				effective.sendDenyMessage(player, zone);
			}
			
			return result;
		}
		
		return false;
	}
	
	public Requirement getEffectiveRequirement(Player player, Zone zone) {

		Requirement override = getAllowingRequirement(overrides, player, zone);
		if (override != null) {
			// There is a Requirement that is overriding everything else
			
			return override;
		}
		
		// The Process has not been overridden, so keep going as nothing has changed
		
		Requirement need = getDenyingRequirement(needs, player, zone);
		if (need != null) {
			// Not all of the needs have been met
			
			return need;
		}
		
		// The needs have been met, now the player must at least one of a few checks
		
		Requirement check = getAllowingRequirement(checks, player, zone);
		if (check != null) {
			// The player has passed one of the checks
			
			check.sendAcceptMessage(player, zone);
			return check;
		}
		
		// check is null, the player has not passed any
		// the player can not teleport
		
		return null;
	}
}
