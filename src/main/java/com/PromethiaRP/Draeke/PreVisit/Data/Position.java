package com.PromethiaRP.Draeke.PreVisit.Data;

import org.bukkit.Location;

public class Position {

	private double x, y, z;
	private float yaw, pitch;
	private String worldName;
	
	
	public Position() {
		this("", 0.0, 0.0, 0.0, 0.0f, 0.0f);
	}


	public Position(String world, double x, double y, double z, float yaw, float pitch) {
		this.worldName = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public double distance(double pX, double pY, double pZ) {
		return Math.sqrt(distanceSquared(pX, pY, pZ));
	}
	
	public double distanceSquared(double pX, double pY, double pZ) {
		return (pX - x) * (pX - x) + (pY - y) * (pY - y) + (pZ - z) * (pZ - z);
	}

	public double distance(Position loc) {
//		if ( ! loc.worldName.equals(worldName)) {
//			throw new IllegalArgumentException("Comparing distance between two locations not in the same world.");
//		}
		return Math.sqrt(distanceSquared(loc));
	}
	
	public double distanceSquared(Position loc) {
		return distanceSquared(loc.getX(), loc.getY(), loc.getZ());
	}
	
	public String getWorld() {
		return worldName;
	}


	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public float getYaw() {
		return yaw;
	}
	public float getPitch() {
		return pitch;
	}
	
	public static Position convertPosition(Location loc) {
		return new Position(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}
}
