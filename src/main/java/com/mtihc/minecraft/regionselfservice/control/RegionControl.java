package com.mtihc.minecraft.regionselfservice.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * 
 * @author Mitch
 *
 */
public class RegionControl {

	private WorldGuardPlugin worldGuard;

	/**
	 * 
	 * @param worldGuard
	 */
	public RegionControl(WorldGuardPlugin worldGuard) {
		this.worldGuard = worldGuard;
	}
	

	/**
	 * 
	 * @param world
	 * @param playerName
	 * @return
	 */
	public int getRegionCountOfPlayer(World world, String playerName) {
		RegionManager mgr = worldGuard.getRegionManager(world);
		Collection<ProtectedRegion> regions = mgr.getRegions().values();
		if(regions == null || regions.isEmpty()) {
			return 0;
		}
		int count = 0;
		for (ProtectedRegion region : regions) {
			if(region.getOwners().getPlayers().contains(playerName.toLowerCase())) {
				count++;
			}
		}
		return count;
		
	}

	/**
	 * 
	 * @param regionName
	 * @return
	 */
	public boolean isValidRegionName(String regionName) {
		if (regionName == null || !ProtectedRegion.isValidId(regionName)
				|| regionName.equalsIgnoreCase("__GLOBAL__")
				|| regionName.matches("\\d")) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Shows info about a region to the command sender.
	 * <p>
	 * Information includes: size, bounds, owners, priority
	 * </p>
	 * 
	 * @param sender
	 *            The command sender
	 * @param world
	 *            The world that the region resides in
	 * @param regionName
	 *            The name of the region to show info of
	 */
	public final void sendRegionInfo(CommandSender sender, World world,
			String regionName) {
		// get region
		RegionManager mgr = worldGuard.getRegionManager(world);
		ProtectedRegion region = mgr.getRegion(regionName);
		if(region == null) {
			sender.sendMessage(ChatColor.RED + "Region " + ChatColor.WHITE + "'" + regionName + "'" + ChatColor.RED + " does not exist in world " + ChatColor.WHITE + "'" + world.getName() + "'" + ChatColor.RED + ".");
		}
		else {
			sendRegionInfo(sender, region);
		}
	}

	public final void sendRegionInfo(CommandSender sender, ProtectedRegion region) {
		BlockVector min = region.getMinimumPoint();
		BlockVector max = region.getMaximumPoint();
		int width = Math.abs(min.getBlockX() - max.getBlockX()) + 1;
		int length = Math.abs(min.getBlockZ() - max.getBlockZ()) + 1;
		int height = Math.abs(min.getBlockY() - max.getBlockY()) + 1;
		sendRegionInfo(sender, region, width, length, height);
	}
	
	/**
	 * Shows info about a region to the command sender.
	 * <p>
	 * Information includes: size, bounds, owners, priority
	 * </p>
	 * 
	 * @param sender
	 *            The command sender
	 * @param region
	 *            The region to show info of
	 */
	public void sendRegionInfo(CommandSender sender, ProtectedRegion region, int width, int length, int height) {
		BlockVector min = region.getMinimumPoint();
		BlockVector max = region.getMaximumPoint();
		
		// send info
		sender.sendMessage(ChatColor.GREEN + "Region name: " + ChatColor.WHITE
				+ region.getId());
		
		// Priority and Parent
		String parentId;
		if(region.getParent() == null) {
			parentId = "no parent";
		}
		else {
			parentId = region.getParent().getId();
		}
		sender.sendMessage(ChatColor.DARK_GREEN + "Priority: " + ChatColor.WHITE
				+ String.valueOf(region.getPriority()) + " " + ChatColor.DARK_GREEN + "Parent: " + ChatColor.WHITE + parentId);
		// Owner(s)
		sender.sendMessage(ChatColor.DARK_GREEN + "Owner(s): " + ChatColor.WHITE
				+ region.getOwners().toUserFriendlyString());
		// Bounds
		sender.sendMessage(ChatColor.YELLOW + "Bounds: " + ChatColor.WHITE
				+ "(" + min.getBlockX() + ", " + min.getBlockY() + ", "
				+ min.getBlockZ() + ") (" + max.getBlockX() + ", "
				+ max.getBlockY() + ", " + max.getBlockZ() + ")");
		// Size
		sender.sendMessage(ChatColor.YELLOW + "Size: " + ChatColor.WHITE
				+ (width + "x" + length) + ChatColor.YELLOW + " (height: "
				+ ChatColor.WHITE + String.valueOf(height) + ChatColor.YELLOW
				+ ")");

		
		
		

		
		
	}
	
	public int getRegionSize(double money, double blockWorth) {
		return (int) Math.sqrt(money / blockWorth);
	}
	
	public boolean overlapsUnownedRegion(ProtectedRegion region, World world, Player player) {
		RegionManager mgr = worldGuard.getRegionManager(world);
		if (mgr == null) {
			return false;
		}
		return mgr.overlapsUnownedRegion(region, worldGuard.wrapPlayer(player));
	}
	
	public ProtectedRegion getAutomaticParentRegion(ProtectedRegion region, World world, Player player) {
		RegionManager mgr = worldGuard.getRegionManager(world);
		if (mgr == null) {
			return null;
		}
		
		LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
		
		// get the regions in which the first corner exists
		ApplicableRegionSet regions = mgr.getApplicableRegions(region.getMinimumPoint());
		
		List<ProtectedRegion> ownedApplicableRegions = new ArrayList<ProtectedRegion>();
		
		// find regions that are cuboid, and owned by the player
		for (ProtectedRegion element : regions) {
			if(!element.getTypeName().equalsIgnoreCase("cuboid")) {
				continue;
			}
			if(!element.isOwner(localPlayer)) {
				continue;
			}
			// add owned, cuboid, region
			ownedApplicableRegions.add(element);
		}
		
		// the first corner is not in an owned, cuboid region
		if(ownedApplicableRegions.size() == 0) {
			return null;
		}
		
		// like before, get the regions in which the second corner exists
		regions = mgr.getApplicableRegions(region.getMaximumPoint());
		
		ProtectedRegion automaticParent = null;
		
		// see of the first corner is also in one of these regions
		// and determine which will be the parent
		for (ProtectedRegion element : regions) {
			if(ownedApplicableRegions.contains(element)) {
				// found a region with both corners in it!
				if(automaticParent == null) {
					// we didn't find one yet, so this is it for now
					automaticParent = element;
				}
				else {
					// we already found one, so we need to compare
					if(element.getPriority() >= automaticParent.getPriority()) {
						// priority is higher
						automaticParent = element;
					}
					else if(automaticParent.getPriority() == element.getPriority()) {
						// priorities are equal
						if(element.volume() <= automaticParent.volume()) {
							// has less volume
							automaticParent = element;
						}
					}
					
				}
			}
		}
		
		return automaticParent;
	}
	
}
