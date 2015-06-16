package com.PromethiaRP.Draeke.PreVisit;

import org.bukkit.Location;

public class Zone {
	
	private Position location;

	private int range = 0;
	private String name = "";
	private boolean isPublic = false;

	public Zone(Position loc, int size, String name){

		location = loc;
		this.name = name;
		range = size;
	}
	
	public Zone(Position loc, String name){

		location = loc;
		this.name = name;
		isPublic = true;
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
	
	public Position getPosition(){
		return location;
	}
	
//	public boolean withinRange(Player play){
//		if(!Position.getWorld().equals(play.getWorld())){
//			return false;
//		}
//		if(Position.distance(play.getLocation())<=range){
//			return true;
//		}
//		return false;
//	}
	
	public boolean withinRange(Location loc) {
		return withinRange(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	public boolean withinRange(Position pos) {
		return withinRange(pos.getWorld(), pos.getX(), pos.getY(), pos.getZ());
	}
	
	public boolean withinRange(String worldName, double x, double y, double z) {
		if (! location.getWorld().equals(worldName)) {
			return false;
		}
		return location.distance(x, y, z) <= range;
	}
}
