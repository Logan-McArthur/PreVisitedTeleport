package com.PromethiaRP.Draeke.PreVisit;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;

import com.PromethiaRP.Draeke.PreVisit.Commands.CreateWarpCommand;
import com.PromethiaRP.Draeke.PreVisit.Commands.DeleteWarpCommand;
import com.PromethiaRP.Draeke.PreVisit.Commands.EnergyCommand;
import com.PromethiaRP.Draeke.PreVisit.Commands.FastTravelCommand;
import com.PromethiaRP.Draeke.PreVisit.Commands.WarpsCommand;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.EnergyManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.PlayerManager;
import com.PromethiaRP.Draeke.PreVisit.DataManagers.ZoneManager;
import com.PromethiaRP.Draeke.PreVisit.StorageManagers.StorageManager;
import com.PromethiaRP.Draeke.PreVisit.Utilities.RequirementManager;


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
	
	private WarpsCommand warps;
	private FastTravelCommand fastTravel;
	private EnergyCommand energy;
	private CreateWarpCommand createWarp;
	private DeleteWarpCommand deleteWarp;
	
	private PlayerMovementListener playerListener;
	
	@Override
	public void onEnable() {
		
		storageManager = new StorageManager(new File("plugins" + File.separator + "PreVisitedTeleport"));
		zoneManager = storageManager.loadZones();
		energyManager = storageManager.loadEnergy();
		playerManager = storageManager.loadDiscoveries();

		warps = new WarpsCommand(zoneManager, energyManager, requirementManager);
		fastTravel = new FastTravelCommand(this, zoneManager, requirementManager);
		energy = new EnergyCommand(energyManager);
		createWarp = new CreateWarpCommand(this, zoneManager);
		deleteWarp = new DeleteWarpCommand(this, zoneManager, playerManager);
		
		
		this.getCommand("warps").setExecutor(warps);
		this.getCommand("ft").setExecutor(fastTravel);
		this.getCommand("energy").setExecutor(energy);
		this.getCommand("svwarp").setExecutor(createWarp);
		this.getCommand("dvwarp").setExecutor(deleteWarp);
		
		playerListener = new PlayerMovementListener(playerManager, energyManager, zoneManager);
		
		getServer().getPluginManager().registerEvents(playerListener, this);
	}
	

	
	@Override
	public void onDisable(){

	}
	
	public static void log(String info){
		logger.info("[PVT]: " + info);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		return false;
	}

	public World getWorld(String worldName) {
		return getServer().getWorld(worldName);
	}
	
	public void saveToFile() {
		storageManager.storeZones(zoneManager);
		storageManager.storeDiscoveries(playerManager);
		storageManager.storeEnergy(energyManager);
	}
	
	
	public static String compileArgs(String[] args, int startLocation) {
		String result = args[startLocation];

		for (int i = startLocation + 1; i < args.length; i++) {
			result += " " + args[i];
		}
		return result;
	}
	
}
