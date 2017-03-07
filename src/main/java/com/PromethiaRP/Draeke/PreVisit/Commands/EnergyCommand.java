package com.PromethiaRP.Draeke.PreVisit.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.PromethiaRP.Draeke.PreVisit.PreVisitPermissions;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.EnergyManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;
import com.PromethiaRP.Draeke.PreVisit.Utilities.Wire;

@Wire
public class EnergyCommand implements CommandExecutor{

	private EnergyManager energyManager;
	
//	public EnergyCommand(EnergyManager energy) {
//		this.energyManager = energy;
//	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!(sender instanceof Player)){
			return false;
		}
		if(args.length > 1 && args[0].equalsIgnoreCase("giveall")) {
			
			if( sender.hasPermission(PreVisitPermissions.GiveEnergy)){
				int amount = Integer.parseInt(args[1]);
				MessageDispatcher.energyGiveAll(sender, amount);
				
				energyManager.giveEnergyToAll(amount);
				
				return true;
			}
			
		}
		
		MessageDispatcher.energyLevelSelf(sender, energyManager.getEnergy((Player)sender));
		return true;
	}

}
