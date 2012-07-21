package com.mtihc.minecraft.regionselfservice.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.RegionTaskDefine;
import com.mtihc.minecraft.regionselfservice.RegionTaskRedefine;
import com.mtihc.minecraft.regionselfservice.exceptions.RegionException;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

public class DefineExecutor {

	

	private RegionSelfServicePlugin plugin;

	public DefineExecutor(RegionSelfServicePlugin plugin) {
		this.plugin = plugin;
	}
	
	private boolean checkRegionCount(Player sender, World world) {
		if(!sender.hasPermission(Permissions.BYPASSMAX_REGIONS)) {
			int count = plugin.regions().getRegionCountOfPlayer(world, sender.getName());
			int maxCount = plugin.config().settings().getMaxRegionsPerPlayer();
			if(count >= maxCount) {
				sender.sendMessage(ChatColor.RED + "You already have " + count + " regions.");
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return true;
		}
	}
	
	public boolean redefine(Player sender, String regionName, int topY, int bottomY) {
		// get player's selection
		Selection sel;
		try {
			sel = plugin.getWorldGuard().getWorldEdit().getSelection(sender);
		} catch(CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return false;
		}
		
		// get selection's world
		World world = sel.getWorld();
		
		// don't check region count
		

		// get selected region
		Region selectedRegion;
		try {
			selectedRegion = sel.getRegionSelector().getRegion();
		} catch (IncompleteRegionException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return false;
		}
		
		RegionManager mgr = plugin.getWorldGuard().getRegionManager(world);

		// check region existance, should already exist
		ProtectedRegion existing = mgr.getRegion(regionName);
		
		if (existing == null) {
			// does not exist
			String msg = new RegionException(RegionException.Type.REGION_NOT_EXIST, regionName, world.getName()).getMessage();
			sender.sendMessage(ChatColor.RED + msg);
			return false;
		}
		else if(!existing.isOwner(plugin.getWorldGuard().wrapPlayer(sender)) && !sender.hasPermission(Permissions.REDEFINE_ANYREGION)) {
			// must be owner
			sender.sendMessage(ChatColor.RED + "You can only redefine you own regions.");
			return false;
		}
		
		

		// check if region name is valid
		if (!isValidRegionName(regionName)) {
			String msg = new RegionException(RegionException.Type.REGION_NAME_INVALID, regionName, "").getMessage();
			sender.sendMessage(ChatColor.RED + msg);
			return false;
		}
		
		//
		// get selection Size and Bounds
		//
		int by;
		int ty;
		if(bottomY == -1) {
			by = selectedRegion.getMaximumPoint().getBlockY();
		}
		else {
			by = bottomY;
		}
		if(topY == -1) {
			ty = selectedRegion.getMinimumPoint().getBlockY();
		}
		else {
			ty = topY;
		}
		
		if(ty < by) {
			int y = ty;
			ty = by;
			by = y;
		}
		if(topY < bottomY) {
			int y = topY;
			topY = bottomY;
			bottomY = y;
		}
		
		BlockVector min = new BlockVector(selectedRegion.getMinimumPoint().getBlockX(), by, selectedRegion.getMinimumPoint().getBlockZ());
		BlockVector max = new BlockVector(selectedRegion.getMaximumPoint().getBlockX(), ty, selectedRegion.getMaximumPoint().getBlockZ());
		
		int width = Math.abs(max.getBlockX() - min.getBlockX()) + 1;
		int length = Math.abs(max.getBlockZ() - min.getBlockZ()) + 1;
		int height = Math.abs(topY - bottomY) + 1;
		
		int minY = plugin.config().settings().getMinimumY();
		int maxY = plugin.config().settings().getMaximumY();
		int minHeight = plugin.config().settings().getMinimumHeight();
		int maxHeight = plugin.config().settings().getMaximumHeight();
		int minWidthLength = plugin.config().settings().getMinimumWidthLength();
		int maxWidthLength = plugin.config().settings().getMaximumWidthLength();
		//
		// check min max
		//
		if(!sender.hasPermission(Permissions.CREATE_ANYSIZE)) {
			if(width < minWidthLength || length < minWidthLength || height < minHeight) {
				String msg = new RegionException(RegionException.Type.SELECTION_TOO_SMALL, width, length, height, minWidthLength, maxWidthLength, minHeight, maxHeight).getMessage();
				sender.sendMessage(ChatColor.RED + msg);
				return false;
			}
			else if(width > maxWidthLength || length > maxWidthLength || height > maxHeight) {
				String msg = new RegionException(RegionException.Type.SELECTION_TOO_BIG, width, length, height, maxWidthLength, maxWidthLength, minHeight, maxHeight).getMessage();
				sender.sendMessage(ChatColor.RED + msg);
				return false;
			}
			if(topY > maxY) {
				String msg = new RegionException(RegionException.Type.SELECTION_TOO_HIGH, topY, bottomY, minY, maxY).getMessage();
				sender.sendMessage(ChatColor.RED + msg);
				return false;
			}
			if(bottomY < minY) {
				String msg = new RegionException(RegionException.Type.SELECTION_TOO_LOW, topY, bottomY, minY, maxY).getMessage();
				sender.sendMessage(ChatColor.RED + msg);
				return false;
			}
		}
		
		

		// create protected region
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName,
				min, max);
		
		region.setFlags(existing.getFlags());
		region.setMembers(existing.getMembers());
		region.setOwners(existing.getOwners());
		try {
			// copy the parent from the existing region,
			// but we will do the normal procedure after this...
			// to see if it gets an automatic parent etc.
			region.setParent(existing.getParent());
		} catch (CircularInheritanceException e) {
		}
		region.setPriority(existing.getPriority());

		
		
		
		if(!plugin.config().settings().allowOverlapUnownedRegions() && plugin.regions().overlapsUnownedRegion(region, sender.getWorld(), sender)) {
			// overlap unowned region, not allowed
			sender.sendMessage(ChatColor.RED + "The new region would overlap with someone else's region.");
			return false;
		}
		else {
			// not overlapping or it's allowed to overlap unowned regions
			boolean doAutomaticParent = plugin.config().settings().automaticParent();
			boolean allowAnywhere = sender.hasPermission(Permissions.CREATE_ANYWHERE);
			ProtectedRegion parentRegion;
			if(!allowAnywhere || doAutomaticParent) {
				// we need a parent
				parentRegion = plugin.regions().getAutomaticParentRegion(region, world, sender);
				
				if(parentRegion == null) {
					if(!allowAnywhere) {
						// automatic parent was not found, but it's required...
						// because player can only create regions inside owned existing regions.
						sender.sendMessage(ChatColor.RED + "The new region wouldn't be inside the existing region that you own");
						return false;
					}
				}
				else if(doAutomaticParent) {
					// found parent region,
					// and according to the configuration,
					// we should do automatic parenting
					try {
						region.setParent(parentRegion);
					} catch (CircularInheritanceException e) {
					}
				}
			}
			
		}
		
		// is cost enabled in config ?
		boolean enableCost = costEnabled();
		// does the player bypass cost ?
		boolean bypassCost = costBypass(sender, Permissions.CREATE_BYPASSCOST, enableCost);
		
		double cost = 0;
		if (enableCost) {
			// how much does the resizing cost?
			double blockWorth = plugin.config().settings().getBlockWorth();
			double worth = plugin.getRegionWorth(region, blockWorth);
			double worthExisting = plugin.getRegionWorth(existing, blockWorth);
			// pay/refund the difference
			cost = worth - worthExisting;
		}
		
		//
		// owners
		//
		// who will get the money ?
		Set<String> depositTo = new HashSet<String>();
		// who are the default owners in the config ?
		List<String> ownerList = plugin.config().settings().getDefaultOwners();
		
		if (enableCost) {
			// cost is enabled
			// owners in config will get money, if there are any
			if (ownerList != null && ownerList.size() > 0) {
				// owners in config will get money
				for (String ownerName : ownerList) {
					depositTo.add(ownerName);
				}
			}
		}
		
		int oldWidth = Math.abs(existing.getMaximumPoint().getBlockX() - existing.getMinimumPoint().getBlockX()) + 1;
		int oldLength = Math.abs(existing.getMaximumPoint().getBlockZ() - existing.getMinimumPoint().getBlockZ()) + 1;
		int oldHeight = Math.abs(existing.getMaximumPoint().getBlockY() - existing.getMinimumPoint().getBlockY()) + 1;
		
		
		RegionTaskRedefine task = new RegionTaskRedefine(plugin, sender.getName(), depositTo, cost, region, world, bypassCost);
		plugin.taskRequest(sender, task);
		if(cost >= 0) {
			sender.sendMessage(ChatColor.GREEN + "To accept the cost of " + ChatColor.WHITE + plugin.economy().format(cost));
			sender.sendMessage(ChatColor.GREEN + "and resize region '" + ChatColor.WHITE + region.getId() + ChatColor.GREEN + "' from " + ChatColor.WHITE + oldWidth + "x" + oldLength + "x" + oldHeight + ChatColor.GREEN + " to " + ChatColor.WHITE + width + "x" + length + "x" + height + ChatColor.GREEN + ",");
			sender.sendMessage(ChatColor.GREEN + "type " + ChatColor.WHITE + "/selfservice accept" + ChatColor.GREEN);
		}
		else {
			sender.sendMessage(ChatColor.GREEN + "To accept resizing region '" + ChatColor.WHITE + region.getId() + ChatColor.GREEN + "' from " + ChatColor.WHITE + oldWidth + "x" + oldLength + "x" + oldHeight + ChatColor.GREEN + " to " + ChatColor.WHITE + width + "x" + length + "x" + height + ChatColor.GREEN + ",");
			sender.sendMessage(ChatColor.GREEN + "type " + ChatColor.WHITE + "/selfservice accept" + ChatColor.GREEN);
			
			String ownerNames = "";
			for (String name : ownerList) {
				ownerNames += ", " + name;
			}
			if(ownerNames.isEmpty()) {
				ownerNames = "nobody";
			}
			else {
				ownerNames = ownerNames.substring(2);
			}
			sender.sendMessage(ChatColor.GREEN + "The refund of " + ChatColor.WHITE + plugin.economy().format(Math.abs(cost)) + ChatColor.GREEN + " will be shared");
			sender.sendMessage(ChatColor.GREEN + "amongst " + ChatColor.WHITE + ownerNames);
		}
		
		return true;
	}

	
	public boolean define(Player sender, String regionName, int priority, int topY, int bottomY) {
		
		// get player's selection
		Selection sel;
		World world;
		try {
			sel = plugin.getWorldGuard().getWorldEdit().getSelection(sender);
			world = sel.getWorld();
		} catch(CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return false;
		} catch(NullPointerException e) {
			sender.sendMessage(ChatColor.RED + "Select a region first.");
			return false;
		}
		
		// get selection's world
		
		// check region count of player, in world
		if(!checkRegionCount(sender, world)) {
			return false;
		}
		
		// get selected region
		Region selectedRegion;
		try {
			selectedRegion = sel.getRegionSelector().getRegion();
		} catch (IncompleteRegionException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return false;
		}
		
		RegionManager mgr = plugin.getWorldGuard().getRegionManager(world);

		// check region existance
		if (mgr.hasRegion(regionName)) {
			// already exists
			String msg = new RegionException(RegionException.Type.REGION_ALREADY_EXIST, regionName, world.getName()).getMessage();
			sender.sendMessage(ChatColor.RED + msg);
			return false;
		}
		
		
		// check if region name is valid
		if (!isValidRegionName(regionName)) {
			String msg = new RegionException(RegionException.Type.REGION_NAME_INVALID, regionName, "").getMessage();
			sender.sendMessage(ChatColor.RED + msg);
			return false;
		}
		
		//
		// get selection Size and Bounds
		//
		int by;
		int ty;
		if(bottomY == -1) {
			by = selectedRegion.getMaximumPoint().getBlockY();
		}
		else {
			by = bottomY;
		}
		if(topY == -1) {
			ty = selectedRegion.getMinimumPoint().getBlockY();
		}
		else {
			ty = topY;
		}
		
		if(ty < by) {
			int y = ty;
			ty = by;
			by = y;
		}
		if(topY < bottomY) {
			int y = topY;
			topY = bottomY;
			bottomY = y;
		}
		
		BlockVector min = new BlockVector(selectedRegion.getMinimumPoint().getBlockX(), by, selectedRegion.getMinimumPoint().getBlockZ());
		BlockVector max = new BlockVector(selectedRegion.getMaximumPoint().getBlockX(), ty, selectedRegion.getMaximumPoint().getBlockZ());
		
		int width = Math.abs(max.getBlockX() - min.getBlockX()) + 1;
		int length = Math.abs(max.getBlockZ() - min.getBlockZ()) + 1;
		int height = Math.abs(topY - bottomY) + 1;
		
		int minY = plugin.config().settings().getMinimumY();
		int maxY = plugin.config().settings().getMaximumY();
		int minHeight = plugin.config().settings().getMinimumHeight();
		int maxHeight = plugin.config().settings().getMaximumHeight();
		int minWidthLength = plugin.config().settings().getMinimumWidthLength();
		int maxWidthLength = plugin.config().settings().getMaximumWidthLength();
		//
		// check min max
		//
		if(!sender.hasPermission(Permissions.CREATE_ANYSIZE)) {
			if(width < minWidthLength || length < minWidthLength || height < minHeight) {
				String msg = new RegionException(RegionException.Type.SELECTION_TOO_SMALL, width, length, height, minWidthLength, maxWidthLength, minHeight, maxHeight).getMessage();
				sender.sendMessage(ChatColor.RED + msg);
				return false;
			}
			else if(width > maxWidthLength || length > maxWidthLength || height > maxHeight) {
				String msg = new RegionException(RegionException.Type.SELECTION_TOO_BIG, width, length, height, maxWidthLength, maxWidthLength, minHeight, maxHeight).getMessage();
				sender.sendMessage(ChatColor.RED + msg);
				return false;
			}
			if(topY > maxY) {
				String msg = new RegionException(RegionException.Type.SELECTION_TOO_HIGH, topY, bottomY, minY, maxY).getMessage();
				sender.sendMessage(ChatColor.RED + msg);
				return false;
			}
			if(bottomY < minY) {
				String msg = new RegionException(RegionException.Type.SELECTION_TOO_LOW, topY, bottomY, minY, maxY).getMessage();
				sender.sendMessage(ChatColor.RED + msg);
				return false;
			}
		}
		
		// create protected region
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName,
				min, max);
		

