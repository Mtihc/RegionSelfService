package com.mtihc.regionselfservice.v2.plots;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;
import com.mtihc.regionselfservice.v2.plots.signs.PlotSignType;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


@DelegateDeserialization(PlotData.class)
public class Plot extends PlotData {

	private PlotWorld plotWorld;
	private PlotManager manager;



	public Plot(PlotWorld plotWorld, PlotData data) {
		super(data.getRegionId(), data.getSellCost(), data.getRentCost());
		this.plotWorld = plotWorld;
		this.manager = plotWorld.getPlotManager();
		
		// get all sign-data from plot-data
		Collection<IPlotSignData> signs = data.getSigns();
		for (IPlotSignData sign : signs) {
			// set sign-data via setSign method, 
			// to make sure they're converted
			setSign(sign);
		}
		
	}
	
	public World getWorld() {
		return plotWorld.getWorld();
	}
	
	public PlotWorld getPlotWorld() {
		return plotWorld;
	}

	public PlotManager getPlotManager() {
		return manager;
	}

	public int getWidth() {
		// get the ProtectedRegion
		ProtectedRegion region = getRegion();
		if(region == null) {
			return 0;
		}
		// calc width from maximum and minimum x
		return Math.abs(
				region.getMaximumPoint().getBlockX() - region.getMinimumPoint().getBlockX()) + 1;
	}
	
	public int getLength() {
		// get the ProtectedRegion
		ProtectedRegion region = getRegion();
		if(region == null) {
			return 0;
		}
		// calc length from maximum and minimum z
		return Math.abs(region.getMaximumPoint().getBlockZ() - region.getMinimumPoint().getBlockZ()) + 1;
	}
	
	public int getHeight() {
		// get the ProtectedRegion
		ProtectedRegion region = getRegion();
		if(region == null) {
			return 0;
		}
		// calc height from maximum and minimum y
		return Math.abs(
				region.getMaximumPoint().getBlockY() - region.getMinimumPoint().getBlockY()) + 1;
	}
	
	public static double getWorth(int width, int length, double blockWorth) {
		return width * length * blockWorth;
	}
	
	public double getWorth(double blockWorth) {
		return getWorth(getWidth(), getLength(), blockWorth);
	}
	
	public double getWorth() {
		return getWorth(plotWorld.getConfig().getBlockWorth());
	}

	public ProtectedRegion getRegion() {
		// get ProtectedRegion via WorldGuard's RegionManager
		return plotWorld.getRegionManager().getRegion(regionId);
	}
	
	
	
	@Override
	public IPlotSignData removeSign(BlockVector coords) {
		IPlotSignData result = super.removeSign(coords);
		Block block = coords.toLocation(plotWorld.getWorld()).getBlock();
		if(block.getState() instanceof Sign) {
			block.breakNaturally();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.PlotData#setSign(com.mtihc.regionselfservice.v2.plots.PlotSignData)
	 */
	@Override
	public void setSign(IPlotSignData data) {
		if(!(data instanceof IPlotSign)) {
			// not converted yet
			try {
				// create sign from sign-data
				data = data.getType().createPlotSign(this, data);
			} catch (SignException e) {
				return;
			}
		}
		// set sign, as sign-data
		super.setSign(data);
	}


	public void save() {
		plotWorld.getPlotData().set(regionId, this);
	}
	
	
	public boolean delete() {
		// break all signs of this plot
		Collection<IPlotSignData> signs = getSigns();
		for (IPlotSignData data : signs) {
			BlockVector vec = data.getBlockVector();
			removeSign(vec);
		}
		// remove plot data
		plotWorld.getPlotData().remove(regionId);
		return true;
	}
	
	public void sendInfo(CommandSender sender) {
		// get the ProtectedRegion
		ProtectedRegion region = getRegion();
		
		if(region == null) {
			String errorMsg = ChatColor.RED + "Failed to send region info. Region \"" + getRegionId() + "\" doesn't exist.";
			sender.sendMessage(errorMsg); 
			return;
		}
		
		int width = getWidth();
		int length = getLength();
		int height = getHeight();
		
		String[] wgInfo = new String[]{
				ChatColor.GREEN + "Region: " + ChatColor.DARK_GREEN + getRegionId() + ChatColor.GREEN + " Priority: " + ChatColor.DARK_GREEN + region.getPriority() + ChatColor.GREEN + " Parent: " + ChatColor.DARK_GREEN + (region.getParent() == null ? "no parent" : region.getParent().getId()),
				ChatColor.GREEN + "World: " + ChatColor.DARK_GREEN + plotWorld.getName() + ChatColor.GREEN + " Min: " + ChatColor.DARK_GREEN + vectorToString(region.getMinimumPoint()) + ChatColor.GREEN + " Max: " + ChatColor.DARK_GREEN + vectorToString(region.getMaximumPoint()),
				ChatColor.GREEN + "Size: " + ChatColor.DARK_GREEN + width + "x" + length + ChatColor.GREEN + " (Height: " + ChatColor.DARK_GREEN + height + ChatColor.GREEN + ")"
		};
		
		double blockWorth = plotWorld.getConfig().getBlockWorth();
		double worth = getWorth(width, length, blockWorth);
		String worthString = manager.getEconomy().format(worth);
		String sellCost = (isForSale() ? manager.getEconomy().format(getSellCost()) : "not for sale");
		String rentCost = (isForRent() ? manager.getEconomy().format(getRentCost()) : "not for rent");
		
		String[] info = new String[] {
				ChatColor.GOLD + "Worth: " + ChatColor.YELLOW + worthString + ChatColor.GOLD + " based on size",
				ChatColor.GOLD + "Sell cost: " + ChatColor.YELLOW + sellCost,
				ChatColor.GOLD + "Rent cost: " + ChatColor.YELLOW + rentCost
		};
		
		// send messages with region info
		sender.sendMessage(wgInfo);
		sender.sendMessage(info);
		
	}
	
	private String vectorToString(com.sk89q.worldedit.BlockVector blockVector) {
		// return BlockVector as string x,y,z
		return blockVector.getBlockX() + "," + blockVector.getBlockY() + "," + blockVector.getBlockZ();
	}

	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.PlotData#setSellCost(double)
	 */
	@Override
	public void setSellCost(double cost) {
		super.setSellCost(cost);
		// 
		// update wooden signs
		// 
		World world = plotWorld.getWorld();
		Collection<IPlotSignData> values = signs.values();
		for (IPlotSignData value : values) {
			if(value.getType() != PlotSignType.FOR_SALE) {
				// not a for-sale sign
				continue;
			}
			Location loc = value.getBlockVector().toLocation(world);
			Block block = loc.getBlock();
			// check if block is still a wooden sign
			if(block.getState() instanceof Sign) {
				// update text on sign
				Sign sign = (Sign) block.getState();
				sign.setLine(1, String.valueOf(cost));
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.PlotData#setRentCost(double)
	 */
	@Override
	public void setRentCost(double cost) {
		super.setRentCost(cost);
		// 
		// update wooden signs
		// 
		World world = plotWorld.getWorld();
		Collection<IPlotSignData> values = signs.values();
		for (IPlotSignData value : values) {
			if(value.getType() != PlotSignType.FOR_RENT) {
				// not a for-rent sign
				continue;
			}
			Location loc = value.getBlockVector().toLocation(world);
			Block block = loc.getBlock();
			// check if block is still a wooden sign
			if(block.getState() instanceof Sign) {
				// update text on sign
				Sign sign = (Sign) block.getState();
				sign.setLine(1, String.valueOf(cost));// TODO add time?
			}
		}
	}
	
	
}
