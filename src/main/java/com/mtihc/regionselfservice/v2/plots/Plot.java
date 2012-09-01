package com.mtihc.regionselfservice.v2.plots;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.DelegateDeserialization;

import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;
import com.mtihc.regionselfservice.v2.plots.signs.IPlotSign;
import com.mtihc.regionselfservice.v2.plots.signs.IPlotSignData;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


@DelegateDeserialization(PlotData.class)
public class Plot extends PlotData {

	private PlotWorld plotWorld;
	private PlotManager manager;



	public Plot(PlotWorld plotWorld, PlotData data) {
		super(data.getWorld(), data.getRegionId(), data.getSellCost(), data.getRentCost());
		this.plotWorld = plotWorld;
		this.manager = plotWorld.getPlotManager();
		Collection<IPlotSignData> signs = data.getSigns();
		for (IPlotSignData sign : signs) {
			setSign(sign);
		}
		
	}
	
	
	
	public PlotWorld getPlotWorld() {
		return plotWorld;
	}

	public PlotManager getPlotManager() {
		return manager;
	}

	public int getWidth() {
		ProtectedRegion region = getRegion();
		if(region == null) {
			return 0;
		}
		return Math.abs(region.getMaximumPoint().getBlockX() - region.getMinimumPoint().getBlockX()) + 1;
	}
	
	public int getLength() {
		ProtectedRegion region = getRegion();
		if(region == null) {
			return 0;
		}
		return Math.abs(region.getMaximumPoint().getBlockZ() - region.getMinimumPoint().getBlockZ()) + 1;
	}
	
	public int getHeight() {
		ProtectedRegion region = getRegion();
		if(region == null) {
			return 0;
		}
		return Math.abs(region.getMaximumPoint().getBlockY() - region.getMinimumPoint().getBlockY()) + 1;
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
		return plotWorld.getRegionManager().getRegion(regionId);
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.PlotData#setSign(com.mtihc.regionselfservice.v2.plots.PlotSignData)
	 */
	@Override
	public void setSign(IPlotSignData data) {
		if(!(data instanceof IPlotSign)) {
			try {
				data = data.getType().createPlotSign(this, data);
			} catch (SignException e) {
				return;
			}
		}
		super.setSign(data);
	}


	public void save() {
		plotWorld.getPlotData().set(regionId, this);
	}
	
	
	public boolean delete() {
		// TODO only if no more renters
		plotWorld.getPlotData().remove(regionId);
		return true;
	}
	
	public void sendInfo(CommandSender sender) {
		ProtectedRegion region = getRegion();
		if(region == null) {
			sender.sendMessage(ChatColor.RED + "Failed to send region info. Region \"" + getRegionId() + "\" doesn't exist.");
		}
		


		int width = getWidth();
		int length = getLength();
		int height = getHeight();
		
		String[] wgInfo = new String[]{
				ChatColor.GREEN + "Region: " + ChatColor.DARK_GREEN + getRegionId() + ChatColor.GREEN + " Priority: " + ChatColor.DARK_GREEN + region.getPriority() + ChatColor.GREEN + " Parent: " + ChatColor.DARK_GREEN + (region.getParent() == null ? "no parent" : region.getParent().getId()),
				ChatColor.GREEN + "World: " + ChatColor.DARK_GREEN + world.getName() + ChatColor.GREEN + " Min: " + ChatColor.DARK_GREEN + vectorToString(region.getMinimumPoint()) + ChatColor.GREEN + " Max: " + ChatColor.DARK_GREEN + vectorToString(region.getMaximumPoint()),
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
		
		sender.sendMessage(wgInfo);
		sender.sendMessage(info);
	}
	
	private String vectorToString(com.sk89q.worldedit.BlockVector blockVector) {
		return blockVector.getBlockX() + "," + blockVector.getBlockY() + "," + blockVector.getBlockZ();
	}
}
