package com.PromethiaRP.Draeke.PreVisit.DataManagers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Location;

import com.PromethiaRP.Draeke.PreVisit.Data.Position;
import com.PromethiaRP.Draeke.PreVisit.Data.Zone;
import com.PromethiaRP.Draeke.PreVisit.Utilities.Provider;

public class ZoneManager implements Provider {

	private Map<String, Zone> zoneMap;
	
	public ZoneManager() {
		this(new HashMap<String, Zone>());
	}
	
	public ZoneManager(Map<String, Zone> zones) {
		zoneMap = zones;
	}
	
	public Iterator<Zone> getIterator() {
		return zoneMap.values().iterator();
	}
	
	public int size() {
		return zoneMap.size();
	}
	
	public boolean createZone(Position loc, String name, int size){
		if (! doesZoneExist(name)) {	// Warp does not exist, make a new one
			zoneMap.put(name, new Zone(loc,size,name));
			return true;
		}
		return false;
	}
	public boolean createZone(Position loc, String name){
		if (! doesZoneExist(name)) {	// Warp does not exist, make a new one
			zoneMap.put(name, new Zone(loc,name));

			return true;
		}
		return false;
	}
	
	
	public boolean deleteZone(String nam) {
		return zoneMap.remove(nam) == null;
	}
	
	public boolean isWithinZone(Position loc) {
		return getContainingZone(loc) != null;
	}
	public Zone getContainingZone(Position loc) {
		return getContainingZone(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
	}
	public Zone getContainingZone(Location loc) {
		return getContainingZone(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	public Zone getContainingZone(String name, double x, double y, double z) {
		for (Zone zon : zoneMap.values()) {
			if (zon.withinRange(name, x, y, z)) {
				return zon;
			}
		}
		return null;
	}

	public boolean doesZoneExist(String zoneName) {
		return zoneMap.containsKey(zoneName);
	}
	
	public Zone getZoneByName(String name) {
		return zoneMap.get(name);
	}
	
	
}
