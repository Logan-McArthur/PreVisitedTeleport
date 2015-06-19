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

import com.PromethiaRP.Draeke.PreVisit.Data.Position;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.EnergyManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.PlayerManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.ZoneManager;
import com.PromethiaRP.Draeke.PreVisit.StorageManagers.StorageManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.MessageDispatcher;
import com.PromethiaRP.Draeke.PreVisit.Utilities.RequirementManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.WarpList;


/**
 * The main class file in the PreVisitedTeleport plugin designed for use with bukkit
 * 
 * @author Draeke_Forther
 * @version 3.0.0
 * 
 */
public class PreVisit extends JavaPlugin implements Listener {

	private static final Logger logger = Logger.getLogger("Minecraft");
	private ZoneManager zoneManager;
	private EnergyManager energyManager;
	private PlayerManager playerManager;
	private StorageManager storageManager;
	private RequirementManager requirementManager;
	
	@Override
	public void onEnable() {
		
		storageManager = new StorageManager(new File("plugins" + File.separator + "PreVisitedTeleport"));
		zoneManager = storageManager.loadZones();
		energyManager = storageManager.loadEnergy();
		playerManager = storageManager.loadDiscoveries();

		getServer().getPluginManager().registerEvents(this, this);
	}
	

	
	@Override
	public void onDisable(){

	}
	
	public static void log(String info){
		logger.info("[PVT]: " + info);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player play = event.getPlayer();
		
		if ( ! playerManager.isTrackingPlayer(play)) {
			playerManager.trackPlayer(play.getUniqueId());
			energyManager.trackPlayer(play.getUniqueId());
			
		}
		
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
			
			Player play = (Player) sender;
			if (! play.hasPermission(PreVisitPermissions.FastTravel)) {
				MessageDispatcher.commandFailPermission(play, cmd.getName());
			}
			String warpName = compileArgs(args, 0);
			
			if ( ! zoneManager.doesZoneExist(warpName)) {
				
				MessageDispatcher.warpNotFound(play, warpName);
				return true;
			}
			
			Zone zone = zoneManager.getZoneByName(warpName);
			
			if (requirementManager.canFastTravel(play, zone)) {
				Position p = zoneManager.getZoneByName(warpName).getPosition();
				play.teleport(new Location(this.getServer().getWorld(p.getWorld()), p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch()));
			}

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
				
				if( sender.hasPermission(PreVisitPermissions.GiveEnergy)){
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
				
				String zoneName = compileArgs(args, compileStartIndex);
				
				boolean createResult;
				if (compileStartIndex == 0) {
					// Public warp
					createResult = zoneManager.createZone(Position.convertPosition( ((Player)sender).getLocation()), zoneName);
				} else {
					createResult = zoneManager.createZone(Position.convertPosition( ((Player)sender).getLocation()), zoneName, radius);
				}
				
				if (createResult) {
					MessageDispatcher.createWarpSuccess(sender, zoneName);
				} else {
					MessageDispatcher.createWarpFailure(sender, zoneName);
				}
				
				return true;
			}
		
		} else if( cmd.getName().equalsIgnoreCase("dvwarp") ) {
			if ( args.length < 1 ) {
				return false;
			}
			
			if( sender.hasPermission(PreVisitPermissions.DeleteWarp) ) {
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
		} else {
			int requiredEnergy = energyManager.getRequiredEnergy(play.getLocation(), warp);
			MessageDispatcher.energyRequiredToTeleport(play, WarpName, requiredEnergy);
		}
		return true;
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
			accessibleWarps[i] = requirementManager.canFastTravel(player, zon);
//			accessibleWarps[i] = teleportManager.getAccessibility(player, zon).getResult();
			i += 1;
		}
		return new WarpList(warpNames, accessibleWarps);
	}
}
