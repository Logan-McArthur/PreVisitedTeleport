package com.PromethiaRP.Draeke.PreVisit;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;


/**
 * The main class file in the PreVisitedTeleport plugin designed for use with bukkit
 * 
 * @author Draeke_Forther
 * @version 3.0.0
 * 
 */
public class PreVisit extends JavaPlugin implements Listener {

	private final Logger logger = Logger.getLogger("Minecraft");
	private ZoneManager zoneManager;
	private EnergyManager energyManager;
	private PlayerManager playerManager;
	private StorageManager storageManager;
	private TeleportationManager teleportManager;
	@Override
	public void onEnable() {
//new File("plugins" + File.separator + "PreVisitedTeleport"))
		
		// TODO: Change how the managers are loaded
		storageManager = new StorageManager(new File("plugins" + File.separator + "PreVisitedTeleport"));
		zoneManager = storageManager.loadZones();
		energyManager = storageManager.loadEnergy();
		playerManager = storageManager.loadDiscoveries();
		teleportManager = new TeleportationManager(zoneManager, playerManager, energyManager);
//		storageManager.load(null, energyManager.getEnergyMap(), zones);
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	

	
	@Override
	public void onDisable(){

	}
	
	public void log(String info){
		logger.info("[PVT]: " + info);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player play = event.getPlayer();
		Zone zone = zoneManager.getContainingZone(play.getLocation());
		if (zone == null) {
			return;
		}
		
		if(playerManager.addZoneToPlayer(play, zone.getName())){
			if (zone.isPublic()) {
				MessageDispatcher.discoverPublicWarp(play, zone.getName());
			} else {
				MessageDispatcher.discoverRegularWarp(play, zone.getName());
			}
		}
		
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

			String warpName = compileArgs(args, 0);

			this.fastTravel((Player)sender, warpName);
			
			return true;
		}else if(cmd.getName().equalsIgnoreCase("warps")){
			//TODO: More warps features
			String warpname = null;
			if(args.length > 0){
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
					
					energyManager.giveEnergyToAll(amount);
					
					return true;
				}
				
			}
			
			MessageDispatcher.energyLevelSelf(sender, energyManager.getEnergy((Player)sender));
			return true;
		
		} else if (cmd.getName().equalsIgnoreCase("svwarp")) {
			if(!(sender instanceof Player)) {
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
					
					if( zoneManager.createZone(Position.convertPosition( ((Player)sender).getLocation() ), nam, radius)){
						
						MessageDispatcher.createWarpSuccess(sender, nam);
						
						// TODO: Store here
						storageManager.storeZones(zoneManager);
						storageManager.storeDiscoveries(playerManager);
						storageManager.storeEnergy(energyManager);
					} else {
						MessageDispatcher.createWarpFailure(sender, nam);
					}
					// Return true because the command was formatted correctly.
					return true;
				} catch ( NumberFormatException e ) {
					
					// There was already a check for at least one argument
					String nam = compileArgs(args,0);
					if ( zoneManager.createZone(Position.convertPosition(( (Player)sender ).getLocation() ), nam) ) {
						MessageDispatcher.createPublicWarpSuccess(sender, nam);
						
						//TODO: Store here
						storageManager.storeZones(zoneManager);
						storageManager.storeDiscoveries(playerManager);
						storageManager.storeEnergy(energyManager);
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

				if( zoneManager.deleteZone(nam) ) {
					playerManager.removeZone(nam);
					
					MessageDispatcher.deleteWarpSuccess(sender, nam);
					
					//TODO: Store Here					
					storageManager.storeZones(zoneManager);
					storageManager.storeDiscoveries(playerManager);
					storageManager.storeEnergy(energyManager);
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
	public boolean fastTravel(Player player, String warpName) {
		if ( ! zoneManager.doesZoneExist(warpName)) {
			// Accessibility.FAIL_NOT_FOUND;
			MessageDispatcher.warpNotFound(player, warpName);
			return false;
		}
		
		Zone zone = zoneManager.getZoneByName(warpName);
		Accessibility access = teleportManager.getAccessibility(player, zone);

		switch (access) {
		case FAIL_ENERGY_LEVEL:
			MessageDispatcher.teleportFailEnergy(player, warpName, energyManager.getRequiredEnergy(player.getLocation(), zone), energyManager.getEnergy(player));
			break;
		case FAIL_NOT_VISITED:
			MessageDispatcher.teleportFailNotVisited(player, warpName);
			break;
		case FAIL_WORLD_CHANGE:
			MessageDispatcher.teleportFailWorldChange(player, warpName);
			break;
		case SUCCEED_ADMIN:
			MessageDispatcher.teleportAdminSuccess(player, warpName);
			break;
		case SUCCEED_PUBLIC:
			MessageDispatcher.teleportPublicSuccess(player, warpName);
			break;
		case SUCCEED_VISITED:
			MessageDispatcher.teleportVisitSuccess(player, warpName);
			break;
		default:
			System.err.println("[PVT]: Error occurred when checking zone accessibility");
			break;
		}
		if (access.getResult()) {
			Position p = zoneManager.getZoneByName(warpName).getPosition();
			//player.teleport(zoneManager.getZoneByName(warpName).getLocation());
			player.teleport(new Location(this.getServer().getWorld(p.getWorld()), p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch()));
			
		}
		return access.getResult();
	}
	
	private boolean onCommandWarps(Player play, String WarpName) {
		if (WarpName == null) {
			WarpList list = getWarps(play);
			
			if (list.warpNames.length == 0) {
				MessageDispatcher.warpsNoneAvailable(play);
			} else {
				MessageDispatcher.warpsList(play, list);
			}
			return true;
		} 
		
		Zone warp = zoneManager.getZoneByName(WarpName);
		
		if ( warp == null) {
			MessageDispatcher.warpNotFound(play, WarpName);
			return true;
		} else {
			
			int requiredEnergy = energyManager.getRequiredEnergy(play.getLocation(), warp);
			MessageDispatcher.energyRequiredToTeleport(play, WarpName, requiredEnergy);
			return true;
		}
	}
	
	private String compileArgs(String[] args, int startLocation) {
		String result = args[startLocation];

		for (int i = startLocation + 1; i < args.length; i++) {
			result += " " + args[i];
		}
		return result;
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
			accessibleWarps[i] = teleportManager.getAccessibility(player, zon).getResult();
			i += 1;
		}
		return new WarpList(warpNames, accessibleWarps);
	}
}
