package com.PromethiaRP.Draeke.PreVisit;

import org.bukkit.entity.Player;

public class TeleportationManager {
	private ZoneManager zoneManager;
	private PlayerManager playerManager;
	private EnergyManager energyManager;
	public TeleportationManager(ZoneManager zones, PlayerManager players, EnergyManager energies) {
		this.zoneManager = zones;
		this.playerManager = players;
		this.energyManager = energies;
	}
	
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