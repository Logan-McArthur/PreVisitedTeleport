package com.PromethiaRP.Draeke.PreVisit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
/**
 * The listener class meant to handle all of the events and manage the Zones
 * @author Draeke_Forther
 *
 */
public class ServerPL implements Listener {

	//TODO: ServerPL should handle the things that go on with the player
	
	public PreVisit plugin;
	
	private Set<Zone> zones = new HashSet<Zone>();
	private static Map<String,Integer> energies = new HashMap<String,Integer>();
	private static Map<UUID, Integer> energiesUUID = new HashMap<UUID, Integer>();
	private static ArrayList<String> coolDown = new ArrayList<String>();
	
	private Date lastDate;
	
	private StorageManager manager;
	
	public static final boolean COMBAT_WAIT = false;
	
	public ServerPL(PreVisit pv, StorageManager manager) {
		plugin = pv;
		lastDate = new Date();
		this.manager = manager;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		checkEnergy(e.getPlayer());
		for(Zone zon: zones){
			if(zon.withinRange(e.getPlayer())){
				if(zon.addPlayer(e.getPlayer())){
					if (zon.isPublic()) {
						MessageDispatcher.discoverPublicWarp(e.getPlayer(), zon.getName());
					} else {
						MessageDispatcher.discoverRegularWarp(e.getPlayer(), zon.getName());
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent e){
		if ( ! COMBAT_WAIT) {
			return;
		}
		if(e.isCancelled()){
			return;
		}
		if(!(e.getEntity() instanceof Player)){
			return;
		}
		if(!((Player)e.getEntity()).hasPermission("previsit.combatwait")){
			return;
		}
		if(coolDown.contains(((Player)e.getEntity()).getName())){
			coolDown.remove(((Player)e.getEntity()).getName());
			
		}
		coolDown.add(((Player)e.getEntity()).getName());	
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

			@Override
			public void run() {
				if(!ServerPL.coolDown.isEmpty()){
					ServerPL.coolDown.remove(0);
				}
			}
			
		}, 100L);
	}
	

	
	public boolean doesWarpExist(String warpName) {
		return getZoneByName(warpName) != null;
	}
	
	public Zone getZoneByName(String name) {
		for (Zone zone : zones) {
			if (zone.getName().equalsIgnoreCase(name)) {
				return zone;
			}
		}
		return null;
	}
	
	public boolean createWarp(Location loc, String name, int size){
		if (! doesWarpExist(name)) {	// Warp does not exist, make a new one
			zones.add(new Zone(loc,size,name));
			return true;
		}
		return false;
	}
	public boolean createWarp(Location loc, String name){
		if (! doesWarpExist(name)) {	// Warp does not exist, make a new one
			zones.add(new Zone(loc,name));

			return true;
		}
		return false;
	}
	
	public static void checkEnergy(Player player){
		if (energies.containsKey(player.getName())) {
			Integer intgr = energies.get(player.getName());
			if (intgr == null) {
				intgr = new Integer(0);
			}
			energiesUUID.put(player.getUniqueId(), intgr);
			energies.remove(player.getName());
		}
		
		if (energiesUUID.get(player.getUniqueId()) == null) {
			energiesUUID.put(player.getUniqueId(), new Integer(0));
		}
	}
	
	public static int getEnergy(Player player){
		checkEnergy(player);
		if (energiesUUID.get(player.getUniqueId()) == null) {
			System.out.println("Error Error, the result from get(player) is null!");
		}
		return energiesUUID.get(player.getUniqueId()).intValue();
	}
	

	/**
	 * If the player has not been on to have their tracker updated, they do not get more energy
	 */
	public void updateEnergies(){
		long value = energyValue();
//		for(String name: energies.keySet()){
//			energies.put(name,new Integer((int)(energies.get(name).intValue()+(value * (plugin.getServer().getPlayer(name)!=null? .125:1)))));
//		}
		for(UUID uid: energiesUUID.keySet()) {
			energiesUUID.put(uid,new Integer((int)(energiesUUID.get(uid).intValue()+(value * (plugin.getServer().getPlayer(uid)!=null? .125:1)))));
		}
		
	}
	private long energyValue(){
		long l = new Date().getTime() - lastDate.getTime();
		lastDate = null; lastDate = new Date();
		return l/6000;
	}
	
	
	public boolean playerHasEnoughEnergy(Player player, Zone zone) {
		return getEnergy(player)>=zone.getRequiredEnergy(player);
	}
	public boolean playerEnergyRequirement(Player player, Zone zone) {
		// if do not have enough, return false if player must use energy
		if ( ! playerHasEnoughEnergy(player, zone)) {
			return ! player.hasPermission("previsit.useenergy");
		}
		// Player has enough energy
		return true;
	}
	
	
	public boolean isTeleportAcrossWorlds(Player player, Zone zone) {
		return !player.getWorld().getName().equalsIgnoreCase(zone.getLocation().getWorld().getName());
	}
	public boolean teleportLocationOkay(Player player, Zone zone) {
		boolean changeAllowed = false;
		boolean changeAttempt = isTeleportAcrossWorlds(player, zone); 
		// if attempt and allow = okay
		// if attempt and not allow = bad
		// if not attempt and not allow = okay
		// if not attempt and allow = okay
		if (changeAttempt) {
			return changeAllowed;
		}
		return true;
	}
	
	
	private boolean isAccessible(Player player, Zone zone){
				
		boolean visited = zone.hasVisited(player);
		
		boolean publicZone = zone.isPublic();
		boolean playerAllWarps = player.hasPermission("previsit.allwarps");
		
		if (playerAllWarps) {	// Ops only
			
			return true;
		}
		
		if (! teleportLocationOkay(player, zone)) {
			
			return false;
		}
		
		if ( ! playerEnergyRequirement(player, zone)) {
			
			return false;
		}
		
		// Zone is not public, and player has not visited
		if ( ! publicZone && !visited) {	// Both false
			
			return false;
		}
		// One is true
		return true;
		
		
	}
	

	public WarpList getWarps(Player player){

		String[] warpNames = new String[zones.size()];
		boolean[] accessibleWarps = new boolean[zones.size()];
		Iterator<Zone> iter = zones.iterator();
		Zone zon = null;
		for (int i = 0; iter.hasNext(); i++) {
			zon = iter.next();
			warpNames[i] = zon.getName();
			accessibleWarps[i] = isAccessible(player, zon);
		}
		return new WarpList(warpNames, accessibleWarps);
	}
	

	
	// It's unnecessary to store the entire file when just the energies are being updated
	public void giveEnergyToAll(int amount){
		for(UUID uid: energiesUUID.keySet()){
			energiesUUID.put(uid, new Integer(energiesUUID.get(uid).intValue()+amount));
		}
	}
	
	public boolean deleteWarp(String nam) {
		for(Zone zone: zones){
			if(zone.getName().equalsIgnoreCase(nam)){
				zones.remove(zone);

				return true;
			}
		}
		return false;
	}
	
	public int getRequiredEnergy(Player player, String zone){
		for(Zone zon : zones){
			if(zon.getName().equalsIgnoreCase(zone)){
				return zon.getRequiredEnergy(player);//.getWorld().getName(),player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ());
			}
		}
		throw new IllegalArgumentException("Zone: " + zone + " not found.");
	}

	public void store() {
		manager.save(energies, energiesUUID, zones);
	}

	public void load() {
		manager.load(energies, energiesUUID, zones);
	}
}
