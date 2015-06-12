package com.PromethiaRP.Draeke.PreVisit;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class file in the PreVisitedTeleport plugin designed for use with bukkit
 * 
 * @author Draeke_Forther
 * @version 2.1.0
 * 
 */
public class PreVisit extends JavaPlugin {

	//TODO: Move the messaging stuff back to the ServerPL ????
	//TODO: PreVisit should be the configuration stuff and the stuff that makes sure commands are properly formatted
	private final Logger logger = Logger.getLogger("Minecraft");
	private ServerPL serverlistener;


	
	
	@Override
	public void onEnable(){

		
		serverlistener = new ServerPL(this, new StorageManager(this, new File("plugins" + File.separator + "PreVisitedTeleport")));
		serverlistener.load();
		
		getServer().getPluginManager().registerEvents(serverlistener, this);
	}
	

	
	@Override
	public void onDisable(){
//		serverlistener.store();
	}
	
	public void log(String info){
		logger.info("[PVT]: " + info);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("ft")){
			if(args.length < 1){
				return false;
			}
			if(!(sender instanceof Player)){
				// Only players can teleport
				return false;
			}

			String warpName = compileArgs(args,0);

//			return onCommandFt((Player) sender, warpname);
			initiateFt((Player) sender, warpName);
			return true;
		}else if(cmd.getName().equalsIgnoreCase("warps")){
			//TODO: More warps features
			String warpname = null;
			if(args.length>0){
				warpname = compileArgs(args,0);
			}
			onCommandWarps((Player)sender,warpname);
			return true;
			
		}else if(cmd.getName().equalsIgnoreCase("energy")){
			if(!(sender instanceof Player)){
				return false;
			}
			if(args.length > 1 && args[0].equalsIgnoreCase("giveall")) {
				
				if( sender.hasPermission("previsit.giveenergy")){
					int amount = Integer.parseInt(args[1]);
					MessageDispatcher.energyGiveAll(sender, amount);
					serverlistener.giveEnergyToAll(amount);
					return true;
				}
				
			}
			
			MessageDispatcher.energyLevelSelf(sender, ServerPL.getEnergy((Player)sender));
			return true;
		
		}else if(cmd.getName().equalsIgnoreCase("svwarp")){
			if(!(sender instanceof Player)){
				return false;
			}
			if(((Player)sender).hasPermission("previsit.svwarp")){
				if( args.length < 1 ) {
					// Nothing specified, at least give name for public warp
					return false;
				}
				
				try{
					// Integer.parseInt() will thrown an error if args[0] is not a number
					int radius = Integer.parseInt(args[0]);		// Effectively a branching statement
					
					if ( args.length < 2 ){
						return false;
					}
					
					String nam = compileArgs(args,1);
					
					if( serverlistener.createWarp(((Player)sender).getLocation(), nam, radius)){
						MessageDispatcher.createWarpSuccess(sender, nam);
						
						
						serverlistener.store();
						
						
					} else {
						MessageDispatcher.createWarpFailure(sender, nam);
					}
					// Return true because the command was formatted correctly.
					return true;
				} catch ( NumberFormatException e ) {
					
					// There was already a check for at least one argument
					String nam = compileArgs(args,0);
					if ( serverlistener.createWarp(( (Player)sender ).getLocation(), nam) ) {
						MessageDispatcher.createPublicWarpSuccess(sender, nam);
						
						
						serverlistener.store();
						
					} else {
						MessageDispatcher.createPublicWarpFailure(sender, nam);
					}
					return true;
				}
			}
		
		} else if( cmd.getName().equalsIgnoreCase("dvwarp") ) {
			if ( args.length < 1 ) {
				return false;
			}
			
			if( sender.hasPermission("previsit.dvwarp") ) {
				String nam = compileArgs(args,0);

				if( serverlistener.deleteWarp(nam) ) {
					MessageDispatcher.deleteWarpSuccess(sender, nam);
					
					
					serverlistener.store();
					
					
				} else {
					MessageDispatcher.deleteWarpFailure(sender, nam);
				}
				return true;
			}
			
		}
		
		return false;
	}
	
	/**
	 * Assumes that the command is properly formatted.
	 * Returns a boolean value indicating if the player was teleported.
	 * 
	 * @param play
	 * @param warpName
	 * @return
	 */
	public boolean initiateFt(Player player, String warpName) {
		ServerPL.checkEnergy(player);
		if ( ! serverlistener.doesWarpExist(warpName)) {
			MessageDispatcher.warpNotFound(player, warpName);
			return false;
		}
		
		Zone zone = serverlistener.getZoneByName(warpName);
		
		boolean visited = zone.hasVisited(player);
		
		boolean publicZone = zone.isPublic();
		boolean playerAllWarps = player.hasPermission("previsit.allwarps");
		
		// TODO: Consider incorporating into visited check, because ops should not need energy anyway
		if (playerAllWarps) {	// Ops only
			MessageDispatcher.teleportAdminSuccess(player, warpName);
			player.teleport(zone.getLocation());
			return true;
		}
		
		if (! serverlistener.teleportLocationOkay(player, zone)) {
			MessageDispatcher.teleportFailWorldChange(player, warpName);
			return false;
		}
		
		if ( ! serverlistener.playerEnergyRequirement(player, zone)) {
			MessageDispatcher.teleportFailEnergy(player, warpName, 0, 0);
			return false;
		}
		
		// Zone is not public, and player has not visited
		if ( ! publicZone && !visited) {	// Both false
			MessageDispatcher.teleportFailNotFound(player, warpName);
			return false;
		}
		// One is true
		if (publicZone) {
			MessageDispatcher.teleportPublicSuccess(player, warpName);
		} else {
			MessageDispatcher.teleportVisitSuccess(player, warpName);
		}
		player.teleport(zone.getLocation());
		return true;
		//return serverlistener.requestTeleport(player, warpName);
	}
	
	private boolean onCommandWarps(Player play, String WarpName) {
		if (WarpName == null) {
			WarpList list = serverlistener.getWarps(play);
			
			if (list.warpNames.length == 0) {
				MessageDispatcher.warpsNoneAvailable(play);
			} else {
				MessageDispatcher.warpsList(play, list);
			}
			return true;
		} 
		if (!serverlistener.doesWarpExist(WarpName)) {
			MessageDispatcher.warpNotFound(play, WarpName);
		}
		int requiredEnergy = serverlistener.getRequiredEnergy(play,WarpName);
		
		MessageDispatcher.energyRequiredToTeleport(play, WarpName, requiredEnergy);
		return true;
	}
	
	private String compileArgs(String[] args, int startLocation) {
		String result = args[startLocation];

		for (int i = startLocation + 1; i < args.length; i++) {
			result += " " + args[i];
		}
		return result;
	}
	
	

}
