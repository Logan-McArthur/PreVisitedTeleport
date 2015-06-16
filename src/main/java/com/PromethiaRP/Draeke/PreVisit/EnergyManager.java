package com.PromethiaRP.Draeke.PreVisit;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EnergyManager{

	private Map<UUID, Integer> energyMap;
	
	private final int STARTING_ENERGY = 500;
	
	private Date lastDate;
	
	public EnergyManager() {
		energyMap = new HashMap<UUID, Integer>();
	}
	
	public EnergyManager(Map<UUID, Integer> energies) {
		energyMap = energies;
	}
	
	private long energyValue(){
		long l = new Date().getTime() - lastDate.getTime();
		lastDate = null; lastDate = new Date();
		return l/6000;
	}
	
	public Iterator<UUID> getIterator() {
		return energyMap.keySet().iterator();
	}
	
	/**
	 * If the player has not been on to have their tracker updated, they do not get more energy
	 */
	public void updateEnergies(){
		long value = energyValue();
//		for(String name: energies.keySet()){
//			energies.put(name,new Integer((int)(energies.get(name).intValue()+(value * (plugin.getServer().getPlayer(name)!=null? .125:1)))));
//		}
		for(UUID uid: energyMap.keySet()) {
			energyMap.put(uid,new Integer((int)(energyMap.get(uid).intValue()+(value * (1)))));
		}
		
	}
	
	public int getRequiredEnergy(Location loc, Zone zone){
		return this.getRequiredEnergy(loc, zone.getPosition());
	}
//	public int getRequiredEnergy(Player player, Zone zone) {
//		return this.getRequiredEnergy(player.getLocation(), zone.getLocation());
//	}
	
	public int getRequiredEnergy(Location playerLoc, Position zoneLoc) {
		return (int) Position.convertPosition(playerLoc).distance(zoneLoc);
	}
	
	
	// It's unnecessary to store the entire file when just the energies are being updated
	public void giveEnergyToAll(int amount){
		for(UUID uid: energyMap.keySet()){
			addEnergy(uid, amount);
		}
	}
	
	public void addEnergy(Player player, int amount) {
		addEnergy(player.getUniqueId(), amount);
	}
	public void addEnergy(UUID uid, int amount) {
		int current = 0;
		if (energyMap.containsKey(uid)) {
			current = energyMap.get(uid).intValue();
		}
		setEnergy(uid, current + amount);
	}
	
	public void setEnergy(Player player, int amount) {
		setEnergy(player.getUniqueId(), amount);
	}
	
	public void setEnergy(UUID uniqueId, int amount) {
		energyMap.put(uniqueId, new Integer(amount));
	}

	public int getEnergy(Player player){
		return getEnergy(player.getUniqueId());
	}
	
	public int getEnergy(UUID uid) {
		checkEnergy(uid);
		if (energyMap.get(uid) == null) {
			System.out.println("Error Error, the result from get(player) is null!");
		}
		return energyMap.get(uid).intValue();
	}
	
	private void checkEnergy(UUID uid) {
		if (energyMap.get(uid) == null) {
			energyMap.put(uid, new Integer(STARTING_ENERGY));
		}
	}
	public Map<UUID, Integer> getEnergyMap() {
		return this.energyMap;
	}
}
