package com.PromethiaRP.Draeke.PreVisit;

import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;

public class DiscoveredStorageConverter {
	private static final String DISCOVERED_STORAGE_PREFIX = 
			  "############################\n"
			+ "#    PreVisitedTeleport    #\n"
			+ "#   Discovered Data File   #\n"
			+ "#       Version: 1.0       #\n"
			+ "############################\n";
	
	
	private final static String DISCOVERED_IDENTIFIER = ":";
	private final static String DISCOVERED_SEPARATOR = "&";
	public static String encodeDiscovered(PlayerManager playerManager) {
		StringBuilder builder = new StringBuilder(DISCOVERED_STORAGE_PREFIX);
		Iterator<UUID> iterator = playerManager.getIterator();
		
		UUID uid;
		while (iterator.hasNext()) {
			uid = iterator.next();
			builder.append(uid);
			builder.append(DISCOVERED_IDENTIFIER);
			Iterator<String> iter = playerManager.getVisitedZones(uid).iterator();
			if (iter.hasNext()) {
				builder.append(iter.next());
			}
			while(iter.hasNext()) {
				builder.append(DISCOVERED_SEPARATOR);
				builder.append(iter.next());
			}
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	public static PlayerManager decodeDiscovered(String data) {
		PlayerManager playerManager = new PlayerManager();
		
		Scanner broadPhase = new Scanner(data);
		String dataLine;
		while (broadPhase.hasNextLine()) {
			dataLine = broadPhase.nextLine();
			
			if (dataLine.startsWith("#")) {
				continue;
			}
			
			String[] identifierSplit = dataLine.split(DISCOVERED_IDENTIFIER);
			UUID playerID = UUID.fromString(identifierSplit[0]);
			
			Scanner dataParser = new Scanner(identifierSplit[1]);
			dataParser.useDelimiter(DISCOVERED_SEPARATOR);
			
			while (dataParser.hasNext()) {
				playerManager.addZoneToPlayer(playerID, dataParser.next());
			}
			
			dataParser.close();
		}
		
		broadPhase.close();
		
		return playerManager;
	}
}
