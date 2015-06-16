package com.PromethiaRP.Draeke.PreVisit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerManager {

	private Map<UUID, HashSet<String>> visitedMap;
	
	public PlayerManager() {
		visitedMap = new HashMap<UUID, HashSet<String> >();
	}
	
	public PlayerManager(Map<UUID, HashSet<String>> visited) {
		visitedMap = visited;
	}
	
	public Iterator<UUID> getIterator() {
		return visitedMap.keySet().iterator();
	}
	
	public boolean hasVisited(Player play, String zoneName) {
		return this.hasVisited(play.getUniqueId(), zoneName);
	}
	public boolean hasVisited(UUID uid, String zoneName) {
		this.checkPlayer(uid);
		return visitedMap.get(uid).contains(zoneName);
	}
	
	
	public boolean addZoneToPlayer(UUID uid, String zoneName) {
		this.checkPlayer(uid);
		Set<String> visited = visitedMap.get(uid);
		if (visited.contains(zoneName)) {
			return false;
		}
		visited.add(zoneName);
		return true;
	}
	public boolean addZoneToPlayer(Player play, String zoneName) {
		return this.addZoneToPlayer(play.getUniqueId(), zoneName);
	}
	
	
	public void addZonesToPlayer(Player play, Collection<String> zones) {
		this.addZonesToPlayer(play.getUniqueId(), zones);
	}
	public void addZonesToPlayer(UUID uid, Collection<String> zones) {
		this.checkPlayer(uid);
		visitedMap.get(uid).addAll(zones);
	}
	
	
	public Set<String> getVisitedZones(Player play) {
		return this.getVisitedZones(play.getUniqueId());
	}
	public Set<String> getVisitedZones(UUID uid) {
		this.checkPlayer(uid);
		return visitedMap.get(uid);
	}
	
	
	public Set<UUID> getTrackedPlayers() {
		return visitedMap.keySet();
	}
	
	
	public boolean isTrackingPlayer(Player play) {
		return this.isTrackingPlayer(play.getUniqueId());
	}
	public boolean isTrackingPlayer(UUID uid) {
		return visitedMap.containsKey(uid);
	}
	
	
	public void removeZone(String zoneName) {
		for (UUID uid : visitedMap.keySet()) {
			visitedMap.get(uid).remove(zoneName);
		}
	}
	
	private void checkPlayer(UUID uid) {
		if (!visitedMap.containsKey(uid)) {
			visitedMap.put(uid, new HashSet<String>());
		}
	}
}