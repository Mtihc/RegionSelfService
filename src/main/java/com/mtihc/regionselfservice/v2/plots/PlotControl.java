package com.mtihc.regionselfservice.v2.plots;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.IPlotPermission.PlotAction;
import com.mtihc.regionselfservice.v2.plots.data.ISignData;
import com.mtihc.regionselfservice.v2.plots.data.SignType;
import com.mtihc.regionselfservice.v2.plots.exceptions.EconomyException;
import com.mtihc.regionselfservice.v2.plots.exceptions.PlotBoundsException;
import com.mtihc.regionselfservice.v2.plots.exceptions.PlotControlException;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

public class PlotControl {

	private PlotManager mgr;

	public PlotControl(PlotManager manager) {
		this.mgr = manager;
	}
	
	public PlotManager getPlotManager() {
		return mgr;
	}
	
	
	
	
	public int getRegionCountOfPlayer(World world, String playerName) {
		RegionManager regionManager = mgr.getPlotWorld(world.getName()).getRegionManager();
		
		Player p = Bukkit.getPlayerExact(playerName);
		if(p != null) {
			return regionManager.getRegionCountOfPlayer(mgr.getWorldGuard().wrapPlayer(p)); 
		}
		
		Collection<ProtectedRegion> regions = regionManager.getRegions().values();
		if(regions == null || regions.isEmpty()) {
			return 0;
		}
		int count = 0;
		for (ProtectedRegion region : regions) {
			if(region.isOwner(playerName.toLowerCase())) {
				count++;
			}
		}
		return count;
		
	}
	
	private static final HashSet<Byte> invisibleBlocks = new HashSet<Byte>();
	
	public static HashSet<Byte> getInvisibleBlocks() {
		if(invisibleBlocks.isEmpty()) {
			invisibleBlocks.add((byte) Material.AIR.getId());
			invisibleBlocks.add((byte) Material.WATER.getId());
			invisibleBlocks.add((byte) Material.STATIONARY_WATER.getId());
			invisibleBlocks.add((byte) Material.LAVA.getId());
			invisibleBlocks.add((byte) Material.STATIONARY_LAVA.getId());
			invisibleBlocks.add((byte) Material.SNOW.getId());
			invisibleBlocks.add((byte) Material.LONG_GRASS.getId());
		}
		return invisibleBlocks;
	}
	
