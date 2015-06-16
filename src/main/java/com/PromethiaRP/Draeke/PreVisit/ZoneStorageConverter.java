package com.PromethiaRP.Draeke.PreVisit;

import java.util.Iterator;
import java.util.Scanner;

public class ZoneStorageConverter {
	private static final String ZONE_STORAGE_PREFIX = 
			  "############################\n"
			+ "#    PreVisitedTeleport    #\n"
			+ "#      Zone Data File      #\n"
			+ "#       Version: 1.0       #\n"
			+ "############################\n";
	
	
	private static final String ZONE_IDENTIFIER = ":";
	private static final String ZONE_SEPARATOR = "_";
	private static final String ZONE_PUBLIC = "public";
	public static String encodeZones(ZoneManager manager){
		Iterator<Zone> iter = manager.getIterator();
		StringBuilder builder = new StringBuilder(ZONE_STORAGE_PREFIX);
		Zone zone;
		while (iter.hasNext()) {
			zone = iter.next();
			builder.append(zone.getName());
			builder.append(ZONE_IDENTIFIER);
			Position pos = zone.getPosition();
			builder.append(zone.isPublic() ? ZONE_PUBLIC : zone.getSize());
			builder.append(ZONE_SEPARATOR);
			builder.append(pos.getWorld());
			builder.append(ZONE_SEPARATOR);
			builder.append(pos.getX());
			builder.append(ZONE_SEPARATOR);
			builder.append(pos.getY());
			builder.append(ZONE_SEPARATOR);
			builder.append(pos.getZ());
			builder.append(ZONE_SEPARATOR);
			builder.append(pos.getYaw());
			builder.append(ZONE_SEPARATOR);
			builder.append(pos.getPitch());
			builder.append("\n");
		}
		return builder.toString();
	}
	
	public static ZoneManager decodeZones(String data) {
		ZoneManager manager = new ZoneManager();
		Scanner broadPhase = new Scanner(data);
		String line;
		while (broadPhase.hasNextLine()) {
			line = broadPhase.nextLine();
			if (line.startsWith("#")) {
				continue;
			}
			String[] nameDataSplit = line.split(ZONE_IDENTIFIER);
			String zoneName = nameDataSplit[0];
			String[] dataSplit = nameDataSplit[1].split(ZONE_SEPARATOR);
			Position pos = new Position(dataSplit[1], 
					Double.parseDouble(dataSplit[2]), 
					Double.parseDouble(dataSplit[3]), 
					Double.parseDouble(dataSplit[4]), 
					Float.parseFloat(dataSplit[5]), 
					Float.parseFloat(dataSplit[6]));
			if (dataSplit[0].equals(ZONE_PUBLIC)) {
				manager.createZone(pos, zoneName);
			} else {
				manager.createZone(pos, zoneName, Integer.parseInt(dataSplit[0]));
			}
		}
		broadPhase.close();
		return manager;
	}
}
