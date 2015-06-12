package com.PromethiaRP.Draeke.PreVisit;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageDispatcher {
	
	public static void createWarpSuccess(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.GREEN + "You have successfully created the warp called: " + ChatColor.GOLD + warpName);
	}
	public static void createWarpFailure(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.RED + "There was a problem creating the warp called: " + ChatColor.GOLD + warpName);
	}
	
	
	public static void createPublicWarpSuccess(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.GREEN + "You have successfully created the public warp called: " + ChatColor.GOLD + warpName);
	}
	public static void createPublicWarpFailure(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.RED + "There was a problem creating the warp called: " + ChatColor.GOLD + warpName);
	}

	
	public static void deleteWarpSuccess(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.GREEN + "Successfully deleted the warp called: " + ChatColor.GOLD + warpName);
	}
	public static void deleteWarpFailure(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.RED + "An error occured deleting the warp called: " + ChatColor.GOLD + warpName);
	}
	
	
	public static void warpNotFound(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.RED + "The warp " + ChatColor.GOLD + warpName + ChatColor.RED+" was not found.");
	}
	public static void warpsList(CommandSender sender, WarpList list) {
		sender.sendMessage(ChatColor.DARK_GREEN+"Here is a list of warps for you.");
		sender.sendMessage("The ones listed in red are too far from you for your current energy level.");
//		warps = warps + (isAccessible(player,zon) ? ChatColor.GOLD:ChatColor.RED)+zon.getName()+ChatColor.WHITE+", ";
		StringBuilder builder = new StringBuilder();
		if (list.accessibleWarps[0]) {
			builder.append(ChatColor.GOLD);
		} else {
			builder.append(ChatColor.RED);
		}
		builder.append(list.warpNames[0]);
		for (int i = 1; i < list.warpNames.length; i++) {
			builder.append(ChatColor.WHITE + ", ");
			if (list.accessibleWarps[i]) {
				builder.append(ChatColor.GOLD);
			} else {
				builder.append(ChatColor.RED);
			}
			builder.append(list.warpNames[i]);
		}
		builder.append(ChatColor.WHITE + ".");
		sender.sendMessage(builder.toString());
	}
	public static void warpsNoneAvailable(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "There are no warps available to you right now.");
	}
	public static void warpNotAccessible(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.RED + "You do not have access to the warp " + ChatColor.GOLD + warpName + ChatColor.RED + ".");
	}
	
	public static void energyRequiredToTeleport(CommandSender sender, String warpName, int energyAmount) {
		sender.sendMessage("The energy required for you to teleport to " + ChatColor.GOLD + warpName + ChatColor.WHITE + " is: " + energyAmount + ".");
	}
	public static void energyLevelSelf(CommandSender sender, int energyLevel) {
		sender.sendMessage("Your current energy level is: " + energyLevel + ".");
	}
	public static void energyGiveAll(CommandSender sender, int energyAmount) {
		sender.sendMessage("Giving " + energyAmount + " energy to every player.");
	}
	
	
	public static void teleportAdminSuccess(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.GREEN + "Using admin permissions to teleport to " + ChatColor.GOLD + warpName + ChatColor.WHITE + ".");
	}
	public static void teleportPublicSuccess(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.GREEN + "You are teleporting to the public warp " + ChatColor.GOLD + warpName + ChatColor.GREEN + ".");
	}
	public static void teleportVisitSuccess(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.GREEN + "You are teleporting to the warp " + ChatColor.GOLD + warpName + ChatColor.GREEN + ".");
	}
	public static void teleportFailEnergy(CommandSender sender, String warpName, int requiredEnergy, int energyLevel) {
		sender.sendMessage(ChatColor.RED + "You do not have enough energy to teleport to " + ChatColor.GOLD + warpName + ChatColor.RED + ".");
	}
	public static void teleportFailWorldChange(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.RED + "You are not allowed to teleport to other worlds.");
	}
	public static void teleportFailNotFound(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.RED + "You must discover the warp before you can teleport there.");
	}
	
	
	public static void discoverPublicWarp(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.GREEN + "You have discovered the public warp " + ChatColor.GOLD + warpName + ChatColor.GREEN + ".");
	}
	public static void discoverRegularWarp(CommandSender sender, String warpName) {
		sender.sendMessage(ChatColor.GREEN + "You have discovered the warp " + ChatColor.GOLD + warpName + ChatColor.GREEN + ".");
	}
}
