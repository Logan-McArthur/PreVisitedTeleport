package com.PromethiaRP.Draeke.PreVisit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class file in the PreVisitedTeleport plugin designed for use with bukkit
 * 
 * @author Draeke_Forther
 * @version 2.0.0
 * 
 */
public class PreVisit extends JavaPlugin {

	
	private final Logger logger = Logger.getLogger("Minecraft");
	private ServerPL serverlistener;
	
	private final String FILENAME = "PreVisit.data";
	private File DATAFOLDER = new File("plugins" +File.separator +"PreVisitedTeleport");
	private File DATAFILE = new File(DATAFOLDER.getPath()+File.separator + FILENAME);
	private File BACKUPFOLDER = new File(DATAFOLDER.getPath() + File.separator + "Backups");
	
	
	@Override
	public void onEnable(){
		checkFile();
		serverlistener = new ServerPL(this,DATAFILE, BACKUPFOLDER);
		serverlistener.load();
		getServer().getPluginManager().registerEvents(serverlistener, this);
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
	
	@Override
	public void onDisable(){
		serverlistener.store();
	}
	
	public void log(String info){
		logger.info("[PVT]: " + info);
	}
	
	public boolean createWarp(Location loc, String name, int size){
		return serverlistener.createWarp(loc, name, size);
	}
	//
	public boolean createWarp(Location loc, String name){
		return serverlistener.createWarp(loc, name);
	}
	public boolean requestTeleport(Player player, String warp){
		return serverlistener.requestTeleport(player, warp);
	}
	
	private void warps(Player player){
		serverlistener.getWarps(player);
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("ft")){
			if(args.length < 1){
				return false;
			}
			if(!(sender instanceof Player)){
				return false;
			}
//			System.out.println("Begin debug for command ft");
			String warpname = compileArgs(args,true);
//			System.out.println("String warpname equals \"" + warpname + "\"");
			return onCommandFt((Player) sender, warpname);
		
		}else if(cmd.getName().equalsIgnoreCase("warps")){
			//TODO: More warps features
			String warpname = null;
			if(args.length>0){
				warpname = compileArgs(args,true);
			}
			onCommandWarps((Player)sender,warpname);
			return true;
			
		}else if(cmd.getName().equalsIgnoreCase("energy")){
			if(!(sender instanceof Player)){
				return false;
			}
			if(((Player)sender).isOp()){
				if(args.length == 0) {
					requestEnergy((Player)sender);
					return true;
				}
				if(args[0].equalsIgnoreCase("giveall")){
					giveEnergyToAll(Integer.parseInt(args[1]));
					return true;
				}
			}
			requestEnergy((Player)sender);
			return true;
		
		}else if(cmd.getName().equalsIgnoreCase("svwarp")){
			if(!(sender instanceof Player)){
				return false;
			}
			if(((Player)sender).hasPermission("previsit.svwarp")){
				if(args.length<1){
					return false;
				}
				try{
					int radius = Integer.parseInt(args[0]);
					if(args.length<2){
						return false;
					}
					String nam = compileArgs(args,false);
					
					if(createWarp(((Player)sender).getLocation(), nam, radius)){
						sender.sendMessage(ChatColor.GREEN + "You have successfully created the warp called: " + ChatColor.GOLD + nam);
					}else{
						sender.sendMessage(ChatColor.RED + "There was a problem creating the warp called: " + ChatColor.GOLD + nam);
					}
					return true;
				}catch(NumberFormatException e){
					String nam = compileArgs(args,true);
					if(createWarp(((Player)sender).getLocation(), nam)){
						sender.sendMessage(ChatColor.GREEN + "You have successfully created the public warp called: " + ChatColor.GOLD + nam);
					}else{
						sender.sendMessage(ChatColor.RED + "There was a problem creating the warp called: " + ChatColor.GOLD + nam);
					}
					return true;
				}
			}
		
		}else if(cmd.getName().equalsIgnoreCase("dvwarp")){
			if(args.length<1){
				return false;
			}
			if(sender instanceof Player){
				if(((Player)sender).hasPermission("previsit.dvwarp")){
					String nam = compileArgs(args,true);
					
					if(deleteWarp(nam)){
						sender.sendMessage(ChatColor.GREEN + "Successfully deleted the warp called: " + ChatColor.GOLD + nam);
						return true;
					}else{
						sender.sendMessage(ChatColor.RED + "An error occured deleting the warp called: " + ChatColor.GOLD + nam);
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private boolean onCommandFt(Player play, String WarpName) {
		return requestTeleport(play, WarpName);
	}
	
	private boolean onCommandWarps(Player play, String WarpName) {
		if (WarpName == null) {
			warps(play);
			
		} else {
			int requiredEnergy = requiredEnergy(play,WarpName);
			if (requiredEnergy == -1) {
				play.sendMessage(ChatColor.RED+"Invalid warp name.");
			} else {
				play.sendMessage("The energy required for you to teleport to " +
						ChatColor.GOLD + WarpName + ChatColor.WHITE + " is: " + requiredEnergy);
			}
			
		}
		
		return true;
	}
	
	private String compileArgs(String[] args, boolean startAt0){
		String nam = "";
		for(int i = (startAt0?0:1); i < args.length; i++){
			nam = nam + args[i]+" ";
		}
		return nam.substring(0, nam.length()-1);
	}
	
	public int requiredEnergy(Player play, String zone){
		return serverlistener.getRequiredEnergy(play, zone);
	}
	public void giveEnergyToAll(int amount){
		
		serverlistener.giveEnergyToAll(amount);
	}
	public void requestEnergy(Player player){
		serverlistener.requestEnergy(player);
	}

	public boolean deleteWarp(String nam) {
		return serverlistener.deleteWarp(nam);
	}
}