	public static Sign getTargetSign(Player player) {
		Block block = player.getTargetBlock(getInvisibleBlocks(), 8);
		if(block.getState() instanceof Sign) {
			return (Sign) block.getState();
		}
		else {
			return null;
		}
	}
	
	
	public void buy(Player player) throws PlotControlException {
		// get sign (is targeting ForSale sign?)
		Sign sign = getTargetSign(player);
		if(sign == null) {
			throw new PlotControlException("You're not looking at a wooden sign.");
		}
		BlockVector coords = sign.getLocation().toVector().toBlockVector();
		PlotWorld world = mgr.getPlotWorld(player.getWorld().getName());
		Plot plot;
		ISignData plotSign;
		SignForSale saleSign;
		
		try {
			plot = world.getPlot(sign);
		} catch (SignException e) {
			throw new PlotControlException("You're not looking at a valid sign: " + e.getMessage(), e);
		}
		
		if(plot != null) {
			plotSign = plot.getSign(coords);
		}
		else {
			plotSign = null;
		}
		
		if(plotSign == null || !(plotSign instanceof SignForSale)) {
			throw new PlotControlException("You're not looking at a for-sale sign.");
		}
		saleSign = (SignForSale) plotSign;
		
		
		
		// get region (still exists?)
		ProtectedRegion region = plot.getRegion();
		if(region == null) {
			throw new PlotControlException("Sorry, the region doesn't exist.");
		}
		

		// already owner?
		if(region.isOwner(player.getName())) {
			throw new PlotControlException("You already own this region.");
		}
		
		// has player reached max regions, or bypass
		int regionCount = getRegionCountOfPlayer(world.getWorld(), player.getName());
		int regionMax = world.getConfig().getMaxRegionCount();
		boolean bypassMax = player.hasPermission(
				mgr.getPermissions().getPermission(PlotAction.BYPASS_MAX_REGIONS));
		
		if(!bypassMax && regionCount >= regionMax) {
			throw new PlotControlException("You already own " + regionCount + " regions (max: " + regionMax + ").");
		}
		

		// get region cost
		double cost = saleSign.getCost();
		
		// if reserve-free-regions is enabled: and cost is 0, then  
		// check regionCount == 0
		boolean reserve = world.getConfig().isReserveFreeRegionsEnabled();
		if(reserve && cost <= 0 && regionCount == 0) {
			throw new PlotControlException("Free regions are reserved for new players.");
		}
		
		// check if players would become homless after sale
		// this is part of preventing cheating with free regions
		Set<String> owners = region.getOwners().getPlayers();
		int ownerCount = owners.size();
		
		if(reserve) {

			if(ownerCount > 0) {
				String homeless = "";
				for (String ownerName : owners) {
					int ownerRegionCount = getRegionCountOfPlayer(world.getWorld(), ownerName);
					if(ownerRegionCount - 1 == 0) {
						homeless += ", " + ownerName;
					}
					
				}
				if(!homeless.isEmpty()) {
					homeless = homeless.substring(2);
					throw new PlotControlException("Sorry, you can't buy this region. The following players would become homeless: " + homeless);
				}
			}
			
		}
		
		// check bypasscost || pay for region
		
		boolean bypassCost = player.hasPermission(
				mgr.getPermissions().getPermission(PlotAction.BYPASS_BUY_COST));
		// TODO remove this logger
		mgr.getPlugin().getLogger().info("Player " + player.getName() + " has permission \"" + mgr.getPermissions().getPermission(PlotAction.BYPASS_BUY_COST) + "\".");
		if(!bypassCost) {
			try {
				mgr.getEconomy().withdraw(player.getName(), cost);
			} catch (EconomyException e) {
				throw new PlotControlException("Failed to pay for region: " + e.getMessage());
			}
		}
		
		// calc share and pay owners their share
		double share = cost / Math.max(1, ownerCount);
		for (String ownerName : owners) {
			mgr.getEconomy().deposit(ownerName, share);
		}
		
		// remove owners, add buyer as owner
		DefaultDomain newOwnerDomain = new DefaultDomain();
		newOwnerDomain.addPlayer(player.getName());
		region.setOwners(newOwnerDomain);
		// save region owner changes
		try {
			world.getRegionManager().save();
		} catch (ProtectionDatabaseException e) {
			String msg = "Failed to save region changes to world \"" + world.getName() + "\", using WorldGuard.";
			mgr.getPlugin().getLogger().log(Level.SEVERE, msg, e);
			throw new PlotControlException(msg + " " + e.getMessage(), e);
		}
		
		// break all for sale signs
		Collection<ISignData> forSaleSigns = plot.getSigns(SignType.FOR_SALE);
		for (ISignData data : forSaleSigns) {
			BlockVector vec = data.getBlockVector();
			Block block = vec.toLocation(world.getWorld()).getBlock();
			if(block.getState() instanceof Sign) {
				block.breakNaturally();
			}
			plot.removeSign(vec);
		}
		plot.save();
		
		
		// TODO delete plot information (because it's no longer for sale.. uhm and for rent?)
		
		// TODO inform buyer, previous owners, current members
		
	}
	
	public void rent(Player player) {
		// TODO
	}
	
	public void define(Player player, String regionId) throws PlotControlException {
		define(player, regionId, -1, -1);
	}
	