		if(!plugin.config().settings().allowOverlapUnownedRegions() && plugin.regions().overlapsUnownedRegion(region, sender.getWorld(), sender)) {
			// overlap unowned region, not allowed
			sender.sendMessage(ChatColor.RED + "This region overlaps with someone else's region.");
			return false;
		}
		else {
			// not overlapping or it's allowed to overlap unowned regions
			boolean doAutomaticParent = plugin.config().settings().automaticParent();
			boolean allowAnywhere = sender.hasPermission(Permissions.CREATE_ANYWHERE);
			ProtectedRegion parentRegion;
			if(!allowAnywhere || doAutomaticParent) {
				// we need a parent
				parentRegion = plugin.regions().getAutomaticParentRegion(region, world, sender);
				
				if(parentRegion == null) {
					if(!allowAnywhere) {
						// automatic parent was not found, but it's required...
						// because player can only create regions inside owned existing regions.
						sender.sendMessage(ChatColor.RED + "You can only claim regions inside existing regions that you own");
						return false;
					}
				}
				else if(doAutomaticParent) {
					// found parent region,
					// and according to the configuration,
					// we should do automatic parenting
					try {
						region.setParent(parentRegion);
					} catch (CircularInheritanceException e) {
					}
				}
			}
			
		}
		
		// is cost enabled in config ?
		boolean enableCost = costEnabled();
		// does the player bypass cost ?
		boolean bypassCost = costBypass(sender, Permissions.CREATE_BYPASSCOST, enableCost);
		
		
		double cost = 0;
		if (enableCost) {
			// how much does the region cost?
			cost = plugin.getRegionWorth(region,
					plugin.config().settings().getBlockWorth());
		}
	
