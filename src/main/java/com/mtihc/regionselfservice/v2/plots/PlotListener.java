package com.mtihc.regionselfservice.v2.plots;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mtihc.regionselfservice.v2.plots.IPlotPermission.PlotAction;
import com.mtihc.regionselfservice.v2.plots.data.ISignData;
import com.mtihc.regionselfservice.v2.plots.data.PlotData;
import com.mtihc.regionselfservice.v2.plots.data.SignType;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PlotListener implements Listener {

	private PlotManager mgr;

	public PlotListener(PlotManager mgr) {
		this.mgr = mgr;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if(event.isCancelled()) {
			return;// event was cancelled
		}
		if(!(event.getBlock().getState() instanceof Sign)) {
			return;
		}
		
		Sign sign = (Sign) event.getBlock().getState();
		if(!mgr.getSignValidator().isPlotSign(sign)) {
			return;// not a plot-sign
		}
		
		// plot world
		PlotWorld plotWorld = mgr.getPlotWorld(sign.getWorld().getName());
		
		// player
		Player player = event.getPlayer();
		
		Plot plot;
		ISignData plotSign;
		
		try {
			// create sign data
			plotSign = mgr.getSignValidator().createPlotSign(sign);
			// get plot data
 			plot = plotWorld.getPlot(plotSign.getRegionId());
			if(plot == null) {
				// plot data doesn't exist yet, 
				// create plot data
				PlotData plotData = new PlotData(sign.getWorld(), plotSign.getRegionId());
				plot = plotWorld.createPlot(plotData);
			}
			// add sign to plot data
			plot.setSign(plotSign);
			
		} catch (SignException e) {
			// invalid sign
			player.sendMessage(ChatColor.RED + "Failed to create plot-sign: " + e.getMessage());
			// break the sign
			event.setCancelled(true);
			sign.getBlock().breakNaturally();
			return;
		}
		
		
		ProtectedRegion region = plot.getRegion();
		if(region == null) {
			player.sendMessage(ChatColor.RED + "Failed to create plot-sign. Region \"" + plotSign.getRegionId() + "\" doesn't exist.");
			event.setCancelled(true);
			sign.getBlock().breakNaturally();
			return;
		}
		
		boolean isOwner = region.isOwner(player.getName());
		boolean isInside = region.contains(sign.getX(), sign.getY(), sign.getZ());
		
		IPlotPermission perms = mgr.getPermissions();
		
		SignType type = plotSign.getSignType();
		
		IPlotWorldConfig config = plot.getPlotWorld().getConfig();
		
		switch(type) {
		case FOR_RENT:
			
			
			
			if(!player.hasPermission(perms.getPermission(PlotAction.RENTOUT))) {
				player.sendMessage(ChatColor.RED + "You don't have permission to rent out regions.");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			
			if(!isOwner && !player.hasPermission(
					perms.getPermission(PlotAction.RENTOUT_ANYREGION))) {
				player.sendMessage(ChatColor.RED + "You can't rent out regions that you don't own.");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			
			if(!isInside && !player.hasPermission(
					perms.getPermission(PlotAction.RENTOUT_ANYWHERE))) {
				player.sendMessage(ChatColor.RED + "You can't place this sign outside the region itself.");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			
			SignForRent rentSign = (SignForRent) plotSign;
			double rentCost = rentSign.getCostPerHour();
			// TODO check if there was no cost, then we need to set the cost automatically using existing signs
			
			// check min/max cost
			double minRentCost = plot.getWorth(config.getOnRentMinBlockCost());
			double maxRentCost = plot.getWorth(config.getOnRentMaxBlockCost());
			if(rentCost < minRentCost) {
				player.sendMessage(ChatColor.RED + "The price is too low.");
				player.sendMessage(ChatColor.RED + "The rent-price must be between " + mgr.getEconomy().format(minRentCost) + " and " + mgr.getEconomy().format(maxRentCost) + ".");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			else if(rentCost > maxRentCost) {
				player.sendMessage(ChatColor.RED + "The price is high.");
				player.sendMessage(ChatColor.RED + "The rent-price must be between " + mgr.getEconomy().format(minRentCost) + " and " + mgr.getEconomy().format(maxRentCost) + ".");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}

			// TODO check if the cost is different, 
			// then we need to set the cost on all other signs
			// (that have no renter)
			
			
			break;
		case FOR_SALE:
			if(!player.hasPermission(perms.getPermission(PlotAction.SELL))) {
				player.sendMessage(ChatColor.RED + "You don't have permission to sell regions.");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			
			// TODO count regions of all owners
			// You can't sell a player's last region, 
			// (if free regions are reserved for players with no regions || configured no homeless)
			// because players would be able to work together, to mess up your server
			// send message: "You can't sell this region because the following players would be homeless."
			
			if(!isOwner && !player.hasPermission(
					perms.getPermission(PlotAction.SELL_ANYREGION))) {
				player.sendMessage(ChatColor.RED + "You can't sell regions that you don't own.");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			
			if(!isInside && !player.hasPermission(
					perms.getPermission(PlotAction.SELL_ANYWHERE))) {
				player.sendMessage(ChatColor.RED + "You can't place this sign outside the region itself.");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			
			SignForSale sellSign = (SignForSale) plotSign;
			double sellCost = sellSign.getCost();
			// TODO check if there was no cost, then we need to set the cost automatically using existing signs
			
			// check min/max cost
			double minSellCost = plot.getWorth(config.getOnSellMinBlockCost());
			double maxSellCost = plot.getWorth(config.getOnSellMaxBlockCost());
			if(sellCost < minSellCost) {
				player.sendMessage(ChatColor.RED + "The price is too low.");
				player.sendMessage(ChatColor.RED + "The sell-cost must be between " + mgr.getEconomy().format(minSellCost) + " and " + mgr.getEconomy().format(maxSellCost) + ".");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			else if(sellCost > maxSellCost) {
				player.sendMessage(ChatColor.RED + "The price is high.");
				player.sendMessage(ChatColor.RED + "The sell-cost must be between " + mgr.getEconomy().format(minSellCost) + " and " + mgr.getEconomy().format(maxSellCost) + ".");
				event.setCancelled(true);
				sign.getBlock().breakNaturally();
				return;
			}
			
			// TODO check if the cost is different,
			// then we need to set the cost on all other signs
			
			
			break;
		default:
			break;
		}
		
		// TODO plot-sign change event, synced
		// event includes Sign, ISign, Plot, Player
		// 
		// if (event is cancelled) don't save plot and break sign
		
		plot.setSign(plotSign);
		plot.save();
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.isCancelled()) {
			return;// event was cancelled
		}
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;// didn't right-click
		}
		if(!(event.getClickedBlock().getState() instanceof Sign)) {
			return;// not a sign
		}
		Sign sign = (Sign) event.getClickedBlock().getState();
		if(!mgr.getSignValidator().isPlotSign(sign)) {
			return;// not a plot-sign
		}
		
		// plot world
		PlotWorld plotWorld = mgr.getPlotWorld(sign.getWorld().getName());
		
		// get plot information using the sign
		Plot plot;
		try {
			plot = plotWorld.getPlot(sign);
		} catch (SignException e) {
			// not a valid sign
			event.getPlayer().sendRawMessage(ChatColor.RED + e.getMessage());
			event.getClickedBlock().breakNaturally();
			return;
		}
		
		if(plot == null) {
			// didn't find the plot information
			event.getPlayer().sendRawMessage(ChatColor.RED + "Sorry, this sign is invalid. Couldn't find the plot information.");
			event.getClickedBlock().breakNaturally();
			return;
		}
		
		if(plot.getRegion() == null) {
			// protected region doesn't exist
			event.getPlayer().sendRawMessage(ChatColor.RED + "Sorry, region '" + plot.getRegionId() + "' doesn't exist anymore.");
			event.getClickedBlock().breakNaturally();
			plot.delete();
			return;
		}
		

		// TODO plot-sign info event, synced
		// event includes Sign, ISign, Plot, Player and list of messages
		
		// send plot info using messages from event
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		onBlockProtect(event.getBlock(), event);
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		onBlockProtect(event.getBlock(), event);
	}
	
	private void onBlockProtect(Block block, Cancellable event) {
		if(event.isCancelled()) {
			return;// event cancelled
		}
		if(!(block.getState() instanceof Sign)) {
			// TODO check if there's a sign attached to this block
			return;// not a sign
		}
		Sign sign = (Sign) block.getState();
		
		if(!mgr.getSignValidator().isPlotSign(sign)) {
			return;// not a plot-sign
		}
		
		
		ISignData data;
		try {
			data = mgr.getSignValidator().createPlotSign(sign);
		} catch (SignException e) {
			// invalid sign, let it break
			return;
		}
		
		PlotWorld plotWorld = mgr.getPlotWorld(sign.getWorld().getName());
		
		Plot plot = plotWorld.getPlot(data.getRegionId());
		
		if(plot == null) {
			// not a saved sign, let it break
			return;
		}
		
		ProtectedRegion region = plot.getRegion();
		if(region == null) {
			// region doesn't exist anymore
			// let it break
			return;
		}
		
		if(event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			Player player = e.getPlayer();
			
			// check region ownership || permission break-any
			boolean isOwner = region.isOwner(player.getName());
			String breakAny = mgr.getPermissions().getPermission(PlotAction.BREAK_ANY_SIGN);
			if(!isOwner && !player.hasPermission(breakAny)) {
				// not an owner, and no special permission
				player.sendMessage(ChatColor.RED + "You don't own this region.");
				// protect the sign
				event.setCancelled(true);
				return;
			}
			else {
				// TODO send info: "You broke a plot-sign, this many signs are left"
			}
		}
		else {
			// protect the sign
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Block> blocks = event.blockList().iterator();
		while(blocks.hasNext()) {
			Block block = blocks.next();
			onBlockProtect(block, event);
			if(event.isCancelled()) {
				break;
			}
		}
	}

}
