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

import com.mtihc.regionselfservice.v2.plots.data.ISignData;
import com.mtihc.regionselfservice.v2.plots.data.PlotData;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;

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
			event.getPlayer().sendRawMessage(ChatColor.RED + "Failed to create plot-sign: " + e.getMessage());
			// break the sign
			event.setCancelled(true);
			sign.getBlock().breakNaturally();
			return;
		}
		
		// TODO plot-sign change event, synced
		// event includes Sign, ISign, Plot, Player
		// 
		// TODO check permissions,
		// - sell/rent perms
		// - anywhere perms
		// - anyregion perms
		// 
		// - check min/max cost
		// 	
		// TODO if (event is cancelled) don't save plot
		
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
		
		// TODO send plot info (and sell-info or rent-info)
		
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
		
		if(event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			Player player = e.getPlayer();
			// TODO check region-ownership || perm break-any
			// protect the sign
			event.setCancelled(true);
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
