package com.PromethiaRP.Draeke.PreVisit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class file in the PreVisitedTeleport plugin designed for use with bukkit
 * 
 * @author Draeke_Forther
 * @version 2.0.0
 * 
 */
public class PreVisit extends JavaPlugin {

	//TODO: Move the messaging stuff back to the ServerPL ????
	//TODO: PreVisit should be the configuration stuff and the stuff that makes sure commands are properly formatted
	private final Logger logger = Logger.getLogger("Minecraft");
	private ServerPL serverlistener;
	private MessageDispatcher messenger;
	
	private final String FILENAME = "PreVisit.data";
	private File DATAFOLDER = new File("plugins" +File.separator +"PreVisitedTeleport");
	private File DATAFILE = new File(DATAFOLDER.getPath()+File.separator + FILENAME);
	private File BACKUPFOLDER = new File(DATAFOLDER.getPath() + File.separator + "Backups");
	
	
	@Override
	public void onEnable(){
		checkFile();
		serverlistener = new ServerPL(this,DATAFILE, BACKUPFOLDER);
		serverlistener.load();
		messenger = new MessageDispatcher();
		getServer().getPluginManager().registerEvents(serverlistener, this);
	}
	
	public void checkFile(){
		if(!DATAFOLDER.exists()){
			DATAFOLDER.mkdir();
		}
		if(!DATAFILE.exists()){
			try {
				DATAFILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!BACKUPFOLDER.exists()) {
			BACKUPFOLDER.mkdir();
		}
	}
	
	@Override
	public void onDisable(){
		serverlistener.store();
	}
	
	public void log(String info){
		logger.info("[PVT]: " + info);
	}
	
	public boolean createWarp(Location loc, String name, int size){
		return serverlistener.createWarp(loc, name, size);
	}
	//
	public boolean createWarp(Location loc, String name){
		return serverlistener.createWarp(loc, name);
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
//			System.out.println("Begin debug for command ft");
			String warpname = compileArgs(args,0);
//			System.out.println("String warpname equals \"" + warpname + "\"");
			return onCommandFt((Player) sender, warpname);
		
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
					messenger.energyGiveAll(sender, amount);
					giveEnergyToAll(amount);
					return true;
				}
				
			}
			
			messenger.energyLevelSelf(sender, getPlayerEnergy((Player)sender));
			return true;
		
		}else if(cmd.getName().equalsIgnoreCase("svwarp")){
			if(!(sender instanceof Player)){
				return false;
			}
			if(((Player)sender).hasPermission("previsit.svwarp")){
				if(args.length<1){
					return false;
				}
				
				try{
					int radius = Integer.parseInt(args[0]);
					if(args.length<2){
						return false;
					}
					String nam = compileArgs(args,1);
					
					if(createWarp(((Player)sender).getLocation(), nam, radius)){
						messenger.createWarpSuccess(sender, nam);
					}else{
						messenger.createWarpFailure(sender, nam);
					}
					return true;
				}catch(NumberFormatException e){
					String nam = compileArgs(args,0);
					if(createWarp(((Player)sender).getLocation(), nam)){
						messenger.createPublicWarpSuccess(sender, nam);
					}else{
						messenger.createPublicWarpFailure(sender, nam);
					}
					return true;
				}
			}
		
		}else if(cmd.getName().equalsIgnoreCase("dvwarp")){
			if(args.length<1){
				return false;
			}
			
			if(sender.hasPermission("previsit.dvwarp")){
				String nam = compileArgs(args,0);

				if(deleteWarp(nam)){
					messenger.deleteWarpSuccess(sender, nam);
				}else{
					messenger.deleteWarpFailure(sender, nam);
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
	private boolean onCommandFt(Player player, String warpName) {
		serverlistener.checkEnergy(player);
		if ( ! serverlistener.doesWarpExist(warpName)) {
			messenger.warpNotFound(player, warpName);
			return false;
		}
		
		Zone zone = serverlistener.getZoneByName(warpName);
		
		boolean visited = zone.hasVisited(player);
		
		boolean publicZone = zone.isPublic();
		boolean playerAllWarps = player.hasPermission("previsit.allwarps");
		
		// TODO: Consider incorporating into visited check, because ops should not need energy anyway
		if (playerAllWarps) {	// Ops only
			messenger.teleportAdminSuccess(player, warpName);
			player.teleport(zone.getLocation());
			return true;
		}
		
		if (! serverlistener.teleportLocationOkay(player, zone)) {
			messenger.teleportFailWorldChange(player, warpName);
			return false;
		}
		
		if ( ! serverlistener.playerEnergyRequirement(player, zone)) {
			messenger.teleportFailEnergy(player, warpName, 0, 0);
			return false;
		}
		
		// Zone is not public, and player has not visited
		if ( ! publicZone && !visited) {	// Both false
			messenger.teleportFailNotFound(player, warpName);
			return false;
		}
		// One is true
		if (publicZone) {
			messenger.teleportPublicSuccess(player, warpName);
		} else {
			messenger.teleportVisitSuccess(player, warpName);
		}
		player.teleport(zone.getLocation());
		return true;
		//return serverlistener.requestTeleport(player, warpName);
	}
	
	private boolean onCommandWarps(Player play, String WarpName) {
		if (WarpName == null) {
			WarpList list = serverlistener.getWarps(play);
			
			if (list.warpNames.length == 0) {
				messenger.warpsNoneAvailable(play);
			} else {
				messenger.warpsList(play, list);
			}
			return true;
		} 
		if (!serverlistener.doesWarpExist(WarpName)) {
			messenger.warpNotFound(play, WarpName);
		}
		int requiredEnergy = requiredEnergy(play,WarpName);
		
		messenger.energyRequiredToTeleport(play, WarpName, requiredEnergy);
		return true;
	}
	
	private String compileArgs(String[] args, int startLocation) {
		String result = args[startLocation];

		for (int i = startLocation + 1; i < args.length; i++) {
			result += " " + args[i];
		}
		return result;
	}
	
	public int requiredEnergy(Player play, String zone){
		return serverlistener.getRequiredEnergy(play, zone);
	}
	public void giveEnergyToAll(int amount){
		
		serverlistener.giveEnergyToAll(amount);
	}

	
	public int getPlayerEnergy(Player player) {
		return serverlistener.getEnergy(player);
	}

	public boolean deleteWarp(String nam) {
		return serverlistener.deleteWarp(nam);
	}
}
