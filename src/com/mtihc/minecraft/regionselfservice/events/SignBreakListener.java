package com.mtihc.minecraft.regionselfservice.events;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.exceptions.WoodenSignException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SignBreakListener implements Listener {

	public SignBreakListener() {
		
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getBlock();
		
		if(block == null || !(block.getState() instanceof Sign)) {
			return;
		}
		Sign sign = (Sign) block.getState();
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		boolean isSaleSign = plugin.woodenSigns().matchFirstLine(
				plugin.config().settings().getFirstLineForSale(), sign.getLine(0));
		boolean isRentSign = plugin.woodenSigns().matchFirstLine(
				plugin.config().settings().getFirstLineForRent(), sign.getLine(0));
		
		
		if(isSaleSign || isRentSign) {
			
			
			String regionName;
			try {
				regionName = plugin.woodenSigns().getRegionNameOnSign(sign.getLines());
			} catch (WoodenSignException e) {
				event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
				return;
			}
			
			if(!event.getPlayer().hasPermission(Permissions.BREAK_ANY_SIGN)) {
				RegionManager mgr = plugin.getWorldGuard().getRegionManager(block.getWorld());
				ProtectedRegion region = mgr.getRegion(regionName);
				if(region != null)
				{
					if(!region.isOwner(plugin.getWorldGuard().wrapPlayer(event.getPlayer()))) {
						event.getPlayer().sendMessage(ChatColor.RED + "Only the owner of this region can break this sign.");
						event.setCancelled(true);
						return;
					}
				}
			}
			
			
			
			if(isSaleSign) {
				onSignBreakForSale(sign, event.getPlayer(), regionName);
			}
			else if(isRentSign) {
				onSignBreakForRent(event, sign, event.getPlayer(), regionName);
			}
		}
		
	}
	

	private void onSignBreakForRent(BlockBreakEvent event, Sign sign, Player player, String regionName) {
		
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		plugin.config().signsRent().clearRegionSign(player.getWorld().getName(), regionName, sign.getX(), sign.getY(), sign.getZ());
		plugin.config().signsRent().save();
		List<String> signs = plugin.config().signsRent().getRegionSignNames(player.getWorld().getName(), regionName);
		if(signs != null && signs.size() == 0) {
			player.sendMessage(ChatColor.GREEN + "You broke the last " + ChatColor.WHITE + "For Rent" + ChatColor.GREEN + " sign of region " + ChatColor.WHITE + "'" + regionName + "'.");
			player.sendMessage(ChatColor.GREEN + "The region is no longer for rent. Existing renters still have membership, until their time runs out.");
			// do not clear region like sale signs. there is still info we need
		}
		
	}

	private void onSignBreakForSale(Sign sign, Player player, String regionName) {
		
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		plugin.config().signsSale().clearRegionSign(player.getWorld().getName(), regionName, sign.getX(), sign.getY(), sign.getZ());
		plugin.config().signsSale().save();
		List<String> signs = plugin.config().signsSale().getRegionSignNames(player.getWorld().getName(), regionName);
		if(signs == null || signs.size() == 0) {
			player.sendMessage(ChatColor.GREEN + "You broke the last " + ChatColor.WHITE + "For Sale" + ChatColor.GREEN + " sign of region " + ChatColor.WHITE + "'" + regionName + "'.");
			player.sendMessage(ChatColor.GREEN + "The region is no longer for sale.");
			plugin.config().signsSale().clearRegion(player.getWorld().getName(), regionName);
		}
	}
	
	

}
