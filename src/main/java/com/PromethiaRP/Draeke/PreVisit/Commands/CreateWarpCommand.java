package com.PromethiaRP.Draeke.PreVisit.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisit;
import com.PromethiaRP.Draeke.PreVisit.PreVisitPermissions;
import com.PromethiaRP.Draeke.PreVisit.Data.Position;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.ZoneManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class CreateWarpCommand implements CommandExecutor {

	private ZoneManager zoneManager;
	private PreVisit plugin;
	
	
	public CreateWarpCommand(PreVisit plugin, ZoneManager zone) {
		zoneManager = zone;
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			return false;
		}
		if(((Player)sender).hasPermission(PreVisitPermissions.CreateWarp)){
			if( args.length < 1 ) {
				// Nothing specified, at least give name for public warp
				return false;
			}
			// Can either be 	/svwarp 10 Name Goes Here
			// Or 				/svwarp Name Goes Here
			int compileStartIndex = 0;
			int radius = -1;
			try {
				radius = Integer.parseInt(args[0]);
				compileStartIndex = 1;
			} catch (NumberFormatException e) {
				compileStartIndex = 0;
			}
			
			String zoneName = PreVisit.compileArgs(args, compileStartIndex);
			
			boolean createResult;
			if (compileStartIndex == 0) {
				// Public warp
				createResult = zoneManager.createZone(Position.convertPosition( ((Player)sender).getLocation()), zoneName);
			} else {
				createResult = zoneManager.createZone(Position.convertPosition( ((Player)sender).getLocation()), zoneName, radius);
			}
			
			if (createResult) {
				MessageDispatcher.createWarpSuccess(sender, zoneName);
				plugin.saveToFile();
			} else {
				MessageDispatcher.createWarpFailure(sender, zoneName);
			}
			
			return true;
		}
		return false;
	}
}
