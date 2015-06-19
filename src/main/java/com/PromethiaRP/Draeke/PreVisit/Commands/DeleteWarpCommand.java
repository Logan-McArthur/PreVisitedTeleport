package com.PromethiaRP.Draeke.PreVisit.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.PromethiaRP.Draeke.PreVisit.PreVisit;
import com.PromethiaRP.Draeke.PreVisit.PreVisitPermissions;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.PlayerManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.ZoneManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;

public class DeleteWarpCommand implements CommandExecutor {

	private ZoneManager zoneManager;
	private PlayerManager playerManager;
	private PreVisit plugin;
	
	public DeleteWarpCommand(PreVisit plugin, ZoneManager zone, PlayerManager player) {
		zoneManager = zone;
		playerManager = player;
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		

		if ( args.length < 1 ) {
			return false;
		}
		
		if( sender.hasPermission(PreVisitPermissions.DeleteWarp) ) {
			String nam = PreVisit.compileArgs(args,0);

			if( zoneManager.deleteZone(nam) ) {
				playerManager.removeZone(nam);
				
				MessageDispatcher.deleteWarpSuccess(sender, nam);
				
				//TODO: Store Here					
				plugin.saveToFile();
			} else {
				MessageDispatcher.deleteWarpFailure(sender, nam);
			}
			return true;
		}
		return false;
	}

}