		//
		// owners
		//
		// who will get the money ?
		Set<String> depositTo = new HashSet<String>();
		// who are the default owners in the config ?
		List<String> ownerList = plugin.config().settings().getDefaultOwners();
		
		DefaultDomain ownersDomain;
		
		if (enableCost) {
			// cost is enabled
			ownersDomain = new DefaultDomain();
			// player will be owner
			ownersDomain.addPlayer(plugin.getWorldGuard().wrapPlayer(sender));
			// owners in config will get money, if there are any
			if (ownerList != null && ownerList.size() > 0) {
				// owners in config will get money
				for (String ownerName : ownerList) {
					depositTo.add(ownerName);
				}
			}
		} else {
			// cost is not enabled
			// who will be owner depends on config
			if (ownerList == null || ownerList.size() < 1) {
				// no owners in config, owner is sender
				ownersDomain = new DefaultDomain();
				ownersDomain.addPlayer(plugin.getWorldGuard().wrapPlayer(sender));
			} else {
				// owners are in config
				// owners from cronfig will be owners
				ownersDomain = new DefaultDomain();
				for (Object ownerName : ownerList) {
					ownersDomain.addPlayer(ownerName.toString().trim());
				}
			}
		}
		region.setOwners(ownersDomain);
		RegionTaskDefine task = new RegionTaskDefine(plugin, sender.getName(), depositTo, cost, region, world, bypassCost);
		plugin.taskRequest(sender, task);
		if(task.acceptIsRequired()){
			sender.sendMessage(ChatColor.GREEN + "To accept the cost of " + ChatColor.WHITE + plugin.economy().format(cost) + ChatColor.GREEN + ", ");
			sender.sendMessage(ChatColor.GREEN + "and create region '" + ChatColor.WHITE + region.getId() + ChatColor.GREEN + "' (size: " + width + "x" + length + "),");
			sender.sendMessage(ChatColor.GREEN + "type " + ChatColor.WHITE + "/selfservice accept" + ChatColor.GREEN);
			
		}
		return true;
	}

	/**
	 * 
	 * @param regionName
	 * @return
	 */
	private boolean isValidRegionName(String regionName) {
		if (regionName == null || !ProtectedRegion.isValidId(regionName)
				|| regionName.equalsIgnoreCase("__GLOBAL__")
				|| regionName.matches("\\d")) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean costEnabled() {
		return plugin.config().settings().getEnableOnCreateCost();
	}
	
	private boolean costBypass(CommandSender sender, String permission, boolean costEnabled) {
		boolean bypassCost = !costEnabled;
		if (!bypassCost
				&& sender.hasPermission(permission)) {
			bypassCost = true;
		}
		return bypassCost;
	}

}
