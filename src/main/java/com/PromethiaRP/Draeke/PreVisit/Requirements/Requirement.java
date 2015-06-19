package com.PromethiaRP.Draeke.PreVisit.Requirements;

import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.Data.Zone;

public interface Requirement {
	
	/**
	 * Returns true if the Requirement will allow the player to access the zone.
	 * 
	 * @param player
	 * @param zone
	 * @return
	 */
	public boolean willAllow(Player player, Zone zone);
	
	/**
	 * Called when this requirement is the deciding factor and allows the player access.
	 * 
	 * @param player
	 * @param zone
	 */
	public void sendAcceptMessage(Player player, Zone zone);
	
	/**
	 * Called when this requirement is the deciding factor and denies the player access.
	 * 
	 * @param player
	 * @param zone
	 */
	public void sendDenyMessage(Player player , Zone zone);
	
	/** 
	 * Called before the Requirement is evaluated. Used to determine if the requirement is applicable to the situation.
	 * 
	 * @param play
	 * @param zone
	 * @return
	 */
	public boolean isInterested(Player play, Zone zone);
}