	public void define(Player player, String regionId, int bottomY, int topY) throws PlotControlException {
		WorldEditPlugin we; 
		try {
			we = mgr.getWorldGuard().getWorldEdit();
		} catch (CommandException e) {
			throw new PlotControlException("Failed to get WorldEdit: " + e.getMessage(), e);
		}
		
		Selection sel = we.getSelection(player);
		if(sel == null || sel.getMaximumPoint() == null || sel.getMinimumPoint() == null) {
			throw new PlotControlException("Select a region first. Use WorldEdit's command: " + ChatColor.LIGHT_PURPLE + "//wand");
		}
		
		World w = sel.getWorld();
		if(!w.getName().equals(player.getWorld().getName())) {
			throw new PlotControlException("You're in a different world than your region selection. Go back to \"" + w.getName() + "\".");
		}
		
		PlotWorld world = mgr.getPlotWorld(w.getName());
		RegionManager regionManager = world.getRegionManager();
		
		if(regionManager.hasRegion(regionId)) {
			throw new PlotControlException("Region \"" + regionId + "\" already exists.");
		}
		
		if(!isValidRegionName(regionId)) {
			throw new PlotControlException("Invalid region name \"" + regionId + "\". Try a different name.");
		}
		
		int by;
		int ty;
		if(bottomY <= -1) {
			by = world.getConfig().getDefaultBottomY();
		}
		else {
			by = sel.getMinimumPoint().getBlockY();
		}
		if(topY <= -1) {
			ty = world.getConfig().getDefaultTopY();
		}
		else {
			ty = sel.getMaximumPoint().getBlockY();
		}
		
		if(ty < by) {
			int y = ty;
			ty = by;
			by = y;
		}
		

		BlockVector min = new BlockVector(sel.getMinimumPoint().getBlockX(), by, sel.getMinimumPoint().getBlockZ());
		BlockVector max = new BlockVector(sel.getMaximumPoint().getBlockX(), ty, sel.getMaximumPoint().getBlockZ());

		int width = Math.abs(max.getBlockX() - min.getBlockX()) + 1;
		int length = Math.abs(max.getBlockZ() - min.getBlockZ()) + 1;
		int height = Math.abs(topY - bottomY) + 1;
		
		int minY = world.getConfig().getMinimumY();
		int maxY = world.getConfig().getMaximumY();
		int minHeight = world.getConfig().getMinimumHeight();
		int maxHeight = world.getConfig().getMaximumHeight();
		int minWidthLength = world.getConfig().getMinimumWidthLength();
		int maxWidthLength = world.getConfig().getMaximumWidthLength();
		//
		// check min max
		//
		if(!player.hasPermission(mgr.getPermissions().getPermission(PlotAction.CREATE_ANYSIZE))) {
			if(width < minWidthLength || length < minWidthLength || height < minHeight) {
				throw new PlotBoundsException(
						PlotBoundsException.Type.SELECTION_TOO_SMALL, 
						width, length, height, minWidthLength, maxWidthLength, minHeight, maxHeight);
			}
			else if(width > maxWidthLength || length > maxWidthLength || height > maxHeight) {
				throw new PlotBoundsException(
						PlotBoundsException.Type.SELECTION_TOO_BIG, 
						width, length, height, maxWidthLength, maxWidthLength, minHeight, maxHeight);
			}
			if(topY > maxY) {
				throw new PlotBoundsException(
						PlotBoundsException.Type.SELECTION_TOO_HIGH, 
						topY, bottomY, minY, maxY);
			}
			if(bottomY < minY) {
				throw new PlotBoundsException(
						PlotBoundsException.Type.SELECTION_TOO_LOW, 
						topY, bottomY, minY, maxY);
			}
		}
		
		// create protected region
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				regionId, 
				new com.sk89q.worldedit.BlockVector(
						min.getBlockX(), min.getBlockY(), min.getBlockZ()), 
				new com.sk89q.worldedit.BlockVector(
						max.getBlockX(), max.getBlockY(), max.getBlockZ()));
		
		
		boolean allowOverlap = world.getConfig().isOverlapUnownedRegionAllowed();
		if(!allowOverlap && overlapsUnownedRegion(region, w, player)) {
			throw new PlotControlException("Your selection overlaps with someone else's region.");
		}
		// TODO this needs another look-over
		else {
			// not overlapping or it's allowed to overlap unowned regions
			boolean doAutomaticParent = world.getConfig().isAutomaticParentEnabled();
			boolean allowAnywhere = player.hasPermission(
					mgr.getPermissions().getPermission(PlotAction.CREATE_ANYWHERE));
			
			ProtectedRegion parentRegion;
			if(!allowAnywhere || doAutomaticParent) {
				// we need a parent
				parentRegion = getAutomaticParentRegion(region, w, player);
				
				if(parentRegion == null) {
					if(!allowAnywhere) {
						// automatic parent was not found, but it's required...
						// because player can only create regions inside owned existing regions.
						throw new PlotControlException("You can only claim regions inside existing regions that you own");
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
		
		boolean enableCost = world.getConfig().isCreateCostEnabled();
		boolean bypassCost = !enableCost;
		if (!bypassCost
				&& player.hasPermission(
						mgr.getPermissions().getPermission(PlotAction.BYPASS_CREATE_COST))) {
			bypassCost = true;
		}
		
		double cost = getWorth(region, world.getConfig().getBlockWorth());
		
		if(!bypassCost) {
			double bal = mgr.getEconomy().getBalance(player.getName());
			if(bal < cost) {
				throw new PlotControlException("You don't have enough money to create a region this big. You have " + mgr.getEconomy().format(bal) + ", but you require " + mgr.getEconomy().format(cost) + ".");
			}
		}
		
		
		// who will get the money ?
		Set<String> depositTo = new HashSet<String>();
		// who are the default owners in the config ?
		List<String> ownerList = world.getConfig().getDefaultOwners();
		
		DefaultDomain ownersDomain;
		
		if (enableCost) {
			// cost is enabled
			ownersDomain = new DefaultDomain();
			// player will be owner
			ownersDomain.addPlayer(player.getName());
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
				ownersDomain.addPlayer(player.getName());
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
		
		// TODO accept cost and stuff, otherwise don't save... use conversation API
		
		try {
			mgr.getEconomy().withdraw(player.getName(), cost);
		} catch (EconomyException e) {
			throw new PlotControlException("Failed to pay for the region: " + e.getMessage(), e);
		}
		

		if(depositTo != null && depositTo.size() != 0) {
			double share = Math.abs(cost) / depositTo.size();
			for (String account : depositTo) {
				mgr.getEconomy().deposit(account, share);
			}
		}
		
		// TODO send region info to indicate it was successful
	}
	
	
	
	public void redefine(Player player, String regionId) {
		// TODO
	}
	
	public void redefine(Player player, String regionId, int bottomY, int topY) {
		// TODO
	}
	
	public void delete(CommandSender sender, String regionId) {
		// TODO
	}
	
	public void sendRegionCount(CommandSender sender, OfflinePlayer owner, World world) {
		int count = getRegionCountOfPlayer(world, owner.getName());

		String countString = String.valueOf(count);
		if (count < mgr.getPlotWorld(world.getName()).getConfig().getMaxRegionCount()) {
			countString = ChatColor.WHITE + countString;
		} else {
			countString = ChatColor.RED + countString;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.WHITE + "'"
				+ owner.getName() + "'" + ChatColor.GREEN + " owns "
				+ countString + ChatColor.GREEN + " regions in world "
				+ ChatColor.WHITE + "'" + world.getName() + "'"
				+ ChatColor.GREEN + ".");
		
		
	}
	
	public void sendWorth(CommandSender sender, String regionId, World world) {
		PlotWorld plotWorld = mgr.getPlotWorld(world.getName());
		RegionManager regionManager = plotWorld.getRegionManager();
		ProtectedRegion region = regionManager.getRegion(regionId);
		if(region == null) {
			sender.sendMessage(ChatColor.RED + "Region '" + regionId + "' doesn't exist in world '" + world.getName() + "'.");
			return;
		}
		
		int width = Math.abs(region.getMaximumPoint().getBlockX() - region.getMinimumPoint().getBlockX()) + 1;
		int length = Math.abs(region.getMaximumPoint().getBlockZ() - region.getMinimumPoint().getBlockZ()) + 1;
		sender.sendMessage(ChatColor.GREEN + "Region " + ChatColor.WHITE + region.getId() + ChatColor.GREEN + " with a size of "
				+ ChatColor.WHITE + String.valueOf(width) + "x"
				+ String.valueOf(length) + ChatColor.GREEN + " blocks, in world \"" + world.getName() + "\" ");
		double cost = getWorth(width, length, plotWorld.getConfig().getBlockWorth());
		sender.sendMessage(ChatColor.GREEN + "is worth about "
				+ ChatColor.WHITE + mgr.getEconomy().format(cost)
				+ ChatColor.GREEN + ", based on the region's size.");
	}
	
	public void sendWorth(CommandSender sender, int width, int length, World world) {
		PlotWorld plotWorld = mgr.getPlotWorld(world.getName());
		double cost = getWorth(width, length, plotWorld.getConfig().getBlockWorth());
		sender.sendMessage(ChatColor.GREEN + "For a region with a size of "
				+ ChatColor.WHITE + String.valueOf(width) + "x"
				+ String.valueOf(length) + ChatColor.GREEN + " blocks, in world \"" + world.getName() + "\" ");
		sender.sendMessage(ChatColor.GREEN + "you would pay about "
				+ ChatColor.WHITE + mgr.getEconomy().format(cost)
				+ ChatColor.GREEN + ".");
	}
	
	public void sendWorth(CommandSender sender, double money, World world) {
		PlotWorld plotWorld = mgr.getPlotWorld(world.getName());
		int size = getSizeByWorth(money, plotWorld.getConfig().getBlockWorth());
		sender.sendMessage(ChatColor.GREEN + "For " + ChatColor.WHITE
				+ mgr.getEconomy().format(money) + ChatColor.GREEN + ", ");
		sender.sendMessage(ChatColor.GREEN
				+ "you can get a region with a size of about "
				+ ChatColor.WHITE + String.valueOf(size) + "x"
				+ String.valueOf(size) + ChatColor.GREEN + " blocks, in world \"" + world.getName() + "\".");
	}
	
	

	public static int getSizeByWorth(double money, double blockWorth) {
		return (int) Math.sqrt(money / blockWorth);
	}
	
	
	public static double getWorth(ProtectedRegion region, double blockWorth) {
		if(region == null) {
			return 0;
		}
		
		int width = region.getMaximumPoint().getBlockX() - region.getMinimumPoint().getBlockX();
		width = Math.abs(width) + 1;
		
		int length = region.getMaximumPoint().getBlockZ() - region.getMinimumPoint().getBlockZ();
		length = Math.abs(length) + 1;
		
		return getWorth(width, length, blockWorth);
	}
	
	public static double getWorth(int width, int length, double blockWorth) {
		return width * length * blockWorth;
	}
	
	
	public boolean overlapsUnownedRegion(ProtectedRegion region, World world, Player player) {
		return mgr.getWorldGuard().getRegionManager(world).overlapsUnownedRegion(region, mgr.getWorldGuard().wrapPlayer(player));
	}
	
	public static boolean isValidRegionName(String regionName) {
		if (regionName == null || !ProtectedRegion.isValidId(regionName)
				|| regionName.equalsIgnoreCase("__GLOBAL__")
				|| regionName.matches("\\d")) {
			return false;
		} else {
			return true;
		}
	}


	public ProtectedRegion getAutomaticParentRegion(ProtectedRegion region, World world, Player player) {
		RegionManager regionManager = mgr.getWorldGuard().getRegionManager(world);
		LocalPlayer localPlayer = mgr.getWorldGuard().wrapPlayer(player);
		
		// get the regions in which the first corner exists
		ApplicableRegionSet regions = regionManager.getApplicableRegions(region.getMinimumPoint());
		
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
		regions = regionManager.getApplicableRegions(region.getMaximumPoint());
		
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
