package com.PromethiaRP.Draeke.PreVisit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
/**
 * The listener class meant to handle all of the events and manage the Zones
 * @author Draeke_Forther
 *
 */
public class ServerPL implements Listener {

	public PreVisit plugin;
	private Set<Zone> zones = new HashSet<Zone>();
	private static Map<String,Integer> energies = new HashMap<String,Integer>();
	private static Map<UUID, Integer> energiesUUID = new HashMap<UUID, Integer>();
	private static ArrayList<String> coolDown = new ArrayList<String>();
	private Date lastDate;
	
	private final File DATAFILE;
	private final File BACKUPFOLDER;
	
	public static final boolean COMBAT_WAIT = false;
	public static final String ENERGY_HEADER = 		"========Energies========";
	public static final String ENERGY_UUID_HEADER = "========UniqueEnergies========";
	public static final String WARP_HEADER = 		"========Warps========";
	
	public ServerPL(PreVisit pv, File dataFile, File backupFolder){
		plugin = pv;
		lastDate = new Date();
		DATAFILE = dataFile;
		BACKUPFOLDER = backupFolder;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		checkEnergy(e.getPlayer());
		for(Zone zon: zones){
			if(zon.withinRange(e.getPlayer())){
				if(zon.addPlayer(e.getPlayer())){
					e.getPlayer().sendMessage(ChatColor.GREEN + "You have discovered the warp " + ChatColor.GOLD + zon.getName());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent e){
		if ( ! COMBAT_WAIT) {
			return;
		}
		if(e.isCancelled()){
			return;
		}
		if(!(e.getEntity() instanceof Player)){
			return;
		}
		if(!((Player)e.getEntity()).hasPermission("previsit.combatwait")){
			return;
		}
		if(coolDown.contains(((Player)e.getEntity()).getName())){
			coolDown.remove(((Player)e.getEntity()).getName());
			
		}
		coolDown.add(((Player)e.getEntity()).getName());	
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

			@Override
			public void run() {
				if(!ServerPL.coolDown.isEmpty()){
					ServerPL.coolDown.remove(0);
				}
			}
			
		}, 100L);
	}
	
	public boolean requestTeleport(Player player, String warp){
		checkEnergy(player);
//		if(!canTeleport(player)){
//			player.sendMessage(ChatColor.RED + "You can not fast travel while in combat.");
//			return false;
//		}
		for(Zone zone: zones){
			if(zone.getName().equalsIgnoreCase(warp)){
				if(isAccessible(player,zone)){
					if(player.hasPermission("previsit.useenergy")) {
						int playerEnergy = energiesUUID.get(player.getUniqueId()).intValue();
						energiesUUID.put(player.getUniqueId(),new Integer(playerEnergy-zone.getRequiredEnergy(player)));
					}
					player.sendMessage(ChatColor.GREEN+"You are teleporting to " + ChatColor.GOLD+zone.getName());
					return player.teleport(zone.getLocation());
					//return ServerInterface.setPlayerLocation(zone.World, zone.X, zone.Y, zone.Z, zone.Yaw, zone.Pitch, player.getName());
				}else{
					player.sendMessage(ChatColor.RED + "You do not have access to this warp");
					return true;
				}
			}
		}
		player.sendMessage(ChatColor.RED + "The warp " + ChatColor.GOLD + warp + ChatColor.RED+" was not found.");
		return true;
	}
	
	public boolean canTeleport(Player player){
		return player.isOp() || !(player.hasPermission("previsit.combatwait") && coolDown.contains(player.getName()));
		
	}
	
	public boolean createWarp(Location loc, String name, int size){
		for(Zone zone: zones){
			if(zone.getName().equalsIgnoreCase(name)){
				return false;
			}
		}
		zones.add(new Zone(loc,size,name));
		store();
		return true;
	}
	public boolean createWarp(Location loc, String name){
		for(Zone zone: zones){
			if(zone.getName().equalsIgnoreCase(name)){
				return false;
			}
		}
		zones.add(new Zone(loc,name));
		store();
		return true;
	}
	
	public static void checkEnergy(Player player){
		if (energies.containsKey(player.getName())) {
			Integer intgr = energies.get(player.getName());
			if (intgr == null) {
				intgr = new Integer(0);
			}
			energiesUUID.put(player.getUniqueId(), intgr);
			energies.remove(player.getName());
		}
		
		if (energiesUUID.get(player.getUniqueId()) == null) {
			energiesUUID.put(player.getUniqueId(), new Integer(0));
		}
	}
	public static int getEnergy(Player player){
		checkEnergy(player);
		if (energiesUUID.get(player.getUniqueId()) == null) {
			System.out.println("Error Error, the result from get(player) is null!");
		}
		return energiesUUID.get(player.getUniqueId()).intValue();
	}
	
	

	public void requestEnergy(Player player){
		player.sendMessage("Your current energy level is: " + getEnergy(player));
	}

	/**
	 * If the player has not been on to have their tracker updated, they do not get more energy
	 */
	public void updateEnergies(){
		long value = energyValue();
//		for(String name: energies.keySet()){
//			energies.put(name,new Integer((int)(energies.get(name).intValue()+(value * (plugin.getServer().getPlayer(name)!=null? .125:1)))));
//		}
		for(UUID uid: energiesUUID.keySet()) {
			energiesUUID.put(uid,new Integer((int)(energiesUUID.get(uid).intValue()+(value * (plugin.getServer().getPlayer(uid)!=null? .125:1)))));
		}
		
	}
	private long energyValue(){
		long l = new Date().getTime() - lastDate.getTime();
		lastDate = null; lastDate = new Date();
		return l/6000;
	}
	
	public boolean isAccessible(Player player, Zone zone){
		
		boolean worldChangeAllowed = false;
		boolean worldChange = !player.getWorld().getName().equalsIgnoreCase(zone.getLocation().getWorld().getName());
//		System.out.println(zone.getName());
//		System.out.println(zone.getLocation().getWorld().getName());
//		System.out.println(player.getWorld().getName());
		boolean visited = zone.hasVisited(player);
		boolean energyRequirementsMet = getEnergy(player)>=zone.getRequiredEnergy(player);
		boolean playerMustUseEnergy = player.hasPermission("previsit.useenergy");
		boolean publicZone = zone.isPublic();
		boolean playerAllWarps = player.hasPermission("previsit.allwarps");
		
		if (playerAllWarps) {	// Ops only
//			System.out.println("Player all warps: " + playerAllWarps);
			return true;
		}
		// If world change is not allowed and the player is trying to change worlds
		if ( ! worldChangeAllowed && worldChange) {
//			System.out.println("World change allowed: " + worldChangeAllowed+ ", Player change world: " + worldChange);
			
			return false;
		}
		
		if (playerMustUseEnergy && !energyRequirementsMet) {
//			System.out.println("Player need energy: " + playerMustUseEnergy + ", Player meets energy: " + energyRequirementsMet);
			return false;
		}
		
		// Zone is not public, and player has not visited
		if ( publicZone) {
//			System.out.println("Public zone returns true");
			return true;
		}
		
		return visited;
		
//		return playerAllWarps || ( ( !worldChangeAllowed || !worldChange ) && ( ( playerMustUseEnergy && energyRequirementsMet) ) );
//		return ((zone.hasVisited(player))
//				&&
//				((getEnergy(player)>=zone.getRequiredEnergy(player))
//				||
//				!player.hasPermission("previsit.useenergy")))
//				||
//				zone.isPublic()
//				||
//				(player.hasPermission("previsit.allwarps"));
	}
	

	public void getWarps(Player player){
		String warps = "";
		for(Zone zon: zones){
			if(zon.hasVisited(player)||zon.isPublic()||player.hasPermission("previsit.allwarps")){
				warps = warps + (isAccessible(player,zon) ? ChatColor.GOLD:ChatColor.RED)+zon.getName()+ChatColor.WHITE+", ";
			}
		}
		if(warps.length()==0){
			player.sendMessage("There are no warps available to you right now.");
			return;
		}else{
			player.sendMessage(ChatColor.DARK_GREEN+"Here is a list of warps for you.");
			player.sendMessage("The ones listed in red are too far from you for your current energy level.");
		}
		if(warps.substring(warps.length()-2).equals(", ")){
			warps = warps.substring(0,warps.length()-2)+".";
		}
		player.sendMessage(warps);
	}
	

	public void store() {
		store(DATAFILE);
		checkBackup();
	}
	
	public void store(File storage){
		updateEnergies();
		//validateFile(storage);
		
		FileWriter fw;
		PrintWriter pw;
		try{
			fw = new FileWriter(storage);
			pw = new PrintWriter(fw,true);
			pw.println(ENERGY_HEADER);
			for(String name: energies.keySet()){
				pw.println(name+Zone.SEPARATOR+energies.get(name).intValue());
			}
			pw.println(ENERGY_UUID_HEADER);
			for(UUID uid: energiesUUID.keySet()) {
				pw.println(uid.toString() + Zone.UUID_SEPARATOR+energiesUUID.get(uid).intValue());
			}
			pw.println(WARP_HEADER);
			for(Zone zone: zones){
				pw.println(zone.toString());
			}
			pw.close();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public boolean checkBackup() {
		Calendar cal = Calendar.getInstance();
		String title = "Backup_" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cal.get(Calendar.MONTH) + "_" + cal.get(Calendar.YEAR) + ".data";
		File[] directory = BACKUPFOLDER.listFiles();
		int oldest = 0;
		long oldestTime = 0;
		boolean savedToday = false;
		for (int i = 0; i < directory.length; i++) {
			if(directory[i].getName().contains("Backup_")) {
				if (oldest == 0) {
					oldest = i;
					oldestTime = directory[i].lastModified();
				}
				
				if (directory[i].getName().equalsIgnoreCase(title)) {
					savedToday = true;
				}
				
				if (directory[i].lastModified() < oldestTime) {
					oldest = i;
					oldestTime = directory[i].lastModified();
				}
			}
			
		}
		if ( ! savedToday) {
			File fl = new File(BACKUPFOLDER.getPath() + File.separator + title);
			if (!fl.exists()) {
				try {
					fl.createNewFile();
					store(fl);
					savedToday = true;
				} catch (IOException e) {
					System.err.println("Error creating a backup file");
					e.printStackTrace();
					savedToday = false;
				}
			}
		}
		
		// I need to exert some control over the delete.
		if (directory.length > 3 && directory[oldest].getName().contains("Backup_")) {
			
			directory[oldest].delete();
		}
		
		return savedToday;
	}
	
	public void load(){
		Scanner fileScanner;
		try{
			fileScanner = new Scanner(DATAFILE);
			fileScanner.useDelimiter(Zone.SEPARATOR);
			
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
				energies.put(str.split(Zone.SEPARATOR)[0], new Integer(Integer.parseInt(str.split(Zone.SEPARATOR)[1])));
			}while(fileScanner.hasNextLine()){
				str = fileScanner.nextLine();
				if(str.equals(ENERGY_UUID_HEADER)){
					continue;
				}
				if(str.equals(WARP_HEADER)){
					break;
				}
				String[] info = str.split(Zone.UUID_SEPARATOR);
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
			String[] prelim = ref.split(Zone.UUID_SEPARATOR);
			String[] info = prelim[0].split(Zone.SEPARATOR);
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
	
	// It's unnecessary to store the entire file when just the energies are being updated
	public void giveEnergyToAll(int amount){
		for(UUID uid: energiesUUID.keySet()){
			energiesUUID.put(uid, new Integer(energiesUUID.get(uid).intValue()+amount));
		}
	}
	
	public boolean deleteWarp(String nam) {
		for(Zone zone: zones){
			if(zone.getName().equalsIgnoreCase(nam)){
				zones.remove(zone);
				store();
				return true;
			}
		}
		return false;
	}
	
	public int getRequiredEnergy(Player player, String zone){
		for(Zone zon : zones){
			if(zon.getName().equalsIgnoreCase(zone)){
				return zon.getRequiredEnergy(player);//.getWorld().getName(),player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ());
			}
		}
		return -1;
	}
}
