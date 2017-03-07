package com.PromethiaRP.Draeke.PreVisit.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisit;
import com.PromethiaRP.Draeke.PreVisit.PreVisitPermissions;
import com.PromethiaRP.Draeke.PreVisit.Data.Position;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.ZoneManager;
import com.PromethiaRP.Draeke.PreVisit.Requirements.Requirement;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;
import com.PromethiaRP.Draeke.PreVisit.Utilities.RequirementManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.Wire;

@Wire
public class FastTravelCommand implements CommandExecutor {

	private ZoneManager zoneManager;
	private RequirementManager requirementManager;
	private PreVisit plugin;
	
//	public FastTravelCommand(PreVisit plugin, ZoneManager zone, RequirementManager requirement) {
//		this.zoneManager = zone;
//		this.requirementManager = requirement;
//		this.plugin = plugin;
//	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(args.length < 1){
			return false;
		}
		if(!(sender instanceof Player)){
			// Only players can teleport
			return false;
		}
		
		Player play = (Player) sender;
		if (! play.hasPermission(PreVisitPermissions.FastTravel)) {
			MessageDispatcher.commandFailPermission(play, command.getName());
		}
		String warpName = PreVisit.compileArgs(args, 0);
		
		if ( ! zoneManager.doesZoneExist(warpName)) {
			
			MessageDispatcher.warpNotFound(play, warpName);
			return true;
		}
		
		Zone zone = zoneManager.getZoneByName(warpName);
		
		Requirement req = requirementManager.getEffectiveRequirement(play, zone);
		if (req == null) {
			MessageDispatcher.teleportFailNoReason(play, warpName);
			return true;
		}
		
		if (req.willAllow(play, zone)) {
			req.sendAcceptMessage(play, zone);
			Position p = zoneManager.getZoneByName(warpName).getPosition();
			play.teleport(new Location(plugin.getWorld(p.getWorld()), p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch()));
		} else {
			req.sendDenyMessage(play, zone);
		}
		

		return true;

	}

}
