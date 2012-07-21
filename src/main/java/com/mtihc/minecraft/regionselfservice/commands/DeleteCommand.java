package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.RegionTaskDelete;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class DeleteCommand extends SimpleCommand {

	public DeleteCommand(SimpleCommand parent, String permission) {
		super(parent, "delete", null, permission, "You don't have permission to delete regions.", "<id> [world]", "Delete a protected region");
		List<String> aliases = new ArrayList<String>();
		aliases.add("rem");
		aliases.add("remove");
		aliases.add("del");
		this.setAliases(aliases);
		

		ArrayList<String> help = new ArrayList<String>();
		
		help.add("Remove a protected region, unless there are still renters");
		help.add("Define the region's id. And optionally the world. If it's not in your current world.");
		help.add("The for-sale and for-rent signs will automatically break.");
		
		setLongDescription(help.toArray(new String[help.size()]));
		
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		
		String regionName;
		try {
			regionName = args[0];
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED
					+ "Incorrect number of arguments. Expected region name.");
			sender.sendMessage(getUsage());
			return false;
		}
	
		World world;
		if (sender instanceof Player) {
			world = ((Player) sender).getWorld();
		} else {
			String worldName;
			try {
				worldName = args[1];
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED
						+ "Incorrect number of arguments. Expected world name.");
				sender.sendMessage(getUsage());
				return false;
			}
			world = plugin.getServer().getWorld(worldName);
			if (world == null) {
				sender.sendMessage(ChatColor.RED + "World " + ChatColor.WHITE
						+ worldName + " does not exist.");
				return false;
			}
		}
		
		RegionManager mgr = plugin.getWorldGuard().getRegionManager(world);
		
		// check ownership and permission
		ProtectedRegion region = mgr.getRegion(regionName);
		if(region != null) {
			if(sender instanceof Player) {
				if(!region.isOwner(plugin.getWorldGuard().wrapPlayer((Player) sender)) && !sender.hasPermission(Permissions.REMOVE_ANYREGION.toString())) {
					sender.sendMessage(ChatColor.RED + "You can only remove owned regions.");
					return false;
				}
			}
			
		}
		else {
			sender.sendMessage(ChatColor.RED + "Region '" + regionName + "' does not exist in world '" + world.getName() + "'.");
			return false;
		}
		
		if(plugin.config().settings().getOnBuyReserveFreeRegions()) {
			// check if players would become homless after sale
			// this is part of preventing cheating with free regions
			Set<String> owners = region.getOwners().getPlayers();
			if(owners != null && owners.size() != 0) {
				String homeless = "";
				for (String owner : owners) {
					int ownerRegionCount = plugin.regions().getRegionCountOfPlayer(world, owner);
					if(ownerRegionCount - 1 == 0) {
						homeless += ", " + owner;
					}
					
				}
				if(!homeless.isEmpty()) {
					homeless = homeless.substring(2);
					sender.sendMessage(ChatColor.RED + "Sorry, you can't delete this region. The following players would become homeless: " + homeless);
					return false;
				}
			}
			
			
		}
		
		Set<String> renters = plugin.config().signsRent().getRentersOf(world.getName(), region.getId());
		if(renters != null && renters.size() != 0) {
			String rentersString = "";
			for (String renter : renters) {
				rentersString += ", " + renter;
			}
			rentersString = rentersString.substring(2);
			sender.sendMessage(ChatColor.RED + "You cannot remove the region while there are still renters. (" + ChatColor.WHITE + rentersString + ChatColor.RED + ")");
			return false;
		}
		
		double refund = 0;
		// refund a percentage of region's worth
		if(plugin.config().settings().getEnableOnCreateCost()) {
			double worth = plugin.getRegionWorth(region, plugin.config().settings().getBlockWorth());
			int percent = plugin.config().settings().getOnDeleteRefundPercent();
			refund = percent * worth / 100;
		}
		
		Set<String> ownerNames = null;
		if(region.getOwners() != null && region.getOwners().size() != 0) {
			ownerNames = region.getOwners().getPlayers();
			if(ownerNames == null || ownerNames.size() == 0) {
				ownerNames = new HashSet<String>();
				List<String> defaultOwners = plugin.config().settings().getDefaultOwners();
				for (String name : defaultOwners) {
					ownerNames.add(name);
				}
			}
		}
		
		
		RegionTaskDelete task = new RegionTaskDelete(plugin, sender.getName(), regionName, world, ownerNames, region.getMembers().getPlayers(), refund);
		plugin.taskRequest(sender, task);
		String nameString = "";
		if(ownerNames != null && ownerNames.size() != 0) {
			for (String name : ownerNames) {
				nameString += ", " + name;
			}
			nameString = nameString.substring(2);
		}
		if(nameString.isEmpty()) {
			nameString = "nobody";
		}
		if(task.acceptIsRequired()) {
			sender.sendMessage(ChatColor.GREEN + "To delete region '" + ChatColor.WHITE + region.getId() + ChatColor.GREEN + "' and share the refund of " + ChatColor.WHITE + plugin.economy().format(refund) + ChatColor.GREEN + " amongst '" + ChatColor.WHITE + nameString + ChatColor.GREEN + "', ");
			sender.sendMessage(ChatColor.GREEN + "type " + ChatColor.WHITE + "/selfservice accept" + ChatColor.GREEN);
		}
		
		return true;
	}

	@Override
	public boolean hasNested() {
		return false;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		return null;
	}

	@Override
	public String[] getNestedCommandLabels() {
		return null;
	}

}
