package com.mtihc.minecraft.regionselfservice.events;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import com.sk89q.worldedit.Vector;

import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.configuration.SignsAbstract;
import com.mtihc.minecraft.regionselfservice.control.WoodenSignControl;
import com.mtihc.minecraft.regionselfservice.exceptions.WoodenSignException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Event listener for sign change event.
 * <p>
 * Checks the sign's first line to see if it is related to the plugin. If the
 * player has no permission to place that type of sign, break the sign.
 * </p>
 * 
 * <p>
 * Also checks if the region name on the sign exists. And other things.
 * </p>
 * 
 * @author Mitch
 * 
 */
public class SignPlaceListener implements Listener {

	/**
	 * Constructor
	 * 
	 */
	public SignPlaceListener() {
		
	}

	/*
	 * @see org.bukkit.event.block.BlockListener#onSignChange(org.bukkit.event.block
	 *      .SignChangeEvent)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled()) {
			return;
		}

		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		if (plugin.woodenSigns().matchFirstLine(
				plugin.config().settings().getFirstLineForSale(), event.getLine(0))) {
			onSignPlaceForSale(event);
		} else if (plugin.woodenSigns().matchFirstLine(
				plugin.config().settings().getFirstLineForRent(), event.getLine(0))) {
			onSignPlaceForRent(event);
		}

	}

	private boolean checkPermission(Player player, String permission) {
		if (!player.hasPermission(permission)) {
			// player does not have permission to place
			player.sendMessage(ChatColor.RED
					+ "You don't have permission to place this sign");
			return false;
		} else {
			return true;
		}
	}

	private double getCost(World world, String[] lines, String regionName, SignsAbstract signsConfig)
			throws WoodenSignException {
		double regionCost;
		
		try {
			RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
			regionCost = plugin.woodenSigns().getRegionCostOnSign(lines);
			adjustAllCostOf(world, regionName, signsConfig, regionCost);
		} catch(WoodenSignException e) {
			regionCost = signsConfig.getRegionCost(world.getName(), regionName);
			if(regionCost == -1) {
				throw e;
			}
		}
		
		return regionCost;
	}
	
	private void adjustAllCostOf(World world, String regionName, SignsAbstract signsConfig, double cost) {
		List<org.bukkit.util.Vector> signs = signsConfig.getRegionSigns(world.getName(), regionName);
		
		if(signs == null) {
			return;
		}
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		for (org.bukkit.util.Vector vector : signs) {
			Block block = world.getBlockAt(vector.toLocation(world));
			if(!plugin.woodenSigns().isSign(block)) {
				continue;
			}
			Sign sign = (Sign) block.getState();
			String costString;
			if(cost == 0) {
				costString = "Free";
			}
			else {
				if((int)cost == cost) {
					costString = Integer.toString((int)cost);
				}
				else {
					costString = Double.toString(cost);
				}
			}
			sign.setLine(WoodenSignControl.LINE_INDEX_COST, costString);
			if(!sign.update()) {
				plugin.woodenSigns().breakSign(block, true);
			}
		}
	}

	private void checkSignPosition(ProtectedRegion region, Block block,
			boolean allowOutside) throws WoodenSignException {
		// is the sign inside the region?
		
		if (!region.contains(new Vector(block.getX(), block.getY(), block
				.getZ())) && !allowOutside) {
			throw new WoodenSignException(WoodenSignException.Type.NOT_ALLOWED_OUTSIDE);
		}
	}

	private void checkCost(double cost, ProtectedRegion region,
			double minBlockCost, double maxBlockCost, boolean allowFree)
			throws WoodenSignException {
		Vector min = region.getMinimumPoint();
		Vector max = region.getMaximumPoint();
		int width = Math.abs(min.getBlockX() - max.getBlockX()) + 1;
		int length = Math.abs(min.getBlockZ() - max.getBlockZ()) + 1;

		double minCost = width * length * minBlockCost;
		double maxCost = width * length * maxBlockCost;

		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		// check price on sign min/max
		if (cost == 0) {
			if(!allowFree) {
				throw new WoodenSignException(WoodenSignException.Type.NOT_ALLOWED_FREE);
			}
		} else if (cost < minCost || cost > maxCost) {
			WoodenSignException.Type type = WoodenSignException.Type.NOT_ALLOWED_THAT_PRICE;
			throw new WoodenSignException(type, type.getMessage().replace("the minimum price", plugin.economy().format(minCost)).replace("the maximum price", plugin.economy().format(maxCost)));
		}

	}

	public void onSignPlaceForSale(SignChangeEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		if (!checkPermission(player, Permissions.SELL)) {
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		// get lines on sign
		String[] lines = event.getLines();

		// get region from sign
		String regionName;
		try {
			regionName = plugin.woodenSigns().getRegionNameOnSign(lines);
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			plugin.woodenSigns().breakSign(block, true);
			return;
		}
		RegionManager mgr = plugin.getWorldGuard().getRegionManager(block.getWorld());
		ProtectedRegion region = mgr.getRegion(regionName);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "Region " + ChatColor.WHITE
					+ "'" + regionName + "'" + ChatColor.RED
					+ " does not exist in world " + ChatColor.WHITE + "'"
					+ player.getWorld().getName() + "'" + ChatColor.RED + ".");
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		// get region count of player
		int regionCount = plugin.regions().getRegionCountOfPlayer(
				player.getWorld(), player.getName());

		// is owner, is not last region
		if (region.isOwner(plugin.getWorldGuard().wrapPlayer(player))) {
			// player is owner
			if (regionCount == 1 && plugin.config().settings().getOnBuyReserveFreeRegions()) {
				// last region can't be sold
				player.sendMessage(ChatColor.RED
						+ "You can't sell your last region.");
				plugin.woodenSigns().breakSign(block, true);
				return;
			}
		} else {
			// player is not owner
			if (!player.hasPermission(Permissions.SELL_ANYREGION)) {
				// no permission for selling others' regions
				player.sendMessage(ChatColor.RED
						+ "You can only sell your own regions.");
				plugin.woodenSigns().breakSign(block, true);
				return;
			}
		}

		// get cost from sign
		double regionCost;
		try {
			regionCost = getCost(block.getWorld(), lines, regionName, plugin.config().signsSale());
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			plugin.woodenSigns().breakSign(block, true);
			return;
		}
		
		try {
			checkCost(regionCost, region, plugin.config().settings()
					.getOnSellMinBlockCost(), plugin.config().settings()
					.getOnSellMaxBlockCost(),
					player.hasPermission(Permissions.SELL_FREE));
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		try {
			checkSignPosition(region, block,
					player.hasPermission(Permissions.SELL_ANYWHERE));
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		// save sign
		plugin.config().signsSale().setSign(player.getWorld().getName(), regionName,
				block.getX(), block.getY(), block.getZ());
		plugin.config().signsSale().setRegionCost(player.getWorld().getName(),
				regionName, regionCost);
		plugin.config().signsSale().save();

		player.sendMessage(ChatColor.GREEN + "Successfully created "
				+ ChatColor.WHITE + "For Sale" + ChatColor.GREEN + " sign.");

		// get the region owners
		Set<String> owners = region.getOwners().getPlayers();
		// get the region members
		Set<String> members = region.getMembers().getPlayers();

		plugin.messages().upForSale(player, owners, members, regionName, regionCost);

		if (regionCost == 0) {
			event.getLines()[WoodenSignControl.LINE_INDEX_COST] = "Free";
		} else {
			String string;
			if(regionCost == Math.round(regionCost)) {
				string = Integer.toString((int)regionCost);
			}
			else {
				string = Double.toString(regionCost);
			}
			event.getLines()[WoodenSignControl.LINE_INDEX_COST] = string;
		}

	}

	public void onSignPlaceForRent(SignChangeEvent event) {

		Block block = event.getBlock();
		Player player = event.getPlayer();

		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		if (!checkPermission(player, Permissions.RENTOUT)) {
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		// get lines on sign
		String[] lines = event.getLines();

		// get region from sign
		String regionName;
		try {
			regionName = plugin.woodenSigns().getRegionNameOnSign(lines);
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		RegionManager mgr = plugin.getWorldGuard().getRegionManager(block.getWorld());
		ProtectedRegion region = mgr.getRegion(regionName);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "Region " + ChatColor.WHITE
					+ "'" + regionName + "'" + ChatColor.RED
					+ " does not exist in world " + ChatColor.WHITE + "'"
					+ player.getWorld().getName() + "'" + ChatColor.RED + ".");
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		// is owner
		if (!region.isOwner(plugin.getWorldGuard().wrapPlayer(player))) {
			// player is not owner
			if (!player.hasPermission(Permissions.RENTOUT_ANYREGION)) {
				// no permission for renting out others' regions
				player.sendMessage(ChatColor.RED
						+ "You can only rent out your own regions.");
				plugin.woodenSigns().breakSign(block, true);
				return;
			}
		}

		// get cost from sign
		double regionCost;
		try {
			regionCost = getCost(block.getWorld(), lines, regionName, plugin.config().signsRent());
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		try {
			checkCost(regionCost, region, plugin.config().settings()
					.getOnRentMinBlockCost(), plugin.config().settings()
					.getOnRentMaxBlockCost(),
					player.hasPermission(Permissions.RENTOUT_FREE));
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		try {
			checkSignPosition(region, block,
					player.hasPermission(Permissions.RENTOUT_ANYWHERE));
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			plugin.woodenSigns().breakSign(block, true);
			return;
		}

		// save sign
		plugin.config().signsRent().setSign(player.getWorld().getName(), regionName,
				block.getX(), block.getY(), block.getZ());
		plugin.config().signsRent().setRegionCost(player.getWorld().getName(),
				regionName, regionCost);
		plugin.config().signsRent().save();

		player.sendMessage(ChatColor.GREEN + "Successfully created "
				+ ChatColor.WHITE + "For Rent" + ChatColor.GREEN + " sign.");

		// get the region owners
		Set<String> owners = region.getOwners().getPlayers();
		// get the region members
		Set<String> members = region.getMembers().getPlayers();

		plugin.messages().upForRent(player, owners, members, regionName, regionCost, "hour");

		if (regionCost == 0) {
			event.getLines()[WoodenSignControl.LINE_INDEX_COST] = "Free";
		} else {
			String string;
			if(regionCost == Math.round(regionCost)) {
				string = Integer.toString((int)regionCost);
			}
			else {
				string = Double.toString(regionCost);
			}
			event.getLines()[WoodenSignControl.LINE_INDEX_COST] = string;
		}
	}

}
