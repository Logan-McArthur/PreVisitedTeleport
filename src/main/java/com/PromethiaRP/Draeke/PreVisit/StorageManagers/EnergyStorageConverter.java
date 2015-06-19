package com.PromethiaRP.Draeke.PreVisit.StorageManagers;

import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;

import com.PromethiaRP.Draeke.PreVisit.DataManagers.EnergyManager;

public class EnergyStorageConverter {

	private final static String ENERGY_STORAGE_PREFIX = 
			  "############################\n"
			+ "#    PreVisitedTeleport    #\n"
			+ "#     Energy Data File     #\n"
			+ "#       Version: 1.0       #\n"
			+ "############################";
	private final static String ENERGY_IDENTIFIER = ":";
	
	public static String encodeEnergy(EnergyManager energyManager) {
		StringBuilder builder = new StringBuilder(ENERGY_STORAGE_PREFIX);
		Iterator<UUID> iterator = energyManager.getIterator();
		UUID uid;
		while (iterator.hasNext()) {
			builder.append("\n");
			uid = iterator.next();
			builder.append(uid.toString());
			builder.append(ENERGY_IDENTIFIER);
			builder.append(energyManager.getEnergy(uid));

		}
		return builder.toString();
	}
	
	public static EnergyManager decodeEnergy(String energyData) {
		EnergyManager energyManager = new EnergyManager();
		
		Scanner broadPhase = new Scanner(energyData);
		String lineData;
		Scanner lineParser;
		while (broadPhase.hasNextLine()) {
			lineData = broadPhase.nextLine();
			if (lineData.startsWith("#")) {
				continue;
			}
			lineParser = new Scanner(lineData);
			lineParser.useDelimiter(ENERGY_IDENTIFIER);
			
			energyManager.setEnergy(UUID.fromString(lineParser.next()), lineParser.nextInt());
			
			lineParser.close();
		}
		
		broadPhase.close();
		
		return energyManager;
	}
}
