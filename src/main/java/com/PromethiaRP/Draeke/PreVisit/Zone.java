package com.PromethiaRP.Draeke.PreVisit;

import java.util.HashSet;
import java.util.Set;




import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Zone {
	
	
	private Set<String> Players = new HashSet<String>();
	private Set<UUID> UUIDs = new HashSet<UUID>();
	private Location Position;

	private int range = 0;
	private String name = "";
	private boolean isPublic = false;
//	public static final String SEPARATOR = "#";
//	public static final String UUID_SEPARATOR = "@";
	public Zone(Location loc, int size, String name){

		Position = loc;
		this.name = name;
		range = size;
	}
	
	public Zone(Location loc, String name){

		Position = loc;
		this.name = name;
		isPublic = true;
	}
	
	public int getRequiredEnergy(Player player){
//		if(!Position.getWorld().equals(player.getWorld())){
//			return 90000;
//		}
		return (int) (Position.distance(player.getLocation()));
	}
	
	public boolean hasVisited(Player play){
		
		return Players.contains(play.getName()) || UUIDs.contains(play.getUniqueId());
	}

	
	public boolean isPublic(){
		return isPublic;
	}
	
	public String getName(){
		return name;
	}
	
	public int getSize(){
		return range;
	}
	
	public Location getLocation(){
		return Position;
	}
	//
	public boolean addPlayer(Player player){
		if (Players.contains(player.getName())) {
			Players.remove(player);
		}
		if(UUIDs.contains(player.getUniqueId())){
			return false;
		}
		else{
			UUIDs.add(player.getUniqueId());
			return true;
		}
	}
	
	public void addPlayers(Set<String> players){
		Players.addAll(players);
	}
	
	public void addPlayerUUIDs(Set<UUID> uuids) {
		UUIDs.addAll(uuids);
	}
	
	public boolean withinRange(Player play){
		if(!Position.getWorld().equals(play.getWorld())){
			return false;
		}
		if(Position.distance(play.getLocation())<=range){
			return true;
		}
		return false;
	}
	
	public Set<UUID> getVisitedPlayersUUID() {
		return UUIDs;
	}
	public Set<String> getVisitedPlayers() {
		return Players;
	}
	
	
	
}
