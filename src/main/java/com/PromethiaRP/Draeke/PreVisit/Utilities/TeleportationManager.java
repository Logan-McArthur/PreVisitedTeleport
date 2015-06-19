package com.PromethiaRP.Draeke.PreVisit.Utilities;

import java.util.Set;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.EnergyManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.PlayerManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.ZoneManager;
import com.PromethiaRP.Draeke.PreVisit.Requirements.Requirement;

public class TeleportationManager {
	private ZoneManager zoneManager;
	private PlayerManager playerManager;
	private EnergyManager energyManager;
	
	private Set<Requirement> overrides;	// 
	private Set<Requirement> checks;
	public TeleportationManager(ZoneManager zones, PlayerManager players, EnergyManager energies) {
		this.zoneManager = zones;
		this.playerManager = players;
		this.energyManager = energies;
	}
	
	// TODO: Reworking the requirements
	// Having different types: Override and Check
	// Each type would have access to the data it needs when it's created.
	// Would work like if (Override.allow(player, zone) || Override.allow(player, zone) || (Check.allow(player, zone) && Check.allow(player, zone))
	// Should each know how to send the appropriate message to the player?
	
	// Some examples
	// class AllWarpsOverride extends Override { public boolean allow(Player player, Zone zone) { return player.hasPermission("previsit.allwarps");}}
	// class EnergyCheck extends Check { public boolean allow(Player player, Zone zone) { return needsEnergy && hasEnoug
	
	// Those examples are no good
	// Use permissions to determine if the Override/Check should be applied in the first case
	// if ( AllWarpsOverride.isInterested(Player player) { do AllWarpsOverride.allow(player, zone); }
	
	// The Requirements should not store anything that is unique to a specific situation.
	
	// There should be 3 sets of requirements: Overriding, Restricting, and Requiring
	// Overriding send messages upon true
	// Restricting sends messages upon false
	// Requiring sends messages upon true???
	// 		maybe false and true?
	
	// 3 sets, Preliminary, Secondary, and Tertiary?
	// Preliminary are the Overriding ones, they are for specific situations and overrule all others.
	// Secondary are the non-zone-specific requirements. Things such as having enough energy or traveling across worlds.
	// Tertiary are the player and zone specific requirements. Things such as the zone being public or the player has visited it.
	// Messages are given priority to the Preliminary that return true, then the Secondary that return false, then the Tertiary
	
	// How would I make something to have unique requirements per player per zone?
	public Accessibility getAccessibility(Player player, Zone zone) {
		boolean visited = playerManager.hasVisited(player, zone.getName());
		
		boolean publicZone = zone.isPublic();
		boolean playerAllWarps = player.hasPermission("previsit.allwarps");
		
		if (playerAllWarps) {	// Ops only
			return Accessibility.SUCCEED_ADMIN;
		}
		
		if (! teleportLocationOkay(player, zone)) {
			return Accessibility.FAIL_WORLD_CHANGE;
		}
		
		if ( ! playerEnergyRequirement(player, zone)) {
			return Accessibility.FAIL_ENERGY_LEVEL;
		}
		
		// Zone is not public, and player has not visited
		if ( ! publicZone && !visited) {	// Both false
			return Accessibility.FAIL_NOT_VISITED;
		}
		
		// TODO: Try having it be: if (publicZone) return .....;
		// And also if (visited) return ......;
		// And then finally FAIL_NOT_VISITED;
		// One is true
		if (publicZone) {
			return Accessibility.SUCCEED_PUBLIC;
		}
		return Accessibility.SUCCEED_VISITED;
	}
	
	private boolean isTeleportAcrossWorlds(Player player, Zone zone) {
		return !player.getWorld().getName().equalsIgnoreCase(zone.getPosition().getWorld());
	}
	private boolean teleportLocationOkay(Player player, Zone zone) {
		boolean changeAllowed = false;
		boolean changeAttempt = isTeleportAcrossWorlds(player, zone); 
		// if attempt and allow = okay
		// if attempt and not allow = bad
		// if not attempt and not allow = okay
		// if not attempt and allow = okay
		if (changeAttempt) {
			return changeAllowed;
		}
		return true;
	}
	
	private boolean playerEnergyRequirement(Player player, Zone zone) {
		// if do not have enough, return false if player must use energy
		if ( ! playerHasEnoughEnergy(player, zone)) {
			return ! player.hasPermission("previsit.useenergy");
		}
		// Player has enough energy
		return true;
	}
	
	private boolean playerHasEnoughEnergy(Player player, Zone zone) {
		return energyManager.getEnergy(player) >= energyManager.getRequiredEnergy(player.getLocation(), zone);
	}
}