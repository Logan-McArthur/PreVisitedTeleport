package com.PromethiaRP.Draeke.PreVisit.Commands;

import java.util.Iterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisit;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.EnergyManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.ZoneManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;
import com.PromethiaRP.Draeke.PreVisit.Utilities.RequirementManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.WarpList;

public class WarpsCommand implements CommandExecutor{

	private ZoneManager zoneManager;
	private EnergyManager energyManager;
	private RequirementManager requirementManager;
	
	public WarpsCommand(ZoneManager zone, EnergyManager energy, RequirementManager requirement) {
		this.zoneManager = zone;
		this.energyManager = energy;
		this.requirementManager = requirement;
	}
	
	public void onCommandWarpsInfo(Player play, String warpName) {

		Zone warp = zoneManager.getZoneByName(warpName);
		
		if ( warp == null) {
			MessageDispatcher.warpNotFound(play, warpName);
		} else {
			int requiredEnergy = energyManager.getRequiredEnergy(play.getLocation(), warp);
			MessageDispatcher.energyRequiredToTeleport(play, warpName, requiredEnergy);
		}
		
	}
	
	public void onCommandWarps(Player play) {
		
		WarpList list = getWarps(play);

		if (list.warpNames.length == 0) {
			MessageDispatcher.warpsNoneAvailable(play);
		} else {
			MessageDispatcher.warpsList(play, list);
		}
	}
	
	private WarpList getWarps(Player player){

		String[] warpNames = new String[zoneManager.size()];
		boolean[] accessibleWarps = new boolean[zoneManager.size()];
		Iterator<Zone> iter = zoneManager.getIterator();
		Zone zon = null;
		int i = 0;
		while ( iter.hasNext() ) {
			zon = iter.next();
			warpNames[i] = zon.getName();
			accessibleWarps[i] = requirementManager.canFastTravel(player, zon);
//			accessibleWarps[i] = teleportManager.getAccessibility(player, zon).getResult();
			i += 1;
		}
		return new WarpList(warpNames, accessibleWarps);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(command.getName().equalsIgnoreCase("warps")){
			
			if(args.length > 0){
				onCommandWarpsInfo((Player)sender, PreVisit.compileArgs(args, 0));
			} else {
				onCommandWarps((Player)sender);
			}
			return true;
			
		}
		
		return false;
	}
}
