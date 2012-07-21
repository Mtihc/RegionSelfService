package com.mtihc.minecraft.regionselfservice.events;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.exceptions.WoodenSignException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SignClickListener implements Listener {

	public SignClickListener() {
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.player.PlayerListener#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if(event.isCancelled()) {
			return;
		}
		
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		Block block = event.getClickedBlock();
		if(block == null || !(block.getState() instanceof Sign)) {
			return;
		}
		Sign sign = (Sign) block.getState();
		
		boolean isForSaleSign = plugin.woodenSigns().matchFirstLine(
				plugin.config().settings().getFirstLineForSale(), sign.getLine(0));
		boolean isRentSign = plugin.woodenSigns().matchFirstLine(
				plugin.config().settings().getFirstLineForRent(), sign.getLine(0));
		if(!isForSaleSign && !isRentSign) {
			return;
		}
		String regionName;
		try {
			regionName = plugin.woodenSigns().getRegionNameOnSign(sign.getLines());
		} catch(WoodenSignException e) {
			event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		double cost;
		try {
			cost = plugin.woodenSigns().getRegionCostOnSign(sign.getLines());
		} catch(WoodenSignException e) {
			event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		RegionManager mgr = plugin.getWorldGuard().getRegionManager(block.getWorld());
		ProtectedRegion region = mgr.getRegion(regionName);
		if(region == null) {
			event.getPlayer().sendMessage(ChatColor.RED + "Sorry, region '" + regionName + "' doesn't exist anymore.");
			plugin.woodenSigns().breakSign(sign.getBlock(), true);
			return;
		}
		double blockWorth = plugin.config().settings().getBlockWorth();
		if(isForSaleSign) {
			
			onSignClickForSale(sign, event.getPlayer(), region, blockWorth, cost);
		}
		else if(isRentSign) {
			
			onSignClickForRent(sign, event.getPlayer(), region, blockWorth, cost);
		}
		if(event.getPlayer().isSneaking()) {
			return;
		}
		event.setCancelled(true);
		
	}
	
	public void onSignClickForSale(Sign sign, Player player, ProtectedRegion region, double blockWorth, double cost) {
		RegionSelfServicePlugin.getPlugin().sendRegionInfo(player, region, blockWorth, cost, false);
	}
	
	public void onSignClickForRent(Sign sign, Player player, ProtectedRegion region, double blockWorth, double cost) {
		RegionSelfServicePlugin.getPlugin().sendRegionInfo(player, region, blockWorth, cost, true);
	}
	
}
