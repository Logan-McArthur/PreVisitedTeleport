package com.PromethiaRP.Draeke.PreVisit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;

public class StorageManager {
	public static final String ENERGY_HEADER = 		"========Energies========";
	public static final String ENERGY_UUID_HEADER = "========UniqueEnergies========";
	public static final String WARP_HEADER = 		"========Warps========";
	public static final String ZONE_SEPARATOR =		"#";
	public static final String ZONE_UUID_SEPARATOR = "@";
	
	private final String FILENAME = "PreVisit.data";
	private File DATAFOLDER;// = new File("plugins" +File.separator +"PreVisitedTeleport");
	private File DATAFILE;// = new File(DATAFOLDER.getPath()+File.separator + FILENAME);
	private File BACKUPFOLDER;// = new File(DATAFOLDER.getPath() + File.separator + "Backups");
	
	private PreVisit plugin;
	
	public StorageManager(PreVisit preVisit, File pluginDataFolder) {
		DATAFOLDER = pluginDataFolder;
		DATAFILE = new File(DATAFOLDER.getPath()+File.separator + FILENAME);
		BACKUPFOLDER = new File(DATAFOLDER.getPath() + File.separator + "Backups");
		
		plugin = preVisit;
		
		checkFile();
	}
	
	
	public void checkFile(){
		if(!DATAFOLDER.exists()){
			DATAFOLDER.mkdir();
		}
		if(!DATAFILE.exists()){
			try {
				DATAFILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!BACKUPFOLDER.exists()) {
			BACKUPFOLDER.mkdir();
		}
	}
	
	public void save(Map<String, Integer> energies, Map<UUID, Integer> energiesUUID, Set<Zone> zones) {
		if (shouldBackup()) {
			createBackup(energies, energiesUUID, zones);
		}
		
		store(DATAFILE, energies, energiesUUID, zones);
	}
	
	public void store(File storage, Map<String, Integer> energies, Map<UUID, Integer> energiesUUID, Set<Zone> zones){

		
		FileWriter fw;
		PrintWriter pw;
		try{
			fw = new FileWriter(storage);
			pw = new PrintWriter(fw,true);
			pw.println(ENERGY_HEADER);
			for(String name: energies.keySet()){
				pw.println(name+ZONE_SEPARATOR+energies.get(name).intValue());
			}
			pw.println(ENERGY_UUID_HEADER);
			for(UUID uid: energiesUUID.keySet()) {
				pw.println(uid.toString() + ZONE_UUID_SEPARATOR+energiesUUID.get(uid).intValue());
			}
			pw.println(WARP_HEADER);
			for(Zone zone: zones){
				pw.println(zoneToString(zone));
			}
			pw.close();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void checkBackup() {
		File[] directory = BACKUPFOLDER.listFiles();
		int oldest = 0;
		long oldestTime = 0;

		for (int i = 0; i < directory.length; i++) {
			if(directory[i].getName().contains("Backup_")) {
				if (oldest == 0) {
					oldest = i;
					oldestTime = directory[i].lastModified();
				}

				if (directory[i].lastModified() < oldestTime) {
					oldest = i;
					oldestTime = directory[i].lastModified();
				}
			}
			
		}
		
		// Make doubly sure that I'm not deleting something important
		if (directory.length > 3 && directory[oldest].getName().contains("Backup_")) {
			directory[oldest].delete();
		}
	}
	
	public void createBackup(Map<String, Integer> energies, Map<UUID, Integer> energiesUUID, Set<Zone> zones) {
		Calendar cal = Calendar.getInstance();
		String title = "Backup_" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cal.get(Calendar.MONTH) + "_" + cal.get(Calendar.YEAR) + ".data";
//		File[] directory = BACKUPFOLDER.listFiles();
//		int oldest = 0;
//		long oldestTime = 0;

//		for (int i = 0; i < directory.length; i++) {
//			if(directory[i].getName().contains("Backup_")) {
//				if (oldest == 0) {
//					oldest = i;
//					oldestTime = directory[i].lastModified();
//				}
//
//				if (directory[i].lastModified() < oldestTime) {
//					oldest = i;
//					oldestTime = directory[i].lastModified();
//				}
//			}
//			
//		}
//		if ( ! savedToday) {
			File fl = new File(BACKUPFOLDER.getPath() + File.separator + title);
			if (!fl.exists()) {
				try {
					fl.createNewFile();
					store(fl, energies, energiesUUID, zones);

				} catch (IOException e) {
					System.err.println("Error creating a backup file");
					e.printStackTrace();
	
				}
			}
		
		
		// I need to exert some control over the delete.
//		if (directory.length > 3 && directory[oldest].getName().contains("Backup_")) {
//			
//			directory[oldest].delete();
//		}
		
		
	}
	
	private boolean shouldBackup() {
		Calendar cal = Calendar.getInstance();
		
		String title = "Backup_" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cal.get(Calendar.MONTH) + "_" + cal.get(Calendar.YEAR) + ".data";
		
		File[] directory = BACKUPFOLDER.listFiles();

		
		boolean savedToday = false;
		for (int i = 0; i < directory.length; i++) {
			if(directory[i].getName().equalsIgnoreCase(title)) {
				savedToday = true;	
			}
			
		}
		
		return ! savedToday;
	}
	
	public void load(Map<String, Integer> energies, Map<UUID, Integer> energiesUUID, Set<Zone> zones){
		Scanner fileScanner;
		try{
			fileScanner = new Scanner(DATAFILE);
			fileScanner.useDelimiter(ZONE_SEPARATOR);
			
			String str;
			while(fileScanner.hasNextLine()){
				str = fileScanner.nextLine();
				if(str.equals(ENERGY_HEADER)){
					continue;
				}
				if(str.equals(ENERGY_UUID_HEADER)){
					break;
				}
				if(str.equals(WARP_HEADER)){
					break;
				}
				energies.put(str.split(ZONE_SEPARATOR)[0], new Integer(Integer.parseInt(str.split(ZONE_SEPARATOR)[1])));
			}while(fileScanner.hasNextLine()){
				str = fileScanner.nextLine();
				if(str.equals(ENERGY_UUID_HEADER)){
					continue;
				}
				if(str.equals(WARP_HEADER)){
					break;
				}
				String[] info = str.split(ZONE_UUID_SEPARATOR);
				energiesUUID.put(UUID.fromString(info[0]), 
						new Integer(Integer.parseInt(info[1])));
			}
			while(fileScanner.hasNextLine()){
				zones.add(fromString(fileScanner.nextLine()));
			}
			str = null;
			fileScanner.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Zone fromString(String ref){
		try{
			String[] prelim = ref.split(ZONE_UUID_SEPARATOR);
			String[] info = prelim[0].split(ZONE_SEPARATOR);
			//List<String> info = new ArrayList<String>();
			
			String nam = info[0];
			Location loc;
			loc = new Location(plugin.getServer().getWorld(info[2]), Double.parseDouble(info[3]), Double.parseDouble(info[4]), Double.parseDouble(info[5]));
			loc.setYaw(Float.parseFloat(info[6]));
			
			if(info[1].equals("open")){
				return new Zone(loc,nam);
			}else{
				Set<String> players = new HashSet<String>();
				for(int i = 7; i<info.length;i++){
					players.add(info[i]);
				}
				Zone zon = new Zone(loc,Integer.parseInt(info[1]),nam);
				zon.addPlayers(players);
				
				Set<UUID> uids = new HashSet<UUID>();
				for (int i = 1; i < prelim.length;i++) {
					uids.add(UUID.fromString(prelim[i]));
				}
				zon.addPlayerUUIDs(uids);
				return zon;
			}
			
		}catch(Error e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String zoneToString(Zone zone){
		StringBuffer sb = new StringBuffer(zone.getName()+ZONE_SEPARATOR+( zone.isPublic() ? "open" : zone.getSize() ) +
				ZONE_SEPARATOR + zone.getLocation().getWorld().getName() + ZONE_SEPARATOR + zone.getLocation().getX() + ZONE_SEPARATOR +
				zone.getLocation().getY() + ZONE_SEPARATOR + zone.getLocation().getZ() + ZONE_SEPARATOR + zone.getLocation().getYaw());
		for(String string: zone.getVisitedPlayers()){
			
			sb.append(ZONE_SEPARATOR+string);
		}
		for(UUID uid: zone.getVisitedPlayersUUID()) {
			sb.append(ZONE_UUID_SEPARATOR + uid.toString());
		}
		return sb.toString();
	}
}
