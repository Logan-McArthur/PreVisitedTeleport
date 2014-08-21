package com.PromethiaRP.Draeke.PreVisit;

import java.util.HashSet;
import java.util.Set;




import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Zone {
	
	
	private Set<String> Players = new HashSet<String>();
	private Location Position;
	//public double X, Y, Z = 0.0;
	//public float Yaw, Pitch = 0.0f;
	//public String World = "";
	private int range = 0;
	private String name = "";
	private boolean isPublic = false;
	public static final String SEPARATOR = "#";
	public Zone(Location loc, int size, String name){
		//this(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), size, name);
		Position = loc;
		this.name = name;
		range = size;
	}
	
	public Zone(Location loc, String name){
		//this(loc.getWorld().getName(),loc.getX(),loc.getY(),loc.getZ(),loc.getYaw(),loc.getPitch(),name);
		Position = loc;
		this.name = name;
		isPublic = true;
	}
	
/**	
	public Zone(String world, double x, double y, double z, float yaw, float pitch, String name){
		World = world;
		X = x;
		Y = y;
		Z = z;
		Yaw = yaw;
		Pitch = pitch;
		this.name = name;
		isPublic = true;
	}
	
	public Zone(String world, double x, double y, double z, float yaw, float pitch, int size, String name){
		World = world;
		X = x;
		Y = y;
		Z = z;
		Yaw = yaw;
		Pitch = pitch;
		this.name = name;
		range = size;
	}
	
	**/
	public int getRequiredEnergy(Player player){
		if(!Position.getWorld().equals(player.getWorld())){
			return 90000;
		}
		return (int) (Position.distance(player.getLocation())/25)^2;
	}
	
	/**
	public int getRequiredEnergy(String world, double x, double y, double z){
		if(world.equals(World)){
			return (((int)distance(x,y,z))/25)^2;
		}else{
			return 90000;
		}
	}
	**/
	
	public boolean hasVisited(Player play){
		return Players.contains(play.getName());
	}
	public boolean hasVisited(String play){
		return Players.contains(play);
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
	
	public boolean addPlayer(Player player){
		if(Players.contains(player.getName())){
			return false;
		}
		else{
			Players.add(player.getName());
			return true;
		}
	}
	
	public void addPlayers(Set<String> players){
		Players.addAll(players);
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
	/**
	public boolean withinRange(String world, double x, double y, double z){
		
		return (distance(x,y,z)<=range) && world.equals(World);
	}
	**/
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(name+SEPARATOR+(isPublic?"open":range)+SEPARATOR+Position.getWorld().getName()+SEPARATOR+Position.getX()+SEPARATOR+Position.getY()+SEPARATOR+Position.getZ()+SEPARATOR+Position.getYaw());
		for(String string: Players){
			sb.append(SEPARATOR+string);
		}
		return sb.toString();
	}
	
	/**
	private double distance(double x, double y, double z){
		double xx = Position.getX()-x;
		double yy = Position.getY()-y;
		double zz = Position.getZ()-z;
		return Math.sqrt(xx*xx+zz*zz+yy*yy);
	}
	**/
}
